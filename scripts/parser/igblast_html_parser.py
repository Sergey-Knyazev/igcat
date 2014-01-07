__author__ = 'pavel'

import argparse


def parse(filename):
    query_pattern = "<b>Query=</b> "
    region_pattern = "<tr><td> {}"
    region_split = " </td><td> "
    current = None
    curreg = []
    result = []
    with open(filename, "rt") as fd:
        for line in fd:
            if line.startswith(query_pattern):
                if current:
                    marking_line = [current]
                    for reg in curreg:
                        marking_line.append("%d\t%d" % reg)
                    for _ in range(7 - len(curreg)):
                        marking_line.append("0\t0")
                    result.append("\t".join(marking_line))
                current = line[len(query_pattern):].strip()
                curreg = []
            if not current:
                continue
            for i, region in enumerate(("%s%d" % (s, j)
                                       for j in range(1, 4)
                                       for s in ["FR", "CDR"]
                                       if not (s == "CDR" and j == 3))):
                pattern = region_pattern.format(region)
                if line.startswith(pattern):
                    regs = line[len(pattern):].split(region_split)
                    curreg.append((int(regs[1]), int(regs[2])))
                    break

    return result


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('html', help='input data [html]')
    parser.add_argument('marking', help='output marking [igblast]')
    return parser.parse_args()


def main():
    args = parse_args()
    marking_lines = parse(args.html)
    with open(args.marking, "wt") as fd:
        for line in marking_lines:
            fd.write("%s\n" % line)


if __name__ == '__main__':
    main()