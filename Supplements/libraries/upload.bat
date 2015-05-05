#!/usr/bin/env bash
call mvn install:install-file -Dfile=jbpt-core.jar -DgroupId=org.jbpt -DartifactId=jbpt-core -Dversion=0.2.393 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=jbpt-deco.jar -DgroupId=org.jbpt -DartifactId=jbpt-deco -Dversion=0.2.393 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpstruct.jar -DgroupId=ee.ut -DartifactId=bpstruct -Dversion=0.1.117 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=rpsdag.jar -DgroupId=tu -DartifactId=rpsdag -Dversion=0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=taskmapping.jar -DgroupId=tu -DartifactId=taskmapping -Dversion=0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=mathCollection.jar -DgroupId=mathCollection -DartifactId=mathCollection -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=edu.mit.jwi_2.1.4.jar -DgroupId=edu.mit -DartifactId=edu.mit.jwi -Dversion=2.1.4 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=edu.sussex.nlp.jws.beta.11.jar -DgroupId=edu.sussex -DartifactId=edu.sussex.nlp.jws -Dversion=0.11-beta -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=lpsolve55j.jar -DgroupId=lpsolve -DartifactId=lpsolve -Dversion=55j -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=jwnl.jar -DgroupId=jwnl -DartifactId=jwnl -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=javabdd-1.0b2.jar -DgroupId=net.sf.javabdd -DartifactId=javabdd -Dversion=1.0b2 -Dpackaging=jar -DgeneratePom=true
rem PQL
call mvn install:install-file -Dfile=pql/themis-0.1.1.jar -DgroupId=org.themis -DartifactId=themis -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=pql/json-20090211.jar -DgroupId=org.json -DartifactId=json -Dversion=20090211 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=pql/jbpt-petri-0.2.393.jar -DgroupId=org.jbpt -DartifactId=jbpt-petri -Dversion=0.2.393 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=pql/PQL.jar -DgroupId=org.pql -DartifactId=pql -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem BPMNMiner
call mvn install:install-file -Dfile=bpmnminer/AlphaMiner.jar -DgroupId=org.processmining -DartifactId=alphaminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Animation.jar -DgroupId=org.processmining -DartifactId=animation -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BPMN.jar -DgroupId=org.processmining -DartifactId=bpmn -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BPMNMiner.jar -DgroupId=org.processmining -DartifactId=bpmnminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/EfficientStorage.jar -DgroupId=org.processmining -DartifactId=efficientstorage -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Flex.jar -DgroupId=org.processmining -DartifactId=flex -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/GraphViz.jar -DgroupId=org.processmining -DartifactId=graphviz -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/HeuristicsMiner.jar -DgroupId=org.processmining -DartifactId=heuristicsminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/InductiveMiner.jar -DgroupId=org.processmining -DartifactId=inductiveminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/InteractiveVisualization.jar -DgroupId=org.processmining -DartifactId=interactivevisualization -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/LogDialog.jar -DgroupId=org.processmining -DartifactId=logdialog -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/lpsolve55j.jar -DgroupId=org.processmining -DartifactId=lpsolve55j -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/OSService.jar -DgroupId=org.processmining -DartifactId=osservice -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/PetriNets.jar -DgroupId=org.processmining -DartifactId=petrinets -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/PNetReplayer.jar -DgroupId=org.processmining -DartifactId=pnetreplayer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProcessTree.jar -DgroupId=org.processmining -DartifactId=processtree -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Properties.jar -DgroupId=org.processmining -DartifactId=properties -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/TransitionSystems.jar -DgroupId=org.processmining -DartifactId=transitionsystems -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Widgets.jar -DgroupId=org.processmining -DartifactId=widgets -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem ApacheUtils-lib
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/ApacheUtils.jar -DgroupId=org.processmining -DartifactId=apacheutils -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/commons-cli-1.2.jar -DgroupId=org.processmining -DartifactId=commons-cli -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/commons-lang3-3.1.jar -DgroupId=org.processmining -DartifactId=commons-lang3 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/compress-lzf-1.0.3.jar -DgroupId=org.processmining -DartifactId=compress-lzf -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/java-merge-sort-1.0.0.jar -DgroupId=org.processmining -DartifactId=java-merge-sort -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/juniversalchardet-1.0.3.jar -DgroupId=org.processmining -DartifactId=juniversalchardet -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/opencsv-2.4-patched.jar -DgroupId=org.processmining -DartifactId=opencsv -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ApacheUtils-lib/uncommons-maths-1.2.3.jar -DgroupId=org.processmining -DartifactId=uncommons-maths -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem BasicUtils-lib
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/BasicUtils.jar -DgroupId=org.processmining -DartifactId=basicutils -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/javacsv-2.1.jar -DgroupId=org.processmining -DartifactId=javacsv -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/javailp-1.2a.jar -DgroupId=org.processmining -DartifactId=javailp -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/jgrapht-jdk1.6.jar -DgroupId=org.processmining -DartifactId=jgrapht-jdk -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/jmathplot.jar -DgroupId=org.processmining -DartifactId=jmathplot -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/trove-3.0.3.jar -DgroupId=org.processmining -DartifactId=trove -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/BasicUtils-lib/ujmp-complete-0.2.5.jar -DgroupId=org.processmining -DartifactId=ujmp-complete -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem Fodina-lib
call mvn install:install-file -Dfile=bpmnminer/Fodina-lib/fodina-2013-01-02.jar -DgroupId=org.processmining -DartifactId=fodina -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Fodina-lib/jgoodies-common-1.2.0.jar -DgroupId=org.processmining -DartifactId=jgoodies-common -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Fodina-lib/jgoodies-forms-1.4.1.jar -DgroupId=org.processmining -DartifactId=jgoodies-forms -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Fodina-lib/kxml2-2.2.3.jar -DgroupId=org.processmining -DartifactId=kxml2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Fodina-lib/nikefsmonitor-20130503.jar -DgroupId=org.processmining -DartifactId=nikefsmonitor -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem GraphViz-lib
call mvn install:install-file -Dfile=bpmnminer/GraphViz-lib/grappa.jar -DgroupId=org.processmining -DartifactId=grappa -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem ILPMiner-lib
call mvn install:install-file -Dfile=bpmnminer/ILPMiner-lib/ILPMiner.jar -DgroupId=org.processmining -DartifactId=ilpminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ILPMiner-lib/oplall.jar -DgroupId=org.processmining -DartifactId=oplall -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem Log-lib
call mvn install:install-file -Dfile=bpmnminer/Log-lib/Log.jar -DgroupId=org.processmining -DartifactId=log -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Log-lib/LogAbstractions.jar -DgroupId=org.processmining -DartifactId=logabstractions -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Log-lib/OpenXES.jar -DgroupId=org.deckfour.xes -DartifactId=OpenXES -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Log-lib/OpenXES-XStream.jar -DgroupId=org.deckfour.xes -DartifactId=OpenXES-Stream -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/Log-lib/Spex.jar -DgroupId=org.processmining -DartifactId=spex -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem PetriNets-lib
call mvn install:install-file -Dfile=bpmnminer/PetriNets-lib/exp4j-0.3.8.jar -DgroupId=org.processmining -DartifactId=exp4j -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem Widgets-lib
call mvn install:install-file -Dfile=bpmnminer/Widgets-lib/japura-1.12.3.jar -DgroupId=org.processmining -DartifactId=japura -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
rem ProM5-lib
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/att.jar -DgroupId=org.processmining -DartifactId=att -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/commons-discovery-0.5.jar -DgroupId=org.processmining -DartifactId=commons-discovery -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/commons-math-1.1.jar -DgroupId=org.processmining -DartifactId=commons-math -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/DecisionMiner.jar -DgroupId=org.processmining -DartifactId=decisionminer -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/GantzGraf-0.9.jar -DgroupId=org.processmining -DartifactId=gantzgraf -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/holub.jar -DgroupId=org.processmining -DartifactId=holub -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/jahmm-0.6.1.jar -DgroupId=org.processmining -DartifactId=jahmm -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/java_cup.jar -DgroupId=org.processmining -DartifactId=java_cup -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/JRclient.jar -DgroupId=org.processmining -DartifactId=jrclient -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/JRI.jar -DgroupId=org.processmining -DartifactId=jri -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/jung-1.7.6.jar -DgroupId=org.processmining -DartifactId=jung -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/kaon2.jar -DgroupId=org.processmining -DartifactId=kaon2 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/mathCollection.jar -DgroupId=org.processmining -DartifactId=mathCollection -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/MXMLib.jar -DgroupId=org.processmining -DartifactId=mxml -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/ProM.jar -DgroupId=org.processmining -DartifactId=prom5-lib -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/ProMmodels.jar -DgroupId=org.processmining -DartifactId=prom5-models -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/ProMplugins.jar -DgroupId=org.processmining -DartifactId=prom5-plugins -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/slickerbox0.5.jar -DgroupId=org.processmining -DartifactId=slickerbox -Dversion=0.5 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/wsml2reasoner-noncom-v0_5.jar -DgroupId=org.processmining -DartifactId=wsml2reasoner-noncom -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/WSML-grammar-20070417.jar -DgroupId=org.processmining -DartifactId=wsml-grammar -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/wsmo4j-0.6.1.jar -DgroupId=org.processmining -DartifactId=wsmo4j -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM5-lib/wsmo-api-0.6.1.jar -DgroupId=org.processmining -DartifactId=wsmo-api -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

rem ProM6-lib
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/bsh-2.0b4.jar -DgroupId=org.processmining -DartifactId=bsh -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/colt.jar -DgroupId=org.processmining -DartifactId=colt -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/FilterableSortableTablePanel.jar -DgroupId=org.processmining -DartifactId=filterableSortableTablePanel -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/flanagan.jar -DgroupId=org.processmining -DartifactId=flanagan -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-export-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-export -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphics2d-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphics2d -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-emf-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-emf -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-java-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-java -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-pdf-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-pdf -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-ps-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-ps -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-svg-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-svg -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-swf-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-swf -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-graphicsio-tests-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-graphicsio-tests -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-io-2.0.2.jar -DgroupId=org.processmining -DartifactId=freehep-io -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-swing-2.0.3.jar -DgroupId=org.processmining -DartifactId=freehep-swing -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-util-2.0.2.jar -DgroupId=org.processmining -DartifactId=freehep-util -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/freehep-xml-2.1.1.jar -DgroupId=org.processmining -DartifactId=freehep-xml -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jargs.jar -DgroupId=org.processmining -DartifactId=jargs -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jas-plotter-2.2.jar -DgroupId=org.processmining -DartifactId=jas-plotter -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jcommon-1.0.16.jar -DgroupId=org.processmining -DartifactId=jcommon -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jfreechart-1.0.14.jar -DgroupId=org.processmining -DartifactId=jfreechart -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jgraph.jar -DgroupId=org.processmining -DartifactId=jgraph -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jlfgr-1_0.jar -DgroupId=org.processmining -DartifactId=jlfgr -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jung-algorithms-2.0.jar -DgroupId=org.processmining -DartifactId=jung-algorithms -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jung-io-2.0.jar -DgroupId=org.processmining -DartifactId=jung-io -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/jung-visualization-2.0.jar -DgroupId=org.processmining -DartifactId=jung-visualization -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/openide-lookup-1.9-patched-1.0.jar -DgroupId=org.processmining -DartifactId=openide-lookup -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/ProM-Contexts.jar -DgroupId=org.processmining -DartifactId=prom6-contexts -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/ProM-Framework.jar -DgroupId=org.processmining -DartifactId=prom6-framework -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/ProM-Models.jar -DgroupId=org.processmining -DartifactId=prom6-models -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/ProM-Plugins.jar -DgroupId=org.processmining -DartifactId=prom6-plugins -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/simmetrics.jar -DgroupId=org.processmining -DartifactId=simmetrics -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/slickerbox1.0rc1.jar -DgroupId=org.processmining -DartifactId=slickerbox1 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/TableLayout-20050920.jar -DgroupId=org.processmining -DartifactId=tableLayout -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/Uitopia.jar -DgroupId=org.processmining -DartifactId=uitopia -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/UITopiaResources.jar -DgroupId=org.processmining -DartifactId=uitopiaResources -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/ProM6-lib/weka.jar -DgroupId=org.processmining -DartifactId=weka -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=bpmnminer/com.springsource.org.apache.tools.ant-1.8.3.jar -DgroupId=org.apache.ant -DartifactId=com.springsource.org.apache.tools.ant -Dversion=1.8.3 -Dpackaging=jar -DgeneratePom=true
rem BP-diff libraries
call mvn install:install-file -Dfile=diffbp-1.0.jar -DgroupId=diffbp -DartifactId=diffbp -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile=jackson-1.0.jar -DgroupId=ut.ee -DartifactId=jackson -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
call mvn install:install-file -Dfile=jbpt-1.0.jar -DgroupId=ut.ee -DartifactId=jbpt -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
call mvn install:install-file -Dfile=pnapi-1.0.jar -DgroupId=ut.ee -DartifactId=pnapi -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
call mvn install:install-file -Dfile=umaBPDiff-1.0.jar -DgroupId=ut.ee -DartifactId=umaBPDiff -Dversion=1.0 -Dpackaging=jar -DegeneratePom=true
