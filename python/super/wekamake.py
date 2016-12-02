import sys

reload(sys)
sys.setdefaultencoding("utf-8")

in1 = sys.argv[1]
inputs = sys.argv[2:]

x = dict()
with open(in1) as f:
    for line in f:
        line = line[:-2].split('\t') # [-2] because of Windows...
        if line[2] == "":
            line[2] = "1"
        x[u"{}#{}".format(line[0], line[1])] = [line[2]]
exc = list()
for inp in inputs[:-1]:
    with open(inp) as f:
        for line in f:
            line = line[:-1].split('\t')
            try:
                x[u"{}#{}".format(line[0], line[1])].append(int(line[2]))
            except:
                exc.append(u"{}#{}".format(line[0], line[1]))

print "{} records not found: {}".format(len(exc), exc)

with open(inputs[-1], 'w') as f:
    f.write('id1,id2,diga,tom,tom_dem,andre,andre_dem,y\n')
    for key in sorted(x.iterkeys()):
        s = '"' + key.replace('#', '","') + '",'
        for v in x[key]:
            s += "{},".format(v)
        f.write("{}\n".format(s[:-1]))
