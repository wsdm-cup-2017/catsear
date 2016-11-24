#!/usr/bin/env python
# -*- coding: utf-8 -*-
import logging
import os.path
import sys
import multiprocessing
 
from gensim.corpora import  WikiCorpus
from gensim.models import Word2Vec
from gensim.models.word2vec import LineSentence

def embed(inp, outp, size):
    
    model = Word2Vec(LineSentence(inp), size=int(size), window=10, min_count=3, workers=multiprocessing.cpu_count())

    # trim unneeded model memory = use (much) less RAM
    model.init_sims(replace=True)

    model.save_word2vec_format(outp, binary=True)

if __name__ == '__main__':
    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)

    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s')
    logging.root.setLevel(level=logging.INFO)
    logger.info("running %s" % ' '.join(sys.argv))

    # check and process input arguments

    if len(sys.argv) < 3:
        print globals()['__doc__'] % locals()
        sys.exit(1)
    inp, outp, size = sys.argv[1:4]

    embed(inp, outp, size)
