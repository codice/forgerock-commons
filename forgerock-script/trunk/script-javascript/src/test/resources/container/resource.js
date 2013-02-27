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
var scimUser1 = {
    "externalId": "701984",
    "userName": "bjensen@example.com",
    "name": {
        "formatted": "Ms. Barbara J Jensen III",
        "familyName": "Jensen",
        "givenName": "Barbara",
        "middleName": "Jane",
        "honorificPrefix": "Ms.",
        "honorificSuffix": "III"
    },
    "assignedDashboard": ["Salesforce", "Google", "ConstantContact"],
    "displayName": "Babs Jensen",
    "nickName": "Babs",
    "profileUrl": "https://login.example.com/bjensen"
}
/**
 * <pre>
 * create(String resourceContainer, String newResourceId, Map content[, List fieldFilter][,Map context])
 * </pre>
 */
var userC1 = router.create("Users", null, scimUser1)

var userC2 = router.create("Users", "bjensen@example.com", scimUser1)

/**
 * <pre>
 * read(String resourceName[, List fieldFilter][,Map context])
 * </pre>
 */
var userR1 = router.read("Users/" + userC1._id)

var userR2 = router.read("Users/bjensen@example.com")

/**
 * <pre>
 * update(String resourceName, String revision, Map content [, List fieldFilter][,Map context])
 * </pre>
 */
var scimUser1Updated = scimUser1
scimUser1Updated.userType = "Employee"
scimUser1Updated.title = "Tour Guide"
scimUser1Updated.preferredLanguage = "en_US"
scimUser1Updated.locale = "en_US"
scimUser1Updated.timezone = "America/Los_Angeles"
scimUser1Updated.active = true

var userU1 = router.update("Users/" + userC1._id, userR1._rev, scimUser1Updated)

var userU2 = router.update("Users/bjensen@example.com", userR2._rev, scimUser1Updated)

/**
 * <pre>
 * patch(String resourceName, String revision, Map patch [, List fieldFilter][,Map context])
 * </pre>
 */

/**
 * <pre>
 * query(String resourceContainer, Map params [, List fieldFilter][,Map context])
 * </pre>
 */
var queryParams1 = {
    "_queryId": "query-all-ids"
}
var queryParams2 = {
    "_filter": "nickName eq \"Babs\""
}

try {
    var userQ1 = router.query("Users", queryParams1)
} catch (e) {
    //expected
}
var userQ2 = router.query("Users", queryParams2)
var userQ3 = router.query("Users", queryParams2, callback)

var printResult = function (resource, error) {
    if (error != null) {
        java.lang.System.out.println("Result" + resource)
    } else {
        java.lang.System.out.println("Error:" + error)
    }
}
var userQ4 = router.query("Users", queryParams2, printResult)

/**
 * <pre>
 * delete(String resourceName, String revision [, List fieldFilter][,Map context])
 * </pre>
 */

/**
 * <pre>
 * action(String resourceName, [String actionId,] Map params, Map content[, List fieldFilter][,Map context])
 * </pre>
 */
var userA1 = router.action("Users", "clear", {"_action": "clear"})

var arrayVar = ["Salesforce", "Google", "ConstantContact"]

var hasGoogle = (userR1.assignedDashboard.indexOf("Google") !== -1);
var ccPosition = userR1.assignedDashboard.indexOf("ConstantContact");

if (!userR1.hasOwnProperty("profileUrl")) {
    throw { "message" : "Property 'profileUrl' expected but not found" };
}

if (!hasGoogle) {
    throw { "message": "Google expected but not present" };
}

if (ccPosition !== 2) {
    throw { "message" : "Constant Contact expected at position 2, indexOf returned " + ccPosition };
}

if (userR1.assignedDashboard.join("+") !== "Salesforce+Google+ConstantContact") {
    throw { "message" : "AssignedDashboard array expected to be joined together into a '+' delimited string"};
}

if (userR1.name.formatted.split(' ').length !== 5) {
    throw { "message" : "formatted name '"+ userR1.name.formatted +"' expected to be split into a 5 element array, based on spaces."};
}