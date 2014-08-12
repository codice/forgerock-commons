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

package org.forgerock.doc.maven.post;

import org.forgerock.doc.maven.AbstractDocbkxMojo;

/**
 * No-op processor for formats that have no post-processing at present.
 */
public class NoOp extends AbstractDocbkxMojo {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public NoOp(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Does no post-processing.
     */
    public void execute() {
        // Nothing to do.
    }
}
