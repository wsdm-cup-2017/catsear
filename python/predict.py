#!/usr/bin/env python
from gensim.models import Word2Vec
import sys

reload(sys)
sys.setdefaultencoding("utf-8")

# input file
inp = sys.argv[1]
# ref
ref = sys.argv[2]
# output file
out = sys.argv[3]
# force lowercase
flc = sys.argv[4]

print "Loading model..."
model = Word2Vec.load_word2vec_format('instance-sentences.bin', binary=True)

print "Loading possible objects..."
nat_list = list()
with open(ref) as f:
    for line in f:
        nat_list.append(line[:-1])
# prf_list = list()
# with open('reference/professions') as f:
#     for line in f:
#         prf_list.append(line[:-1])

print "Processing KB {}...".format(inp)
nat_dict = dict()
with open(inp) as f:
    for line in f:
        line = line[:-1].split('\t')
        if line[0] not in nat_dict:
            nat_dict[line[0]] = list()
        nat_dict[line[0]].append(line[1])

# for each raw name...
with open(out, 'w') as f:
    for key in nat_dict:
        name = unicode(key.replace(' ', '_'), "utf-8")
        # print name, nat
        if name not in model.vocab:
            # give 7 as we do not have any hint...
            for nat_x in nat_dict[key]:
                f.write("{}\t{}\t{}\n".format(key, nat_x, "7"))
        else:
            # get list of aim nationalities
            nat_aim = nat_dict[key]
            sim_max, sim_min = 0.0, 1.0
            sims = dict()
            # for each nationality...
            for nat_x in nat_list:
                if flc == "True":
                    s = nat_x.lower()
                else:
                    s = nat_x
                # get similarity
                sim = model.n_similarity([name], s.split(' '))
                if sim > sim_max:
                    sim_max = sim
                if sim < sim_min:
                    sim_min = sim
                if nat_x in nat_aim:
                    sims[nat_x] = sim
            # print name, sims
            # normalize in [1,7]
            for nat_x in sims:
                sim_norm = 1 + 6 * (sims[nat_x] - sim_min) / (sim_max - sim_min)
                f.write("{}\t{}\t{}\n".format(key, nat_x, int(sim_norm)))
        