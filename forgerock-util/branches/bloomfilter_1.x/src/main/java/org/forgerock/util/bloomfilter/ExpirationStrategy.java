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
 * Copyright 2015 ForgeRock AS.
 */

package org.forgerock.util.bloomfilter;

/**
 * Strategy for determining the expiration time for elements of a {@link RollingBloomFilter}.
 */
public interface ExpirationStrategy<T> {
    /**
     * Determines the expiration time for the given element.
     *
     * @param element the element.
     * @return the expiration time of the element as milliseconds since midnight, 1st Jan 1970 UTC.
     */
    public long expiryTime(T element);
}
