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

package org.forgerock.script.groovy;

import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.ConnectionProvider;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.Resources;
import org.forgerock.script.scope.Function;

import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class FunctionWrapper extends Wrapper {

    /** TODO: Description. */
    private final Map<String, Function> function;

    public FunctionWrapper(Class constrainedType, Map<String, Function> function) {
        super(constrainedType);
        this.function = function;
    }

    public Object unwrap() {
        return getWrapped();
    }

    protected Object getWrapped() {
        return function;
    }

    protected MetaClass getDelegatedMetaClass() {
        return new MetaClass() {
            public Object invokeMethod(Class sender, Object receiver, String methodName,
                    Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object getProperty(Class sender, Object receiver, String property,
                    boolean isCallToSuper, boolean fromInsideClass) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public void setProperty(Class sender, Object receiver, String property, Object value,
                    boolean isCallToSuper, boolean fromInsideClass) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }

            public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object invokeMissingProperty(Object instance, String propertyName,
                    Object optionalValue, boolean isGetter) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object getAttribute(Class sender, Object receiver, String messageName,
                    boolean useSuper) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public void setAttribute(Class sender, Object receiver, String messageName,
                    Object messageValue, boolean useSuper, boolean fromInsideClass) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }

            public void initialize() {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }

            public List<MetaProperty> getProperties() {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public List<MetaMethod> getMethods() {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public ClassNode getClassNode() {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public List<MetaMethod> getMetaMethods() {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public int selectConstructorAndTransformArguments(int numberOfConstructors,
                    Object[] arguments) {
                return 0; // To change body of implemented methods use File |
                          // Settings | File Templates.
            }

            public MetaMethod pickMethod(String methodName, Class[] arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public List<MetaMethod> respondsTo(Object obj, String name, Object[] argTypes) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public List<MetaMethod> respondsTo(Object obj, String name) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public MetaProperty hasProperty(Object obj, String name) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public MetaProperty getMetaProperty(String name) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public MetaMethod getStaticMetaMethod(String name, Object[] args) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public MetaMethod getMetaMethod(String name, Object[] args) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Class getTheClass() {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object invokeConstructor(Object[] arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object invokeMethod(Object object, String methodName, Object[] arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object invokeMethod(Object object, String methodName, Object arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public Object getProperty(Object object, String property) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public void setProperty(Object object, String property, Object newValue) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }

            public Object getAttribute(Object object, String attribute) {
                return null; // To change body of implemented methods use File |
                             // Settings | File Templates.
            }

            public void setAttribute(Object object, String attribute, Object newValue) {
                // To change body of implemented methods use File | Settings |
                // File Templates.
            }
        };
    }

    public Object invokeMethod(String name, Object args) {
        try {
            // return function.get(name).call(args);
        } catch (Throwable throwable) {
            return null;
        }
        return null;
    }

    public Object getProperty(String propertyName) {
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    public void setProperty(String propertyName, Object newValue) {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    public void setMetaClass(MetaClass metaClass) {
    }
}
