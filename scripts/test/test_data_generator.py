from __future__ import division

import os
import argparse
from random import sample, seed
from Bio import SeqIO


def get_split(name):
    bname = os.path.basename(name)
    pos = bname.rfind('.')
    return bname[:pos], bname[pos + 1:]


def split(fasta, marking, out_dir, ratio, count):
    fpref, fsuff = get_split(fasta)
    mpref, msuff = get_split(marking)

    with open(marking, "rt") as fd:
        marking_strings = fd.readlines()
    marking_dict = {line.split('\t')[0]: line for line in marking_strings}

    train_mf = open(os.path.join(out_dir, "%s-train.%s" % (mpref, msuff)), "wt")
    train_ff = open(os.path.join(out_dir, "%s-train.%s" % (fpref, fsuff)), "wt")
    test_mf = open(os.path.join(out_dir, "%s-test.%s" % (mpref, msuff)), "wt")
    test_ff = open(os.path.join(out_dir, "%s-test.%s" % (fpref, fsuff)), "wt")

    seqs = SeqIO.to_dict(SeqIO.parse(fasta, "fasta")).values()

    seed()
    current = 0
    for rec in sample(seqs,count):
        if current < ratio*count:
            SeqIO.write(rec, train_ff, "fasta")
            train_mf.write("%s" % marking_dict[rec.id])
        else:
            SeqIO.write(rec, test_ff, "fasta")
            test_mf.write("%s" % marking_dict[rec.id])
        current += 1

    train_mf.close()
    train_ff.close()
    test_mf.close()
    test_ff.close()


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('fasta', help='input data [fasta]')
    parser.add_argument('marking', help='input marking [igblast]')
    parser.add_argument('--out-dir', help='output directory')
    parser.add_argument('--max-count', dest="count", type=int, help="maximum count of processed sequences")
    parser.add_argument('--split-ratio', dest="ratio", default=0.3, type=float,
                        help='share of training data in resulting split [default=0.3]')
    return parser.parse_args()


def main():
    args = parse_args()
    out_dir = args.out_dir if args.out_dir else os.path.dirname(args.fasta)
    split(args.fasta, args.marking, out_dir, args.ratio, args.count)


if __name__ == '__main__':
    main()
