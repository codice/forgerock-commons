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

import static org.mockito.BDDMockito.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.forgerock.guava.common.hash.Funnels;
import org.forgerock.util.time.TimeService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RollingBloomFilterTest {

    @Mock
    private TimeService clock;

    private RollingBloomFilter<CharSequence> testFilter;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testFilter = RollingBloomFilter.create(Funnels.unencodedCharsFunnel())
                .withExpectedInsertions(1)
                .withOverallFalsePositiveProbability(0.01d)
                .withBucketCapacityGrowthFactor(2.0d)
                .withBucketFalsePositiveProbabilityScaleFactor(0.5d)
                .withClock(clock)
                .build();
    }

    @Test
    public void shouldDestroyBucketsThatHaveExpired() {
        // Given
        given(clock.now()).willReturn(1L);
        for (int i = 0; i < 10; ++i) {
            testFilter.addUntil("element" + i, 1L);
        }
        assertTrue(testFilter.getBucketStatistics().size() > 1);

        // When
        given(clock.now()).willReturn(2L);
        testFilter.mightContain("test"); // Trigger cleanup

        // Then
        assertEquals(testFilter.getBucketStatistics().size(), 1);
    }

}