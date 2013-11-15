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

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * This class overrides the text watcher and launch a new task after a selected waiting time in milliseconds. Previous
 * call to async task will be interrupted.
 */
public abstract class PendingTextWatcher implements TextWatcher {

    /**
     * The waiting time till the asynctask is launched.
     */
    private final long waitingTime;

    /**
     * The task to launch.
     */
    private PendingTask asyncTask;

    /**
     * Default constructor.
     *
     * @param waitingTime
     *            The time in milliseconds.
     */
    public PendingTextWatcher(final long waitingTime) {
        super();
        this.waitingTime = waitingTime;
    }

    @Override
    public void afterTextChanged(final Editable s) {
        synchronized (this) {
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
            asyncTask = new PendingTask();
            asyncTask.execute(s);
        }
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        // Nothing to do.

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        // Nothing to do.

    }

    /**
     * This method is called to notify you that, somewhere within s, the text has been changed.
     *
     * @param s
     *            The editable from the input text.
     */
    public abstract void afterTextChangedDelayed(final Editable s);

    private class PendingTask extends AsyncTask<Editable, Void, Editable> {

        @Override
        protected Editable doInBackground(final Editable... params) {
            try {
                Thread.sleep(waitingTime);
            } catch (final InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(final Editable result) {
            super.onPostExecute(result);
            afterTextChangedDelayed(result);
        }
    }

}
