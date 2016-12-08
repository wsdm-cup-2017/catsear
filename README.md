# catsear
The Catsear Triple Scorer


- [Project Setup](https://github.com/tira-io/catsear#project-setup)
 - [Eclipse](https://github.com/tira-io/catsear#eclipse)
 - [Maven](https://github.com/tira-io/catsear#eclipse)

- [Executing](https://github.com/tira-io/catsear#executing)

#Project Setup

Before start it is necessary to setup the Maven in your machine.
Please read the documentation on how to intall Maven:

http://maven.apache.org/install.html

##Eclipse:

###Step 1: Clone the project

```
git clone https://github.com/tira-io/catsear.git
```

###Step 2: Create the Eclipse project:
Execute the following command line at your cloned project.

```
mv eclipse:eclipse
```

##Maven

Before compile the project using Maven it is necessary to install localy the following libraries found at 
*libs* directory:

* kbox-v0.0.1-alpha2.jar
* dbtrends.scc-v0.1.3-beta.jar
* dbtrends.core-v0.1.3-beta.jar
* starpath.benchmark.jar
* starpath.indexbuilder.jar
* starpath.indexbuilder.xingu.jar

###Step 1: To setup the libraries above you should execute the following command line for each of them:

```
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
```

You can also copy paste the following commands:

```
mvn install:install-file -Dfile=/libs/kbox-v0.0.1-alpha2.jar -DgroupId=org.aksw.kbox -DartifactId=kbox -Dversion=v0.0.1-alpha2 -Dpackaging=jar
mvn install:install-file -Dfile=/libs/dbtrends.core-v0.1.3-beta.jar -DgroupId=org.aksw.dbtrends -DartifactId=dbtrends.core -Dversion=v0.1.3-beta -Dpackaging=jar
mvn install:install-file -Dfile=/libs/dbtrends.scc-v0.1.3-beta.jar -DgroupId=org.aksw.dbtrends -DartifactId=dbtrends.scc -Dversion=v0.1.3-beta -Dpackaging=jar
mvn install:install-file -Dfile=/libs/starpath.benchmark.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.benchmark -Dversion=v0.0.1-beta -Dpackaging=jar
mvn install:install-file -Dfile=/libs/starpath.indexbuilder.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.indexbuilder -Dversion=v0.0.1-beta -Dpackaging=jar
mvn install:install-file -Dfile=/libs/starpath.indexbuilder.xingu.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.indexbuilder.xingu -Dversion=v0.0.1-beta -Dpackaging=jar
```

###Step 2: Now, you must setup your pom file.
Now that the libraries are installed, you can comment/remove the *scope* and *systemPath* tags from your pom file for all the six files in the libs directory as follows:

```
	<dependency>
        <groupId>org.aksw.starpath</groupId>
        <artifactId>starpah.indexbuilder.xingu</artifactId>
        <version>v0.0.1-beta</version>
        <!--scope>system</scope>                                                            #remove/comment this line
        <systemPath>${project.basedir}/libs/starpath.indexbuilder.xingu.jar</systemPath-->  #remove/comment this line
	</dependency>

```

###Step 3: Execute MVN compile in a single jar

```
mvn clean compile assembly:single
```

###Step 4: Copy the generated jar at *\target* to the root directory and rename it to starpath.v0.0.1-beta.jar

###Step 5: Execute the script setup.sh
This script will compile graph-cross project.

#Executing

The project can be easily executed by the script catsear.sh as following:

```
./catsear.sh -i input -o output
```

