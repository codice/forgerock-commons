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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * This class executes an asynchronous request. It connects to the server, authenticating and making the request then,
 * when the result is given, re-throws it to the calling activity which needs it.
 * <p>
 * When an asynchronous task is executed, the task goes through 4 steps:
 * <p>
 * onPreExecute() / doInBackground(Params...) / onProgressUpdate(Progress...) / onPostExecute(Result)
 */
public class AsyncServerRequest extends AsyncTask<String, Integer, JSONObject> {

    /**
     * Error message of the request.
     */
    private String error = "";

    /**
     * The activity who launch the request.
     */
    private final Activity activity;

    /**
     * The progress bar linked to the request process.
     */
    private ProgressBar progressBar;

    /**
     * The progress value.
     */
    static int progressValue;

    /**
     * A marker for a pagination request.
     */
    private boolean isPagedCookie = true;

    /**
     * The page offset.
     */
    int pageOffset;

    AsyncServerRequest(final Activity act, final boolean paged) {
        activity = act;
        isPagedCookie = paged;
    }

    AsyncServerRequest(final Activity act, final ProgressBar pb) {
        activity = act;
        progressBar = pb;
    }

    AsyncServerRequest(final Activity act, final ProgressBar pb, final int pOffset) {
        activity = act;
        progressBar = pb;
        pageOffset = pOffset;
        isPagedCookie = false;
    }

    @Override
    protected void onPreExecute() {
        progressValue = 0;
        if (progressBar != null) {
            progressBar.setVisibility(0);
        }
    }

    @Override
    protected JSONObject doInBackground(final String... givenUrl) {
        final ServerConfiguration serverConfiguration = AppContext.getServerConfiguration();

        if (serverConfiguration == null || (serverConfiguration != null && !serverConfiguration.isValid())) {
            logError(new IOException(), "No server configured");
            return null;
        }

        URLConnection c = null;
        try {
            String partialURL = serverConfiguration.getAddress() + givenUrl[0];
            if (activity instanceof SearchActivity) {
                partialURL += Constants.LIST_PAGINATION;
                if (isPagedCookie) {
                    partialURL += getPagedCookie();
                } else {
                    partialURL += getPageOffset();
                }
            }
            final String authString = serverConfiguration.getUsername() + ":" + serverConfiguration.getPassword();

            if (!serverConfiguration.isSSL()) {
                // Basic authentication.
                try {
                    publishProgress(20);
                    final URL url = getHTTPUrl(partialURL);
                    c = url.openConnection();
                    c.setUseCaches(false);
                    c.setConnectTimeout(3000);
                    c.setReadTimeout(3000);
                    c.setDoOutput(false);
                    c.setRequestProperty("Authorization",
                            "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP));
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                        c.setDoInput(true);
                        ((HttpURLConnection) c).setInstanceFollowRedirects(false);
                    }

                    c.connect();

                    publishProgress(50);
                    if (((HttpURLConnection) c).getResponseCode() != 200) {
                        if (((HttpURLConnection) c).getResponseCode() == 401) {
                            error = "Invalid credentials";
                            throw new IOException("Invalid credentials");
                        } else {
                            final String msg = ((HttpURLConnection) c).getResponseMessage();
                            if (msg != null) {
                                throw new ConnectException(msg);
                            }
                            throw new ConnectException();
                        }
                    }
                } catch (final SocketTimeoutException e) {
                    logError(e, "Unable to connect to server.");
                } catch (final SocketException e) {
                    logError(e, "Invalid server configuration.");
                } catch (final Exception e) {
                    logError(e, "An error occured.");
                }
            } else {
                // SSL Connection.

                setSSLConnection();

                final URL url = getHTTPsUrl(partialURL);
                try {
                    c = url.openConnection();
                    c.setRequestProperty("Authorization",
                            "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP));
                } catch (final IOException e) {
                    logError(e, "An error occured during SSL transaction.");
                }
                ((HttpsURLConnection) c).setHostnameVerifier(new SSLHostNameVerifier());
            }
            return readStream(c.getInputStream());
        } catch (final IOException e) {
            logError(e, "An error when reading request result.");
        } finally {
            Utils.closeConnection(c);
        }

        return null;
    }

    private void setSSLConnection() {

        final TrustManager[] trustManager = new TrustManager[] { new SSLTrustManager() };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManager, null);
        } catch (final NoSuchAlgorithmException e) {
            logError(e, "NoSuchAlgorithmException.");
        } catch (final KeyManagementException e) {
            logError(e, "KeyManagementException.");
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    @Override
    protected void onProgressUpdate(final Integer... val) {
        if (progressBar != null) {
            progressBar.setProgress(val[0]);
        }
    }

    @Override
    protected void onPostExecute(final JSONObject result) {

        if (progressBar != null) {
            progressBar.setVisibility(4);
        }

        if (result == null && error.length() > 0) {
            // An error occurred.
            Toast.makeText(activity.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        } else {
            super.onPostExecute(result);
            if (activity instanceof ContactActivity) {
                ((ContactActivity) activity).setDataAndRestart(result);
            } else if (activity instanceof SearchActivity) {
                ((SearchActivity) activity).displayContactList(result);
            }
        }
    }

    @Override
    protected void onCancelled() {
        if (progressBar != null) {
            progressBar.setVisibility(4);
        }
        if (error != null) {
            Toast.makeText(activity.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void logError(final Exception e, final String details) {
        if ("".equals(error)) {
            error = details + " " + (e.getMessage() != null ? e.getMessage() : "");
        }
        Log.e("Unable to connect to server : ", error);
        cancel(true);
    }

    /**
     * Reads a stream from a provided input stream.
     *
     * @param in
     *            The input stream to read.
     * @return A string resulting from the action.
     */
    private static JSONObject readStream(final InputStream in) {
        BufferedReader reader = null;
        final StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in), 8 * 1024);
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } catch (final IOException e) {
            Log.e("Error when reading stream. ", e.getMessage(), e);
        } catch (final JSONException e) {
            Log.e("Error when parsing stream. ", e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    in.close();
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Closing failure", e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Returns the basic http url {@code http://}.
     *
     * @param path
     *            The http path.
     * @return The basic http url.
     */
    URL getHTTPUrl(final String path) {
        try {
            return new URL("http://" + path);
        } catch (final MalformedURLException e) {
            Log.e("HTTP malformed", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the HTTPs url {@code https://}.
     *
     * @param path
     *            The HTTPs path.
     * @return Returns the HTTPs url.
     */
    URL getHTTPsUrl(final String path) {
        try {
            return new URL("https://" + path);
        } catch (final MalformedURLException e) {
            Log.e("HTTPs malformed", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the paged URL if required.
     * <p>
     * e.g : {@code &_pageResultsCookie=AAAAAAAAAAg= } if paging is enabled.
     *
     * @return The paged URL used to retrieve previous page.
     */
    private String getPagedCookie() {
        if (isPagedCookie) {
            return PagedResultCookie.getPagedResultCookie();
        }
        return "";
    }

    /**
     * Returns the page offset.
     * <p>
     * e.g :&_pagedResultsOffset=3
     *
     * @return The page offset used to retrieve selected page.
     */
    private String getPageOffset() {
        if (pageOffset != 0) {
            return String.format(Constants.PAGE_RESULT_OFFSET, pageOffset);
        }
        return "";
    }
}
