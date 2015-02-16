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

import java.beans.ConstructorProperties;

/**
 * Encapsulates statistics about a bucket in a {@link RollingBloomFilter} for monitoring purposes.
 */
public final class BucketStatistics {
    private final double configuredFpp;
    private final double expectedFpp;
    private final long capacity;
    private final long latestExpiryTime;

    @ConstructorProperties({"configuredFpp", "expectedFpp", "capacity", "latestExpiryTime"})
    public BucketStatistics(final double configuredFpp,
                            final double expectedFpp,
                            final long capacity,
                            final long latestExpiryTime) {
        this.configuredFpp = configuredFpp;
        this.expectedFpp = expectedFpp;
        this.capacity = capacity;
        this.latestExpiryTime = latestExpiryTime;
    }

    /**
     * The configured false positive probability (FPP) of this bucket.
     */
    public double getConfiguredFpp() {
        return configuredFpp;
    }

    /**
     * The expected actual false positive probability (FPP) of this bucket at this time.
     */
    public double getExpectedFpp() {
        return expectedFpp;
    }

    /**
     * The configured capacity (expected number of insertions) of this bucket.
     */
    public long getCapacity() {
        return capacity;
    }

    /**
     * The expiry time of the latest expiring element contained in this bucket.
     */
    public long getLatestExpiryTime() {
        return latestExpiryTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BucketStatistics that = (BucketStatistics) o;

        return capacity == that.capacity
                && Double.compare(that.configuredFpp, configuredFpp) == 0
                && Double.compare(that.expectedFpp, expectedFpp) == 0
                && latestExpiryTime == that.latestExpiryTime;

    }

    @Override
    public int hashCode() {
        int result;
        result = Double.valueOf(configuredFpp).hashCode();
        result = 31 * result + Double.valueOf(expectedFpp).hashCode();
        result = 31 * result + Long.valueOf(capacity).hashCode();
        result = 31 * result + Long.valueOf(latestExpiryTime).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BucketStatistics{" +
                "configuredFpp=" + configuredFpp +
                ", expectedFpp=" + expectedFpp +
                ", capacity=" + capacity +
                ", latestExpiryTime=" + latestExpiryTime +
                '}';
    }
}
