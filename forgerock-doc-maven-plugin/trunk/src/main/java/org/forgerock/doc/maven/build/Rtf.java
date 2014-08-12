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

package org.forgerock.doc.maven.build;

import org.forgerock.doc.maven.AbstractDocbkxMojo;

/**
 * Build RTF output.
 */
public final class Rtf extends Fo {

    /**
     * Constructor setting the Mojo that holds the configuration,
     * and setting the format to "rtf".
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Rtf(final AbstractDocbkxMojo mojo) {
        super(mojo);
        super.setFormat("rtf");
    }
}
