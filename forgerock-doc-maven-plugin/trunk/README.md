# ForgeRock Doc Build Maven Plugin

As of early March 2012 the configurations for ForgeRock core documentation
builds are maintained in sync by copy/paste. A better solution would centralize
configuration, leaving only the source files and a small amount of
configuration per core documentation project. A centralized configuration would
ensure that output formats are formatted uniformly.

With centralized configuration handled by a Maven plugin, the core
documentation-related project configuration takes at least two arguments:

*   `<projectName>`: the short name for the project such as OpenAM, OpenDJ,
    or OpenIDM
*   `<googleAnalyticsId>` to add Google Analytics JavaScript to the HTML
    output

The project then runs two plugin executions:

1.  A `build` goal in the `pre-site` phase to build and massage output
2.  A `layout` goal in the `site` phase to copy content under
    `site-doc`


		<build>
		 <plugins>
		  <plugin>
		   <groupId>org.forgerock.commons</groupId>
		   <artifactId>forgerock-doc-maven-plugin</artifactId>
		   <version>0.1.0-SNAPSHOT</version>
		   <inherited>false</inherited>
		   <configuration>
		    <projectName>OpenAM</projectName>
		    <googleAnalyticsId>UA-23412190-7</googleAnalyticsId>
		   </configuration>
		   <executions>
		    <execution>
		     <id>build-doc</id>
		     <phase>pre-site</phase>
		     <goals>
		      <goal>build</goal>
		     </goals>
		    </execution>
		    <execution>
		     <id>layout-doc</id>
		     <phase>site</phase>
		     <goals>
		      <goal>layout</goal>
		     </goals>
		    </execution>
		   </executions>
		  </plugin>
		 </plugins>
		</build>

To exclude formats from the build, you can use the optional
`<excludes>` configuration element. The following example
excludes all formats but HTML from the build.

     <excludes>
      <exclude>epub</exclude>
      <exclude>man</exclude>
      <exclude>pdf</exclude>
      <exclude>rtf</exclude>
     </excludes>

More to come...

* * *
This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2012 ForgeRock AS
