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

import org.forgerock.util.Reject;

import java.util.List;

/**
 * Wrapper around a {@link FalsePositiveSet} that provides synchronization of all methods for thread-safety. Also
 * implements all JMX monitoring interfaces to ensure thread-safety in the presence of concurrent JMX monitoring.
 */
public final class SynchronizedFalsePositiveSet<T> implements FalsePositiveSet<T>, RollingBloomFilterMXBean {
    private final FalsePositiveSet<T> delegate;

    public SynchronizedFalsePositiveSet(final FalsePositiveSet<T> delegate) {
        Reject.ifNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public boolean mightContain(final T element) {
        synchronized (delegate) {
            return delegate.mightContain(element);
        }
    }

    @Override
    public MightContainPredicate<T> asPredicate() {
        return new MightContainPredicate<T>(this);
    }

    @Override
    public void add(final T element) {
        synchronized (delegate) {
            delegate.add(element);
        }
    }

    @Override
    public List<BucketStatistics> getBucketStatistics() {
        if (delegate instanceof RollingBloomFilterMXBean) {
            synchronized (delegate) {
                return ((RollingBloomFilterMXBean) delegate).getBucketStatistics();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public double getConfiguredFalsePositiveProbability() {
        if (delegate instanceof BloomFilterMXBean) {
            synchronized (delegate) {
                return ((BloomFilterMXBean) delegate).getConfiguredFalsePositiveProbability();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public double getExpectedFalsePositiveProbability() {
        if (delegate instanceof BloomFilterMXBean) {
            synchronized (delegate) {
                return ((BloomFilterMXBean) delegate).getExpectedFalsePositiveProbability();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCapacity() {
        if (delegate instanceof BloomFilterMXBean) {
            synchronized (delegate) {
                return ((BloomFilterMXBean) delegate).getCapacity();
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public long getMemoryUsed() {
        if (delegate instanceof BloomFilterMXBean) {
            synchronized (delegate) {
                return ((BloomFilterMXBean) delegate).getMemoryUsed();
            }
        }
        throw new UnsupportedOperationException();
    }
}
