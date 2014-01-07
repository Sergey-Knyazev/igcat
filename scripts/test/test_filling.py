from __future__ import division
import os
import shlex
import subprocess
import multiprocessing
import argparse
import itertools

__author__ = 'Sergey'


def ensure_dir(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)


def get_split(name):
    bname = os.path.basename(name)
    pos = bname.rfind('.')
    return bname[:pos], bname[pos + 1:]


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('-n', '--max-threads', default=1, type=int
        , help='maximum count of threads')
    parser.add_argument('-o', '--out-dir', help='output directory')
    parser.add_argument('-m', '--tests-count', help='count of tests', type=int)
    parser.add_argument('-c', '--train-seq-count'
        , help='maximum count of sequences in container', type=int)
    parser.add_argument('-a', '--test-seq-count'
        , help='maximum count of sequences for annotation', type=int)
    parser.add_argument('-p', '--mode', default='affine-semiglobal'
        , help='alignment mode')
    parser.add_argument('-d', '--script-dir', help='scripts location')
    parser.add_argument('-s', '--fasta', help='input fasta file')
    parser.add_argument('-r', '--kabat', help='input kabat file')
    parser.add_argument('-f', '--test-fasta'
        , help='input fasta file with test data.'
               + 'use it with --test-kabat')
    parser.add_argument('-k', '--test-kabat'
        , help='input kabat file with test data.'
               + 'use it with --test-fasta')
    return parser.parse_args()


def extract_data_files_names(args):
    return dict(extract_test_files_names(args).items()
                + generate_train_files_names(args, args.data_dir).items())


def extract_test_files_names(args):
    if args.test_fasta and args.test_kabat:
        return dict(s=args.test_fasta,
                    t=args.test_kabat)
    fasta_pref, fasta_suff = get_split(os.path.basename(args.fasta))
    kabat_pref, kabat_suff = get_split(os.path.basename(args.kabat))
    return dict(s=args.data_dir + '/' + fasta_pref + '-test.' + fasta_suff,
                t=args.data_dir + '/' + kabat_pref + '-test.' + kabat_suff)


def generate_train_files_names(args, base_dir):
    fasta_pref, fasta_suff = get_split(os.path.basename(args.fasta))
    kabat_pref, kabat_suff = get_split(os.path.basename(args.kabat))
    return dict(r=base_dir + '/' + fasta_pref + '-train.' + fasta_suff,
                m=base_dir + '/' + kabat_pref + '-train.' + kabat_suff)


def generate_out_file_name(args, base_dir):
    kabat_pref, kabat_suff = get_split(os.path.basename(args.kabat))
    return base_dir + '/' + kabat_pref \
                  + '-prediction.' + args.mode + '.' + kabat_suff


def copy_strings_fasta(fin_name, fout_name, strings_count):
    fasta_seq_counter = 0
    with open(fin_name) as fin:
        with open(fout_name, 'w') as fout:
            for line in fin:
                if line[0] == '>':
                    if fasta_seq_counter == strings_count:
                        break
                    fasta_seq_counter += 1
                fout.write(line)


def copy_strings_kabat(fin_name, fout_name, strings_count):
    with open(fin_name) as fin:
        with open(fout_name, 'w') as fout:
            fout.writelines(itertools.islice(fin, strings_count))


def create_train_files(in_data_files_names, out_data_files_names
                       , seq_count_for_annotation):
    copy_strings_fasta(in_data_files_names['r'],
                       out_data_files_names['r'],
                       seq_count_for_annotation)
    copy_strings_kabat(in_data_files_names['m'],
                       out_data_files_names['m'],
                       seq_count_for_annotation)


def execute(args, i):
    out_dir = args.out_dir + '/train-' + str(i)
    ensure_dir(out_dir)
    data_files_names = extract_data_files_names(args)
    train_files_names = generate_train_files_names(args, out_dir)
    out_file_name = generate_out_file_name(args, out_dir)
    create_train_files(data_files_names, train_files_names, i)
    out_file = open(out_file_name, 'w')
    command = 'java -jar ' \
              + args.script_dir \
              + '/../../ig-regions/target/ig-regions-1.0-SNAPSHOT.jar' \
              + ' -s ' + data_files_names['s'] \
              + ' -r ' + train_files_names['r'] \
              + ' -m ' + train_files_names['m'] \
              + ' --amino ' + '--' + args.mode + ' --igblast-like'
    p = subprocess.Popen(shlex.split(command)
        , universal_newlines=True, stdout=out_file
        , cwd=args.script_dir+'/..')
    p.wait()
    out_file.close()

    out_file = open(out_dir + '/' + 'result', 'w')
    p = subprocess.Popen(shlex.split('python ' + args.script_dir
                                     + '/../compare_marking.py '
                                     + data_files_names['t']
                                     + ' ' + out_file_name),
                         universal_newlines=True, stdout=out_file)
    p.wait()
    out_file.close()


def executestar(args):
    return execute(*args)


def rebase_paths(args):
    impl_dir = os.getcwd() + '/'
    args.script_dir = impl_dir + args.script_dir
    args.out_dir = impl_dir + args.out_dir
    ensure_dir(args.out_dir)
    args.data_dir = args.out_dir + '/test-data'
    ensure_dir(args.data_dir)
    args.fasta = impl_dir + args.fasta
    args.kabat = impl_dir + args.kabat
    if args.test_kabat and args.test_fasta:
        args.test_kabat = impl_dir + args.test_kabat
        args.test_fasta = impl_dir + args.test_fasta
    return args


def main():
    args = parse_args()
    args = rebase_paths(args)
    command = 'python test_data_generator.py ' \
              + args.fasta + ' ' \
              + args.kabat \
              + ' --out-dir ' + args.data_dir

    if args.test_fasta and args.test_kabat:
        command += ' --max-count ' + str(args.train_seq_count) \
                   + ' --split-ratio 1 '
    else:
        command += ' --max-count ' + str(args.train_seq_count
                                         + args.test_seq_count) \
                   + ' --split-ratio ' + str(args.train_seq_count
                                             / (args.train_seq_count
                                                + args.test_seq_count))
    p = subprocess.Popen(shlex.split(command), cwd=args.script_dir)
    p.wait()
    tasks = [(args, i) for i in range(int(args.train_seq_count
                                          / args.tests_count),
                                      args.train_seq_count + 1,
                                      int(args.train_seq_count
                                          / args.tests_count))]
    for i in tasks:
        executestar(i)
    p = multiprocessing.Pool(args.max_threads)
    p.map(executestar, tasks)

if __name__ == '__main__':
    main()
