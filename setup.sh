#!/bin/bash

# install KBox (>140 GB disk needed)
echo -e 'Test\tTest\t0' > foo.train
java -Xmx2g -jar starpath.v0.0.1-beta.jar foo.train foo.txt
rm foo.train foo.txt
# download model
wget http://tsoru.aksw.org/public/wsdmcup/instance-sentences.bin -O python/skipgram/instance-sentences.bin
# download and extract MS graph
wget http://tsoru.aksw.org/public/wsdmcup/Microsoft-Concept-Graph.zip
unzip Microsoft-Concept-Graph.zip -d graph-cross
# compile graph-cross
cd graph-cross
mvn compile package
cd ..
