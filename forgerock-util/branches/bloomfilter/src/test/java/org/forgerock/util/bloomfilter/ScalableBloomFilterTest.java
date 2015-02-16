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

import static org.testng.Assert.*;

import org.forgerock.guava.common.hash.Funnels;
import org.forgerock.util.encode.Base64;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class ScalableBloomFilterTest {
    private static final Random RANDOM = new Random();
    private static final int CAPACITY = 8;
    private static final double CAPACITY_GROWTH_FACTOR = 3.0d;
    private static final double OVERALL_FPP = 0.01d; // 1%
    private static final double FPP_SCALE_FACTOR = 0.5d;
    private static final int MAX_BUCKETS = 5;

    private ScalableBloomFilter<CharSequence> testFilter;

    @BeforeMethod
    public void setup() {
        testFilter = ScalableBloomFilter.create(Funnels.unencodedCharsFunnel())
                .withExpectedInsertions(CAPACITY)
                .withBucketCapacityGrowthFactor(CAPACITY_GROWTH_FACTOR)
                .withOverallFalsePositiveProbability(OVERALL_FPP)
                .withBucketFalsePositiveProbabilityScaleFactor(FPP_SCALE_FACTOR)
                .withMaximumBuckets(MAX_BUCKETS)
                .build();
    }

    @Test(dataProvider = "randomStrings")
    public void shouldNotIncludeAnyElementsInitially(String testData) {
        assertFalse(testFilter.mightContain(testData));
    }

    @Test(dataProvider = "randomStrings")
    public void shouldIncludeAllItemsExplicitlyAdded(String testData) {
        // Given
        testFilter.add(testData);

        // When
        boolean result = testFilter.mightContain(testData);

        // Then
        assertTrue(result);
    }

    @Test
    public void shouldGrowAccordingToGrowthFactor() {
        // Given
        testFilter.add("one"); // First bucket full
        List<BucketStatistics> stats = testFilter.getBucketStatistics();
        assertEquals(stats.size(), 1);
        assertEquals(stats.get(0).getCapacity(), CAPACITY);

        // When
        // NB: need to add a few more than capacity to ensure we actually saturate the bitset
        for (int i = 0; i < CAPACITY * 2 - 1; ++i) {
            testFilter.add("element" + i);
        }

        // Then
        stats = testFilter.getBucketStatistics();
        assertEquals(stats.size(), 2);
        assertEquals(stats.get(1).getCapacity(), (int) (CAPACITY * CAPACITY_GROWTH_FACTOR));
    }

    @Test
    public void shouldScaleFalsePositiveProbability() {
        // Given
        double p0 = OVERALL_FPP * (1.0d - FPP_SCALE_FACTOR);
        testFilter.add("one"); // First bucket full
        List<BucketStatistics> stats = testFilter.getBucketStatistics();
        assertEquals(stats.size(), 1);
        assertEquals(stats.get(0).getConfiguredFpp(), p0);

        // When
        // NB: need to add a few more than capacity to ensure we actually saturate the bitset
        for (int i = 0; i < CAPACITY * 2 - 1; ++i) {
            testFilter.add("element" + i);
        }

        // Then
        stats = testFilter.getBucketStatistics();
        assertEquals(stats.size(), 2);
        assertEquals(stats.get(1).getConfiguredFpp(), p0 * FPP_SCALE_FACTOR);
    }

    @Test(expectedExceptions = CapacityExceededException.class)
    public void shouldNotExceedMaxBuckets() {
        for (int i = 0; i < 100; ++i) {
            for (String[] randomStr : randomStrings()) {
                testFilter.add(randomStr[0]);
                assertFalse(testFilter.getBucketStatistics().size() > MAX_BUCKETS);
            }
        }
    }

    /**
     * Provides 100 random strings.
     */
    @DataProvider
    public String[][] randomStrings() {
        final String[][] strings = new String[100][1];
        for (int i = 0; i < 100; ++i) {
            int size = RANDOM.nextInt(256);
            byte[] bytes = new byte[size];
            RANDOM.nextBytes(bytes);
            strings[i][0] = Base64.encode(bytes); // TODO: b64 is not entirely random...
        }
        return strings;
    }
}