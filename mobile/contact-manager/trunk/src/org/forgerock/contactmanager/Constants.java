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
 *       Copyright 2013 ForgeRock AS.
 */

package org.forgerock.contactmanager;


final class Constants {

    /**
     * The default picture used in list / contact to be displayed if none exists.
     */
    final static int DEFAULT_ID_PICTURE = R.drawable.avatar_male_gray_frame_200x200;

    final static String SEARCH_SPECIFICUSER_BY_ID = "/users/%s";
    final static String FILTER_FAMILYNAME_STARTSWITH = "/users?_queryFilter=name/familyName+sw+\"%s\"";
    // TODO Add other filter
    // e.g. :
    // final static String FILTER_GIVENNAME_STARTSWITH
    //    = "/users?_queryFilter=name/givenName+sw+\"%s\"";
    // final static String FILTER_MAIL_STARTSWITH = "/users?_queryFilter=userName+sw+\"%s\"";

    // List pagination
    final static int PAGED_RESULT = 8;
    final static String LIST_PAGINATION = "&_pageSize=" + PAGED_RESULT;
    final static String PAGE_RESULT_OFFSET = "&_pagedResultsOffset=%s";

    // Internal
    final static String ALL_SERVER_CONFIGURATIONS = "srvconf";
    final static String SELECTED_SERVER_CONFIGURATION = "selectedsrvconf";
    final static String PREF_NAME_APPLICATION = "OPENDJ";
    final static String PREF_STORAGE_APPLICATION = "ContactManager_cache";

    // Prevent instantiation.
    private Constants() {
        throw new AssertionError();
    }

}
