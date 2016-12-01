#!/usr/bin/env python
from gensim.models import Word2Vec
import sys
import re

reload(sys)
sys.setdefaultencoding("utf-8")

# input file (named as <predicate>.<extension>)
inp = sys.argv[1]
# ref directory
ref = sys.argv[2]
# output file 1
out1 = sys.argv[3]
# output file 2
out2 = sys.argv[4]

if "profession" in inp:
    name = "/professions"
    # force lowercase
    flc = True
else:
    if "nationality" in inp:
        name = "/nationalities"
        flc = False
    else:
        sys.exit("Predicate not supported.")


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

def load_possible():
    x_list = list()
    with open(ref + name) as f:
        for line in f:
            x_list.append(line[:-1])
    return x_list

def process_kb():
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
    return x_dict

print "Loading other forms..."
dem = load_demonyms()

print "Loading possible objects..."
x_list = load_possible()

print "Processing KB {}...".format(inp)
x_dict = process_kb()

print "Loading model..."
model = Word2Vec.load_word2vec_format('instance-sentences.bin', binary=True)

# for each raw name...
with open(out1, 'w') as f1:
    with open(out2, 'w') as f2:
        for key in x_dict:
            name = unicode(key.replace(' ', '_'), "utf-8")
            # print name, nat
            if name not in model.vocab:
                # give 7 as we do not have any hint...
                for x_x in x_dict[key]:
                    f1.write("{}\t{}\t{}\n".format(key, x_x, "7"))
                    f2.write("{}\t{}\t{}\n".format(key, x_x, "7"))
            else:
                # get list of aim objects
                x_aim = x_dict[key]
                sim_max, sim_min = -1.0, 1.0
                sims = dict()
                dsim_max, dsim_min = -1.0, 1.0
                dsims = dict()
                # for each possible object...
                for x_x in x_list:
                    if flc:
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
                        # for each demonym...
                        for form in dem[x_x]:
                            if flc:
                                s = form.lower()
                            else:
                                s = form
                            dsim = model.n_similarity([name], s.split(' '))
                            # print name, form, str(sim)
                            # compute max/min similarity
                            if dsim > dsim_max:
                                dsim_max = dsim
                            if dsim < dsim_min:
                                dsim_min = dsim
                            # save similarity of the raw name and an aim object
                            if x_x in x_aim:
                                dsims[x_x] = dsim
                    else:
                        if x_x in x_aim:
                            f2.write("{}\t{}\t0\n".format(key, x_x))

                # normalize in [2,7]
                for x_x in sims:
                    sim_norm = int(2 + 5 * (sims[x_x] - sim_min) / (sim_max - sim_min))
                    f1.write("{}\t{}\t{}\n".format(key, x_x, sim_norm))
                # normalize in [2,7]
                for x_x in dsims:
                    dsim_norm = int(2 + 5 * (dsims[x_x] - dsim_min) / (dsim_max - dsim_min))
                    f2.write("{}\t{}\t{}\n".format(key, x_x, dsim_norm))
        