ig-regions
==========

Regions finding tool.

Run ig-regions
--------------

To run ig-regions you can do this:

```
$ java -jar target/ig-regions-1.0-SNAPSHOT.jar [args]
```

or just use:

```
$ run-regions.sh [args]
```

Arguments
---------

To use the tool you have to set some required arguments:

```
  -s <value> | --source <value>
        file to annotate [fasta]
  -r <value> | --reference <value>
        reference file [fasta]
  -m <value> | --marking <value>
        reference marking [igblast marking format]
```

If you want to run script on protein data, just set:

```
  --amino
        use amino acid data
```

To change output to default IgBLAST marking data files use:

```
  --igblast-like
        output as igblast marking
```

Most of time you need not to use additional output filtration, but you can:
```
  --filter-window <value>
        set window size for filtration (default: disabled)
```

You can choose the alignment method for annotation, but we strongly recommend to use default for regions:

```
  --global
        use global alignment
  --local
        use local alignment
  --semiglobal
        use semiglobal alignment (default)
  --affine-global
        use global alignment
  --affine-local
        use global alignment
  --affine-semiglobal
        use global alignment
```

If you want to customize alignment options, set:

```
  --matrix <value>
        use external alignment matrix [txt]
  --gap <value>
        simple gap score (default: -5)
  --gap-open <value>
        affine open gap score (default: -10)
  --gap-ext <value>
        affine extension gap score (default: -1)
```

You can always see this help, using:

```
  --help
        this message
```

Example
-------

To test the system just run:

```
./run.sh -r ../data/germline/human/vl.fasta -m ../data/nomenclature/human/vl.fasta -s 'TODO: ADD_FILE' --amino --igblast-like
```

Citation
--------

If you use our region annotator and obtain scientific results that you publish,
we would ask you to acknowledge the usage of IG-regions by referencing the paper:
```
TODO: Paper info here
```
