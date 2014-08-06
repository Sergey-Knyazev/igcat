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
    parser.add_argument('fasta', help='input fasta file')
    parser.add_argument('marking', help='input marking file')
    parser.add_argument('-f', '--test-fasta',
                       help='input fasta file with test data, should be sinchronized with test-marking file.')
    parser.add_argument('-k', '--test-marking',
                       help='input marking file with test data, should be sinchronized with test-fasta file.')
    parser.add_argument('-n', '--max-threads', default=1, type=int,
                       help='maximum count of threads')
    parser.add_argument('-o', '--out-dir', default='out_random_filling', help='output directory')
    parser.add_argument('-m', '--tests-count', default=7, help='count of tests', type=int)
    parser.add_argument('-c', '--train-seq-count', default=1000,
                       help='maximum count of sequences in container', type=int)
    parser.add_argument('-a', '--test-seq-count', default=1000,
                       help='count of sequences for annotation', type=int)
    parser.add_argument('-t', '--seq-step', default=100, help='count of steps', type=int)
    parser.add_argument('-p', '--mode', default='affine-semiglobal',
                       help='alignment mode')
    parser.add_argument('-d', '--script-dir', default='..', help='scripts location')
    return parser.parse_args()


def execute(args, i):
    out_directory = args.out_dir + '/test-' + str(i)
    ensure_dir(out_directory)
    command = 'python ' + args.script_dir + '/test/test_filling.py ' \
              + args.fasta + ' ' \
              + args.marking \
              + ' -n ' + str(args.max_threads) \
              + ' -o ' + out_directory \
              + ' -m ' + str(int(args.train_seq_count/args.seq_step)) \
              + ' -c ' + str(args.train_seq_count) \
              + ' -p ' + args.mode \
              + ' -d ' + args.script_dir
    if args.test_fasta and args.test_marking:
        command += ' -f ' + args.test_fasta \
                + ' -k ' + args.test_marking
    else:
        command += ' -a ' + str(args.test_seq_count)
    p = subprocess.Popen(command, shell=True)
    p.wait()


def main():
    args = parse_args()

    tasks = [(args, i) for i in range(args.tests_count)]
    for i in tasks:
        print 'running test # ' + str(i[1] + 1) + ' of ' + str(args.tests_count)
        execute(*i)

if __name__ == '__main__':
    main()
