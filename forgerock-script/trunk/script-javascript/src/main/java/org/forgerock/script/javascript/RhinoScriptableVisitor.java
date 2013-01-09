/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

package org.forgerock.script.javascript;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.UpdateRequest;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.ScriptableVisitor;
import org.forgerock.script.scope.ObjectConverter;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class RhinoScriptableVisitor implements ScriptableVisitor<Object, ObjectConverter> {

    public Object visitFunction(ObjectConverter parameter, Function function) {
        return new ScriptableFunction(parameter.getOperationParameter() ,function);
    }

    public Object visitJsonValue(ObjectConverter parameter, JsonValue value) {
        return ScriptableWrapper.wrap(parameter.getOperationParameter(), value);
    }

    public Object visitObject(ObjectConverter parameter, Object value) {
        return ScriptableWrapper.wrap(parameter.getOperationParameter(), value);
    }

    public Object visitActionRequest(ObjectConverter parameter, ActionRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitCreateRequest(ObjectConverter parameter, CreateRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitDeleteRequest(ObjectConverter parameter, DeleteRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitPatchRequest(ObjectConverter parameter, PatchRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitQueryRequest(ObjectConverter parameter, QueryRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitReadRequest(ObjectConverter parameter, ReadRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object visitUpdateRequest(ObjectConverter parameter, UpdateRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
