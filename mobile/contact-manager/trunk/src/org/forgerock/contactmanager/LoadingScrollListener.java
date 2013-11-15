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

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

/**
 * This class implements the on scroll listenener and will be able to be linked to a list. Is used in scroll and lazy
 * load of the search list results.
 */
public class LoadingScrollListener implements OnScrollListener {

    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean scrollDown = false;
    private boolean scrollUp = false;
    private boolean isStopped = false;
    private final Activity activity;
    private final AsyncServerRequest serverRequest;
    private final String url;

    /**
     * Default constructor.
     * @param current The current activity.
     * @param request The request to send.
     * @param urlRequest The URL of the request.
     */
    public LoadingScrollListener(final Activity current, final AsyncServerRequest request, final String urlRequest) {
        activity = current;
        serverRequest = request;
        url = urlRequest;
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        view.setOnTouchListener(getOntouchListener());

        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            isStopped = true;
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            break;
        }
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
            final int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + Constants.PAGED_RESULT) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            serverRequest.execute(url);
            loading = true;
        }

    }

    private OnTouchListener getOntouchListener() {
        return new OnTouchListener() {
            private float mInitialY;

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mInitialY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    final float y = event.getY();
                    final float yDiff = y - mInitialY;
                    if (yDiff > 0.0) {
                        scrollUp = true;
                        scrollDown = false;
                        return false;
                    } else if (yDiff < 0.0) {
                        scrollDown = true;
                        scrollUp = false;
                        Toast.makeText(AppContext.getContext(), "down", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                }
                return false;
            }
        };
    }
}
