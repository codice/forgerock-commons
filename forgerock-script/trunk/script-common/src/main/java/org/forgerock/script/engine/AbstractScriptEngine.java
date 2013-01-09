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

package org.forgerock.script.engine;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.Request;
import org.forgerock.script.scope.Element;
import org.forgerock.script.scope.ObjectConverter;
import org.forgerock.script.scope.ScriptableVisitor;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public abstract class AbstractScriptEngine implements ScriptEngine {

    /**
     * This is the writer to be used to output from scripts. By default, a
     * <code>PrintWriter</code> based on <code>System.out</code> is used.
     * Accessor methods getWriter, setWriter are used to manage this field.
     * 
     * @see java.lang.System#out
     * @see java.io.PrintWriter
     */
    protected Writer writer;

    /**
     * This is the writer to be used to output errors from scripts. By default,
     * a <code>PrintWriter</code> based on <code>System.err</code> is used.
     * Accessor methods getErrorWriter, setErrorWriter are used to manage this
     * field.
     * 
     * @see java.lang.System#err
     * @see java.io.PrintWriter
     */
    protected Writer errorWriter;

    /**
     * This is the reader to be used for input from scripts. By default, a
     * <code>InputStreamReader</code> based on <code>System.in</code> is used
     * and default charset is used by this reader. Accessor methods getReader,
     * setReader are used to manage this field.
     * 
     * @see java.lang.System#in
     * @see java.io.InputStreamReader
     */
    protected Reader reader;


    protected AbstractScriptEngine() {
        reader = new InputStreamReader(System.in);
        writer = new PrintWriter(System.out , true);
        errorWriter = new PrintWriter(System.err, true);
    }

    /** @see javax.script.ScriptContext#getWriter() */
    public Writer getWriter() {
        return writer;
    }

    /** @see javax.script.ScriptContext#getReader() */
    public Reader getReader() {
        return reader;
    }

    /** @see javax.script.ScriptContext#setReader(java.io.Reader) */
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /** @see javax.script.ScriptContext#setWriter(java.io.Writer) */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /** @see javax.script.ScriptContext#getErrorWriter() */
    public Writer getErrorWriter() {
        return errorWriter;
    }

    /** @see javax.script.ScriptContext#setErrorWriter(java.io.Writer) */
    public void setErrorWriter(Writer writer) {
        this.errorWriter = writer;
    }

    /** {@inheritDoc  */
    public Object compileObject(final Context context, Object value) {
        return compileObject(getVisitor(), getObjectConverter(context), value);
    }

    /** {@inheritDoc  */
    public Bindings compileBindings(final Context context, final Bindings request,
            Bindings... scopes) {
        Bindings result = new SimpleBindings();
        ObjectConverter param = getObjectConverter(context);
        ScriptableVisitor<Object, ObjectConverter> visitor = getVisitor();

        if (null != request) {
            for (Map.Entry<String, Object> entry : request.entrySet()) {
                result.put(entry.getKey(), compileObject(visitor, param, entry.getValue()));
            }
        }

        for (Bindings next : scopes) {
            if (null == next)
                continue;
            for (Map.Entry<String, Object> entry : next.entrySet()) {
                if (!result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), compileObject(visitor, param, Utils.deepCopy(entry
                            .getValue())));
                }
            }
        }
        return result;
    }

    protected Object compileObject(final ScriptableVisitor<Object, ObjectConverter> visitor,
            final ObjectConverter param, final Object value) {
        if (null == value) {
            return getNull();
        } else if (value instanceof Element) {
            return ((Element) value).accept(visitor, param);
        }
        if (value instanceof Request) {
            return ((Request) value).accept(visitor, param);
        } else {
            return visitor.visitObject(param, value);
        }
    }

    /**
     * Gets the request context
     * 
     * @param context
     * @return
     */
    protected abstract ObjectConverter getObjectConverter(final Context context);

    /**
     * Gets the Visitor
     * 
     * @return
     */
    protected abstract ScriptableVisitor<Object, ObjectConverter> getVisitor();

    /**
     * Gets the {@code null} object representation.
     * <p/>
     * If the {@code null} object has special representation in the script scope
     * this method returns with that object.
     * 
     * @return {@code null} or representation of {@code null} object.
     */
    protected Object getNull() {
        return null;
    }
}
