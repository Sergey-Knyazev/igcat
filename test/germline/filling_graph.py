import os
import pandas as pd
from ggplot import *

lst = []
for test_d in filter(lambda x: x.startswith('test-'), os.listdir('.')):
    for fill in os.listdir(test_d):
        if not fill.startswith('train-'):
            continue
        num = int(fill.split('-')[1])
        with open(os.path.join(os.path.join(test_d, fill), 'result.filtered'), "rt") as fd:
            #fd.readline()
            error = float(fd.readline().split(':')[1].strip())
            lst.append((num, error))

with open("match.csv", "wt") as fd:
    fd.write("size,error\n")
    for size, error in sorted(lst):
        fd.write("%d, %f\n" % (size, error))

dataframe = pd.read_csv("test.csv")

gg = ggplot(aes(x='size', y='error'), data=dataframe) + \
    geom_point(color='lightblue') + \
    stat_smooth(span=.15, color='black', se=True) + \
    ggtitle("Germline data") + \
    xlab("References count") + \
    ylab("Error rate")

print(gg)
