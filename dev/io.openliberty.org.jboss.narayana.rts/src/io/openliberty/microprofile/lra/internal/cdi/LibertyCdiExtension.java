/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.microprofile.lra.internal.cdi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

import io.narayana.lra.client.internal.proxy.nonjaxrs.LRAParticipant;
import io.narayana.lra.client.internal.proxy.nonjaxrs.LRAParticipantRegistry;
import io.openliberty.microprofile.lra.internal.StartupComponent;

/**
 *
 */
public class LibertyCdiExtension implements Extension {

    public void observe(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
        System.err.println("I'm observing again");
        Map<String, Set<String>> lraData = StartupComponent.getLraData();
        J2EEName name = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData().getModuleMetaData().getApplicationMetaData().getJ2EEName();
        System.err.println("name is " + name);
        Set<String> classNames = lraData.get(name.toString());
        if (classNames == null) {
            // This app is not an LRA app
            // TODO This is only valid if the filters aren't registered for the app
            // currently the filters are registered for all applications
            // And so the bean must be registered for all apps, otherwise a cdi exception will happen due to an injection failure in the filter.
            System.err.println("App " + name + " is not lra app. No bean will be registered");
            return;
        }

        Map<String, LRAParticipant> participants = new HashMap<>();

        // The list of classes is a list of classes with lra annotations.
        // The list has not been checked to see if they are interfaces/abstract, as the class needs to be loaded for that.
        ClassLoader ldr = Thread.currentThread().getContextClassLoader();
        for (String className : classNames) {
            System.err.println("checkign participant " + className);
            Class<?> loaded;
            try {
                loaded = ldr.loadClass(className);
                System.err.println("Loaded class " + className);
                int mods = loaded.getModifiers();
                if (!Modifier.isInterface(mods) && !Modifier.isAbstract(mods)) {
                    System.err.println("LRASD: loaded class " + name + " and it is real");
                    // create the participant and check that it is non-jaxrs
                } else {
                    System.err.println("LRASD: loaded class " + name + " but it was fake");
                    continue;
                }

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
                // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
                System.err.println("Couldn't load class " + className);
                return;
            }
            try {
                System.err.println("LRASD: lchecking for nonjaxrs methods");
                Constructor<LRAParticipant> participantConstructor = LRAParticipant.class.getDeclaredConstructor(Class.class);
                participantConstructor.setAccessible(true);
                LRAParticipant participant = participantConstructor.newInstance(loaded);
                Method checkMethod = LRAParticipant.class.getDeclaredMethod("hasNonJaxRsMethods");
                checkMethod.setAccessible(true);
                Boolean hasNonJaxRsMethods = (Boolean) checkMethod.invoke(participant);
                if (hasNonJaxRsMethods) {
                    System.err.println("LRASD: lchecking for nonjaxrs methods: found some");
                    participants.put(loaded.getName(), participant);
                } else {
                    System.err.println("LRASD: lchecking for nonjaxrs methods: none there");
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
                // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
                System.err.println("Couldn't construct a participant: " + e);
                return;
            }
            //loaded.get
        }

        LRAParticipantRegistry reg;

        try {
            Constructor<LRAParticipantRegistry> con = LRAParticipantRegistry.class.getDeclaredConstructor(Map.class);
            con.setAccessible(true);
            reg = con.newInstance(participants);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
            System.err.println("Failed to construct registry: " + e);
            return;
        }

        event.addBean().read(beanManager.createAnnotatedType(LRAParticipantRegistry.class)).beanClass(LRAParticipantRegistry.class).scope(ApplicationScoped.class).createWith(context -> reg);
        System.err.println("I have registered a bean with no participants");
    }

}
