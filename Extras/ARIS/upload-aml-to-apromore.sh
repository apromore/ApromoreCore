#!/bin/sh

if test $# != 1;   then echo "Usage: $0 [AML fragment directory]" ; exit 1; fi
if test ! -d "$1"; then echo "Not a directory: $1"                ; exit 2; fi

source config.sh

cd `dirname $1`
find `basename $1` -name "*.aml" -exec java -jar $APROMORE_ROOT/Extras/cpfImporter/target/cpfImporter-1.0.one-jar.jar {} \;
