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

package org.forgerock.script.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.swissbox.extender.BundleScanner;
import org.ops4j.pax.swissbox.extender.ManifestEntry;
import org.osgi.framework.Bundle;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class ScriptEngineManifestScanner implements BundleScanner<ManifestEntry> {

    private static final String BUNDLE_PREFIX = "SPI-Provider";

    public List<ManifestEntry> scan(Bundle bundle) {
        NullArgumentException.validateNotNull(bundle, "Bundle");

        String providers = null;

        final Dictionary bundleHeaders = bundle.getHeaders();
        if (bundleHeaders != null && !bundleHeaders.isEmpty()) {
            final Enumeration keys = bundleHeaders.keys();

        }
        if (false) {
            List<ManifestEntry> result = new ArrayList<ManifestEntry>(1);
            result.add(new ManifestEntry(BUNDLE_PREFIX, providers));
            return result;
        } else {
            return Collections.emptyList();
        }
    }
}
