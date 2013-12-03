/*
 * Copyright 2013 ForgeRock AS.
 *
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
 */

package org.forgerock.openam.mobile.commons;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Small interface to allow our configs to be stored in an uncomplicated way.
 *
 * @param <T> The resource type to load/save
 */
public interface Resource<T extends Resource> {

    public String toJSON() throws JSONException;
    public T fromJSON(JSONObject json) throws JSONException;

}
