/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2014 ForgeRock Inc. All rights reserved.
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

package org.forgerock.script.source;

import org.forgerock.script.ScriptEntry;
import org.forgerock.script.ScriptName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class URLScriptSource implements ScriptSource {

    private ScriptEntry.Visibility visibility;
    private URL source;
    private ScriptName scriptName;
    private SourceContainer parent = null;

    public URLScriptSource(ScriptEntry.Visibility visibility, URL source, ScriptName scriptName) {
        if (null == source) {
            throw new IllegalArgumentException("URL source is null");
        }
        if (null == scriptName) {
            throw new IllegalArgumentException("ScriptName is null");
        }
        this.visibility = visibility;
        this.source = source;

        this.scriptName = new ScriptName(
                scriptName.getName(),
                scriptName.getType(),
                Long.toHexString(getURLRevision(source, null)),
                scriptName.getRequestBinding());
    }

    public static long getURLRevision(URL source, String revision) {
        if (null == source) {
            throw new NullPointerException();
        }
        long lastMod = 0;

        if (null == revision || "0".equals(revision)) {
            InputStream in = null;
            try {
                URLConnection urlConnection = source.openConnection();
                if (urlConnection != null) {
                    lastMod = ((urlConnection.getLastModified() / 1000) + 1) * 1000 - 1;
                    // We need to get the input stream and close it to force the
                    // open
                    // file descriptor to be released. Otherwise, we will reach
                    // the limit
                    // for number of files open at one time.
                    in = urlConnection.getInputStream();
                }
            } catch (IOException e) {
                /* ignore */
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return lastMod;
    }

    public URLScriptSource(ScriptEntry.Visibility visibility, URL source, ScriptName scriptName,
            final SourceContainer parent) {
        if (null == source) {
            throw new IllegalArgumentException("URL source is null");
        }
        if (null == scriptName) {
            throw new IllegalArgumentException("ScriptName is null");
        }
        this.visibility = visibility;
        this.source = source;
        this.scriptName = new ScriptName(
                scriptName.getName(),
                scriptName.getType(),
                Long.toHexString(getURLRevision(source, null)),
                scriptName.getRequestBinding());
        this.parent = parent;
    }

    public String guessType() {
        return getName().getType();
    }

    public ScriptName getName() {
        return scriptName;
    }

    public URL getSource() {
        return source;
    }

    public Reader getReader() throws IOException {
        // TODO Do we need the doPrivileged read?
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Reader>() {
                public Reader run() throws Exception {
                    return new BufferedReader(new InputStreamReader(getSource().openStream()));
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    public ScriptEntry.Visibility getVisibility() {
        return null != parent ? parent.getVisibility() : visibility;
    }

    public ScriptName[] getDependencies() {
        return null != parent ? new ScriptName[] { parent.getName() } : new ScriptName[0];
    }

    public SourceContainer getParentContainer() {
        return parent;
    }
}
