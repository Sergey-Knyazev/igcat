#!/bin/sh

cd lib/ig-alicont
mvn clean install
cd ../ig-container
mvn clean install
cd ../../ig-regions
mvn clean package

echo "Installed!"
