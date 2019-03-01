# haplogrep-core
This repository includes the haplogrep core functionality used in the command line tool and the web server. 

It's build via the maven-site plugin and used in the pom.xml:

	<repositories>
		<repository>
			<id>genepi</id>
			<url>https://raw.github.com/genepi/maven-repository/mvn-repo/</url>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
			</snapshots>
		</repository>
	</repositories>
  

		<dependency>
			<groupId>genepi</groupId>
			<artifactId>haplogrep-core</artifactId>
			<version>2.0.10</version>
		</dependency>
