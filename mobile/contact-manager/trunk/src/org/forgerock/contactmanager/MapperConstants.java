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
 *       Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.contactmanager;

/**
 * This class maps the attributes from the http-config.json configuration file
 * used in OpenDJ to constants used in this application.
 */
final class MapperConstants {
    static final String RESULT_COUNT = "resultCount";
    static final String RESULT = "result";
    static final String ATTRIBUTE = "attributes";
    static final String SCHEMA = "schemas";
    static final String ID = "_id";
    static final String REVISION = "_rev";
    static final String USERNAME = "userName";
    static final String DISPLAY_NAME = "displayName";
    static final String PAGED_RESULT_COOKIE = "pagedResultsCookie";
    static final String NAME = "name";
    static final String GIVEN_NAME = "givenName";
    static final String FAMILY_NAME = "familyName";
    static final String MANAGER = "manager";
    static final String LDAP_ATTRIBUTE = "ldapAttribute";
    static final String BASE_DN = "baseDN";
    static final String PRIMARY_KEY = "primaryKey";
    static final String CONTACT_INFORMATION = "contactInformation";
    static final String TELEPHONE_NUMBER = "telephoneNumber";
    static final String MOBILE_NUMBER = "mobileNumber";
    static final String EMAIL_ADDRESS = "emailAddress";
    static final String JPEG_PHOTO = "jpegPhoto";
    static final String JPEG_URL = "jpegURL";
    static final String DESCRIPTION = "description";
    static final String ORGANIZATION = "organization";
    static final String CONTACT_ADDRESS = "contactAddress";
    static final String POSTAL_ADDRESS = "postalAddress";
    static final String POSTAL_CODE = "postalCode";
    static final String LOCATION = "location";
    static final String STATE = "state";

    /** Prevent instantiation. */
    private MapperConstants() {
        throw new AssertionError();
    }
}
