/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
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

package org.forgerock.script.scope;

import java.util.List;

import org.forgerock.json.fluent.JsonPointer;
import org.forgerock.json.fluent.JsonValue;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class FunctionFactory {

    // public static Map<String, Function<Void>> getLogger(final String
    // loggerName) {
    // return new LazyMap<String, Function>(new Factory<Map<String, Function>>()
    // {
    // @Override
    // public Map<String, Function> newInstance() {
    // final Logger logger = LoggerFactory.getLogger(loggerName
    // /*
    // * "org.forgerock.openidm.script.javascript.JavaScript." + (file
    // * == null ? "embedded-source" : file.getName())
    // */);
    // HashMap<String, Function> loggerWrap = new HashMap<String, Function>();
    // // error(string id, object... param)
    // // Wraps SLF4j error(String format, Object[] argArray)
    // // Log a message at the error level according to the specified
    // // format and arguments.
    // loggerWrap.put("error", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // logger.error(p.get(0).required().asString(), params.size() > 1 ? params
    // .subList(1, params.size()).toArray() : null);
    // return null; // no news is good news
    // }
    // });
    // // warn(string id, object... param)
    // // Wraps SLF4j warn(String format, Object[] argArray)
    // // Log a message at the warn level according to the specified
    // // format and arguments.
    // loggerWrap.put("warn", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // logger.warn(p.get(0).required().asString(), params.size() > 1 ? params
    // .subList(1, params.size()).toArray() : null);
    // return null; // no news is good news
    // }
    // });
    // // info(string id, object... param)
    // // Wraps SLF4j info(String format, Object[] argArray)
    // // Log a message at the info level according to the specified
    // // format and arguments.
    // loggerWrap.put("info", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // logger.info(p.get(0).required().asString(), params.size() > 1 ? params
    // .subList(1, params.size()).toArray() : null);
    // return null; // no news is good news
    // }
    // });
    // // debug(string id, object... param)
    // // Wraps SLF4j debug(String format, Object[] argArray)
    // // Log a message at the debug level according to the specified
    // // format and arguments.
    // loggerWrap.put("debug", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // logger.debug(p.get(0).required().asString(), params.size() > 1 ? params
    // .subList(1, params.size()).toArray() : null);
    // return null; // no news is good news
    // }
    // });
    // // trace(string id, object... param)
    // // Wraps SLF4j trace(String format, Object[] argArray)
    // // Log a message at the trace level according to the specified
    // // format and arguments.
    // loggerWrap.put("trace", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // logger.trace(p.get(0).required().asString(), params.size() > 1 ? params
    // .subList(1, params.size()).toArray() : null);
    // return null; // no news is good news
    // }
    // });
    //
    // return loggerWrap;
    // }
    // });
    // }

    /**
     * 
     action(String endPoint[, String id], String type, Map params, Map
     * content[, List fieldFilter][,Map context])
     * 
     * create(String endPoint[, String id], Map content[, List fieldFilter][,Map
     * context])
     * 
     * delete(String endPoint, String id[, String rev][, List fieldFilter][,Map
     * context])
     * 
     * patch(String endPoint[, String id], Map content [, String rev][, List
     * fieldFilter][,Map context])
     * 
     * query(String endPoint[, Map params][, String filter][, List
     * fieldFilter][,Map context])
     * 
     * read(String endPoint[, String id][, List fieldFilter][,Map context])
     * 
     * update(String endPoint[, String id], Map content [, String rev][, List
     * fieldFilter][,Map context])
     * 
     * @param context
     * @param resource
     * @return
     */

    // public static Map<String, Function> getResource(final Context context,
    // final Connection resource) {
    // return new LazyMap<String, Function>(new Factory<Map<String, Function>>()
    // {
    // @Override
    // public Map<String, Function> newInstance() {
    // HashMap<String, Function<JsonValue>> openidm = new HashMap<String,
    // Function<JsonValue>>();
    // // create(String endPoint[, String id], Map content[, List
    // fieldFilter][,Map context])
    // openidm.put("create", new Function<JsonValue>() {
    // @Override
    // //@SuppressWarnings("fall-through")
    // public JsonValue call(Object[] arguments) throws Exception {
    //
    // // Required
    // String endPoint = null;
    // String id = null;
    // Map content = null;
    // List fieldFilter = null;
    // Map context = null;
    //
    // for (int i = 0 ; i < arguments.length ; i++) {
    // Object param = arguments[i];
    // switch (i) {
    // case 1 : {
    // // Required
    // if (param instanceof String) {
    // endPoint = (String) param;
    // } else {
    // //TODO exception
    // }
    // break;
    // }
    // case 2 : {
    // //Optional
    // if (param instanceof String) {
    // id = (String) param;
    // break;
    // } else {
    // // Fall to the next
    // i++;
    // }
    // }
    // case 3 : {
    // // Required
    // if (param instanceof Map) {
    // content = (Map) param;
    // } else {
    // //TODO exception
    // }
    // break;
    // }
    // case 4 : {
    // //Optional
    // if (param instanceof List) {
    // fieldFilter = (List) param;
    // break;
    // } else {
    // // Fall to the next
    // i++;
    // }
    // }
    // case 5 : {
    // //Optional
    // if (param instanceof Map) {
    // context = (Map) param;
    // break;
    // }
    // }
    // }
    // }
    //
    // Connection connection = null;
    //
    //
    //
    //
    //
    //
    //
    // JsonValue p = paramsValue(params);
    // JsonValue request = new JsonValue(new HashMap<String, Object>(2));
    //
    // request.put("id", p.get(0).required().asString());
    // request.put("value", p.get(1).required().expect(Map.class)); // OpenIDM
    // // resources
    // // are
    // // maps
    // // only
    //
    // CreateRequestImpl createIn = new CreateRequestImpl();
    // createIn.setRequest(request);
    // createIn.setFieldFilter(null);
    //
    // CreateResultHandlerImpl createOut = new CreateResultHandlerImpl();
    // resource.create(createIn, context, createOut);
    // // This is a short-term solution until we support async
    // // processing throughout
    // return createOut.waitForResult().getWrappedObject();
    // }
    // });
    // // read(string id)
    // openidm.put("read", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // try {
    // JsonValue p = paramsValue(params);
    // JsonValue request = new JsonValue(new HashMap<String, Object>(1));
    //
    // request.put("id", p.get(0).required().asString());
    //
    // ReadRequestImpl readIn = new ReadRequestImpl();
    // readIn.setRequest(request);
    // readIn.setFieldFilter(null);
    //
    // ReadResultHandlerImpl readOut = new ReadResultHandlerImpl();
    // resource.read(readIn, context, readOut);
    // // This is a short-term solution until we support
    // // async processing throughout
    // return readOut.waitForResult().getWrappedObject();
    // } catch (NotFoundException e) {
    // return null;
    // }
    // }
    // });
    // // update(string id, string rev, object value)
    // openidm.put("update", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // JsonValue request = new JsonValue(new HashMap<String, Object>(3));
    //
    // request.put("id", p.get(0).required().asString());
    // request.put("rev", p.get(1).asString());
    // request.put("value", p.get(2).required().expect(Map.class)); // OpenIDM
    // // resources
    // // are
    // // maps
    // // only
    //
    // UpdateRequestImpl updateIn = new UpdateRequestImpl();
    // updateIn.setRequest(request);
    // updateIn.setFieldFilter(null);
    // UpdateResultHandlerImpl updateOut = new UpdateResultHandlerImpl();
    // resource.update(updateIn, context, updateOut);
    // // This is a short-term solution until we support async
    // // processing throughout
    // return updateOut.waitForResult().getWrappedObject();
    // }
    // });
    // // delete(string id, string rev)
    // openidm.put("delete", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // JsonValue request = new JsonValue(new HashMap<String, Object>(2));
    //
    // request.put("id", p.get(0).required().asString());
    // request.put("rev", p.get(1).asString());
    //
    // DeleteRequestImpl deleteIn = new DeleteRequestImpl();
    // deleteIn.setRequest(request);
    // deleteIn.setFieldFilter(null);
    // DeleteResultHandlerImpl deleteOut = new DeleteResultHandlerImpl();
    // resource.delete(deleteIn, context, deleteOut);
    // // This is a short-term solution until we support async
    // // processing throughout
    // return deleteOut.waitForResult().getWrappedObject();
    // }
    // });
    // // query(string id, object params)
    // openidm.put("query", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // JsonValue request = new JsonValue(new HashMap<String, Object>(2));
    //
    // request.put("id", p.get(0).required().asString());
    // request.put("params", p.get(1).required());
    //
    // QueryRequestImpl queryIn = new QueryRequestImpl();
    // queryIn.setRequest(request);
    // queryIn.setFieldFilter(null);
    // QueryResultHandlerImpl queryOut = new QueryResultHandlerImpl();
    // resource.query(queryIn, context, queryOut);
    // // This is a short-term solution until we support async
    // // processing throughout
    // return queryOut.waitForResult().getWrappedObject();
    // }
    // });
    // // action(string id, object params, any value)
    // openidm.put("action", new Function() {
    // @Override
    // public Object call(Map<String, Object> scope, Map<String, Object> _this,
    // List<Object> params) throws Throwable {
    // JsonValue p = paramsValue(params);
    // JsonValue value = p.get(2); // optional parameter
    // if (value.isNull()) {
    // value = p.get(1).get("_entity"); // backwards
    // // compatibility
    // }
    //
    // JsonValue request = new JsonValue(new HashMap<String, Object>(2));
    //
    // request.put("id", p.get(0).required().asString());
    // request.put("value", value);
    // request.put("params", p.get(1).required());
    //
    // ActionRequestImpl actionIn = new ActionRequestImpl();
    // actionIn.setRequest(request);
    // actionIn.setFieldFilter(null);
    // ActionResultHandlerImpl actionOut = new ActionResultHandlerImpl();
    // resource.action(actionIn, context, actionOut);
    // // This is a short-term solution until we support async
    // // processing throughout
    // return actionOut.waitForResult().getWrappedObject();
    // }
    // });
    // /*
    // * case patch: PatchRequestImpl patchIn = new
    // * PatchRequestImpl(); patchIn.setRequest(request);
    // * patchIn.setFieldFilter(filter); PatchResultHandlerImpl
    // * patchOut = new PatchResultHandlerImpl();
    // * resource.patch(patchIn, context, patchOut); // This is a
    // * short-term solution until we support async processing
    // * throughout return patchOut.waitForResult();
    // */
    // // // encrypt(any value, string cipher, string alias)
    // // openidm.put("encrypt", new Function() {
    // // @Override
    // // public Object call(Map<String, Object> scope,
    // // Map<String, Object> _this, List<Object> params) throws
    // // Throwable {
    // // JsonValue jv = paramsValue(params);
    // // return cryptoService.encrypt(
    // // jv.get(0).required(),
    // // jv.get(1).required().asString(),
    // // jv.get(2).required().asString()
    // // ).getWrappedObject();
    // // }
    // // });
    // // // decrypt(any value)
    // // openidm.put("decrypt", new Function() {
    // // @Override
    // // public Object call(Map<String, Object> scope,
    // // Map<String, Object> _this, List<Object> params) throws
    // // Throwable {
    // // JsonValue jv = paramsValue(params);
    // // return cryptoService.decrypt(
    // // jv.get(0).required()
    // // ).getObject();
    // // }
    // // });
    // return openidm;
    // }
    // });
    // }

    private static JsonValue paramsValue(List<Object> params) {
        return new JsonValue(params, new JsonPointer("params"));
    }

}
