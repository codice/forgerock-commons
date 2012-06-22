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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2012 ForgeRock AS. All rights reserved.
 */

package org.forgerock.resource.provider.impl;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.resource.provider.Patch;
import org.forgerock.resource.provider.PatchRequest;

/**
 * Provides access to the request details for a patch method
 *
 * @author aegloff
 */
public class PatchRequestImpl extends RequestImpl implements PatchRequest {

    /**
     * {@inheritDoc}
     */
    public String getRevision() {
        return super.getRevision();
    }
    
    /**
     * {@inheritDoc}
     */
    public Patch getPatch() {
        return null;//new PatchImpl(super.getValue());
    }

}