/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

package org.forgerock.script.groovy;

import java.net.URL;

import org.testng.Assert;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class ScriptClassLoaderTest {

    // @Test
    public void urlTest() throws Exception {

        URL[] roots = new URL[] { ScriptClassLoaderTest.class.getResource("/") };
        GroovyScriptEngine gse = new GroovyScriptEngine(roots);
        Binding binding = new Binding();
        binding.setVariable("input", "world");
        gse.run("container/TestMe.groovy", binding);
        System.out.println(binding.getVariable("output"));

        Assert.assertTrue(true);
    }
}
