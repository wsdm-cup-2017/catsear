#!/bin/python
import sys
from urllib import quote

msgraph = sys.argv[1]
predicate = sys.argv[2]
output = sys.argv[3]

reload(sys)
sys.setdefaultencoding("utf-8")

maxima = dict()

with open(msgraph) as f_in:
    with open(output, 'w') as f_out:
        for line in f_in:
            line = line[:-2].split('\t')
            if line[1] not in maxima:
                maxima[line[1]] = int(line[2])
                weight = 7
            else:
                weight = 1 + int(round(6 * float(line[2]) / maxima[line[1]]))
            f_out.write("<http://catsear.wsdm-cup-2017.org/{}> <http://catsear.wsdm-cup-2017.org/property/{}/{}> <http://catsear.wsdm-cup-2017.org/{}> .\n".format(quote(line[1]), predicate, weight, quote(line[0])))
