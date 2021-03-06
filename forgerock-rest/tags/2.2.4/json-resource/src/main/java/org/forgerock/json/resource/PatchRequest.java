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

import java.util.List;

import org.forgerock.json.fluent.JsonPointer;
import org.forgerock.json.fluent.JsonValue;

/**
 * A request to update a JSON resource by applying a set of changes to its
 * existing content. See the documentation for {@link PatchOperation} for more
 * details regarding the various types of patch operation, and their semantics.
 */
public interface PatchRequest extends Request {

    /**
     * The name of the field which contains the patch content in the JSON
     * representation.
     */
    public static final String FIELD_PATCH = "patch";

    /**
     * The name of the field which contains the patch operations in the JSON
     * representation.
     */
    public static final String FIELD_PATCH_OPERATIONS = "patchOperations";

    /**
     * The name of the field which contains the resource version in the JSON
     * representation.
     */
    public static final String FIELD_REVISION = "revision";

    /**
     * {@inheritDoc}
     */
    <R, P> R accept(RequestVisitor<R, P> v, P p);

    /**
     * {@inheritDoc}
     */
    @Override
    PatchRequest addField(JsonPointer... fields);

    /**
     * {@inheritDoc}
     */
    @Override
    PatchRequest addField(String... fields);

    /**
     * Adds one or more patch operations which should be performed against the
     * targeted resource.
     *
     * @param operations
     *            One or more patch operations which should be performed against
     *            the targeted resource.
     * @return This patch request.
     * @throws UnsupportedOperationException
     *             If this patch request does not permit changes to the patch
     *             operations.
     */
    PatchRequest addPatchOperation(PatchOperation... operations);

    /**
     * Adds a single patch operation which should be performed against the
     * targeted resource.
     *
     * @param operation
     *            The type of patch operation to be performed.
     * @param field
     *            The field targeted by the patch operation.
     * @param value
     *            The possibly {@code null} value for the patch operation.
     * @return This patch request.
     * @throws UnsupportedOperationException
     *             If this patch request does not permit changes to the patch
     *             operations.
     */
    PatchRequest addPatchOperation(String operation, String field, JsonValue value);

    /**
     * {@inheritDoc}
     */
    @Override
    List<JsonPointer> getFields();

    /**
     * Returns the list of patch operations which should be performed against
     * the targeted resource.
     *
     * @return The list of patch operations which should be performed against
     *         the targeted resource (never null).
     */
    List<PatchOperation> getPatchOperations();

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
     * Returns the expected version information associated with the JSON
     * resource to be patched. Version information can be used in order to
     * implement multi-version concurrency control (MVCC).
     * <p>
     * The returned version information may be {@code null} indicating that the
     * client does not care if the resource has been modified concurrently. If
     * the version information is non-{@code null}, and it does not match the
     * current version of the targeted JSON resource, then the patch request
     * will be rejected by the provider.
     *
     * @return The expected version information associated with the JSON
     *         resource to be patched.
     */
    String getRevision();

    /**
     * {@inheritDoc}
     */
    @Override
    Request setResourceName(ResourceName name);

    /**
     * {@inheritDoc}
     */
    @Override
    PatchRequest setResourceName(String name);

    /**
     * Sets the expected version information associated with the JSON resource
     * to be patched. Version information can be used in order to implement
     * multi-version concurrency control (MVCC).
     * <p>
     * The provided version information may be {@code null} indicating that the
     * client does not care if the resource has been modified concurrently. If
     * the version information is non-{@code null}, and it does not match the
     * current version of the targeted JSON resource, then the patch request
     * will be rejected by the provider.
     *
     * @param version
     *            The expected version information associated with the JSON
     *            resource to be patched.
     * @return This patch request.
     * @throws UnsupportedOperationException
     *             If this patch request does not permit changes to the version
     *             information.
     */
    PatchRequest setRevision(String version);
}
