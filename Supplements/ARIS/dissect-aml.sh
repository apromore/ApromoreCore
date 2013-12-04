#!/bin/sh

if test $# != 2;   then echo "Usage: $0 [source AML file] [target dir]" ; exit 1; fi
if test ! -f "$1"; then echo "No AML source file $1"                    ; exit 2; fi
if test -a "$2";   then echo "Pre-existing target directory $2"         ; exit 3; fi

source config.sh

SRC=$1
DESTDIR=$2

echo Created target directory $DESTDIR
mkdir -p "$DESTDIR"

echo Creating extraction script $SCRATCH/dissect-aml-2.sh
xsltproc --stringparam src "$SRC" --stringparam dest "$DESTDIR" --stringparam bin "`pwd`/`dirname $0`" dissect-aml.xsl "$SRC" > $SCRATCH/dissect-aml-2.sh

echo Executing $SCRATCH/dissect-aml-2.sh
source $SCRATCH/dissect-aml-2.sh
