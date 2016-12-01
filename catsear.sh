#!/bin/bash
#
# AUTHOR:
#	Tommaso Soru <tsoru@informatik.uni-leipzig.de>
# USAGE:
#	./catsear.sh -i <inputfile1> [-i <inputfile2> ...] -o <outputdir>
#
trData="/media/training-datasets/triple-scoring/wsdmcup17-triple-scoring-training-dataset-2016-09-16"

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
	cd src
	java -Xmx32g Cross ${inp} ../data-concept/data-concept-instance-relations.txt ../python/skipgram/demonyms/demonyms.clean.tsv
	cd ..
	
	# super-classifier
	# write output to ${output}/${inp}
	python python/super/wekastrategy.py ${inp} predictions-1.txt predictions-2.txt predictions-3.txt predictions-4.txt > ${output}/${inp}	
	
done