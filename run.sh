for i in {1..40}
do
  for ((j = 50; j <= 2000; j += 50))
  do
    python3 scripts/diff_info.py test/rosie/filling/test-$i/train-$j/vh-prediction.affine-semiglobal.kabat test/rosie/rosetta/vh.kabat --chothia 2 > test/rosie/filling/test-$i/train-$j/diff
    python3 scripts/diff_info.py test/rosie/filling/test-$i/train-$j/vh-prediction.filtered.kabat test/rosie/rosetta/vh.kabat --chothia 2 > test/rosie/filling/test-$i/train-$j/diff.filtered
  done
done
