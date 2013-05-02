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

package org.forgerock.jaspi.filter;

import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.testng.AssertJUnit.assertTrue;

public class AuthNFilterTest {

    public static final String MODULE_CHAIN = "moduleChain";
    private AuthNFilter authNFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private AuthConfigFactory authConfigFactory;

    @BeforeClass
    public void setUp() {
        authNFilter = new AuthNFilter();

        authConfigFactory = mock(AuthConfigFactory.class);
        AuthConfigFactory.setFactory(authConfigFactory);
    }

    @BeforeMethod
    public void setUpMethod() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    public void shouldThrowExceptionIfModuleChainIsNull() {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn(null);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.init(filterConfig);
        } catch (ServletException e) {
            exceptionCaught = true;
        }

        //Then
        assertTrue(exceptionCaught);
        verifyZeroInteractions(request, response, filterChain);
    }

    @Test
    public void shouldThrowExceptionIfModuleChainIsEmptyString() {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("");

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.init(filterConfig);
        } catch (ServletException e) {
            exceptionCaught = true;
        }

        //Then
        assertTrue(exceptionCaught);
        verifyZeroInteractions(request, response, filterChain);
    }

    @Test
    public void shouldNotRunAuthnModulesWhenProviderIsNull() throws IOException, ServletException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(null);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void shouldNotRunAuthnModulesWhenContextIsNull() throws IOException, ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(null);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void shouldNotProcessFilterChainWhenValidateRequestReturnsSendSuccess() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_SUCCESS);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain, times(0)).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldNotProcessFilterChainWhenValidateRequestReturnsSendFailure() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_FAILURE);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain, times(0)).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldNotProcessFilterChainWhenValidateRequestReturnsSendContinue() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_CONTINUE);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain, times(0)).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldNotProcessFilterChainWhenValidateRequestReturnsInvalidAuthStatusFailure() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.FAILURE);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain, times(0)).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotProcessFilterChainWhenValidateRequestThrowsAuthException() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willThrow(AuthException.class);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain, times(0)).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void
    shouldNotCallSecureResponseIfFilterChainThrowsException() throws IOException, ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        doThrow(ServletException.class).when(filterChain).doFilter(request, response);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext, times(0)).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldExitWhenSecureResponseReturnsSendSuccess() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_SUCCESS);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldExitWhenSecureResponseReturnsSendFailure() throws IOException, ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_FAILURE);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldExitWhenSecureResponseReturnsSendContinue() throws IOException, ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SEND_CONTINUE);

        //When
        authNFilter.doFilter(request, response, filterChain);

        //Then
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldExitWhenSecureResponseReturnsInvalidAuthStatusSuccess() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldExitWhenSecureResponseReturnsInvalidAuthStatusFailure() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldProvideCorrectResponsesWhenUsingHTTPRedirect() throws IOException,
            ServletException, AuthException {

        // Tests the particular set of expected responses when using HTTPRedirect callback
        // In practice this would be HTTP Basic Auth or SPNEGO/Kerberos

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldExitWhenSecureResponseThrowsAuthException() throws IOException,
            ServletException, AuthException {

        //Given
        FilterConfig filterConfig = mock(FilterConfig.class);
        given(filterConfig.getInitParameter(MODULE_CHAIN)).willReturn("MODULE_CHAIN_NAME");
        authNFilter.init(filterConfig);

        given(request.getRequestURL()).willReturn(new StringBuffer("http://openam.internal.forgerock" +
                ".org/openam/jaspi/1/test"));
        given(request.getContextPath()).willReturn("/openam");
        AuthConfigProvider authConfigProvider = mock(AuthConfigProvider.class);
        given(authConfigFactory.getConfigProvider("HttpServlet", "openam.internal.forgerock.org /openam",
                null)).willReturn(authConfigProvider);
        ServerAuthConfig serverAuthConfig = mock(ServerAuthConfig.class);
        given(authConfigProvider.getServerAuthConfig(eq("HttpServlet"), eq("openam.internal.forgerock.org /openam"),
                Matchers.<CallbackHandler>anyObject())).willReturn(serverAuthConfig);
        given(serverAuthConfig.getAuthContextID(Matchers.<MessageInfo>anyObject())).willReturn("AUTH_CONTEXT_ID");
        ServerAuthContext serverAuthContext = mock(ServerAuthContext.class);
        given(serverAuthConfig.getAuthContext(eq("AUTH_CONTEXT_ID"), Matchers.<Subject>anyObject(),
                anyMap())).willReturn(serverAuthContext);
        given(serverAuthContext.validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject())).willReturn(AuthStatus.SUCCESS);
        given(serverAuthContext.secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject())).willThrow(AuthException.class);

        //When
        boolean exceptionCaught = false;
        try {
            authNFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            assertTrue(AuthException.class.isAssignableFrom(e.getCause().getClass()));
            exceptionCaught = true;
        }

        //Then
        Assert.assertTrue(exceptionCaught);
        verify(serverAuthContext).validateRequest(Matchers.<MessageInfo>anyObject(), Matchers.<Subject>anyObject(),
                Matchers.<Subject>anyObject());
        verify(filterChain).doFilter(request, response);
        verify(serverAuthContext).secureResponse(Matchers.<MessageInfo>anyObject(),
                Matchers.<Subject>anyObject());
    }

    @Test
    public void shouldCallDestroyWithNoConsequences() {

        //Given

        //When
        authNFilter.destroy();

        //Then
        verifyZeroInteractions(request, response, filterChain, authConfigFactory);
    }
}
