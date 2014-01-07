__author__ = 'pavel'

import argparse
from itertools import chain


def filter_marking(marking):
    marking = open(marking, "rt")
    lines = []

    for line in marking:
        name = line.strip().split('\t')[0]
        data = [int(k) for k in line.strip().split('\t')[1:]]
        borders = [c for i, c in enumerate(zip(data, data[1:])) if i % 2]
        for i, border in enumerate(borders):
            if border[1] - border[0] > 1:
                # i % 2 == 0 ? F->C : C->F
                if i % 2 == 0:
                    borders[i] = (border[0], border[0] + 1)
                else:
                    borders[i] = (border[1] - 1, border[1])
        data = data[:1] + list(chain(*borders)) + data[13:]
        lines.append("%s\t%s\n" % (name, "\t".join(map(str, data))))

    return lines


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('marking', help='input marking [igblast]')
    parser.add_argument('--out', help='output [igblast]')
    return parser.parse_args()


def main():
    args = parse_args()
    lines = filter_marking(args.marking)
    if not args.out:
        for line in lines:
            print(line, end='')
    else:
        with open(args.out, "wt") as fd:
            for line in lines:
                fd.write(line)

if __name__ == "__main__":
    main()
