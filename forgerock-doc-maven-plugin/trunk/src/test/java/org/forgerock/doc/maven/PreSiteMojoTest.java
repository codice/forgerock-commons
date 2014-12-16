/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 *     Copyright 2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import static org.assertj.core.api.Assertions.*;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

@SuppressWarnings("javadoc")
public class PreSiteMojoTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test() throws Exception {
        File pom = getTestFile("src/test/resources/unit/pom.xml");
        assertThat(pom).isNotNull();

        PreSiteMojo preSiteMojo = (PreSiteMojo) lookupMojo("build", pom);
        assertThat(preSiteMojo).isNotNull();

        // FixMe preSiteMojo.execute();
    }
}
