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

package org.forgerock.jaspi.container.config;

import org.forgerock.jaspi.container.config.AuthConfigFactoryImpl;
import org.forgerock.jaspi.container.config.AuthConfigProviderImpl;
import org.forgerock.jaspi.container.initialisation.AuthConfigProviderLoader;
import org.forgerock.jaspi.container.initialisation.AuthConfigProviderLoaderFactoryTestDelegate;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthConfigFactoryImplTest {

    private AuthConfigFactoryImpl authConfigFactory;

    private AuthConfigProviderLoader authConfigProviderLoader;

    private AuthConfigFactoryImpl.RegistrationIdGenerator registrationIdGenerator;

    @Before
    public void setUp() throws AuthException {

        authConfigProviderLoader = mock(AuthConfigProviderLoader.class);

        AuthConfigProviderLoaderFactoryTestDelegate.setAuthConfigProviderLoader(authConfigProviderLoader);

        registrationIdGenerator = mock(AuthConfigFactoryImpl.RegistrationIdGenerator.class);

        authConfigFactory = new AuthConfigFactoryImpl();

//        authConfigFactory.setRegistrationIdGenerator(registrationIdGenerator);
    }

    @Test
    public void shouldGetConfigProvider() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider3 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider4 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId(null, null)).willReturn("REGISTRATION_ID1");
        given(registrationIdGenerator.generateRegistrationId("LAYER1", null)).willReturn("REGISTRATION_ID2");
        given(registrationIdGenerator.generateRegistrationId(null, "APPCONTEXT1")).willReturn("REGISTRATION_ID3");
        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID4");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(new String[]{null, null});
        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID2")).willReturn(
                new String[]{"LAYER1", null});
        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID3")).willReturn(
                new String[]{null, "APPCONTEXT1"});
        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID4")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, null, null, "DESCRIPTION");
        authConfigFactory.registerConfigProvider(authConfigProvider2, "LAYER1", null, "DESCRIPTION");
        authConfigFactory.registerConfigProvider(authConfigProvider3, null, "APPCONTEXT1", "DESCRIPTION");
        authConfigFactory.registerConfigProvider(authConfigProvider4, "LAYER1", "APPCONTEXT1", "DESCRIPTION");

        //When
        AuthConfigProvider provider1 = authConfigFactory.getConfigProvider("LAYER1", "APPCONTEXT1", null);
        AuthConfigProvider provider2 = authConfigFactory.getConfigProvider("LAYER2", "APPCONTEXT1", null);
        AuthConfigProvider provider3 = authConfigFactory.getConfigProvider("LAYER1", "APPCONTEXT2", null);
        AuthConfigProvider provider4 = authConfigFactory.getConfigProvider("LAYER3", "APPCONTEXT3", null);
        AuthConfigProvider provider5 = authConfigFactory.getConfigProvider(null, "APPCONTEXT1", null);
        AuthConfigProvider provider6 = authConfigFactory.getConfigProvider("LAYER1", null, null);
        AuthConfigProvider provider7 = authConfigFactory.getConfigProvider(null, null, null);

        //Then
        assertEquals(authConfigProvider4, provider1);
        assertEquals(authConfigProvider3, provider2);
        assertEquals(authConfigProvider2, provider3);
        assertEquals(authConfigProvider1, provider4);
        assertEquals(authConfigProvider3, provider5);
        assertEquals(authConfigProvider2, provider6);
        assertEquals(authConfigProvider1, provider7);
    }

    @Test
    public void shouldGetNullConfigProvider() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION");

        //When
        AuthConfigProvider provider1 = authConfigFactory.getConfigProvider("LAYER2", "APPCONTEXT2", null);

        //Then
        assertNull(provider1);
    }

    @Test
    public void shouldRegisterAndNotifyListenerWhenConfigProviderGotAndThenRemoved() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION");

        RegistrationListener registrationListener = mock(RegistrationListener.class);

        authConfigFactory.getConfigProvider("LAYER1", "APPCONTEXT1", registrationListener);

        //When
        boolean result = authConfigFactory.removeRegistration("REGISTRATION_ID1");

        //Then
        assertTrue(result);
        verify(registrationListener).notify("LAYER1", "APPCONTEXT1");
    }

    @Test
    public void shouldRegisterAndNotifyListenerWhenConfigProviderReplaced() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION");

        RegistrationListener registrationListener = mock(RegistrationListener.class);

        authConfigFactory.getConfigProvider("LAYER1", "APPCONTEXT1", registrationListener);

        //When
        String registrationId = authConfigFactory.registerConfigProvider(authConfigProvider2, "LAYER1", "APPCONTEXT1",
                "DESCRIPTION1");

        //Then
        assertEquals("REGISTRATION_ID1", registrationId);
        verify(registrationListener).notify("LAYER1", "APPCONTEXT1");
    }

    @Test
    public void shouldDetachListener() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION");

        RegistrationListener registrationListener = mock(RegistrationListener.class);

        authConfigFactory.getConfigProvider("LAYER1", "APPCONTEXT1", registrationListener);

        //When
        String[] registrationIds = authConfigFactory.detachListener(registrationListener, "LAYER1", "APPCONTEXT1");

        //Then
        assertEquals(1, registrationIds.length);
        assertEquals("REGISTRATION_ID1", registrationIds[0]);
    }

    @Test
    public void shouldGetRegistrationIDs() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");
        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT2")).willReturn("REGISTRATION_ID2");
        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT3")).willReturn("REGISTRATION_ID3");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});
        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID2")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT2"});
        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID3")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT3"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION1");
        authConfigFactory.registerConfigProvider(authConfigProvider2, "LAYER1", "APPCONTEXT2", "DESCRIPTION2");
        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT3", "DESCRIPTION3");

        //When
        String[] registrationIds = authConfigFactory.getRegistrationIDs(authConfigProvider1);

        //Then
        assertEquals(2, registrationIds.length);
        assertEquals("REGISTRATION_ID1", registrationIds[0]);
        assertEquals("REGISTRATION_ID3", registrationIds[1]);
    }

    @Test
    public void shouldGetRegistrationContextPersistent() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);

        given(registrationIdGenerator.generateRegistrationId("LAYER1", "APPCONTEXT1")).willReturn("REGISTRATION_ID1");

        given(registrationIdGenerator.decodeRegistrationId("REGISTRATION_ID1")).willReturn(
                new String[]{"LAYER1", "APPCONTEXT1"});

        authConfigFactory.registerConfigProvider(authConfigProvider1, "LAYER1", "APPCONTEXT1", "DESCRIPTION1");

        //When
        AuthConfigFactory.RegistrationContext registrationContext = authConfigFactory.getRegistrationContext(
                "REGISTRATION_ID1");

        //Then
        assertNotNull(registrationContext);
        assertEquals(registrationContext.getMessageLayer(), "LAYER1");
        assertEquals(registrationContext.getAppContext(), "APPCONTEXT1");
        assertEquals(registrationContext.getDescription(), "DESCRIPTION1");
    }

    @Test
    public void shouldRefresh() {

    }
}
