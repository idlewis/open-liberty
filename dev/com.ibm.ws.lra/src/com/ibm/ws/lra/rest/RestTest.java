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
package com.ibm.ws.lra.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 */
@ApplicationPath("/app")
public class RestTest extends Application {

    private static final Logger LOGGER = Logger.getLogger(RestTest.class.getName());

    public RestTest() {
        LOGGER.log(Level.FINE, "I am fine and constructing  classes thank you");
        LOGGER.log(Level.SEVERE, "I am SEVERE and constructing classes thank you");

    }

    /*
     * @Override
     * public Set<Class<?>> getClasses() {
     * 
     * LOGGER.log(Level.FINE, "I am fine and getting classes thank you");
     * LOGGER.log(Level.SEVERE, "I am SEVERE and getting classes thank you");
     * 
     * return Collections.singleton(TestResource.class);
     * }
     */

}
