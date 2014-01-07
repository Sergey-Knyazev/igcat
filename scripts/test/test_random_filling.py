from __future__ import division
import os
import shlex
import subprocess
import multiprocessing
import argparse

__author__ = 'Sergey'


def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('-n', '--max-threads', default=1, type=int,
                       help='maximum count of threads')
    parser.add_argument('-o', '--out-dir', help='output directory')
    parser.add_argument('-m', '--tests-count', help='count of tests', type=int)
    parser.add_argument('-c', '--train-seq-count',
                       help='maximum count of sequences in container', type=int)
    parser.add_argument('-a', '--test-seq-count',
                       help='count of sequences for annotation', type=int)
    parser.add_argument('-t', '--seq-step', help='count of steps', type=int)
    parser.add_argument('-p', '--mode', default='affine-semiglobal',
                       help='alignment mode')
    parser.add_argument('-d', '--script-dir', help='scripts location')
    parser.add_argument('-s', '--fasta', help='input fasta file')
    parser.add_argument('-r', '--kabat', help='input kabat file')
    parser.add_argument('-f', '--test-fasta',
                       help='input fasta file with test data.' 
                       + 'use it with --test-kabat')
    parser.add_argument('-k', '--test-kabat',
                       help='input kabat file with test data.' 
                       + 'use it with --test-fasta')
    return parser.parse_args()


def execute(args, i):
    out_directory = args.out_dir + '/test-' + str(i)
    ensure_dir(out_directory)
    command = 'python ' + args.script_dir + '/test_filling.py' \
              + ' -n ' + str(args.max_threads) \
              + ' -o ' + out_directory \
              + ' -m ' + str(int(args.train_seq_count/args.seq_step)) \
              + ' -c ' + str(args.train_seq_count) \
              + ' -p ' + args.mode \
              + ' -d ' + args.script_dir \
              + ' -s ' + args.fasta \
              + ' -r ' + args.kabat
    if args.test_fasta and args.test_kabat:
        command += ' -f ' + args.test_fasta \
                + ' -k ' + args.test_kabat
    else:
        command += ' -a ' + str(args.test_seq_count)
    p = subprocess.Popen(shlex.split(command)
                         , universal_newlines=True)
    p.wait()


def executestar(args):
    return execute(*args)


def main():
    args = parse_args()

    tasks = [(args, i) for i in range(args.tests_count)]
    for i in tasks:
        print 'running test # ' + str(i[1] + 1) + ' of ' + str(args.tests_count)
        executestar(i)

if __name__ == '__main__':
    main()
