# About the Archetype

This archetype includes generates basic files
for two types of ForgeRock core documentation projects.

To generate files for MyProject:

    mvn archetype:generate                                  \
      -DarchetypeRepository=http://maven.forgerock.org/repo \
      -DarchetypeGroupId=commons.forgerock.org              \
      -DarchetypeArtifactId=forgerock-doc-maven-archetype   \
      -DarchetypeVersion=3.0.0-SNAPSHOT                     \
      -DgroupId=projectGroupId                              \
      -DartifactId=projectArtifactId                        \
      -DgoogleAnalyticsId=UA-xxxxxxxx-x                     \
      -Dname=MyProject                                      \
      -Dversion=1.0.0-SNAPSHOT

In general, after creating your new documentation set by using this archetype,
go through each of the XML files to determine what you must change.
Your documentation set might not even have an Admin Guide or Reference,
though you can still use the files as templates.

For more information about ForgeRock core documentation,
see https://wikis.forgerock.org/confluence/display/devcom/Documentation .

# Preparing a Full Documentation Set

If you are creating an entirely new documentation set,
then remove the directory `src/main/docbkx/maintenance-release-notes/`
and everything it contains.

Every documentation project is different
and depends at least to some extent on the underlying software.
Consider the generated files as flexible templates, not stone tablets.

Release Notes, however, tend to have a standard layout for all projects.
You should not deviate far from the release notes templates
without very good reason.

# Preparing Maintenance Release Notes

If you are creating release notes for a maintenance release,
then remove the other directories instead,
and rename the directory `src/main/docbkx/maintenance-release-notes/`
to be called `src/main/docbkx/release-notes/`.
