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

import static org.forgerock.util.bloomfilter.FalsePositiveSetBuilder.*;
import static org.testng.Assert.*;

import org.forgerock.guava.common.hash.Funnel;
import org.forgerock.guava.common.hash.Funnels;
import org.forgerock.util.time.TimeService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FalsePositiveSetBuilderTest {
    private static final Funnel<CharSequence> FUNNEL = Funnels.unencodedCharsFunnel();

    private FalsePositiveSetBuilder<CharSequence, ScalableBloomFilter<CharSequence>> testBuilder;

    @BeforeMethod
    public void createTestBuilder() {
        testBuilder = new TestBuilder(FUNNEL);
    }


    @Test
    public void shouldUseCorrectDefaults() {
        assertEquals(testBuilder.expectedInsertions, DEFAULT_EXPECTED_INSERTIONS);
        assertEquals(testBuilder.overallFalsePositiveProbability, DEFAULT_OVERALL_FALSE_POSITIVE_PROBABILITY);
        assertEquals(testBuilder.bucketCapacityGrowthFactor, DEFAULT_CAPACITY_GROWTH_FACTOR);
        assertEquals(testBuilder.bucketFalsePositiveProbabilityScaleFactor, DEFAULT_FPP_SCALE_FACTOR);
        assertEquals(testBuilder.maximumNumberOfBuckets, DEFAULT_MAX_NUMBER_OF_BUCKETS);
        assertEquals(testBuilder.clock, TimeService.SYSTEM);
    }

    @Test
    public void shouldUseSpecifiedFunnel() {
        assertEquals(testBuilder.funnel, FUNNEL);
    }

    @Test
    public void shouldUseSpecifiedExpectedInsertions() {
        // Given
        int expectedInsertions = 314159;

        // When
        testBuilder = testBuilder.withExpectedInsertions(expectedInsertions);

        // Then
        assertEquals(testBuilder.expectedInsertions, expectedInsertions);
    }

    @Test
    public void shouldUseSpecifiedOverallFalsePositiveProbability() {
        // Given
        double fpp = 3.14159d;

        // When
        testBuilder = testBuilder.withOverallFalsePositiveProbability(fpp);

        // Then
        assertEquals(testBuilder.overallFalsePositiveProbability, fpp);
    }

    @Test
    public void shouldUseSpecifiedBucketCapacityGrowthFactor() {
        // Given
        double growthFactor = 3.14159d;

        // When
        testBuilder = testBuilder.withBucketCapacityGrowthFactor(growthFactor);

        // Then
        assertEquals(testBuilder.bucketCapacityGrowthFactor, growthFactor);
    }

    @Test
    public void shouldUseSpecifiedBucketFPPScaleFactor() {
        // Given
        double scaleFactor = 3.14159d;

        // When
        testBuilder = testBuilder.withBucketFalsePositiveProbabilityScaleFactor(scaleFactor);

        // Then
        assertEquals(testBuilder.bucketFalsePositiveProbabilityScaleFactor, scaleFactor);
    }

    @Test
    public void shouldUseSpecifiedMaxNumberOfBuckets() {
        // Given
        int max = 3141;

        // When
        testBuilder = testBuilder.withMaximumBuckets(max);

        // Then
        assertEquals(testBuilder.maximumNumberOfBuckets, max);
    }

    private static class TestBuilder extends FalsePositiveSetBuilder<CharSequence, ScalableBloomFilter<CharSequence>> {
        public TestBuilder(final Funnel<CharSequence> funnel) {
            super(funnel);
        }

        @Override
        public ScalableBloomFilter<CharSequence> build() {
            return null;
        }
    }

}