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
 *       Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import android.text.TextUtils;

/**
 * The paged result cookie class is utility to maintain pagination on main 'search activity'.
 */
final class PagedResultCookie {

    static String cookie = "";

    static void setCookie(final String prCookie) {
        if (prCookie == null || prCookie.equals("null")) {
            cookie = "";
        } else {
            cookie = prCookie;
        }
    }

    static String getPagedResultCookie() {
        return !TextUtils.isEmpty(cookie) ? "&_pagedResultsCookie=" + cookie : "";
    }

    static void initialize() {
        cookie = "";
    }

    // Prevent instantiation.
    private PagedResultCookie() {
        throw new AssertionError();
    }
}
