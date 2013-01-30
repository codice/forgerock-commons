/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock Inc.
 */
package org.forgerock.jaspi.container.modules;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.AssertJUnit.assertTrue;

public class AuthModuleManagerImplTest {

    private AuthModuleManager authModuleManager;

    @Test
    public void should() {

        //Given


        //When
//        authModuleManager.addModule("MODULE_1_NAME", );
//        authModuleManager.addModuleChain("MODULE_CHAIN_1_NAME", );
//        authModuleManager.getModuleChain("MODULE_CHAIN_1_NAME");

        //Then

    }

    @Test
    public void shouldDoNothingWhenRemovingModuleWhichIsNotPresent() throws ModuleManagerException {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_1_NAME", "MODULE_1_CLASSNAME");
        authModuleManager.addModule("MODULE_2_NAME", "MODULE_2_CLASSNAME");

        //When
        authModuleManager.removeModule("MODULE_NAME");

        //Then
        //TODO add getModule method to AuthModuleManager to verify in tests.
//        authModuleManager.
    }

    @Test
    public void shouldNotRemoveModuleWhichIsInModuleChain() throws ModuleManagerException {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_1_NAME", "MODULE_1_CLASSNAME");
        authModuleManager.addModule("MODULE_2_NAME", "MODULE_2_CLASSNAME");
        authModuleManager.addModule("MODULE_3_NAME", "MODULE_3_CLASSNAME");
        authModuleManager.addModuleChain("MODULE_CHAIN_1_NAME", Arrays.asList("MODULE_1_NAME"));
        authModuleManager.addModuleChain("MODULE_CHAIN_2_NAME", Arrays.asList("MODULE_1_NAME", "MODULE_3_NAME"));

        //When
        boolean exceptionCaught = false;
        try {
            authModuleManager.removeModule("MODULE_3_NAME");
        } catch (ModuleManagerException e) {
            exceptionCaught = true;
        }

        //Then
        assertTrue(exceptionCaught);
    }

    @Test
    public void shouldRemoveModule() throws ModuleManagerException {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_1_NAME", "MODULE_1_CLASSNAME");
        authModuleManager.addModule("MODULE_2_NAME", "MODULE_2_CLASSNAME");
        authModuleManager.addModule("MODULE_3_NAME", "MODULE_3_CLASSNAME");
        authModuleManager.addModuleChain("MODULE_CHAIN_1_NAME", Arrays.asList("MODULE_1_NAME"));
        authModuleManager.addModuleChain("MODULE_CHAIN_2_NAME", Arrays.asList("MODULE_1_NAME", "MODULE_3_NAME"));

        //When
        authModuleManager.removeModule("MODULE_2_NAME");

        //Then
        //TODO refactor AuthServerModule creation to separate class so can mock and verify "get" method return values
    }

    @Test
    public void shouldNotAddModuleChainIfModuleNotPresent() {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_1_NAME", "MODULE_1_CLASSNAME");
        authModuleManager.addModule("MODULE_3_NAME", "MODULE_3_CLASSNAME");

        //When
        boolean exceptionCaught = false;
        try {
            authModuleManager.addModuleChain("MODULE_CHAIN", Arrays.asList("MODULE_1_NAME", "MODULE_2_NAME",
                    "MODULE_3_NAME"));
        } catch (ModuleManagerException e) {
            exceptionCaught = true;
        }

        //Then
        assertTrue(exceptionCaught);
    }

    @Test
    public void shouldDoNothingWhenRemovingModuleChainWhichIsNotPresent() throws ModuleManagerException {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_NAME", "MODULE_CLASSNAME");
        authModuleManager.addModuleChain("MODULE_CHAIN_1", Arrays.asList("MODULE_NAME"));

        //When
        authModuleManager.removeModuleChain("MODULE_CHAIN");

        //Then
        //TODO get to verify
    }

    @Test
    public void shouldRemoveModuleChain() throws ModuleManagerException {

        //Given
        authModuleManager = new AuthModuleManagerImpl();

        authModuleManager.addModule("MODULE_NAME", "MODULE_CLASSNAME");
        authModuleManager.addModuleChain("MODULE_CHAIN_1", Arrays.asList("MODULE_NAME"));
        authModuleManager.addModuleChain("MODULE_CHAIN_2", Arrays.asList("MODULE_NAME"));

        //When
        authModuleManager.removeModuleChain("MODULE_CHAIN_1");

        //Then
        //TODO get to verify
    }
}
