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
package io.openliberty.microprofile.lra.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.container.service.annocache.AnnotationsBetaHelper;
import com.ibm.ws.container.service.annotations.WebAnnotations;
import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedApplicationInfo;
import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.anno.targets.AnnotationTargets_Targets;

/**
 *
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, service = { ApplicationStateListener.class })
public class StartupComponent implements ApplicationStateListener {

    /**  */
    private static final String ORG_ECLIPSE_MICROPROFILE_LRA_ANNOTATIONS_WS_RS_LRA = "org.eclipse.microprofile.lra.annotation.ws.rs.LRA";
    private static Map<String, Set<String>> lraData = new HashMap<>();

    public static Map<String, Set<String>> getLraData() {
        return lraData;
    }

    /** {@inheritDoc} */
    @Override
    public void applicationStarting(ApplicationInfo appInfo) throws StateChangeException {
        Container appContainer = appInfo.getContainer();
        String containerName = appContainer.getName();
        ExtendedApplicationInfo infoEx = (ExtendedApplicationInfo) appInfo;
        String j2eeName = infoEx.getMetaData().getJ2EEName().toString();
        System.err.println("Application is starting: " + appInfo.getName() + " container " + containerName + "j2ee " + j2eeName);
        WebAnnotations webAnnotations;
        AnnotationTargets_Targets targets;
        try {
            webAnnotations = AnnotationsBetaHelper.getWebAnnotations(appContainer);
            targets = webAnnotations.getAnnotationTargets();

        } catch (UnableToAdaptException e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
            System.err.println("error: " + e);
            return;
        }
        //Set<String> classes = targets.getAnnotatedClasses("javax.ws.rs.Path");
        //targets.getClassesWithMethodAnnotation(annotationName)
        //for (String className : classes) {
        //    System.err.println("name: " + className);
        //}

        Set<String> result = getParticipants(targets, j2eeName.toString());
        lraData.put(j2eeName, result);

    }

    /**
     * This is effectively an implementation of the LraClassFinder interface that is proposed
     * for narayana.
     * Need to return a set of classes from the application where:
     * - The class is not abstract or an interface
     * - There is a jaxrs @Path annotation on either the class or any method
     * - The @LRA annotation is present on either
     * * the class
     * * one or more methods in the class
     * That is, the class is an LRA participant
     *
     * TODO:
     * - return classes not class names
     * - as per the LraClassFinder interface, find a way to throw an error
     * if the class is an invalid participant, in that there is neither
     * a compensate or afterLRA method in the class
     * - add the check for abstract classes/interfaces
     * -
     */
    private Set<String> getParticipants(AnnotationTargets_Targets targets, String appName) {
        System.err.println("LRASD: scanning app " + appName);
        Set<String> result = new HashSet<>();
        // TODO this is currently just using the 'SEED' results.
        // Need to extend as per the suggestiongs from martin (and talk to thomas b.)
        Set<String> classNames = targets.getAnnotatedClasses("javax.ws.rs.Path");
        if (classNames.isEmpty()) {
            System.err.println("LRASD: no jaxrs class for app " + appName);
            return result;
        }
        for (String name : classNames) {
            boolean lra = false;
            Set<String> methodAnnotations = targets.getMethodAnnotations(name);
            if (methodAnnotations.contains(ORG_ECLIPSE_MICROPROFILE_LRA_ANNOTATIONS_WS_RS_LRA)) {
                System.err.println("LRASD: there is an lra method annotation in class " + name);
                lra = true;
            }
            Set<String> classAnnotations = targets.getClassAnnotations(name);
            if (classAnnotations.contains(ORG_ECLIPSE_MICROPROFILE_LRA_ANNOTATIONS_WS_RS_LRA)) {
                System.err.println("LRASD: there is an lra class annotation in class " + name);
                lra = true;
            }
            if (!lra) {
                System.err.println("LRASD: there are  no lra annotations in class " + name);
                continue;
            }

            // TODO there needs to be a check that there is at least one of compensate or afterlra
            // It probably needs to be done here, as this is where there are access to the annotations
            result.add(name);
        }
        return result;

    }

    /** {@inheritDoc} */
    @Override
    public void applicationStarted(ApplicationInfo appInfo) throws StateChangeException {
        System.err.println("Application has started: " + appInfo.getName());

    }

    /** {@inheritDoc} */
    @Override
    public void applicationStopping(ApplicationInfo appInfo) {
        System.err.println("Application is stopping: " + appInfo.getName());

    }

    /** {@inheritDoc} */
    @Override
    public void applicationStopped(ApplicationInfo appInfo) {
        System.err.println("Application has stopped: " + appInfo.getName());
    }

}
