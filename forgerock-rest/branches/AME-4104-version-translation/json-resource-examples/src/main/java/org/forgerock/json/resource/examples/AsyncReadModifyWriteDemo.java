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
 * Copyright 2014 ForgeRock AS.
 */
package org.forgerock.json.resource.examples;

import static org.forgerock.json.resource.examples.DemoUtils.ctx;
import static org.forgerock.json.resource.examples.DemoUtils.getConnectionFactory;
import static org.forgerock.json.resource.examples.DemoUtils.log;
import static org.forgerock.json.resource.examples.DemoUtils.userAliceWithIdAndRev;

import java.util.concurrent.atomic.AtomicReference;

import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Requests;
import org.forgerock.json.resource.Resource;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.util.promise.AsyncFunction;
import org.forgerock.util.promise.Promise;
import org.forgerock.util.promise.SuccessHandler;

/**
 * An example client application which performs an asynchronous read, modify,
 * write update cycle for a resource in an in memory resource container. This
 * example does not require any command line arguments.
 */
public final class AsyncReadModifyWriteDemo {
    private AsyncReadModifyWriteDemo() {
        // No implementation.
    }

    /**
     * Main method.
     *
     * @param args
     *            The command line arguments: this example does not have any.
     * @throws ResourceException
     *             If an unexpected error occurred.
     */
    public static void main(final String[] args) throws ResourceException {
        final ConnectionFactory server = getConnectionFactory();
        final AtomicReference<Connection> connectionHolder = new AtomicReference<Connection>();

        // @formatter:off
        log("Opening connection");
        final Promise<Resource, ResourceException> promise = server.getConnectionAsync()
            .thenAsync(new AsyncFunction<Connection, Resource, ResourceException>() {
                /*
                 * Read resource.
                 */
                @Override
                public Promise<Resource, ResourceException> apply(final Connection connection)
                        throws ResourceException {
                    log("Reading resource");
                    connectionHolder.set(connection); // Save connection for later.
                    return connection.readAsync(ctx(), Requests.newReadRequest("users/1"));
                }
            }).thenAsync(new AsyncFunction<Resource, Resource, ResourceException>() {
                /*
                 * Update resource.
                 */
                @Override
                public Promise<Resource, ResourceException> apply(final Resource user)
                        throws ResourceException {
                    log("Resource read and has revision " + user.getRevision());
                    log("Updating resource");
                    return connectionHolder.get().updateAsync(ctx(),
                            Requests.newUpdateRequest("users/1", userAliceWithIdAndRev(1, 1)));
                }
            }).then(new SuccessHandler<Resource>() {
                /*
                 * Check updated resource.
                 */
                @Override
                public void handleResult(final Resource user) {
                    log("Updated resource now has revision " + user.getRevision());
                }
            }).thenAlways(new Runnable() {
                /*
                 * Close the connection.
                 */
                @Override
                public void run() {
                    log("Closing connection");
                    final Connection connection = connectionHolder.get();
                    if (connection != null) {
                        connection.close();
                    }
                }
            });
        // @formatter:on

        // Wait for update to complete/fail.
        promise.getOrThrowUninterruptibly();
    }

}
