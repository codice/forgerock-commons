/*
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
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2012-2013 ForgeRock AS.
 */

package org.forgerock.json.resource;

import org.forgerock.json.fluent.JsonPointer;
import org.forgerock.json.fluent.JsonValue;

import java.util.List;

/**
 * An implementation specific batch request.
 */
public interface BatchRequest extends Request {
    /**
     * Flag indicating whether the batch should be aborted upon the first error.
     */
    public static final String FIELD_FAILONERROR = "failOnError";

    /**
     * Flag indicating whether the batch should be processed synchronously.
     */
    public static final String FIELD_WAITFORCOMPLETION = "waitForCompletion";

    /**
     * Field containing the list of batched operations.
     */
    public static final String FIELD_CONTENT = "content";

    /**
     * {@inheritDoc}
     */
    <R, P> R accept(RequestVisitor<R, P> v, P p);

    /**
     * {@inheritDoc}
     */
    @Override
    BatchRequest addField(JsonPointer... fields);

    /**
     * {@inheritDoc}
     */
    @Override
    BatchRequest addField(String... fields);

    /**
     * Returns the content of this action request. The structure of the content
     * is defined by the action.
     *
     * @return The content of this action request.
     */
    JsonValue getContent();

    /**
     * {@inheritDoc}
     */
    @Override
    List<JsonPointer> getFields();

    /**
     * {@inheritDoc}
     */
    @Override
    RequestType getRequestType();

    /**
     * {@inheritDoc}
     */
    @Override
    String getResourceName();

    /**
     * {@inheritDoc}
     */
    @Override
    ResourceName getResourceNameObject();

    /**
     * Sets the content of this batch request.
     *
     * @param content
     *            The content of this batch request.
     * @return This batch request.
     */
    BatchRequest setContent(JsonValue content);

    /**
     * {@inheritDoc}
     */
    @Override
    Request setResourceName(ResourceName name);

    /**
     * {@inheritDoc}
     */
    @Override
    BatchRequest setResourceName(String name);

    /**
     * {@inheritDoc}
     */
    @Override
    BatchRequest setAdditionalParameter(String name, String value) throws BadRequestException;
}
