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

/**
 * <a href="http://en.wikipedia.org/wiki/Bloom_filter">Bloom filter</a>-based implementations of sets that can store
 * very large numbers of elements efficiently, but which allow the possibility of false positives when testing if a
 * member is part of the set. Useful as a first level cache for large distributed blacklists and other cases where
 * there may be too many elements to represent directly in memory.
 * <p/>
 * In addition to a standard bloom filter implementation, this package also provides a <em>scalable</em> bloom
 * filter, which can expand to any number of elements, and a <em>rolling</em> bloom filter that allows elements to
 * expire from the filter over time, freeing up memory.
 */
package org.forgerock.util.bloomfilter;