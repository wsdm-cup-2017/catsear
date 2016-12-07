import sys
import os

reload(sys)
sys.setdefaultencoding("utf-8")

thr = float(sys.argv[1])
out = sys.argv[3] + "/" + os.path.basename(sys.argv[2])
inputs = sys.argv[4:]

#
# Nationality=0.81 & Professions=0.71
# if(starpath >= 2) return 5; else return bound(word2vec);
#
def strategy(diga, tom):
    if diga >= 2 or tom >= 5:
        return 5
    else:
        return 2

#
# Learned with weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8 -num-decimal-places 4
# in 10-fold cross-validation on the union of the two datasets (all-4.csv).
#   Correlation coefficient                  0.4758
#   Mean absolute error                      1.8397
#   Root mean squared error                  2.1677
#   Total Number of Instances              677
# 
# Combined with the TwoFive rule (sep=3.5) gives: Nationality=0.86 & Professions=0.76
#
def multi_strategy(v):
    diga, tom, tom_dem, andre = v[0], v[1], v[2], v[3]
    # LinearRegression
    ans = 0.5245 * diga + 0.4532 * tom + 0.3513 * tom_dem + 0.3824 * andre + (-0.5606)
    # TwoFive rule
    global thr
    if ans > thr:
        return 5
    else:
        return 2

def as_binary(v):
    diga, tom, tom_dem, andre = v[0], v[1], v[2], v[3]
    # SMO
    ans = (0.5715) * diga + (0.2853) * tom + (0.4) * tom_dem + (0.5707) * andre + (-3.57)
    global thr
    if ans > thr:
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
x2 = list()
with open(inp0) as f:
    for line in f:
        line = line[:-1].split('\t')
        ans = line[2]
        key = u"{}\t{}".format(line[0], line[1])
        x2.append(key)
        if key not in x:
            x[key] = []
        x[key].append(int(ans))

for inp in inputs:
    with open(inp) as f:
        for line in f:
            line = line[:-1].split('\t')
            ans = line[2]
            key = u"{}\t{}".format(line[0], line[1])
            if key not in x:
                x[key] = []
            x[key].append(int(ans))

with open(out, 'w') as f:
    for key in x2:
        line = key.split('\t')
        # get vector
        v = x[key]
        # ans = strategy(v[0], v[1])
        ans = multi_strategy(v)
        # ans = as_binary(v)
        f.write("{}\t{}\t{}\n".format(line[0], line[1], int(ans)))
    