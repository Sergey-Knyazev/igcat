__author__ = 'Sergey'

import os
import shlex
import subprocess
import multiprocessing
import argparse


def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)


def get_split(name):
    bname = os.path.basename(name)
    pos = bname.rfind('.')
    return bname[:pos], bname[pos + 1:]


def parse_args():
    parser = argparse.ArgumentParser(description='This script tests prediction quality of IgCAT annotation. ' +
                                     'It receives preannotated data as input, and uses one part of it for ' +
                                     'training and another one for testing. Partition is executed rundomly, ' +
                                     'so there is a posibility to gather statistics from different runs. ' + 
                                     'And if you need to run test many times, you can just an according option. ' +
                                     '')
    parser.add_argument('fasta', help='input fasta file')
    parser.add_argument('marking', help='input marking file')
    parser.add_argument('-n', '--max-threads', default=1, type=int, help='maximum count of threads')
    parser.add_argument('-o', '--out-dir', default='out_random_runs', help='output directory')
    parser.add_argument('-m', '--tests-count', default=1, help='count of tests', type=int)
    parser.add_argument('-c', '--train-seq-count', default=100, help='maximum count of train sequences')
    parser.add_argument('-t', '--test-seq-count', default=200, help='maximum count of test sequences')
    parser.add_argument('-p', '--mode', default='affine-semiglobal', help='alignment mode')
    parser.add_argument('-d', '--script-dir', default='..',help='scripts location')
    return parser.parse_args()


def executestar(args):
    return execute(*args)


def execute(args, i):
    out_directory = args.out_dir + "/test-" + str(i)
    ensure_dir(out_directory)
    fasta_pref, fasta_suff = get_split(os.path.basename(args.fasta))
    marking_pref, marking_suff = get_split(os.path.basename(args.marking))
    annotator_out_file_name = out_directory + '/' + marking_pref + '-prediction.' + args.mode + '.' + marking_suff
    annotator_ref_file_name = out_directory + '/' + marking_pref + '-test.' + marking_suff  

    out_file = open(annotator_out_file_name, 'w')
    p = subprocess.Popen('python test_data_generator.py ' + args.fasta + ' ' + args.marking +
                         ' --max-count ' + str(args.train_seq_count + args.test_seq_count) +
                         ' --out-dir ' + out_directory + ' --split-ratio ' + 
                         str(float(args.train_seq_count)/(args.train_seq_count + args.test_seq_count))
                         , shell=True)
    p = p.wait()
    p = subprocess.Popen(shlex.split('java -Dlogback.configurationFile=../../logback.xml '
                         + '-jar ../../ig-regions/target/ig-regions-1.0-SNAPSHOT.jar'
                         + ' -s ' + out_directory + '/' + fasta_pref + '-test.' + fasta_suff
                         + ' -r ' + out_directory + '/' + fasta_pref + '-train.' + fasta_suff
                         + ' -m ' + out_directory + '/' + marking_pref + '-train.' + marking_suff
                         + ' --amino ' + '--' + args.mode + ' --igblast-like'),
                         universal_newlines=True, stdout=out_file)
    p.wait()
    out_file.close()

    out_file = open(out_directory + '/' + 'result', 'w')    
    p = subprocess.Popen(shlex.split('python ' + args.script_dir + '/compare_marking.py '
                         + annotator_ref_file_name + ' ' + annotator_out_file_name),
                         universal_newlines=True, stdout=out_file)
    p.wait()
    out_file.close()

    out_file = open(out_directory + '/' + 'diff', 'w')
    p = subprocess.Popen(shlex.split('python ' + args.script_dir + '/diff_info.py '
                         + annotator_ref_file_name + ' ' + annotator_out_file_name),
                         universal_newlines=True, stdout=out_file)
    p.wait()
    out_file.close()


def main():
    args = parse_args()
    args.init_dir = os.getcwd()
    p = multiprocessing.Pool(args.max_threads)
    tasks = [(args, i) for i in range(args.tests_count)]
    p.map(executestar, tasks)


if __name__ == '__main__':
    main()
