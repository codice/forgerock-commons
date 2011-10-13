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
package org.forgerock.json.schema;

import org.codehaus.jackson.map.ObjectMapper;
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.schema.validator.Constants;
import org.forgerock.json.schema.validator.FailFastErrorHandler;
import org.forgerock.json.schema.validator.ObjectValidatorFactory;
import org.forgerock.json.schema.validator.exceptions.SchemaException;
import org.forgerock.json.schema.validator.validators.Validator;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.kohsuke.args4j.ExampleMode.ALL;
import static org.kohsuke.args4j.ExampleMode.REQUIRED;

/**
 * @author $author$
 * @version $Revision$ $Date$
 */
public class Main {


    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String ROOT_SCHEMA_ID = "http://www.forgerock.org/schema/";

    private final Map<URI, Validator> schemaCache = new HashMap<URI, Validator>();

    /*@Option(name = "-ra", usage = "recursively run something")
    private boolean recursive;*/

    @Option(name = "-f", aliases = {"-file", "-dir"}, required = true, usage = "file or folder contains the schema(s)")
    private File schemaFile = new File(".");

    @Option(name = "-i", aliases = {"-input"}, required = false, usage = "input from this file", metaVar = "INPUT")
    private File inputFile;

    @Option(name = "-s", aliases = {"-schema"}, usage = "name of the schema. Optional if the object has \"$schema\" property")
    private String schemaURI;

    @Option(name = "-r", aliases = {"-root"}, usage = "default schema base: " + ROOT_SCHEMA_ID)
    private String schemeBase = ROOT_SCHEMA_ID;

    @Option(name = "-v", aliases = {"-verbose"}, usage = "display all validation error not just the first")
    private boolean verbose;

    // receives other command line parameters than options
    @Argument
    private List<String> arguments = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        new Main().doMain(args);
    }

    public void doMain(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);

        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            // you can parse additional arguments if you want.
            // parser.parseArgument("more","args");

            // after parsing arguments, you should check
            // if enough arguments are given.
            /*if (arguments.isEmpty())
                throw new CmdLineException(parser, "No argument is given");*/

        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java Main [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: java Main" + parser.printExample(REQUIRED));
            System.err.println("  Example: java Main" + parser.printExample(ALL));

            return;
        }

        // set the base for all relative schema
        URI base = new URI(schemeBase);
        if (!base.isAbsolute()) {
            throw new IllegalArgumentException("-r (-root) must be an absolute URI");
        }

        // load all schema
        init(base);

        if (null == inputFile) {
            for (; ; ) {
                try {
                    validate(loadFromConsole());
                } catch (SchemaException e) {
                    printOutException(e);
                } catch (URISyntaxException e) {
                    System.out.append("Validation failed with exception: ").println(e.getMessage());
                }
            }
        } else {
            validate(loadFromFile());
        }
    }

    //Initialization

    private void init(URI base) throws IOException {
        System.out.append("Loading schemas from: ").append(schemaFile.getAbsolutePath()).append(" with base ").append(base.getPath()).println(" URI");
        if (schemaFile.isDirectory()) {
            validateDirectory(schemaFile);
            FileFilter filter = new FileFilter() {

                public boolean accept(File f) {
                    return (f.isDirectory()) || (f.getName().endsWith(".json"));
                }
            };

            for (File f : getFileListingNoSort(schemaFile, filter)) {
                //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6226081
                //org.apache.http.client.utils.URIUtils.resolve(URI,URI)
                URI relative = schemaFile.toURI().relativize(f.toURI());
                loadSchema(base.resolve(relative), f);
            }
        } else if (schemaFile.isFile()) {
            loadSchema(base, schemaFile);
        } else {
            System.exit(1);
        }
    }

    private void loadSchema(URI base, File schemaFile) throws IOException {
        JsonNode schemaMap = new JsonNode(mapper.readValue(new FileInputStream(schemaFile), Map.class));
        URI id = schemaMap.get(Constants.ID).required().asURI();
        Validator v = ObjectValidatorFactory.getTypeValidator(schemaMap.asMap());
        if (!id.isAbsolute()) {
            id = base.resolve(id);
        }
        schemaCache.put(id, v);
        System.out.append("Schema from ").append(schemaFile.getAbsolutePath()).append(" cached with id: ").println(id.toString());
    }

    /**
     * Recursively walk a directory tree and return a List of all
     * Files found; the List is sorted using File.compareTo().
     *
     * @param aStartingDir is a valid directory, which can be read.
     * @param filter
     * @return
     * @throws java.io.FileNotFoundException
     */
    private List<File> getFileListingNoSort(File aStartingDir, FileFilter filter) throws FileNotFoundException {
        List<File> result = new ArrayList<File>();
        List<File> filesDirs = Arrays.asList(aStartingDir.listFiles(filter));
        for (File file : filesDirs) {
            if (!file.isFile()) {
                //must be a directory
                //recursive call!
                List<File> deeperList = getFileListingNoSort(file, filter);
                result.addAll(deeperList);
            } else {
                result.add(file);
            }
        }
        return result;
    }

    /**
     * Directory is valid if it exists, does not represent a file, and can be read.
     * @param aDirectory
     * @throws java.io.FileNotFoundException
     */
    private void validateDirectory(File aDirectory) throws FileNotFoundException {
        if (aDirectory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!aDirectory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + aDirectory);
        }
        if (!aDirectory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + aDirectory);
        }
        if (!aDirectory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
        }
    }

    //Validation

    private void validate(JsonNode node) throws SchemaException, URISyntaxException {
        URI schemaId = node.get(Constants.SCHEMA).asURI();
        if (null == schemaId && isEmptyOrBlank(schemaURI)) {
            throw new IllegalArgumentException("-s (-schema) must be an URI");
        } else if (null == schemaId) {
            schemaId = new URI(schemaURI);
        }

        Validator validator = schemaCache.get(schemaId);
        if (null != validator) {
            validator.validate(node.getValue(), null, new FailFastErrorHandler());
            System.out.println("OK - Object is valid!");
        } else {
            System.out.append("Schema ").append(schemaId.toString()).println(" not found!");
        }
    }

    private JsonNode loadFromConsole() {
        System.out.println("Type 'exit' to exit");
        System.out.println("Type '.' to finish input");
        String input = null;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            input = System.console().readLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.exit(0);
            } else if (".".equals(input)) {
                break;
            } else {
                stringBuilder.append(input);
            }
        } while (true);
        return new JsonNode(mapper.readValue(stringBuilder.toString(), Object.class));
    }

    private JsonNode loadFromFile() {
        return new JsonNode(mapper.readValue(inputFile, Object.class));
    }


    private static boolean isEmptyOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void printOutException(SchemaException ex) {
        System.out.append("> > > > > ").append(ex.getClass().getSimpleName()).println(" < < < < <");
        if (null != ex.getNode())
            System.out.append("Path: ").println(ex.getNode().getPointer().toString());
        System.out.append("Message: ").println(ex.getMessage());
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }
}
