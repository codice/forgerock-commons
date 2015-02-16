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
import org.forgerock.util.time.TimeService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * A variation on the {@link ScalableBloomFilter} implementation, in which buckets of entries in the filter can
 * expire and be removed over time, allowing space to be reclaimed. Despite this, the implementation is free from false
 * negatives, and is guaranteed to never exceed the overall false positive probability. Objects of this class may
 * temporarily use more space than optimal, while remaining within acceptable bounds.
 *
 * @param <T> The type of elements to be stored in the bloom filter.
 * @see ScalableBloomFilter
 */
public final class RollingBloomFilter<T> implements FalsePositiveSet<T>, RollingBloomFilterMXBean {

    /**
     * The chain of bloom filter buckets.
     */
    private final Deque<Bucket> chain = new ArrayDeque<Bucket>();

    private final int expectedInsertions;
    private final double bucketCapacityGrowthFactor;
    private final double bucketFalsePositiveProbabilityScaleFactor;
    private final TimeService clock;
    private final int maxNumberOfBuckets;
    private final BitSet nextBucketNumber;
    private final Funnel<? super T> funnel;

    /**
     * False positive probability of the first bucket in the chain.
     */
    private final double P0;

    RollingBloomFilter(final FalsePositiveSetBuilder<T, ?> builder) {
        Reject.ifFalse(builder.expectedInsertions >= 1, "expectedInsertions must be >= 1");
        Reject.ifFalse(builder.overallFalsePositiveProbability > 0.0 && builder.overallFalsePositiveProbability < 1.0,
                "overallFalsePositiveProbability must be in range (0, 1)");
        Reject.ifFalse(builder.bucketCapacityGrowthFactor > 0.0, "bucketCapacityGrowthFactor must be > 0");
        Reject.ifFalse(builder.bucketFalsePositiveProbabilityScaleFactor > 0.0 &&
                        builder.bucketFalsePositiveProbabilityScaleFactor < 1.0,
                "bucketFalsePositiveProbabilityScaleFactor should be in range (0, 1)");
        Reject.ifFalse(builder.maximumNumberOfBuckets >= 1, "maximumNumberOfBuckets must be >= 1");
        Reject.ifNull(builder.clock);
        Reject.ifNull(builder.funnel);

        this.expectedInsertions = builder.expectedInsertions;
        this.bucketCapacityGrowthFactor = builder.bucketCapacityGrowthFactor;
        this.bucketFalsePositiveProbabilityScaleFactor = builder.bucketFalsePositiveProbabilityScaleFactor;
        this.maxNumberOfBuckets = builder.maximumNumberOfBuckets;
        this.nextBucketNumber = new BitSet(builder.maximumNumberOfBuckets);
        this.clock = builder.clock;
        this.funnel = builder.funnel;

        this.P0 = builder.overallFalsePositiveProbability * (1.0d - bucketFalsePositiveProbabilityScaleFactor);
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

    /**
     * Adds an element to this bloom filter until at least the given expiry time. No guarantees are made about how
     * long after the expiry time the element will still be considered to be in the set.
     *
     * @param object the object to add to this bloom filter.
     * @param expiryTime the time after which the element may be removed from the set, as milliseconds since midnight
     *                   1st Jan 1970, UTC.
     */
    public void addUntil(T object, long expiryTime) {
        Reject.ifNull(object);
        Bucket bucket = chain.peekLast();
        if (bucket == null || bucket.isSaturated()) {
            bucket = createNewBucket();
            chain.addLast(bucket);
        }
        bucket.add(object, expiryTime);
    }

    private Bucket createNewBucket() {
        final int n = nextBucketNumber.nextClearBit(0);
        if (n >= maxNumberOfBuckets) {
            throw new CapacityExceededException("Maximum number of buckets exceeded in bloom filter: " + n);
        }
        nextBucketNumber.set(n);
        return new Bucket(n);
    }

    @Override
    public boolean mightContain(T object) {
        for (Iterator<Bucket> it = chain.iterator(); it.hasNext();) {
            final Bucket bucket = it.next();
            // Remove any buckets that have both expired and become saturated.
            if (bucket.isExpired() && bucket.isSaturated()) {
                it.remove();
                nextBucketNumber.clear(bucket.n);
            } else if (bucket.mightContain(object)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MightContainPredicate<T> asPredicate() {
        return new MightContainPredicate<T>(this);
    }

    @Override
    public double getConfiguredFalsePositiveProbability() {
        return P0 / (1.0d - bucketFalsePositiveProbabilityScaleFactor);
    }

    @Override
    public double getExpectedFalsePositiveProbability() {
        double fpp = 0.0d;
        for (BucketStatistics stats : getBucketStatistics()) {
            fpp += stats.getExpectedFpp();
        }
        return fpp;
    }

    @Override
    public long getCapacity() {
        long capacity = 0;
        for (BucketStatistics stats : getBucketStatistics()) {
            capacity += stats.getCapacity();
        }
        return capacity;
    }

    @Override
    public long getMemoryUsed() {
        long size = 0;
        for (Bucket bucket : chain) {
            size += bucket.getMemoryUsed();
        }
        return size;
    }

    @Override
    public List<BucketStatistics> getBucketStatistics() {
        final List<BucketStatistics> stats = new ArrayList<BucketStatistics>();
        for (Bucket bucket : chain) {
            stats.add(bucket.stats());
        }
        return stats;
    }

    public static class Builder<T> extends FalsePositiveSetBuilder<T, RollingBloomFilter<T>> {
        Builder(final Funnel<? super T> funnel) {
            super(funnel);
        }

        @Override
        public RollingBloomFilter<T> build() {
            return new RollingBloomFilter<T>(this);
        }
    }

    /**
     * A bucket in the bloom filter chain. Contains a bloom filter together with a timestamp of the last expiry time
     * of any element within the bucket. Assumes that it will be used by a single thread at a time and that the
     * caller is responsible for ensuring visibility of any changes to this object to other threads.
     */
    private final class Bucket {
        private final int n;
        private final FixedBloomFilter<T> bloomFilter;
        private long lastExpiryTime = Long.MIN_VALUE;

        Bucket(final int n) {
            this.n = n;
            double fpp = P0 * Math.pow(bucketFalsePositiveProbabilityScaleFactor, n);
            int capacity = (int) (expectedInsertions * Math.pow(bucketCapacityGrowthFactor, n));
            this.bloomFilter = new FixedBloomFilter<T>(funnel, capacity, fpp);
        }

        void add(T object, long expiryTime) {
            bloomFilter.add(object);
            this.lastExpiryTime = Math.max(lastExpiryTime, expiryTime);
        }

        boolean mightContain(T object) {
            return bloomFilter.mightContain(object);
        }

        boolean isExpired() {
            return clock.now() > lastExpiryTime;
        }

        boolean isSaturated() {
            return bloomFilter.isSaturated();
        }

        BucketStatistics stats() {
            return new BucketStatistics(bloomFilter.getConfiguredFalsePositiveProbability(),
                    bloomFilter.getExpectedFalsePositiveProbability(),
                    bloomFilter.getCapacity(), lastExpiryTime);
        }

        long getMemoryUsed() {
            return bloomFilter.getMemoryUsed();
        }
    }
}
