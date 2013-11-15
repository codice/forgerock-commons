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

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class represents common server configuration which is used by this application to connect to the REST OpenDJ
 * server. A list of server configurations is also stored in shared preferences.
 */
public class ServerConfiguration {

    private String serverName;
    private String username;
    private String password;
    private String serverIP;
    private String port;
    private boolean isSSL;

    /**
     * Default constructor.
     */
    ServerConfiguration() {
        // Nothing to do.
    }

    /**
     * Server configuration constructor.
     *
     * @param serverName
     *            The server name used in this application.
     * @param serverIP
     *            The IP of the OpenDJ server.
     * @param port
     *            The port used to connect to OpenDJ.
     */
    public ServerConfiguration(final String serverName, final String serverIP, final String port) {
        super();
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.port = port;
    }

    /**
     * Server configuration constructor.
     *
     * @param serverName
     *            The server name used in this application.
     */
    ServerConfiguration(final String serverName) {
        this.setServerName(serverName);
    }

    /**
     * Returns the user name.
     *
     * @return The user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user name.
     *
     * @param username
     *            The user name to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * The password used by basic authentication.
     *
     * @return The password used by basic authentication.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password used in this configuration.
     *
     * @param password
     *            The password used in this configuration.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Returns the server IP.
     *
     * @return The server's IP.
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * Sets the server IP.
     *
     * @param serverIP
     *            The IP of the server.
     */
    public void setServerIP(final String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * Returns the ports used.
     *
     * @return The port in use.
     */
    public String getPort() {
        return port;
    }

    /**
     * Sets the port for this configuration.
     *
     * @param port
     *            The port used in this configuration.
     */
    public void setPort(final String port) {
        this.port = port;
    }

    /**
     * Returns the server name aka. the name of this configuration.
     *
     * @return The name of this configuration.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the name of this configuration.
     *
     * @param serverName
     *            The name of this configuration.
     */
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    /**
     * Returns the server url like : 192.168.1.1:8080.
     *
     * @return The server URL for this configuration.
     */
    public final String getServerURL() {
        return getServerIP() + ":" + getPort();
    }

    /**
     * Returns {@code: true} if SSL is enabled.
     *
     * @return {@code: true} if SSL is enabled.
     */
    public boolean isSSL() {
        return isSSL;
    }

    /**
     * Sets if this configuration is SSL enabled.
     *
     * @param ssl
     *            {@code: true} if SSL is enabled.
     */
    public void setSSL(final boolean ssl) {
        this.isSSL = ssl;
    }

    /**
     * Returns this configuration into a JSON string format.
     *
     * @return A string as a JSON object.
     */
    public String toJSON() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getServerIP());
            jsonObject.put("servername", getServerName());
            jsonObject.put("port", getPort());
            jsonObject.put("username", getUsername());
            jsonObject.put("password", getPassword());
            jsonObject.put("ssl", String.valueOf(isSSL()));

            return jsonObject.toString();
        } catch (final JSONException e) {
            Log.w("JSON Server Configuration", e);
            return "";
        }
    }

    /**
     * Retrieves the configuration from a given JSON object.
     *
     * @param json
     *            A server configuration.
     * @return A server configuration.
     */
    static ServerConfiguration fromJSON(final JSONObject json) {
        final ServerConfiguration srv = new ServerConfiguration("serverName");
        try {
            srv.setServerIP(json.getString("id"));
            srv.setServerName(json.getString("servername"));
            srv.setPort(json.getString("port"));
            srv.setUsername(json.getString("username"));
            srv.setPassword(json.getString("password"));
            srv.setSSL(Boolean.valueOf(json.getString("ssl")));

        } catch (final JSONException e) {
            Log.w("Server Configuration", e);
        }
        return srv;
    }

}
