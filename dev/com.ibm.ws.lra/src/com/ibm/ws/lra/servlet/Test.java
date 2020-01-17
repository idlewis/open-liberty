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
package com.ibm.ws.lra.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class Test extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final TraceComponent tc = Tr.register(Test.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Tr.error(tc, "I am getting");
        res.getOutputStream().println("I am a servlet on liberty");
    }

}
