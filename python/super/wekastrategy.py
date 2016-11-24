import sys

reload(sys)
sys.setdefaultencoding("utf-8")

in1, in2, in3, out = sys.argv[1:5]

#
# Nationality=0.81 & Professions=0.71
# if(starpath >= 2) return 5; else return bound(word2vec);
#
def strategy(diga, tom):
    if diga >= 5 or tom >= 5:
        return 5
    else:
        return 2

#
# Learned with weka.classifiers.trees.J48 -C 0.25 -M 2
# Gives 0.74 on Professions. However, it seems overfitting (0.79 on Nationality).
#
def decide(diga, tom):
    if tom == 1:
        if diga == 0:
            return 5
        else:
            return 2
    if tom == 2:
        return 2
    if tom == 3:
        return 2
    if tom == 4:
        if diga == 0:
            return 2
        else:
            return 5
    else:
        return 5

#
# Learned by Weka on Professions
# weka.classifiers.trees.RandomTree -K 0 -M 1.0 -V 0.001 -S 1
# Definitely overfitting.
#
def from_weka(tom):
    if tom < 4.5:
        if tom < 1.5:
            value = 4.5
        else:
            if tom < 2.5:
                value = 1.5
            else:
                if tom < 3.5:
                    value = 2.6
                else:
                    value = 3.19
    else:
        if tom < 6.5:
            if tom < 5.5:
                value = 3.82
            else:
                value = 4.02
        else:
            value = 5.05
    return int(round(value))

x = dict()
with open(in1) as f:
    for line in f:
        line = line[:-2].split('\t') # [-2] because of Windows...
        if int(line[2]) >= 2:
            ans = "5"
        else:
            ans = line[2]
        x[u"{}#{}".format(line[0], line[1])] = [ans]
exc = list()
with open(in2) as f:
    for line in f:
        line = line[:-1].split('\t')
        try:
            x[u"{}#{}".format(line[0], line[1])].append(line[2])
            # ans = from_weka(int(line[2]))
            # ans = bound(int(line[2]))
            # ans = decide(int(x[u"{}#{}".format(line[0], line[1])][0]), int(line[2]))
            ans = strategy(int(x[u"{}#{}".format(line[0], line[1])][0]), int(line[2]))
            print "{}\t{}\t{}".format(line[0], line[1], ans)
        except:
            exc.append(u"{}#{}".format(line[0], line[1]))
with open(in3) as f:
    for line in f:
        line = line[:-1].split('\t')
        try:
            x[u"{}#{}".format(line[0], line[1])].append(line[2])
        except:
            exc.append(u"{}#{}".format(line[0], line[1]))

# print "{} records not found: {}".format(len(exc), exc)

with open(out, 'w') as f:
    f.write('ID1,ID2,Diga,Tom,Y\n')
    for key in sorted(x.iterkeys()):
        s = '"' + key.replace('#', '","') + '",'
        for v in x[key]:
            s += "{},".format(v)
        f.write("{}\n".format(s[:-1]))
