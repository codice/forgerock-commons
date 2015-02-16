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
import org.forgerock.util.annotations.VisibleForTesting;
import org.forgerock.util.time.TimeService;

/**
 * Abstract base class for builder pattern implementations for bloom filters.
 *
 * @param <T> the type of elements to be stored in the set.
 * @param <F> the concrete type of set that will be built.
 */
abstract class FalsePositiveSetBuilder<T, F extends FalsePositiveSet<T>> {
    /**
     * The default number of items to expect to be inserted into each bucket before it expires [1000].
     */
    public static final int DEFAULT_EXPECTED_INSERTIONS = 1000;
    int expectedInsertions = DEFAULT_EXPECTED_INSERTIONS;
    /**
     * The default overall false positive probability (FPP) to maintain [0.01 = 1%].
     */
    public static final double DEFAULT_OVERALL_FALSE_POSITIVE_PROBABILITY = 0.01d;
    double overallFalsePositiveProbability = DEFAULT_OVERALL_FALSE_POSITIVE_PROBABILITY;
    /**
     * The default bucket capacity growth factor [2x].
     */
    public static final double DEFAULT_CAPACITY_GROWTH_FACTOR = 2.0d;
    double bucketCapacityGrowthFactor = DEFAULT_CAPACITY_GROWTH_FACTOR;
    /**
     * The default FPP scaling factor [0.8].
     */
    public static final double DEFAULT_FPP_SCALE_FACTOR = 0.8d;
    double bucketFalsePositiveProbabilityScaleFactor = DEFAULT_FPP_SCALE_FACTOR;
    /**
     * The default maximum number of buckets to allow in the bucket chain before rejecting further insertions [64].
     */
    public static final int DEFAULT_MAX_NUMBER_OF_BUCKETS = 64;
    int maximumNumberOfBuckets = DEFAULT_MAX_NUMBER_OF_BUCKETS;
    TimeService clock = TimeService.SYSTEM;
    final Funnel<T> funnel;

    FalsePositiveSetBuilder(final Funnel<T> funnel) {
        this.funnel = funnel;
    }

    /**
     * Sets the expected number of elements that will be inserted into the bloom filter within one expiration
     * time period. The buckets of the bloom filter will initially be sized to this amount, but will expand
     * according to the capacity growth factor, compensating for mis-estimates of several orders of magnitude if
     * necessary.
     *
     * @param expectedInsertions the expected number of distinct items inserted within a given expiration window.
     * @return the builder to continue configuration.
     */
    public FalsePositiveSetBuilder<T, F> withExpectedInsertions(int expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
        return this;
    }

    /**
     * Sets the overall desired false positive probability (FPP) to be achieved by this bloom filter. The
     * actual false positive probability should never exceed this value.
     *
     * @param fpp the desired overall false positive probability.
     * @return the builder to continue configuration.
     */
    public FalsePositiveSetBuilder<T, F> withOverallFalsePositiveProbability(double fpp) {
        this.overallFalsePositiveProbability = fpp;
        return this;
    }

    /**
     * The factor to increase the capacity of each subsequent bucket added to the bloom filter in the case where
     * the capacity of existing buckets has been exceeded before any has expired. This allows the filter to
     * accommodate orders of magnitude mis-estimates of the expected insertions by exponential capacity growth,
     * which ensures that the size of the bucket chain should only grow at worst logarithmically in the required
     * capacity. This can be set to 1 to avoid increasing the capacity for subsequent buckets, but in the worst
     * case this may result in slow downs as the number of buckets in a chain may increase more quickly.
     *
     * @param growthFactor the growth factor to apply to the capacity of subsequent buckets added the chain.
     * @return the builder to continue configuration.
     */
    public FalsePositiveSetBuilder<T, F> withBucketCapacityGrowthFactor(double growthFactor) {
        this.bucketCapacityGrowthFactor = growthFactor;
        return this;
    }

    /**
     * The factor by which to <em>decrease</em> the expected false positive probability of each subsequent bucket
     * added to the internal chain. This ensures that the overall false positive probability (which is the sum of
     * the FPP of all of the buckets) never exceeds the desired overall FPP.
     *
     * @param scaleFactor the FPP scale factor, between 0 and 1.
     * @return the builder to continue configuration.
     */
    public FalsePositiveSetBuilder<T, F> withBucketFalsePositiveProbabilityScaleFactor(double scaleFactor) {
        this.bucketFalsePositiveProbabilityScaleFactor = scaleFactor;
        return this;
    }

    /**
     * Sets a limit on the maximum number of buckets that will be created before an exception is thrown
     * indicating maximum capacity reached. Defaults to 64.
     *
     * @param maxBuckets the maximum number of buckets to create.
     * @return the builder to continue configuration.
     */
    public FalsePositiveSetBuilder<T, F> withMaximumBuckets(int maxBuckets) {
        this.maximumNumberOfBuckets = maxBuckets;
        return this;
    }

    @VisibleForTesting
    FalsePositiveSetBuilder<T, F> withClock(TimeService clock) {
        this.clock = clock;
        return this;
    }

    /**
     * Constructs the false positive set instance according to the given configuration options.
     *
     * @return the configured false positive set.
     * @throws IllegalArgumentException if the configuration is invalid.
     */
    public abstract F build();

}
