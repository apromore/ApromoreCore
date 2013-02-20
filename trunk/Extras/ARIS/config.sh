# the root of the Apromore checkout
APROMORE_ROOT=`pwd`/`dirname $0`/../..

# Location of the XSL transforms from the ARIS markup canoniser
XSL_ROOT=$APROMORE_ROOT/Apromore-Plugins/plugin-canoniser/aris/src/main/resources/xsd

# intermediate files end up in this directory
SCRATCH=/tmp

echo Apromore root directory: $APROMORE_ROOT
echo Scratch directory:       $SCRATCH
