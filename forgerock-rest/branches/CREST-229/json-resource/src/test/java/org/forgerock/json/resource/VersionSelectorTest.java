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
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.json.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("javadoc")
public class VersionSelectorTest {

    private VersionSelector versionSelector;

    private Object candidateOneDotZero;
    private Object candidateOneDotFive;
    private Object candidateOneDotNine;
    private Object candidateTwoDotFive;

    private Map<Version, Object> candidates = new HashMap<Version, Object>();

    @BeforeClass
    public void setUp() {
        Version oneDotZero = Version.valueOf(1, 0);
        Version oneDotOne = Version.valueOf(1, 1);
        Version oneDotFive = Version.valueOf(1, 5);
        Version oneDotNine = Version.valueOf(1, 9);
        Version twoDotOne = Version.valueOf(2, 1);
        Version twoDotFive = Version.valueOf(2, 5);

        candidateOneDotZero = mock(Object.class);
        Object candidateOneDotOne = mock(Object.class);
        candidateOneDotFive = mock(Object.class);
        candidateOneDotNine = mock(Object.class);
        Object candidateTwoDotOne = mock(Object.class);
        candidateTwoDotFive = mock(Object.class);

        candidates.put(oneDotZero, candidateOneDotZero);
        candidates.put(oneDotOne, candidateOneDotOne);
        candidates.put(oneDotFive, candidateOneDotFive);
        candidates.put(oneDotNine, candidateOneDotNine);
        candidates.put(twoDotOne, candidateTwoDotOne);
        candidates.put(twoDotFive, candidateTwoDotFive);
    }

    @BeforeMethod
    public void setUpMethod() {
        versionSelector = new VersionSelector();
    }

    @Test (expectedExceptions = InternalServerErrorException.class)
    public void selectShouldThrowInternalServerErrorExceptionWhenCandidatesNull() throws Exception {

        //Given
        Version requested = Version.valueOf(1, 0);

        //When
        versionSelector.select(requested, null);

        //Then
        //Expected InternalServerErrorException
    }

    @Test (expectedExceptions = InternalServerErrorException.class)
    public void selectShouldThrowInternalServerErrorExceptionWhenCandidatesEmpty() throws Exception {

        //Given
        Version requested = Version.valueOf(1, 0);
        Map<Version, Object> candidates = Collections.emptyMap();

        //When
        versionSelector.select(requested, candidates);

        //Then
        //Expected InternalServerErrorException
    }

    @Test (expectedExceptions = NotFoundException.class)
    public void selectShouldThrowNotFoundExceptionWhenVersionNotMatched() throws Exception {

        //Given
        Version requested = Version.valueOf(3, 5);
        versionSelector.noDefault();

        //When
        versionSelector.select(requested, candidates);

        //Then
        //Expected NotFoundException
    }

    @Test (expectedExceptions = BadRequestException.class)
    public void selectShouldThrowBadRequestExceptionWhenNoVersion() throws Exception {

        //Given
        versionSelector.noDefault();

        //When
        versionSelector.select(null, candidates);

        //Then
        //Expected BadRequestException
     }

    @Test
    public void selectShouldReturnLatestWhenRequestVersionIsNullByDefault() throws Exception {

        //Given

        //When
        Object selected = versionSelector.select(null, candidates);

        //Then
        assertThat(selected).isEqualTo(candidateTwoDotFive);
    }

    @Test
    public void selectShouldReturnOldestWhenRequestVersionIsNullWhenOldestBehaviourSet() throws Exception {

        //Given
        versionSelector.defaultToOldest();

        //When
        Object selected = versionSelector.select(null, candidates);

        //Then
        assertThat(selected).isEqualTo(candidateOneDotZero);
    }

    @Test
    public void selectShouldReturnObjectWhenMatchFound() throws ResourceException {

        //Given
        Version requested = Version.valueOf(1, 2);

        //When
        Object selected = versionSelector.select(requested, candidates);

        //Then
        assertThat(selected).isEqualTo(candidateOneDotNine);
    }

    @Test (expectedExceptions = ResourceException.class)
    public void selectShouldThrowVersionSelectionExceptionWhenVersionNotMatched() throws ResourceException {

        //Given
        Version requested = Version.valueOf(2, 6);

        //When
        Object selected = versionSelector.select(requested, candidates);

        //Then
        assertThat(selected).isEqualTo(candidateOneDotFive);
    }
}
