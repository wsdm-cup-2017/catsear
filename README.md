# 😺 Catsear

The **Catsear Triple Scorer** is a competitor in the Triple Scoring Challenge at WSDM 2017. It is based on a hybrid approach involving several modules. The answers from all modules are finally combined by a Linear Regression classifier.

- [Project Setup](https://github.com/tira-io/catsear#project-setup)
 - [Eclipse](https://github.com/tira-io/catsear#eclipse)
 - [Maven](https://github.com/tira-io/catsear#eclipse)
- [Executing](https://github.com/tira-io/catsear#executing)

## Project Setup

Before starting, it is necessary to setup the Maven in your machine.
Please read the documentation about how to install Maven: http://maven.apache.org/install.html

The Python-based module requires the `gensim` package:

```
pip install gensim
```

Then, clone the project:

```
git clone https://github.com/tira-io/catsear.git
```

## [Optional] Compile main module using Eclipse

Execute the following command line to create the Eclipse project.

```
mvn eclipse:eclipse
```

Eclipse should automatically import the external libraries from the `libs/` folder. Export a self-containing JAR as `starpath.v0.0.1-beta.jar` and save it in the main directory.

## Compile main module using Maven

Before compiling the project using Maven, it is necessary to install locally the following libraries found in the `libs/` directory:

* kbox-v0.0.1-alpha2.jar
* dbtrends.scc-v0.1.3-beta.jar
* dbtrends.core-v0.1.3-beta.jar
* starpath.benchmark.jar
* starpath.indexbuilder.jar
* starpath.indexbuilder.xingu.jar

### Step 1

To setup the libraries above, execute the following command line for each of them:

```
mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>
```

You can also copy-paste the following commands:

```
mvn install:install-file -Dfile=libs/kbox-v0.0.1-alpha2.jar -DgroupId=org.aksw.kbox -DartifactId=kbox.kibe -Dversion=v0.0.1-alpha2 -Dpackaging=jar
mvn install:install-file -Dfile=libs/dbtrends.core-v0.1.3-beta.jar -DgroupId=org.aksw.dbtrends -DartifactId=dbtrends.core -Dversion=v0.1.3-beta -Dpackaging=jar
mvn install:install-file -Dfile=libs/dbtrends.scc-v0.1.3-beta.jar -DgroupId=org.aksw.dbtrends -DartifactId=dbtrends.scc -Dversion=v0.1.3-beta -Dpackaging=jar
mvn install:install-file -Dfile=libs/starpath.benchmark.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.benchmark -Dversion=v0.0.1-beta -Dpackaging=jar
mvn install:install-file -Dfile=libs/starpath.indexbuilder.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.indexbuilder -Dversion=v0.0.1-beta -Dpackaging=jar
mvn install:install-file -Dfile=libs/starpath.indexbuilder.xingu.jar -DgroupId=org.aksw.starpath -DartifactId=starpath.indexbuilder.xingu -Dversion=v0.0.1-beta -Dpackaging=jar
```

### Step 2

Now, you must setup your pom file. Note that since the libraries are installed, you can comment/remove the `<scope>` and `<systemPath>` tags from your pom file for all the six files in the libs directory.

```
	<dependency>
        <groupId>org.aksw.starpath</groupId>
        <artifactId>starpah.indexbuilder.xingu</artifactId>
        <version>v0.0.1-beta</version>
        <!--scope>system</scope>                                                            #remove/comment this line
        <systemPath>${project.basedir}/libs/starpath.indexbuilder.xingu.jar</systemPath-->  #remove/comment this line
	</dependency>
```

### Step 3

Execute MVN compile in a single jar.

```
mvn clean compile assembly:single
```

### Step 4

Copy the generated jar at `target/` to the root directory and rename it to `starpath.v0.0.1-beta.jar`. This way, it can be found by the script that launches **Catsear**.

```
mv target/WSDMTriplescoreChallenge-0.0.1-beta-jar-with-dependencies.jar starpath.v0.0.1-beta.jar
```

## Compile other modules

Execute the script `setup.sh`. This script will fetch the needed files and compile the `graph-cross` module.

## Learning

The Python-based module is based on Word2Vec. The `wiki-sentences` file is first pre-processed with:

```
python python/skipgram/process_corpus.py /path/to/wiki-sentences > processed-sentences.txt
```

The embeddings are learned with:

```
python python/skipgram/embed.py processed-sentences.txt python/skipgram/instance-sentences.bin <size>
```

Set `10` for the size parameter.

The Linear Regression classifier can be found in file `python/super/wekastrategy.py`. The used formula was found by maximizing the accuracy on the training set using 10-fold cross-validation. To reproduce the experiments, download [Weka](http://www.cs.waikato.ac.nz/ml/weka/) and create the CSV file to use as training set with:

```
python python/super/wekamake.py /path/to/input/dataset predictions-1.txt predictions-2.txt predictions-3.txt predictions-4.txt predictions-5.txt training.csv
```

Note that outputs `predictions-*.txt` from all 5 modules must have been generated before.

## Prediction

The project can be easily executed by the script `catsear.sh` as following:

```
./catsear.sh -i /path/to/input/dataset [-i /path/to/another/input/dataset] -o /path/to/output/
```



## Citing this work

```
TBA
```
