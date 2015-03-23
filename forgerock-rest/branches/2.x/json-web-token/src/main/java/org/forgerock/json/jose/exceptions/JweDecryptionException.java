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
 * Copyright 2013-2015 ForgeRock AS.
 */

package org.forgerock.json.jose.exceptions;

/**
 * Represents an exception for when decryption of the JWE fails. This class deliberately provides no information
 * about why decryption failed as such information leakage is a potential attack vector (e.g., padding oracle attacks).
 * The root cause of exceptions should be logged before this exception is thrown.
 *
 * @since 2.0.0
 */
public class JweDecryptionException extends JweException {

    /** Serializable class version number. */
    private static final long serialVersionUID = 2L;

    /**
     * Constructs a new JweDecryptionException with a stock error message.
     */
    public JweDecryptionException() {
        super("Decryption failed");
    }

    /**
     * Constructs a new JweDecryptionException with a stock error message. The provided error message will be ignored
     * to prevent information leakage from decryption failure.
     *
     * @param message The exception message.
     * @deprecated since 2.4.3 - use the no-argument constructor instead.
     */
    @Deprecated
    public JweDecryptionException(String message) {
        this();
    }

    /**
     * Constructs a new JweDecryptionException with a stock error message. The provided error message and cause will
     * be ignored to prevent information leakage from decryption failure.
     *
     * @param message The exception message.
     * @param throwable The underlying throwable.
     * @deprecated since 2.4.3 - use the no-argument constructor instead.
     */
    @Deprecated
    public JweDecryptionException(String message, Throwable throwable) {
        this();
    }

    /**
     * Constructs a new JweDecryptionException with a stock error message. The provided cause will be ignored to
     * prevent information leakage from decryption failure.
     *
     * @param throwable The underlying throwable.
     * @deprecated since 2.4.3 - use the no-argument constructor instead.
     */
    @Deprecated
    public JweDecryptionException(Throwable throwable) {
        this();
    }
}
