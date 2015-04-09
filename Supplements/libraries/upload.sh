mvn install:install-file -Dfile=jbpt-core.jar -DgroupId=org.jbpt -DartifactId=jbpt-core -Dversion=0.2.393 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=jbpt-deco.jar -DgroupId=org.jbpt -DartifactId=jbpt-deco -Dversion=0.2.393 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=bpstruct.jar -DgroupId=ee.ut -DartifactId=bpstruct -Dversion=0.1.117 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=rpsdag.jar -DgroupId=tu -DartifactId=rpsdag -Dversion=0.1 -Dpackaging=jar
mvn install:install-file -Dfile=taskmapping.jar -DgroupId=tu -DartifactId=taskmapping -Dversion=0.1 -Dpackaging=jar
mvn install:install-file -Dfile=mathCollection.jar -DgroupId=mathCollection -DartifactId=mathCollection -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=edu.mit.jwi_2.1.4.jar -DgroupId=edu.mit -DartifactId=edu.mit.jwi -Dversion=2.1.4 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=edu.sussex.nlp.jws.beta.11.jar -DgroupId=edu.sussex -DartifactId=edu.sussex.nlp.jws -Dversion=0.11-beta -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lpsolve55j.jar -DgroupId=lpsolve -DartifactId=lpsolve -Dversion=55j -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=jwnl.jar -DgroupId=jwnl -DartifactId=jwnl -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=javabdd-1.0b2.jar -DgroupId=net.sf.javabdd -DartifactId=javabdd -Dversion=1.0b2 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=OpenXES-1.5.jar -DgroupId=org.deckfour.xes -DartifactId=OpenXES -Dversion=1.5 -Dpackaging=jar -DgeneratePom=true

# BP-diff libraries
mvn install:install-file -Dfile=diffbp-1.0.jar -DgroupId=diffbp -DartifactId=diffbp -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=jackson-1.0.jar -DgroupId=ut.ee -DartifactId=jackson -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
mvn install:install-file -Dfile=jbpt-1.0.jar -DgroupId=ut.ee -DartifactId=jbpt -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
mvn install:install-file -Dfile=pnapi-1.0.jar -DgroupId=ut.ee -DartifactId=pnapi -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
mvn install:install-file -Dfile=umaBPDiff-1.0.jar -DgroupId=ut.ee -DartifactId=umaBPDiff -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true

# In order to compile Quaestio with LiveConnect support, the system plugin.jar
# needs to be added to the Maven repository.  This will also need to be ported
# to upload.bat for Windows.
#
# mvn install:install-file -Dfile=${JAVA_HOME}/jre/lib/plugin.jar -DgroupId=java -DartifactId=plugin -Dversion=1.7.0 -Dpackaging=jar -DgeneratePom=true
