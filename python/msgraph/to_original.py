#!/usr/bin/env python
import sys

# alignment from MS
in1 = sys.argv[1]
# original file
in2 = sys.argv[2]

ms = dict()
with open(in1) as f:
    for line in f:
        line = line[:-1].split('\t')
        if "nationality" in in2:
            v = 3 * int(line[2])
            if v > 7:
                v = 7
        else:
            v = int(line[2])
        ms["{}\t{}".format(line[0], line[1])] = v
        
with open(in2) as f:
    for line in f:
        line = line[:-1].split('\t')
        key = "{}\t{}".format(line[0].lower(), line[1].lower())
        if key in ms:
            print "{}\t{}\t{}".format(line[0], line[1], ms[key])
        else:
            print "{}\t{}\t0".format(line[0], line[1])
