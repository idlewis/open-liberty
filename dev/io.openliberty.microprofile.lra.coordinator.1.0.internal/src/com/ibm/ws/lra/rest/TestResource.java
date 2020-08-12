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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("res")
public class TestResource {

    private static final Logger LOGGER = Logger.getLogger(TestResource.class.getName());

    @GET
    public String getTest() {
        LOGGER.log(Level.FINE, "I am fine thank you");
        LOGGER.log(Level.SEVERE, "I am SEVERE thank you");
        return "Hello from a rooty feature/jaxrs at " + new Date().toString() + "\n";
    }

}
