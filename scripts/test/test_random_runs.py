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
    parser = argparse.ArgumentParser()
    parser.add_argument('-n', '--max-threads', default=1, type=int, help='maximum count of threads')
    parser.add_argument('-o', '--out-dir', help='output directory')
    parser.add_argument('-m', '--tests-count', help='count of tests', type=int)
    parser.add_argument('-c', '--seq-count', help='maximum count of processed sequences')
    parser.add_argument('-p', '--mode', default='affine-semiglobal', help='alignment mode')
    parser.add_argument('-d', '--script-dir', help='scripts location')
    parser.add_argument('-s', '--fasta', help='input fasta file')
    parser.add_argument('-r', '--kabat', help='input kabat file')
    return parser.parse_args()


def executestar(args):
    return execute(*args)


def execute(args, i):
    out_directory = args.init_dir + '/' + args.out_dir + "/test-" + str(i)
    ensure_dir(out_directory)
    fasta_pref, fasta_suff = get_split(os.path.basename(args.fasta))
    kabat_pref, kabat_suff = get_split(os.path.basename(args.kabat))
    annotator_out_file_name = out_directory + '/' + kabat_pref + '-prediction.' + args.mode + '.' + kabat_suff
    annotator_ref_file_name = out_directory + '/' + kabat_pref + '-test.' + kabat_suff  

    out_file = open(annotator_out_file_name, 'w')
    p = subprocess.Popen(shlex.split('python test_data_generator.py ' + args.init_dir + '/' + args.fasta + ' '
                     + args.init_dir + '/' + args.kabat + ' --max-count ' + args.seq_count + ' --out-dir '
                     + out_directory), cwd=args.script_dir)
    p = p.wait()
    p = subprocess.Popen(shlex.split('java -jar ../ig-regions/target/ig-regions-1.0-SNAPSHOT.jar' + ' '
                         + '-s ' + out_directory + '/' + fasta_pref + '-test.' + fasta_suff + ' '
                         + '-r ' + out_directory + '/' + fasta_pref + '-train.' + fasta_suff + ' '
                         + '-m ' + out_directory + '/' + kabat_pref + '-train.' + kabat_suff + ' '
                         + '--amino ' + '--' + args.mode + ' --igblast-like'),
                         universal_newlines=True, stdout=out_file, cwd=args.script_dir)
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
