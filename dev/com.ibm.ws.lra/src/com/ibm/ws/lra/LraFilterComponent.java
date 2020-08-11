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
package com.ibm.ws.lra;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.providers.api.JaxRsProviderRegister;

import io.narayana.lra.client.NarayanaLRAClient;
import io.narayana.lra.filter.ClientLRARequestFilter;
import io.narayana.lra.filter.ClientLRAResponseFilter;
import io.narayana.lra.filter.ServerLRAFilter;

/**
 * A declarative services component can be completely POJO based
 * (no awareness/use of OSGi services).
 *
 * OSGi methods (activate/deactivate) should be protected.
 */
@Component(service = { JaxRsProviderRegister.class })
public class LraFilterComponent implements JaxRsProviderRegister {

    private static final TraceComponent tc = Tr.register(LraFilterComponent.class);

    @Reference
    private LraConfig config;

    /**
     * DS method to activate this component.
     * Best practice: this should be a protected method, not public or private
     *
     * @param properties : Map containing service & config properties
     *            populated/provided by config admin
     */
    @Activate
    protected void activate(Map<String, Object> properties) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "LraFilterComponent activated with a service", properties);
        }
        String coordString = "http://" + config.getHost() + ":" + config.getPort() + "/" + config.getPath();
        if (TraceComponent.isAnyTracingEnabled() && tc.isInfoEnabled()) {
            Tr.info(tc, "Attempting to contact coordinator at " + coordString);
        }
        try {
            URI coord = new URI(coordString);
            NarayanaLRAClient.setDefaultCoordinatorEndpoint(coord);
        } catch (URISyntaxException e) {
            // TODO Not sure how to handle config problems yet
            Tr.error(tc, "Things went wrong");
            throw new RuntimeException("Failed to set coordinator path to " + coordString, e);
        }

    }

    /**
     * DS method to deactivate this component.
     * Best practice: this should be a protected method, not public or private
     *
     * @param reason int representation of reason the component is stopping
     */
    protected void deactivate(int reason) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "SampleComponent deactivated, reason=" + reason);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void installProvider(boolean clientSide, List<Object> providers, Set<String> features) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Attempting to register LRA filters");
        }

        if (clientSide) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.event(tc, "Registering client side filters");
            }
            ClientLRARequestFilter requestFilter = new ClientLRARequestFilter();
            providers.add(requestFilter);
            ClientLRAResponseFilter responseFilter = new ClientLRAResponseFilter();
            providers.add(responseFilter);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.event(tc, "Registering serverside side filters");
            }

            ServerLRAFilter filter = null;
            try {
                filter = new ServerLRAFilter();
            } catch (Exception e) {

                // TODO Do something meaningful

                // TODO Auto-generated catch block
                // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
                // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
                Tr.error(tc, "Couldn't register the Server filter", e);
                return;
            }
            providers.add(filter);
        }

    }

}
