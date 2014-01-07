import os
import argparse


def split(directory):
    os.chdir(directory)
    os.mkdir("vh")
    os.mkdir("vl")
    for n in os.listdir('.'):
        if os.path.isdir(n):
            continue
        with open(n, "rt") as fd:
            lines = "".join(fd.readlines())
            if "<h2>Results:</h2>" not in lines:
                continue
            try:
                lines = lines.split("<h2>Results:</h2>")[1]
                lines = lines.split("</table>")[0]
                lines = lines.split("<td style=\"background-color:#eef;\">")[1]
                light, heavy = lines.split("</p>")[:2]
                light = light.split("<br />")[1]
                light = "\n".join(l[:l.find('\n')]for l in light.split("\">")[1:])
                heavy = heavy.split("<br />")[1]
                heavy = "\n".join(h[:h.find('\n')]for h in heavy.split("\">")[1:])
                open("vl/vl-%s" % n, "wt").write(light)
                open("vh/vh-%s" % n, "wt").write(heavy)
            except Exception:
                print(n)


def merge(directory, outdir):
    seqs = []
    anno = []
    for f in os.listdir(directory):
        try:
            name = f.split('-')[1]
            path = os.path.join(directory, f)
            lines = [l.strip() for l in open(path, "rt").readlines()]
            i = 1
            coords = []
            for seq in lines:
                coords.append(str(i))
                i += len(seq)
                coords.append(str(i - 1))
            seqs.append(">%s\n%s\n\n" % (name, "".join(lines)))
            anno.append("%s\t%s\n" % (name, "\t".join(coords)))
        except Exception:
            print(f)
    with open(os.path.join(outdir, "%s.fasta" % os.path.basename(directory)), "wt") as fd:
        for rec in seqs:
            fd.write(rec)
    with open(os.path.join(outdir, "%s.kabat" % os.path.basename(directory)), "wt") as fd:
        for rec in anno:
            fd.write(rec)


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('raw', help='input raw data directory')
    parser.add_argument('outdir', help='output directory')
    return parser.parse_args()


def main():
    args = parse_args()
    split(args.raw)
    merge(os.path.join(args.raw, "vh"), args.outdir)
    merge(os.path.join(args.raw, "vl"), args.outdir)


if __name__ == "__main__":
    main()