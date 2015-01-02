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
 *       Copyright 2015 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import static org.forgerock.contactmanager.AppContext.getContext;
import java.util.Locale;

/**
 * List the filters which can may used in this application by the user
 * to increase accuracy or enlarge the results returned by the search.
 */
enum Filter {

    /** The start-with filter search. Default. */
    START_WITH("startWith",
               "/users?_queryFilter=name/familyName+sw+\"%s\"+or+name/givenName+sw+\"%s\"",
               getContext().getResources().getString(R.string.filter_start_with)),

    /** The exact match filter search. */
    EXACT_MATCH("exactMatch",
             "/users?_queryFilter=displayName+eq+\"%s\"",
             getContext().getResources().getString(R.string.filter_exact_match)),

    /** Search by employee function. */
    FUNCTION("function",
         "/users?_queryFilter=contactInformation/description+sw+\"%s\"",
         getContext().getResources().getString(R.string.filter_function)),

    /** Search by manager. */
    MANAGER("manager",
            "/users?_queryFilter=manager/displayName+co+\"%s\"+or+manager/displayName+eq+\"%s\"",
            getContext().getResources().getString(R.string.filter_manager));

    private final String name;
    private final String expression;
    private final String description;

    Filter(final String name, final String expression, final String description) {
        this.name = name;
        this.expression = expression;
        this.description = description;
    }

    /**
     * Returns the name of this filter.
     *
     * @return The name of this filter.
     */
    String getName() {
        return name;
    }

    /**
     * Returns the corresponding expression.
     *
     * @return The expression of this filter.
     */
    String getExpression() {
        return expression;
    }

    /**
     * Returns the corresponding description.
     *
     * @return The description of this filter.
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns the filter matching the name.
     *
     * @param filterName
     *            The filter to retrieve.
     * @return The matching filter or {code null} if not found.
     */
    static Filter forName(final String filterName) {
        // Basic ascii to lower case.
        final String lowerName = filterName.toLowerCase(Locale.US);
        if (lowerName.equals("startwith")) {
            return START_WITH;
        } else if (lowerName.equals("exactmatch")) {
            return EXACT_MATCH;
        } else if (lowerName.equals("function")) {
            return FUNCTION;
        } else if (lowerName.equals("manager")) {
            return MANAGER;
        }
        return null;
    }
}
