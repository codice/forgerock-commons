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

import org.forgerock.guava.common.hash.Funnel;
import org.forgerock.util.Reject;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A bloom filter that can grow to arbitrary size. Geometric series are used to scale the capacity and false positive
 * probability rates as the filter grows. This ensures that the filter grows at a sustainable rate while not
 * exceeding the overall desired false positive probability. This implementation is based on the
 * {@link RollingBloomFilter} implementation with all elements set to never expire.
 *
 * @see <a href="http://gsd.di.uminho.pt/members/cbm/ps/dbloom.pdf">"Scalable Bloom Filters"</a> by Paulo Sérgio
 * Almeida, Carlos Baquero, Nuno Preguiça and David Hutchison, <em>Information Processing Letters</em> 101 (2007)
 * pp.255–261
 */
@NotThreadSafe
public final class ScalableBloomFilter<T> implements FalsePositiveSet<T>, RollingBloomFilterMXBean {

    private final RollingBloomFilter<T> rbf;

    private ScalableBloomFilter(final Builder<T> builder) {
        this.rbf = new RollingBloomFilter<T>(builder);
    }

    /**
     * Begins construction of a scalable bloom filter.
     *
     * @param funnel the funnel to use to convert elements into byte data.
     * @param <T> the type of elements to be stored in the set.
     * @return a builder instance to further configure and build the scalable bloom filter.
     */
    public static <T> Builder<T> create(final Funnel<? super T> funnel) {
        return new Builder<T>(funnel);
    }

    @Override
    public boolean mightContain(final T element) {
        return rbf.mightContain(element);
    }

    @Override
    public MightContainPredicate<T> asPredicate() {
        return rbf.asPredicate();
    }

    @Override
    public double getConfiguredFalsePositiveProbability() {
        return rbf.getConfiguredFalsePositiveProbability();
    }

    @Override
    public double getExpectedFalsePositiveProbability() {
        return rbf.getExpectedFalsePositiveProbability();
    }

    @Override
    public long getCapacity() {
        return rbf.getCapacity();
    }

    @Override
    public long getMemoryUsed() {
        return rbf.getMemoryUsed();
    }

    @Override
    public List<BucketStatistics> getBucketStatistics() {
        return rbf.getBucketStatistics();
    }

    /**
     * Adds an element to this bloom filter. Subsequent calls to {@link #mightContain(Object)} will return true for
     * this object from now on.
     *
     * @param object the object to add to the bloom filter.
     */
    @Override
    public void add(final T object) {
        rbf.addUntil(object, Long.MAX_VALUE);
    }

    public static class Builder<T> extends FalsePositiveSetBuilder<T, ScalableBloomFilter<T>> {
        Builder(final Funnel<? super T> funnel) {
            super(funnel, NeverExpiringStrategy.<T>instance());
        }

        @Override
        public ScalableBloomFilter<T> build() {
            return new ScalableBloomFilter<T>(this);
        }
    }
}
