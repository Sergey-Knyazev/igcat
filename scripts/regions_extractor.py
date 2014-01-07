from __future__ import division

import argparse
from Bio import SeqIO


def parse_marking_line(line):
    elems = line.split()[1:]
    bounds = [(int(elems[2 * i]) - 1, int(elems[2 * i + 1])) for i in range(7)]
    return bounds


def generate(fasta, marking, regions):
    with open(marking, "rt") as fd:
        marking_strings = fd.readlines()
    marking_dict = {line.split()[0]: parse_marking_line(line) for line in marking_strings}

    for rec in SeqIO.parse(fasta, "fasta"):
        if rec.id not in marking_dict:
            continue
        seq = str(rec.seq)

        data = ">%s\n%s\n\n" % (rec.id,
                                "$".join(seq[reg[0]:reg[1]]
                                         for i, reg in enumerate(marking_dict[rec.id])
                                         if regions[i]))
        yield data


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('fasta', help='input data [fasta]')
    parser.add_argument('marking', help='input marking [igblast]')
    parser.add_argument('--out', help='output [fasta]')
    parser.add_argument('--fr1', action="store_const", const=True, default=False, dest="fr1", help="add FR1")
    parser.add_argument('--cdr1', action="store_const", const=True, default=False, dest="cdr1", help="add CDR1")
    parser.add_argument('--fr2', action="store_const", const=True, default=False, dest="fr2", help="add FR2")
    parser.add_argument('--cdr2', action="store_const", const=True, default=False, dest="cdr2", help="add CDR2")
    parser.add_argument('--fr3', action="store_const", const=True, default=False, dest="fr3", help="add FR3")
    parser.add_argument('--cdr3', action="store_const", const=True, default=False, dest="cdr3", help="add CDR3")
    parser.add_argument('--fr4', action="store_const", const=True, default=False, dest="fr4", help="add FR4")
    return parser.parse_args()


def main():
    args = parse_args()
    regions = [int(reg) for reg in [args.fr1, args.cdr1, args.fr2, args.cdr2, args.fr3, args.cdr3, args.fr4]]
    gen = generate(args.fasta, args.marking, regions)

    if not args.out:
        for text in gen:
            print(text, end="")
    else:
        with open(args.out, "wt") as fd:
            for text in gen:
                fd.write(text)

if __name__ == "__main__":
    main()