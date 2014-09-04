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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * This is extended verification option that implementers can provide. It is to be used during a handshake if the URL's
 * hostname does not match the peer's identification hostname.
 */
public class SSLHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(final String hostname, final SSLSession session) {
        // Verifies that the specified hostname is allowed within the specified SSL session.
        return true;
    }
}
