# toolprovider-maven-plugin
Maven plugin using Java 9 ToolProvider SPI to execute tasks.

The plugin provides two goals:

* exec - which is a general interface to execute any tool providing interface according to java.util.spi.ToolProvider.
* jar - which is a replacement for maven-jar-plugin that uses jar ToolProvider.

Standard maven-jar-plugin does not (yet) handle Java (JPMS) modules fully. For example it does not generate ModulePackages constant in compiled module-info.

To replace standard maven-jar-plugin in your project set packaging in the pom file to *toolsjar* and configure toolprovider-maven-plugin as build extension:

~~~~
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
  <groupId>...</groupId>
	<artifactId>...</artifactId>
  <version>...</version>
	<packaging>toolsjar</packaging>

	<build>
		<plugins>
			<plugin>
				<groupId>org.kleczek</groupId>
				<artifactId>toolprovider-maven-plugin</artifactId>
				<version>1.0.0-SNAPSHOT</version>
				<extensions>true</extensions>
			</plugin>
		</plugins>
	</build>
</project>
~~~~
