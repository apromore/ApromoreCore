call mvn install:install-file -Dfile=jbpt.jar -DgroupId=org.jbpt -DartifactId=jbpt -Dversion=0.2.197 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpstruct.jar -DgroupId=ee.ut -DartifactId=bpstruct -Dversion=0.1.117 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=rpsdag.jar -DgroupId=tu -DartifactId=rpsdag -Dversion=0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=taskmapping.jar -DgroupId=tu -DartifactId=taskmapping -Dversion=0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=jalopy-1.9.4-198.jar -DgroupId=triemax -DartifactId=jalopy -Dversion=1.9.4-198 -Dpackaging=jar
call mvn install:install-file -Dfile=jalopy-maven-1.9.4-198.jar -DgroupId=triemax -DartifactId=jalopy-maven -Dversion=1.9.4-198 -Dpackaging=jar