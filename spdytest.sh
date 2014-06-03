#!/bin/bash

args="$@"

mvn compile &&
mvn -q -e exec:java -Dexec.mainClass=com.twitter.spdy.Spdy -Dexec.args="$args"
