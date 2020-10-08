#!/bin/bash

for f in res/images/feature-*.png; do
  echo $f;
  convert $f -gravity south -crop x610+0+0 +repage $f;
done
