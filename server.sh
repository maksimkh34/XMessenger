#!/bin/bash
git pull
cd Server/src
javac -cp .:../../JavaPlugins/jackson-annotations-2.18.0-rc1.jar:../../JavaPlugins/jackson-core-2.18.0-rc1.jar:../../JavaPlugins/jackson-databind-2.18.0-rc1.jar:../../JavaPlugins/jackson-datatype-jsr310-2.18.0-rc1.jar -d ../bin Main.java
cd ../bin
java -cp .:../../JavaPlugins/jackson-annotations-2.18.0-rc1.jar:../../JavaPlugins/jackson-core-2.18.0-rc1.jar:../../JavaPlugins/jackson-databind-2.18.0-rc1.jar:../../JavaPlugins/jackson-datatype-jsr310-2.18.0-rc1.jar Main