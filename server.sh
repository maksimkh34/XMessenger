#!/bin/bash
git pull
cd Server/src
javac -cp .$(for filename in ../../JavaPlugins/*; do echo -n ":$filename"; done) -d ../bin Main.java
cd ../bin
java -cp .$(for filename in ../JavaPlugins/*; do echo -n ":$filename"; done) Main