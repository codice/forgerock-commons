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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.crypto;


import org.codehaus.jackson.map.ObjectMapper;
import org.forgerock.json.crypto.simple.SimpleDecryptor;
import org.forgerock.json.crypto.simple.SimpleEncryptor;
import org.forgerock.json.crypto.simple.SimpleKeyStoreSelector;
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonPointer;
import org.forgerock.json.fluent.JsonTransformer;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.util.ArrayList;

/**
 * @author $author$
 * @version $Revision$ $Date$
 */
public class Main {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length % 2 != 1) {
            usage();
            return;
        }

        String cmd = args[0];
        if ("-encrypt".equals(cmd) || "-decrypt".equals(cmd)) {
            //TODO process arguments somewhere here.
        } else {
            usage();
            return;
        }
        String alias = null;
        String cipher = "AES/CBC/PKCS5Padding";
        String srcjson = null;
        String destjson = null;
        String keypass = null;
        String keystore = null;
        String storepass = null;
        String storetype = KeyStore.getDefaultType();
        String providername = null;
        String providerclass = null;
        String[] providerarg = null;
        String providerpath = null;


        for (int i = 1; i < args.length; i += 2) {
            String name = args[i];
            String value = args[i + 1];

            if (value.startsWith("-")) {
                usage();
                return;
            }

            //TODO Implement a better argument parser. -providerarg has multiple values
            if (name.equalsIgnoreCase("-alias")) {
                alias = value;
            } else if (name.equalsIgnoreCase("-cipher")) {
                cipher = value;
            } else if (name.equalsIgnoreCase("-srcjson")) {
                srcjson = value;
            } else if (name.equalsIgnoreCase("-destjson")) {
                destjson = value;
            } else if (name.equalsIgnoreCase("-keypass")) {
                keypass = value;
            } else if (name.equalsIgnoreCase("-keystore")) {
                keystore = value;
            } else if (name.equalsIgnoreCase("-storepass")) {
                storepass = value;
            } else if (name.equalsIgnoreCase("-storetype")) {
                storetype = value;
            } else if (name.equalsIgnoreCase("-providername")) {
                providername = value;
            } else if (name.equalsIgnoreCase("-providerclass")) {
                providerclass = value;
            } else if (name.equalsIgnoreCase("-providerarg")) {
                providerarg = new String[]{value};
            } else if (name.equalsIgnoreCase("-providerpath")) {
                providerpath = value;
            } else {
                usage();
                return;
            }
        }


        if ("-encrypt".equals(cmd)) {
            Key key = getSimpleKeySelector(keystore,
                    storetype, storepass, providername).select(alias);
            if (key == null) {
                throw new JsonCryptoException("key not found: " + alias);
            }
            JsonTransformer encryptionTransformer = new JsonCryptoTransformer(new SimpleEncryptor(cipher, key, alias));
            JsonNode node = getSourceNode(srcjson, true);
            encryptionTransformer.transform(node);
            setDestinationNode(destjson, node);
        } else if ("-decrypt".equals(cmd)) {
            final ArrayList<JsonTransformer> decryptionTransformers = new ArrayList<JsonTransformer>(1);
            decryptionTransformers.add(new JsonCryptoTransformer(new SimpleDecryptor(getSimpleKeySelector(keystore,
                    storetype, storepass, providername))));
            JsonNode node = getSourceNode(srcjson, true);
            setDestinationNode(destjson, new JsonNode(node.getValue(), new JsonPointer(), decryptionTransformers));
        } else {
            usage();
        }


    }

    private static SimpleKeyStoreSelector getSimpleKeySelector(String keystore, String type, String password, String provider) throws Exception {
        KeyStore ks = (provider == null ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider));
        File ksFile = new File(keystore);
        if (ksFile.exists()) {
            ks.load(new FileInputStream(ksFile), password == null ? null : password.toCharArray());
        } else {
            throw new FileNotFoundException("KeyStore file not found at: " + ksFile.getAbsolutePath());
        }
        return new SimpleKeyStoreSelector(ks, password);
    }

    private static JsonNode getSourceNode(String source, boolean file) throws IOException {
        JsonNode src = null;
        if (file) {
            File srcFile = new File(source);
            if (srcFile.exists()) {
                src = new JsonNode(mapper.readValue(srcFile, Object.class));
            } else {
                throw new FileNotFoundException("JsonSource file not found at: " + srcFile.getAbsolutePath());
            }
        } else {
            src = new JsonNode(mapper.readValue(source, Object.class));
        }
        return src;
    }

    private static void setDestinationNode(String destination, JsonNode value) throws IOException {
        if (null == destination) {
            mapper.writeValue(System.out, value.getValue());
        } else {
            File dest = new File(destination);
            dest.getParentFile().mkdirs();
            mapper.writeValue(dest, value.getValue());
        }
    }

    private static void usage() {
        System.out.println("-encrypt 	[-alias <alias>] [-cipher <cipher>]");
        System.out.println("		[-srcjson <srcjson>] [-destjson <destjson>]");
        System.out.println(" 		[-keypass <keypass>] [-keystore <keystore>]");
        System.out.println(" 		[-storepass <storepass>] [-storetype <storetype>]");
        System.out.println("		[-providername <name>]");
        System.out.println("		[-providerclass <provider_class_name> [-providerarg <arg>]] ...");
        System.out.println("		[-providerpath <pathlist>]");

        System.out.println("-decrypt 	[-srcjson <srcjson>] [-destjson <destjson>]");
        System.out.println("		[-keypass <keypass>] [-keystore <keystore>]");
        System.out.println("		[-storepass <storepass>] [-storetype <storetype>]");
        System.out.println("		[-providername <name>]");
        System.out.println("		[-providerclass <provider_class_name> [-providerarg <arg>]] ...");
        System.out.println("		[-providerpath <pathlist>]");
    }
}

