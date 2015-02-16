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

import javax.management.MXBean;

/**
 * Defines properties all bloom filters that can be monitored via JMX or other facilities.
 */
@MXBean
public interface BloomFilterMXBean {
    /**
     * The overall probability of false positives that has been configured for this bloom filter.
     */
    double getConfiguredFalsePositiveProbability();

    /**
     * The expected probability of false positives given the current state of the bloom filter. This will increase as
     * the number of 1 bits in the bloom filter increases. For chained bloom filters, this is the sum of the FPP for
     * each bucket in the chain.
     */
    double getExpectedFalsePositiveProbability();

    /**
     * The total number of distinct items that can be inserted into this bloom filter before it becomes saturated.
     */
    long getCapacity();

    /**
     * The total amount of memory being used by this bloom filter, in bytes. This is just the size of the bit arrays
     * used in the core bloom filter implementation, and not any additional constant sized overheads for object
     * wrappers or other details. As such, this should be used only as a rough guide to the amount of memory in use.
     */
    long getMemoryUsed();
}
