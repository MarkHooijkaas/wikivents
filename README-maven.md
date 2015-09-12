# Create distribution / executable jar

The maven-shade-plugin is used to create a 'fat' jar, a single jar containing all dependencies.
To perform a build execute the 'package' lifecycle goal, and the 'fat' jar will be created in target/wikivents-*.jar

    mvn package


# Run in-place

Run-in-place uses the maven-exec-plugin:

    mvn compile exec:java
    