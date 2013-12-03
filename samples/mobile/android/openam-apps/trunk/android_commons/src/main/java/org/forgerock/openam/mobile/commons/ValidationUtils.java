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

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Collection of methods used to check data entered into our interfaces are
 * appropriate.
 */
public class ValidationUtils {

    /**
     * Keeping the constructor private
     */
    private ValidationUtils() { }

    /**
     * Validates a string isn't empty.
     *
     * @param text to check
     * @return true if the string is not null and its length is > 0
     */
    private static boolean validateText(String text) {
        if (text != null && text.length() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Validates each text supplied, displays message if any are invalid.
     *
     * @param context Activity this validation was called from
     * @param textEntry The text entries to
     * @return true if all texts validate, false otherwise
     */
    public static boolean validateAllText(Context context, EditText... textEntry) {
        for (EditText text : textEntry) {
            if (!validateText(text.getText().toString())) {
                AndroidUtils.showToast(text.getHint(), context);
                return false;
            }
        }

        return true;
    }

    /**
     * Validates each URL supplied, displays message if any are invalid.
     *
     * @param context Activity this validation was called from
     * @param url text entries this was applied to
     * @return true if all texts validate, false otherwise
     */
    public static boolean validateAllUrl(Context context, EditText... url) {
        for (EditText text : url) {
            if (!validateUrl(text.getText().toString())) {
                AndroidUtils.showToast(text.getHint(), context);
                return false;
            }
        }

        return true;
    }

    /**
     * checks a url is not malformed
     *
     * @param url to check
     * @return true if valid, false otherwise
     */
    private static boolean validateUrl(String url) {
        try {
            URL check = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    /**
     * Compares two text entry boxes to make sure the contents of one
     * is a subdomain of the other
     *
     * @param context The actvity this validation was called from
     * @param domainT The (sub)domain which to check
     * @param hostT The host domain of which the subdomain must be a part
     * @return true if domainT is a subdomain of hostT of the type ".subdomain.toplevel"
     */
    public static boolean validateSubdomain(Context context, EditText domainT, EditText hostT) {
        String domain = domainT.getText().toString();
        String host = hostT.getText().toString();

        Uri openam = Uri.parse(host);

        //empty or null
        if (domain == null || domain.equals("")) {
            return true;
        }

        //matches subdomain, includes at least two dots and starts with one of them
        if (openam.getHost().endsWith(domain) && domain.matches("^\\..+\\..+")) {
            return true;
        }

        AndroidUtils.showToast(domainT.getHint(), context);

        return false;
    }
}
