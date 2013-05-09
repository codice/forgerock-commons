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

package org.forgerock.jaspi.container;

import org.forgerock.jaspi.container.config.AuthConfigProviderImpl;
import org.forgerock.jaspi.container.config.AuthConfigProviderMap;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

public class AuthConfigProviderMapTest {

    private AuthConfigProviderMap authConfigProviderMap;

    @Before
    public void setUp() {
        authConfigProviderMap = new AuthConfigProviderMap();
    }

    @Test
    public void shouldAddAuthConfigProvider() {

        //Given
        AuthConfigProvider authConfigProvider = new AuthConfigProviderImpl(new HashMap(), null);

        //When
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID", authConfigProvider);

        //Then
        boolean hasRegistration = authConfigProviderMap.hasAuthConfigProviderRegistration("REGISTRATION_ID");
        AuthConfigProvider provider = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID");

        assertTrue(hasRegistration);
        assertEquals(authConfigProvider, provider);
    }

    @Test
    public void shouldAddRegistrationListenerToProviderWithNoneAlready() {

        //Given
        RegistrationListener registrationListener = mock(RegistrationListener.class);

        //When
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener);

        //Then
        Set<RegistrationListener> regListeners = authConfigProviderMap.getRegistrationListener("REGISTRATION_ID");

        assertEquals(1, regListeners.size());
        assertTrue(regListeners.contains(registrationListener));
    }

    @Test
    public void shouldAddRegistrationListenerToProviderWithSomeAlready() {

        //Given
        RegistrationListener registrationListener1 = mock(RegistrationListener.class);
        RegistrationListener registrationListener2 = mock(RegistrationListener.class);
        RegistrationListener registrationListener3 = mock(RegistrationListener.class);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener1);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener2);

        //When
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener3);

        //Then
        Set<RegistrationListener> regListeners = authConfigProviderMap.getRegistrationListener("REGISTRATION_ID");

        assertEquals(3, regListeners.size());
        assertTrue(regListeners.contains(registrationListener1));
        assertTrue(regListeners.contains(registrationListener2));
        assertTrue(regListeners.contains(registrationListener3));
    }

    @Test
    public void shouldAddRegistrationListenerToProviderWithSameOneAlready() {

        //Given
        RegistrationListener registrationListener1 = mock(RegistrationListener.class);
        RegistrationListener registrationListener2 = mock(RegistrationListener.class);
        RegistrationListener registrationListener3 = mock(RegistrationListener.class);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener1);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener2);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener3);

        //When
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID", registrationListener3);

        //Then
        Set<RegistrationListener> regListeners = authConfigProviderMap.getRegistrationListener("REGISTRATION_ID");

        assertEquals(3, regListeners.size());
        assertTrue(regListeners.contains(registrationListener1));
        assertTrue(regListeners.contains(registrationListener2));
        assertTrue(regListeners.contains(registrationListener3));
    }

    @Test
    public void shouldReplaceAuthConfigProvider() throws AuthException {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID", authConfigProvider1);

        //When
        authConfigProviderMap.replaceAuthConfigProvider("REGISTRATION_ID", authConfigProvider2);

        //Then
        AuthConfigProvider provider = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID");
        assertEquals(authConfigProvider2, provider);
    }

    @Test
    public void shouldFailToReplaceAuthConfigProviderIfNotAlreadyPresent() {

        //Given
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);

        //When
        boolean exceptionCaught = false;
        try {
            authConfigProviderMap.replaceAuthConfigProvider("REGISTRATION_ID", authConfigProvider2);
        } catch (AuthException e) {
            exceptionCaught = true;
        }

        //Then
        AuthConfigProvider provider = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID");
        assertNull(provider);
        assertTrue(exceptionCaught);
    }

    @Test
    public void shouldAddRegistrationContext() {

        //Given
        AuthConfigFactory.RegistrationContext registrationContext = new RegistrationContextImpl("LAYER",
                "APPCONTEXT", "DESCRIPTION", true);

        //When
        authConfigProviderMap.addRegistrationContext("REGISTRATION_ID", registrationContext);

        //Then
        AuthConfigFactory.RegistrationContext regContext = authConfigProviderMap.getRegistrationContext(
                "REGISTRATION_ID");
        assertEquals(registrationContext, regContext);
    }

    @Test
    public void shouldRemoveAuthConfigProviderRegistration() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID1", authConfigProvider1);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID2", authConfigProvider2);

        //When
        boolean result = authConfigProviderMap.removeAuthConfigProviderRegistration("REGISTRATION_ID2");

        //Then
        assertTrue(result);
        AuthConfigProvider provider1 = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID1");
        AuthConfigProvider provider2 = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID2");
        assertEquals(authConfigProvider1, provider1);
        assertNull(provider2);
    }

    @Test
    public void shouldNotRemoveAuthConfigProviderRegistrationIfNotAlreadyPresent() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID1", authConfigProvider1);

        //When
        boolean result = authConfigProviderMap.removeAuthConfigProviderRegistration("REGISTRATION_ID2");

        //Then
        assertFalse(result);
        AuthConfigProvider provider1 = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID1");
        AuthConfigProvider provider2 = authConfigProviderMap.getAuthConfigProvider("REGISTRATION_ID2");
        assertEquals(authConfigProvider1, provider1);
        assertNull(provider2);
    }

    @Test
    public void shouldRemoveRegistrationListener() {

        //Given
        RegistrationListener registrationListener1 = mock(RegistrationListener.class);
        RegistrationListener registrationListener2 = mock(RegistrationListener.class);
        RegistrationListener registrationListener3 = mock(RegistrationListener.class);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID1", registrationListener1);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID2", registrationListener2);
        authConfigProviderMap.addRegistrationListener("REGISTRATION_ID3", registrationListener3);

        //When
        String[] registrationIds = authConfigProviderMap.removeRegistrationListener("REGISTRATION_ID1",
                registrationListener1);

        //Then
        assertEquals(1, registrationIds.length);
        assertEquals("REGISTRATION_ID1", registrationIds[0]);
    }

    @Test
    public void shouldGetRegistrationIds() {

        //Given
        AuthConfigProvider authConfigProvider1 = new AuthConfigProviderImpl(new HashMap(), null);
        AuthConfigProvider authConfigProvider2 = new AuthConfigProviderImpl(new HashMap(), null);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID1", authConfigProvider1);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID2", authConfigProvider1);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID3", authConfigProvider2);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID4", authConfigProvider1);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID5", authConfigProvider2);
        authConfigProviderMap.addAuthConfigProvider("REGISTRATION_ID6", authConfigProvider1);

        //When
        String[] registrationIds = authConfigProviderMap.getRegistrationIds(authConfigProvider1);

        //Then
        assertEquals(4, registrationIds.length);
        List<String> regIds = Arrays.asList(registrationIds);
        assertTrue(regIds.contains("REGISTRATION_ID1"));
        assertTrue(regIds.contains("REGISTRATION_ID2"));
        assertTrue(regIds.contains("REGISTRATION_ID4"));
        assertTrue(regIds.contains("REGISTRATION_ID6"));
    }
}
