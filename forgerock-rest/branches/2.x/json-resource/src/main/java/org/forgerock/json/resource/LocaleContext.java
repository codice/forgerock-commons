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
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */

package org.forgerock.json.resource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A context to represent the client's locale preference.
 * @deprecated This will move to CHF in future releases.
 */
public class LocaleContext extends AbstractContext {

    private final List<Locale> locales;

    /**
     * Construct a new context using the specified context and the list of locales.
     * @param parent The parent context.
     * @param locales The list of preferred locales, starting with the most preferred and decreasing in preference.
     */
    public LocaleContext(Context parent, List<Locale> locales) {
        super("locale", parent);
        if (locales == null || locales.isEmpty()) {
            locales = Collections.singletonList(Locale.ROOT);
        }
        this.locales = Collections.unmodifiableList(locales);
    }

    /**
     * The preferred locale for the context.
     * @return The locale.
     */
    public Locale getPreferredLocale() {
        return locales.get(0);
    }

    /**
     * The ordered list of preferred locales.
     * @return The preferred locales.
     */
    public List<Locale> getPreferredLocales() {
        return locales;
    }

    /**
     * Get a {@code ResourceBundle} using the context's preferred locale list and using the {@code ClassLoader}
     * as specified in the {@code ResourceBundle.getBundle(String, Locale)} method.
     * @param bundleName The of the bundle to load.
     * @return The bundle in the best matching locale.
     */
    public ResourceBundle getBundleInPreferredLocale(String bundleName) {
        for (Locale locale : locales) {
            ResourceBundle candidate = ResourceBundle.getBundle(bundleName, locale);
            if (matches(locale, candidate.getLocale())) {
                return candidate;
            }
        }
        return ResourceBundle.getBundle(bundleName, Locale.ROOT);
    }

    /**
     * Get a {@code ResourceBundle} using the context's preferred locale list and using the provided
     * {@code ClassLoader}.
     * @param bundleName The of the bundle to load.
     * @param classLoader The {@code ClassLoader} to use to load the bundle.
     * @return The bundle in the best matching locale.
     */
    public ResourceBundle getBundleInPreferredLocale(String bundleName, ClassLoader classLoader) {
        for (Locale locale : locales) {
            ResourceBundle candidate = ResourceBundle.getBundle(bundleName, locale, classLoader);
            if (matches(locale, candidate.getLocale())) {
                return candidate;
            }
        }
        return ResourceBundle.getBundle(bundleName, Locale.ROOT, classLoader);
    }

    /**
     * Is the candidate locale the best match for the requested locale? Exclude {@code Locale.ROOT} unless it
     * is the requested locale, as it should be the fallback only when all locales are tried.
     */
    private boolean matches(Locale requested, Locale candidate) {
        return candidate.equals(requested) || (!Locale.ROOT.equals(candidate) && !locales.contains(candidate));
    }

    @Override
    public String getContextName() {
        return "locale";
    }
}
