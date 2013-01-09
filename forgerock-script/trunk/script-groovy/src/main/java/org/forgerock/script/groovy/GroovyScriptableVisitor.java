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

package org.forgerock.script.groovy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Request;
import org.forgerock.json.resource.UpdateRequest;
import org.forgerock.script.engine.Utils;
import org.forgerock.script.scope.Element;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.ObjectConverter;
import org.forgerock.script.scope.ScriptableVisitor;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class GroovyScriptableVisitor implements ScriptableVisitor<Object, ObjectConverter> {
    public Object visitFunction(ObjectConverter objectConverter, Function function) {
        return new FunctionClosure(null, objectConverter.getOperationParameter(), function);
    }

    public Object visitJsonValue(ObjectConverter objectConverter, JsonValue value) {
        return value.getObject();
    }

    public Object visitObject(ObjectConverter objectConverter, Object source) {
        if (source instanceof JsonValue) {
            return new JsonValue(visitObject(objectConverter, ((JsonValue) source).getObject()));
        } else if (source instanceof Collection || source instanceof Map) {
            return deepCopy(objectConverter, source, new Stack<Utils.Pair<Object, Object>>());
        } else {
            return source;
        }
    }

    public Object visitActionRequest(ObjectConverter objectConverter, ActionRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitCreateRequest(ObjectConverter objectConverter, CreateRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitDeleteRequest(ObjectConverter objectConverter, DeleteRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitPatchRequest(ObjectConverter objectConverter, PatchRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitQueryRequest(ObjectConverter objectConverter, QueryRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitReadRequest(ObjectConverter objectConverter, ReadRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public Object visitUpdateRequest(ObjectConverter objectConverter, UpdateRequest request) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    @SuppressWarnings({"unchecked"})
    private  Object deepCopy(final ObjectConverter param, Object source, final Stack<Utils.Pair<Object,Object>> valueStack) {
        Iterator<Utils.Pair<Object,Object>> i = valueStack.iterator();
        while (i.hasNext()) {
            Utils.Pair<Object,Object> next = i.next();
            if (next.fst == source){
                return next.snd;
            }
        }

        if (source instanceof JsonValue) {
            return  new JsonValue(deepCopy(param, ((JsonValue)source).getObject(), valueStack));
        }  else if (source instanceof Collection) {
            List<Object> copy = new ArrayList<Object>(((Collection)source).size());
            valueStack.push(Utils.Pair.of(source, (Object) copy));
            for (Object o : (Collection) source) {
                copy.add(deepCopy(param,o,valueStack ));
            }
            //valueStack.pop();
            return copy;
        }  else if (source instanceof Map) {
            Map copy = new LinkedHashMap(((Map)source).size());
            valueStack.push(Utils.Pair.of(source, (Object) copy));
            for (Map.Entry<Object,Object> entry  : ((Map<Object,Object>) source).entrySet()) {
                copy.put(entry.getKey(),deepCopy(param,entry.getValue(), valueStack));
            }
            //valueStack.pop();
            return copy;
        } else {
            if (null == source) {
                return null;
            } else if (source instanceof Element) {
                return ((Element) source).accept(this, param);
            }
            if (source instanceof Request) {
                return ((Request) source).accept(this, param);
            } else {
                return visitObject(param, source);
            }
        }
    }
}
