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

package org.forgerock.jaspi.inttest;

import org.forgerock.jaspi.container.config.ConfigurationManager;
import org.forgerock.jaspi.filter.AuthNFilter;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.security.auth.message.AuthException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class IntTest {

    @AfterMethod
    public void setUp() {
        ConfigurationManager.unconfigure();
    }

    @Test
    public void shouldThrowServletExceptionWhenModuleConfigurationNotSpecified() throws IOException, ServletException {

        //Given

        //When
        FilterRunner filterRunner = new FilterRunner();
        boolean exceptionCaught = false;
        Exception exception = null;
        try {
            filterRunner.run();
        } catch (ServletException e) {
            exceptionCaught = true;
            exception = e;
        }

        //Then
        assertTrue(exceptionCaught);
        assertTrue(ServletException.class.isAssignableFrom(exception.getClass()));
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
    }

    @Test
    public void shouldThrowServletExceptionWhenNotConfigured() throws IOException, ServletException {

        //Given

        //When
        FilterRunner filterRunner = new FilterRunner();
        boolean exceptionCaught = false;
        Exception exception = null;
        try {
            filterRunner.run("default");
        } catch (ServletException e) {
            exceptionCaught = true;
            exception = e;
        }

        //Then
        assertTrue(exceptionCaught);
        assertTrue(ServletException.class.isAssignableFrom(exception.getClass()));
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
    }

    @Test
    public void shouldThrowAuthExceptionWhenConfigurationEmpty() throws IOException, AuthException, ServletException {

        //Given
        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        boolean exceptionCaught = false;
        Exception exception = null;
        try {
            filterRunner.run("none");
        } catch (ServletException e) {
            exceptionCaught = true;
            exception = e;
        }

        //Then
        assertTrue(exceptionCaught);
        assertTrue(AuthException.class.isAssignableFrom(exception.getCause().getClass()));
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
    }

    @Test
    public void shouldAuthenticateAndNotCallSecureResponseWithValidateSuccessSessionModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-only");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldNotAuthenticateWithValidateSendFailureSessionModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateAndNotCallSecureResponseWithValidateSendSuccessSessionModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendSuccessSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldNotAuthenticateAndNotCallSecureResponseWithValidateSendContinueSessionModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendContinueSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateAndNotCallSecureResponseWithValidateSuccessAuthModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, Object> contextProperties = new HashMap<String, Object>();

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("auth-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("auth-only");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse()).addHeader(eq("AUTH_SUCCESS"), anyString());
    }

    @Test
    public void shouldNotAuthenticateWithValidateSendFailureAuthModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, Object> contextProperties = new HashMap<String, Object>();

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("auth-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("auth-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateAndNotCallSecureResponseWithValidateSendSuccessAuthModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, Object> contextProperties = new HashMap<String, Object>();

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("auth-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("auth-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldNotAuthenticateAndNotCallSecureResponseWithValidateSendContinueAuthModule() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, Object> contextProperties = new HashMap<String, Object>();

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendContinueAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("auth-only", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("auth-only");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateJustUsingValidateSuccessSessionModuleAndNotCallAnySecureResponse() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldNotAuthenticateUsingValidateSendSuccessSessionModuleAndNotCallAnySecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendSuccessSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldNotAuthenticateUsingValidateSendContinueSessionModuleAndNotCallAnySecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendContinueSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateUsingValidateSuccessAuthModuleAndCallBothSecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse()).addHeader(eq("session"), anyString());
        verify(filterRunner.getResponse()).addHeader(eq("AUTH_SUCCESS"), anyString());
    }

    @Test
    public void shouldNotAuthenticateUsingValidateSendSuccessAuthModuleAndCallSessionSecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendSuccessAuthModule");
        authModules.add(moduleProps);

        moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth-auth");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse()).addHeader(eq("session"), anyString());
        verify(filterRunner.getResponse(), never()).addHeader(eq("AUTH_SEND_SUCCESS"), anyString());
        verify(filterRunner.getResponse(), never()).addHeader(eq("AUTH_SUCCESS"), anyString());
    }

    @Test
    public void shouldNotAuthenticateUsingValidateSendFailureAuthModuleAndNotCallAnySecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
        verify(filterRunner.getResponse()).setStatus(401);
    }

    @Test
    public void shouldNotAuthenticateUsingValidateSendContinueAuthModuleAndNotCallAnySecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendContinueAuthModule");
        authModules.add(moduleProps);

        moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        verify(filterRunner.getFilterChain(), never()).doFilter((HttpServletRequest) anyObject(),
                (HttpServletResponse) anyObject());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
    }

    @Test
    public void shouldAuthenticateUsingSecondValidateSuccessAuthModuleAndCallSecondAndSessionSecureResponse()
            throws IOException, ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureAuthModule");
        authModules.add(moduleProps);

        moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSuccessAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth-auth");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse()).addHeader(eq("session"), anyString());
        verify(filterRunner.getResponse()).addHeader(eq("AUTH_SUCCESS"), anyString());
        verify(filterRunner.getResponse(), never()).addHeader(eq("AUTH_SEND_FAILURE"), anyString());
    }

    @Test
    public void shouldAuthenticateAndNotCallSessionSecureResponseWithSecureSendContinue() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.SecureSendContinueAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
        verify(filterRunner.getResponse()).setStatus(100);
    }

    @Test
    public void shouldAuthenticateAndNotCallSessionSecureResponseWithSecureSendFailure() throws IOException,
            ServletException, AuthException {

        //Given
        Map<String, String> sessionModuleProps = new HashMap<String, String>();
        sessionModuleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.ValidateSendFailureSessionModule");

        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("session-module", sessionModuleProps);

        List<Map<String, String>> authModules = new ArrayList<Map<String, String>>();
        contextProperties.put("auth-modules", authModules);

        Map<String, String> moduleProps = new HashMap<String, String>();
        moduleProps.put("class-name", "org.forgerock.jaspi.inttest.modules.SecureSendFailureAuthModule");
        authModules.add(moduleProps);

        Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();
        authContexts.put("session-auth", contextProperties);
        ConfigurationManager.configure(authContexts);

        //When
        FilterRunner filterRunner = new FilterRunner();
        filterRunner.run("session-auth");

        //Then
        ArgumentCaptor<HttpServletRequestWrapper> requestArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        ArgumentCaptor<HttpServletResponseWrapper> responseArgumentCaptor =
                ArgumentCaptor.forClass(HttpServletResponseWrapper.class);

        verify(filterRunner.getFilterChain()).doFilter(requestArgumentCaptor.capture(),
                responseArgumentCaptor.capture());
        assertEquals(requestArgumentCaptor.getValue().getRequest(), filterRunner.getRequest());
        assertEquals(responseArgumentCaptor.getValue().getResponse(), filterRunner.getResponse());
        verify(filterRunner.getResponse(), never()).addHeader(anyString(), anyString());
        verify(filterRunner.getResponse()).setStatus(500);
    }
}

class FilterRunner {

    private final AuthNFilter authFilter = new AuthNFilter();
    private final FilterConfig filterConfig;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final FilterChain filterChain;

    public FilterRunner() {
        filterConfig = mock(FilterConfig.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    public void run() throws IOException, ServletException {
        run(null);
    }

    public void run(String moduleConfigurationValue) throws IOException, ServletException {

        given(request.getRequestURL()).willReturn(new StringBuffer("http://localhost:8080/jaspi/resource.jsp"));
        given(request.getContextPath()).willReturn("CONTEXT_PATH");
        given(filterConfig.getInitParameter(AuthNFilter.MODULE_CONFIGURATION_PROPERTY))
                .willReturn(moduleConfigurationValue);

        authFilter.init(filterConfig);
        authFilter.doFilter(request, response, filterChain);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
}
