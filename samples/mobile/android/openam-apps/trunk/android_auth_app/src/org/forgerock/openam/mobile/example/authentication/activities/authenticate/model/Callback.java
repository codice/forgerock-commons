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

package org.forgerock.openam.mobile.example.authentication.activities.authenticate.model;

/**
 * Simple representation of a callback
 */
public class Callback {

    private String type;
    private CallbackElement[] output;
    private CallbackElement[] input;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CallbackElement[] getOutput() {
        return output;
    }

    public void setOutput(CallbackElement[] output) {
        this.output = output;
    }

    public CallbackElement[] getInput() {
        return input;
    }

    public void setInput(CallbackElement[] input) {
        this.input = input;
    }
}
