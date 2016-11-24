#
#   Regular expression: https://regex101.com/r/MVUYjW/1
#
#!/usr/bin/env python
import re
import redis
import sys

inp = sys.argv[1]

def scrape(regexStr, target):
    mo = re.findall(regexStr, target)
    res = list()
    for m in mo:
        res.append(m)
    return res

r = redis.StrictRedis(host='localhost', port=6379, db=0)

r2 = re.compile(r"\[([^\[]*)\|[^\[]*\]")

with open(inp) as f:
    # j = 0
    for line in f:
        # j += 1
        # if j % 100000 == 0:
        #     print "{}% completed".format(j / 331593)
        res = scrape(r"(?<=\[)[^\]]*(?=\])", line)
        print r2.sub(r"\1", line[:-1])
        # if j > 10:
        #     break
        for m in res:
            m = m.split("|")
            r.set(m[1], m[0])

# with open('corpus/wiki-sentences') as f:
#     with open('wiki/surface-forms.sql', 'w') as o:
#         j = 0
#         for line in f:
#             j += 1
#             if j % 100000 == 0:
#                 print "{}% completed".format(j / 331593)
#             # print line
#             res = scrape(r"(?<=\[)[^\]]*(?=\])", line)
#             # print res
#             for m in res:
#                 m = m.split("|")
#                 o.write("INSERT INTO surface_forms (`key`, `value`) VALUES (`{}`, `{}`);\n".format(m[1], m[0]))
