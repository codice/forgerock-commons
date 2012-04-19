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
import org.forgerock.resource.provider.FieldFilter;
import org.forgerock.resource.provider.Request;

/**
 * Provides access to request details.
 * 
 * Scope visibility of some implementations is limited to leave it to
 * sub-classes to provide more specific contracts for a given request method.
 * 
 * Some data elements are not relevant (present) for a given request method, and 
 * sub-classes choose to provide public access to the relevant elements.
 *
 * @author aegloff
 */
public class RequestImpl implements Request {

    private JsonValue request;
    private FieldFilter fieldFilter;
    
    /**
     * Set the raw, internal request detail
     * @param request the full request detail
     */
    public void setRequest(JsonValue request) {
        this.request = request;
    }

    /**
     * {@inheritDoc} 
     */
    public String getId() {
        return request.get("id").required().asString();
    }
    
    /**
     * {@inheritDoc}
     */
    public FieldFilter getFieldFilter() {
        return fieldFilter;
    }
    
    /**
     * @param fieldFilter to set. Not exposed on the interface as this
     * is intended to internal population.
     */
    public void setFieldFilter(FieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

    /**
     * @return the value body of the request
     */
    protected JsonValue getValue() {
        return request.get("value");
    }

}

