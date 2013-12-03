/*
 * Copyright 2013 ForgeRock AS.
 *
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
 */

package org.forgerock.openam.mobile.auth;

import org.forgerock.openam.mobile.commons.ActionType;

/**
 * Authentication-specific actions
 */
public enum AuthNAction implements ActionType {

    /**
     * authenticate the user, continue & failure
     */
    AUTH, AUTH_FAIL, AUTH_CONT,

    /**
     * validate the auth token & failure
     */
    VALIDATE, VALIDATE_FAIL,

    /**
     * logout & failure
     */
    LOGOUT, LOGOUT_FAIL,

    /**
     * logging in via web ui & failure
     */
    WEB_AUTH, WEB_AUTH_FAIL,

    /**
     * information about cookie & failure
     */
    GET_COOKIE_NAME, GET_COOKIE_NAME_FAIL,

    /**
     * information about domain & failure
     */
    GET_COOKIE_DOMAINS, GET_COOKIE_DOMAINS_FAIL;
}
