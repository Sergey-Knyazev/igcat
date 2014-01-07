import os
import pandas as pd
from ggplot import *

lst = []
for test_d in filter(lambda x: x.startswith('test-'), os.listdir('.')):
    for fill in os.listdir(test_d):
        if not fill.startswith('train-'):
            continue
        num = int(fill.split('-')[1])
        with open(os.path.join(os.path.join(test_d, fill), 'diff.filtered'), "rt") as fr:
            fr_lines = fr.readlines()
            error_fr1_start = float(fr_lines[1].split()[8].strip()[:-1])
            error_fr1_end = float(fr_lines[2].split()[8].strip()[:-1])
            error_fr2_start = float(fr_lines[5].split()[8].strip()[:-1])
            error_fr2_end = float(fr_lines[6].split()[8].strip()[:-1])
            error_fr3_start = float(fr_lines[9].split()[8].strip()[:-1])
            error_fr3_end = float(fr_lines[10].split()[8].strip()[:-1])
            error_fr4_start = float(fr_lines[13].split()[8].strip()[:-1])
            error_fr4_end = float(fr_lines[14].split()[8].strip()[:-1])
            lst.append((num, error_fr1_start, "FR1_start", "unsigned"))
            lst.append((num, error_fr1_end, "FR1_end", "unsigned"))
            lst.append((num, error_fr2_start, "FR2_start", "unsigned"))
            lst.append((num, error_fr2_end, "FR2_end", "unsigned"))
            lst.append((num, error_fr3_start, "FR3_start", "unsigned"))
            lst.append((num, error_fr3_end, "FR3_end", "unsigned"))
            lst.append((num, error_fr4_start, "FR4_start", "unsigned"))
            lst.append((num, error_fr4_end, "FR4_end", "unsigned"))

            error_fr1_start = float(fr_lines[1].split()[6].strip()[1:])
            error_fr1_end = float(fr_lines[2].split()[6].strip()[1:])
            error_fr2_start = float(fr_lines[5].split()[6].strip()[1:])
            error_fr2_end = float(fr_lines[6].split()[6].strip()[1:])
            error_fr3_start = float(fr_lines[9].split()[6].strip()[1:])
            error_fr3_end = float(fr_lines[10].split()[6].strip()[1:])
            error_fr4_start = float(fr_lines[13].split()[6].strip()[1:])
            error_fr4_end = float(fr_lines[14].split()[6].strip()[1:])
            lst.append((num, error_fr1_start, "FR1_start", "signed"))
            lst.append((num, error_fr1_end, "FR1_end", "signed"))
            lst.append((num, error_fr2_start, "FR2_start", "signed"))
            lst.append((num, error_fr2_end, "FR2_end", "signed"))
            lst.append((num, error_fr3_start, "FR3_start", "signed"))
            lst.append((num, error_fr3_end, "FR3_end", "signed"))
            lst.append((num, error_fr4_start, "FR4_start", "signed"))
            lst.append((num, error_fr4_end, "FR4_end", "signed"))


with open("borderShift.csv", "wt") as fd:
    fd.write("size,error,region,error_type\n")
    for size, error, region, error_type in sorted(lst):
        fd.write("%d,%f,%s, %s\n" % (size, error, region, error_type))

#dataframe = pd.read_csv("mismatch.csv")

#gg = ggplot(aes(x='size', y='error'), data=dataframe) + \
#    geom_point(color='lightblue') + \
#    stat_smooth(span=.15, color='black', se=True) + \
#    ggtitle("Germline data") + \
#    xlab("References count") + \
#    ylab("Error rate")

#print(gg)
