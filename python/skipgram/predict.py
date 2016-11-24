#!/usr/bin/env python
from gensim.models import Word2Vec
import sys
import re

reload(sys)
sys.setdefaultencoding("utf-8")

def load_demonyms():
    dem = dict()
    with open('demonyms/demonyms.tsv') as f:
        for line in f:
            line = re.sub("[\(].*?[\)]", "", line)
            line = line[:-1].replace('"', '').replace(',', ';').replace('(', '').replace(')', '').split('\t')
            country = unicode(line[0], "utf-8")
            for l in line[1].split(';'):
                demonym = unicode(l.strip(), "utf-8")
                if demonym == "":
                    continue
                if country not in dem:
                    dem[country] = list()
                dem[country].append(demonym)
    return dem

# input file
inp = sys.argv[1]
# ref
ref = sys.argv[2]
# output file
out = sys.argv[3]
# force lowercase
flc = sys.argv[4]

print "Loading other forms..."
dem = load_demonyms()

print "Loading possible objects..."
x_list = list()
with open(ref) as f:
    for line in f:
        x_list.append(line[:-1])

print "Processing KB {}...".format(inp)
x_dict = dict()
# x_forms = dict()
with open(inp) as f:
    for line in f:
        line = line[:-1].split('\t')
        # create list of aim objects
        if line[0] not in x_dict:
            x_dict[line[0]] = list()
        x_dict[line[0]].append(line[1])
        # # check other forms
        # if line[1] in dem:
        #     # get list of forms
        #     forms = dem[line[1]]
        #     #
        #     for form in forms:
        #         x_forms[] = line[1]

print "Loading model..."
model = Word2Vec.load_word2vec_format('instance-sentences.bin', binary=True)

# for each raw name...
with open(out, 'w') as f:
    for key in x_dict:
        name = unicode(key.replace(' ', '_'), "utf-8")
        # print name, nat
        if name not in model.vocab:
            # give 7 as we do not have any hint...
            for x_x in x_dict[key]:
                f.write("{}\t{}\t{}\n".format(key, x_x, "7"))
        else:
            # get list of aim objects
            x_aim = x_dict[key]
            sim_max, sim_min = -1.0, 1.0
            sims = dict()
            # for each possible object...
            for x_x in x_list:
                if flc == "True":
                    s = x_x.lower()
                else:
                    s = x_x
                # get similarity
                sim = model.n_similarity([name], s.split(' '))
                # compute max/min similarity
                if sim > sim_max:
                    sim_max = sim
                if sim < sim_min:
                    sim_min = sim
                # save similarity of the raw name and an aim object
                if x_x in x_aim:
                    sims[x_x] = sim
                if x_x in dem:
                    forms = dem[x_x]
                    for form in forms:
                        sim = model.n_similarity([name], form.split(' '))
                        # print name, form, str(sim)
                        # compute max/min similarity
                        if sim > sim_max:
                            sim_max = sim
                        if sim < sim_min:
                            sim_min = sim
                        # save similarity of the raw name and an aim object
                        if x_x in x_aim:
                            sims[x_x] = sim
            
            # normalize in [2,7]
            for x_x in sims:
                sim_norm = int(2 + 5 * (sims[x_x] - sim_min) / (sim_max - sim_min))
                f.write("{}\t{}\t{}\n".format(key, x_x, sim_norm))
        