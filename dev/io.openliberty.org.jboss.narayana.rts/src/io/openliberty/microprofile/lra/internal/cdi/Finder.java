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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.narayana.lra.client.internal.proxy.nonjaxrs.LraClassFinder;
import io.openliberty.microprofile.lra.internal.StartupComponent;

/**
 *
 */
public class Finder implements LraClassFinder {

    /** {@inheritDoc} */
    @Override
    public Set<Class<?>> getClasses() throws IOException, ClassNotFoundException {
        System.err.println("I am not going to find any classes");
        Map<String, Set<String>> data = StartupComponent.getLraData();
        System.err.println("but I have found " + data.size() + " apps");
        return Collections.emptySet();
    }

}
