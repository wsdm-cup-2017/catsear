#!/bin/bash
#
# AUTHOR:
#	Tommaso Soru <tsoru@informatik.uni-leipzig.de>
# USAGE:
#	./catsear.sh -i <inputfile1> [-i <inputfile2> ...] -o <outputdir>
#
trData="/media/training-datasets/triple-scoring/wsdmcup17-triple-scoring-training-dataset-2016-09-16"
export PATH=$PATH:/home/catsear/anaconda2/bin/

while [[ $# -gt 1 ]]
  do
	key="$1"

	case $key in
	    -i)
		s=${#input[*]}
	    input[s]="$2"
	    shift # past argument
	    ;;
	    -o)
	    output="$2"
	    shift # past argument
	    ;;
	    *)
	    # unknown option
		echo "--- Unknown option: $1"
	    ;;
	esac
	shift # past argument or value
done
echo "--- Input(s) = ${input[*]}"
echo "--- Output = ${output}"

for inp in "${input[@]}"
  do
	
	# predictions from (1) Starpath
	java -Xmx2g -jar starpath.v0.0.1-beta.jar ${inp} predictions-1.txt
	
	# predictions from (2) Skip-gram relatedness and (3) Skip-gram demonyms
	cd python/skipgram
	python predict.py ${inp} ${trData} ../../predictions-2.txt ../../predictions-3.txt
	cd ../..
	
	# predictions from (4) Microsoft Concept Graph
	cd graph-cross
	java -Xmx4g -jar target/graph-cross-0.0.2-SNAPSHOT-jar-with-dependencies.jar cross ${inp} ../python/skipgram/demonyms/demonyms.clean.tsv > /dev/null
	cd ..
	python python/msgraph/to_original.py graph-cross/predictions-4.txt ${inp} > predictions-4.txt
	python python/msgraph/to_original.py graph-cross/predictions-5.txt ${inp} > predictions-5.txt
	
	# super-classifier
	# write output to ${output}/${inp}
	python python/super/wekastrategy.py 3.4 ${inp} ${output} predictions-1.txt predictions-2.txt predictions-3.txt predictions-4.txt predictions-5.txt
	
done
