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

/**
 * Interface for collections that provide {@link java.util.Set}-like semantics, but allow false positives from the
 * "contains" method. In other words, if the contains method returns {@code false}, then the element is definitely
 * not contained in the set, however if it returns {@code true} then the element may be in the set or may not be in
 * the set with some (small) probability of false positives.
 */
public interface FalsePositiveSet<T> {
    /**
     * Indicates whether the given individual <em>might</em> be contained in this set. If the answer is {@code false}
     * then the element is <em>definitely not</em> in the set, however if the answer is {@code true} then the element
     * may be in the set, but it also might not be (with some small probability of false positives). In this case, a
     * more expensive check can be made to obtain a more definitive answer.
     *
     * @param element the element to check for containment in the set.
     * @return {@code false} if the element is definitely not in the set, otherwise {@code true}.
     */
    boolean mightContain(T element);

    /**
     * A predicate that tests for membership in this set. As for the set as a whole, false positives are possible.
     *
     * @return a predicate that tests for membership in this set.
     */
    MightContainPredicate<T> asPredicate();
}
