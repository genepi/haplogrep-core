[![Java CI with Maven](https://github.com/seppinho/haplogrep-core/actions/workflows/run-tests.yml/badge.svg)](https://github.com/seppinho/haplogrep-core/actions/workflows/run-tests.yml)

# haplogrep-core
This repository includes the haplogrep core functionality used in the command line tool and the web server. 


Add the following repository to your pom.xml or gradle file:

```
<repositories>
	<repository>
		<id>jfrog-genepi-maven</id>
		<name>jfrog-genepi-maven</name>
		<url>https://genepi.jfrog.io/artifactory/maven/</url>
	</repository>
</repositories>
```

Add the following dependency to your pom.xml or gradle file:
  
```
<dependency>
	<groupId>genepi</groupId>
	<artifactId>haplogrep-core</artifactId>
	<version>2.7.4</version>
</dependency>
```
