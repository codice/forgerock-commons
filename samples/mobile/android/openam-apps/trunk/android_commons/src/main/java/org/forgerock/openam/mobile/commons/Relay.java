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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract client class implementing a listener system for objects
 * using the {@link org.forgerock.openam.mobile.commons.Listener} interface.
 *
 * Before asking the client to perform some operation, register the
 * class you wish to recieve the response in with the client here, then
 * perform the actions. The response of the client's action will be
 * handled in the {@link org.forgerock.openam.mobile.commons.Listener#onEvent(ActionType, Object)} method
 * of the listening class.
 */
public abstract class Relay<T, U> implements Listener<T> {

    private List<Listener<U>> listeners = new ArrayList<Listener<U>>();

    protected List<Listener<U>> getListeners() {
        return listeners;
    }

    public void registerListener(Listener<U> listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener<U> listener) {
        listeners.remove(listener);
    }

    protected void notify(ActionType action, U response) {
        for (Listener<U> listener : listeners) {
            listener.onEvent(action, response);
        }
    }

}
