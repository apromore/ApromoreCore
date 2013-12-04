#!/bin/sh

if test $# != 1;   then echo "Usage: $0 [AML fragment file]" ; exit 1; fi
if test ! -f "$1"; then echo "No EPML file $1"               ; exit 2; fi

# Read configurable parameters
source config.sh

# Location of the XSL transforms from the ARIS markup canoniser
XSL_ROOT=$APROMORE_ROOT/Apromore-Plugins/plugin-canoniser/aris/src/main/resources/xsd

# Classpath for the Cpf2Epml utility
CLASSPATH=\
$APROMORE_ROOT/Apromore-Plugins/plugin-canoniser/epml/target/canoniser-epml-1.1.jar:\
$APROMORE_ROOT/Apromore-Plugins/plugin-canoniser/core/api/target/canoniser-api-1.1.jar:\
$APROMORE_ROOT/Apromore-Plugins/plugin-core/api/target/plugin-api-1.1.jar:\
$APROMORE_ROOT/Apromore-Schema/anf-schema/target/anf-schema-1.1.jar:\
$APROMORE_ROOT/Apromore-Schema/cpf-schema/target/cpf-schema-1.1.jar:\
$APROMORE_ROOT/Apromore-Schema/epml-schema/target/epml-schema.jar:\
$APROMORE_ROOT/Apromore-Portal/target/portal/WEB-INF/lib/logback-classic-0.9.28.jar:\
$APROMORE_ROOT/Apromore-Portal/target/portal/WEB-INF/lib/slf4j-api-1.6.6.jar

EPC="$1"

xsltproc $XSL_ROOT/aml2cpf.xsl "$EPC" > $SCRATCH/out.cpf+anf
xsltproc $XSL_ROOT/cpf2cpf.xsl $SCRATCH/out.cpf+anf > $SCRATCH/out.cpf
xmllint --schema ~/Project/apromore/Apromore-Schema/cpf-schema/src/main/resources/xsd/cpf_1.0.xsd --noout $SCRATCH/out.cpf 
xsltproc $XSL_ROOT/cpf2anf.xsl $SCRATCH/out.cpf+anf > $SCRATCH/out.anf
xmllint --schema ~/Project/apromore/Apromore-Schema/anf-schema/src/main/resources/xsd/anf_0.3.xsd --noout $SCRATCH/out.anf 
java -classpath $CLASSPATH org.apromore.canoniser.epml.Cpf2Epml $SCRATCH/out.anf < $SCRATCH/out.cpf > $SCRATCH/out.epml
xmllint --format --noout --schema EPML_2.0.xsd $SCRATCH/out.epml
cp $SCRATCH/out.epml "${APROMORE_ROOT}/Apromore-Editor/test.epml"
#cp "$APROMORE_ROOT/Apromore-Plugins/plugin-canoniser/epml/src/test/resources/EPML/Audio.epml" "${APROMORE_ROOT}/Apromore-Editor/test.epml"

# Translate the EPML to Signavio's native JSON format
cd "${APROMORE_ROOT}/Apromore-Editor"
ant epml2json

# Optionally, create a pretty-printed version of the Signavio JSON
#python -m json.tool < test.json > test-prettyprinted.json

# AJAX call to delete the pre-existing Test model
curl -X DELETE "http://localhost:9000/editor/p/model/root-directory;Test.signavio.xml"
echo

# AJAX call to upload the new Test model
curl -X POST \
        --data-urlencode comment= \
        --data-urlencode description= \
        --data-urlencode glossary_xml="[]" \
        --data-urlencode json_xml@test.json \
        --data-urlencode svg_xml=NO-SVG-PROVIDED \
        --data-urlencode name=Test \
        --data-urlencode namespace=http://b3mn.org/stencilset/epc# \
        --data-urlencode parent=root-directory \
        --data-urlencode type="EPC" \
        --data-urlencode views="[]" \
        "http://localhost:9000/editor/p/model"
echo
