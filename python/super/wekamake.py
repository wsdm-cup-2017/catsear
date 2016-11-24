import sys

reload(sys)
sys.setdefaultencoding("utf-8")

in1, in2, in3, out = sys.argv[1:5]

x = dict()
with open(in1) as f:
    for line in f:
        line = line[:-2].split('\t') # [-2] because of Windows...
        if line[2] == "":
            line[2] = "1"
        x[u"{}#{}".format(line[0], line[1])] = [line[2]]
exc = list()
with open(in2) as f:
    for line in f:
        line = line[:-1].split('\t')
        try:
            x[u"{}#{}".format(line[0], line[1])].append(line[2])
        except:
            exc.append(u"{}#{}".format(line[0], line[1]))
with open(in3) as f:
    for line in f:
        line = line[:-1].split('\t')
        try:
            x[u"{}#{}".format(line[0], line[1])].append(line[2])
        except:
            exc.append(u"{}#{}".format(line[0], line[1]))

print "{} records not found: {}".format(len(exc), exc)

with open(out, 'w') as f:
    f.write('ID1,ID2,Diga,Tom,Y\n')
    for key in sorted(x.iterkeys()):
        s = '"' + key.replace('#', '","') + '",'
        for v in x[key]:
            s += "{},".format(v)
        f.write("{}\n".format(s[:-1]))
