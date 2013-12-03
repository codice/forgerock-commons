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
 * Java representation of an Authenticate stage (an individual authentication module instance in an
 * authentication chain). Used to demonstrate the potential of unmarshalling the callbacks for analysis.
 */
public class Authenticate {

    private String authId;
    private String template;
    private String stage;
    private Callback[] callbacks;

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getAuthId() {
        return authId;
    }

    public Callback[] getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(Callback[] callbacks) {
        this.callbacks = callbacks;
    }
}
