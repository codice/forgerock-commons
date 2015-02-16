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

import org.forgerock.guava.common.hash.BloomFilter;
import org.forgerock.guava.common.hash.Funnel;

/**
 * A simple fixed capacity Bloom Filter.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Bloom_filter">Bloom filter</a> entry at Wikipedia.
 */
public final class FixedBloomFilter<T> implements FalsePositiveSet<T>, BloomFilterMXBean {
    private final double configuredFpp;
    private final int capacity;

    private final BloomFilter<T> bloomFilter;

    public FixedBloomFilter(final Funnel<? super T> funnel, final int expectedInsertions, final double fpp) {
        this.configuredFpp = fpp;
        this.capacity = expectedInsertions;
        this.bloomFilter = BloomFilter.create(funnel, expectedInsertions, fpp);
    }

    public FixedBloomFilter(final Funnel<? super T> funnel, final int expectedInsertions) {
        this(funnel, expectedInsertions, FalsePositiveSetBuilder.DEFAULT_OVERALL_FALSE_POSITIVE_PROBABILITY);
    }

    @Override
    public boolean mightContain(final T element) {
        return bloomFilter.mightContain(element);
    }

    /**
     * Adds an element to this bloom filter.
     */
    public void add(final T element) {
        bloomFilter.put(element);
    }

    @Override
    public MightContainPredicate<T> asPredicate() {
        return new MightContainPredicate<T>(this);
    }

    public boolean isSaturated() {
        return bloomFilter.expectedFpp() > configuredFpp;
    }

    @Override
    public double getConfiguredFalsePositiveProbability() {
        return configuredFpp;
    }

    @Override
    public double getExpectedFalsePositiveProbability() {
        return bloomFilter.expectedFpp();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getMemoryUsed() {
        // Adapted from Guava BloomFilter#optimalNumOfBits
        return (long)((double)(-capacity) * Math.log(configuredFpp) / (Math.log(2.0D) * Math.log(2.0D) * 8.0D));
    }
}
