package nl.rug.ds.bpm.pnml.verifier.apm;

import hub.top.petrinet.PetriNet;
import nl.rug.ds.bpm.ConfigBean;
import nl.rug.ds.bpm.event.VerificationEvent;
import nl.rug.ds.bpm.event.listener.VerificationEventListener;
import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;
import nl.rug.ds.bpm.log.listener.VerificationLogListener;
import nl.rug.ds.bpm.pnml.verifier.ExtPnmlStepper;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;
import nl.rug.ds.bpm.specification.jaxb.Group;
import nl.rug.ds.bpm.specification.marshaller.SpecificationUnmarshaller;
import nl.rug.ds.bpm.specification.parser.SetParser;
import nl.rug.ds.bpm.verification.Verifier;
import nl.rug.ds.bpm.verification.checker.CheckerFactory;
import nl.rug.ds.bpm.verification.checker.nusmv2.NuSMVFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Nick van Beest on 02-June-17.
 */
public class PnmlVerifierAPM implements VerificationEventListener, VerificationLogListener {
	private SetParser setParser;
	private CheckerFactory factory;
	private boolean reduce;
	private PetriNet pn;
	private boolean userFriendly;

	private String eventoutput;
	private List<String> feedback;
	private BPMSpecification bpmSpecification;
	
	private Set<String> conditions;
	private Set<String> transitionguards;

	private Map<String, Group> groupMap;

	public PnmlVerifierAPM(PetriNet pn, boolean userFriendly) {
		reduce = true;
		this.userFriendly = userFriendly;
		
		setParser = new SetParser();

		//Implement listeners and
		//Add listeners to receive log notifications
		Logger.addLogListener(this);
		
		eventoutput = "";
		feedback = new ArrayList<String>();
		
		this.pn = pn;
		
		//Create the wanted model checker factory
		factory = new NuSMVFactory(new File(ConfigBean.getNuSMVPath()));
		
		conditions = new HashSet<String>();
		transitionguards = new HashSet<String>();
	}

	public PnmlVerifierAPM(PetriNet pn, String specxml, boolean userFriendly) {
		this(pn, userFriendly);

		if(specxml != null) addSpecificationFromXML(specxml);
	}
	
	public PnmlVerifierAPM(PetriNet pn, String specxml, Set<String> conditions, String txtGuards, Set<String> transitionguards, boolean userFriendly) {
		this(pn, specxml, userFriendly);

		if(txtGuards != null) addGuardsFromFile(txtGuards);

		this.conditions = conditions;
		this.transitionguards = transitionguards;
	}

	public PnmlVerifierAPM(PetriNet pn, String[] specifications, boolean userFriendly) {
		this(pn, userFriendly);
		
		addSpecifications(specifications);
		
		bpmSpecification = getSpecifications();
        createGroupMap();

    }

    private void addGuardsFromFile(String txtGuards){
        StringTokenizer token = new StringTokenizer(txtGuards, "\n");
        while (token.hasMoreTokens())
            transitionguards.add(token.nextToken());
    }

    private void createGroupMap() {
        this.groupMap = new HashMap<>();
        for (Group g: bpmSpecification.getGroups()) {
            groupMap.put(g.getId(), g);
        }
    }
	
	public PnmlVerifierAPM(PetriNet pn, String[] specifications, Set<String> conditions, Set<String> transitionguards, boolean userFriendly) {
		this(pn, specifications, userFriendly);

		this.conditions = conditions;
		this.transitionguards = transitionguards;
	}

		
	public String[] verify() {
		verify(false);

		String[] feedbackArray = new String[feedback.size()];
		int i = 0;
		for(String f : feedback){
			feedbackArray[i] = f;
			i++;
		}

		return feedbackArray;
	}
	
	public String[] verify(Boolean getAllOutput) {
		//Make step class for specific Petri net type
		ExtPnmlStepper stepper;
		
		if (bpmSpecification == null) {
			bpmSpecification = getSpecifications();
		}
		
		try {
			stepper = new ExtPnmlStepper(pn);
			
			stepper.setConditions(conditions);
			stepper.setTransitionGuards(transitionguards);
			
			//Make a verifier which uses that step class
			Verifier verifier = new Verifier(stepper, factory);
			verifier.addEventListener(this);
			//Start verification
			verifier.verify(bpmSpecification, reduce);
		} 
		catch (Exception e) {
			Logger.log("Verification failure", LogEvent.CRITICAL);
		}
		System.out.println(eventoutput);

		String[] feedbackArray = new String[feedback.size()];
		int i = 0;
		for(String f : feedback){
			feedbackArray[i] = f;
			i++;
		}

		return feedbackArray;
	}
	
	public void addSpecificationFromXML(String specxml) {
		SpecificationUnmarshaller unmarshaller;
		try {
			unmarshaller = new SpecificationUnmarshaller(new ByteArrayInputStream(specxml.getBytes("UTF-8")));
			bpmSpecification = unmarshaller.getSpecification();
		} catch (Exception e) {
			Logger.log("Invalid specification xml", LogEvent.CRITICAL);
			return;
		}
	}

	public void addSpecification(String line) {
		setParser.parse(line);
	}

	public void addSpecifications(String[] lines) {
		for (String line: lines)
			addSpecification(line);
	}

	public BPMSpecification getSpecifications() {
		return setParser.getSpecification();
	}
	
	public void setReduction(boolean reduce) {
		this.reduce = reduce;
	}

	public boolean getReduction() {
		return reduce;
	}

	public void setConditions(Set<String> conditions) {
		this.conditions = conditions;
	}
	
	public void setTransitionGuards(Set<String> transitionguards) {
		this.transitionguards = transitionguards;
	}
	
	//Set maximum amount of tokens at a single place
	//Safety feature, prevents infinite models
	//Standard value of 3
	public void setMaximumTokensAtPlaces(int amount) {
		Verifier.setMaximumTokensAtPlaces(amount);
	}

	public int getMaximumTokensAtPlaces() {
		return Verifier.getMaximumTokensAtPlaces();
	}

	//Set maximum size of state space
	//Safety feature, prevents memory issues
	//Standard value of 7 million
	//(equals models of 4 parallel branches with each 50 activities)
	//Lower if on machine with limited memory
	public void setMaximumStates(int amount) {
		Verifier.setMaximumStates(amount);
	}

	public int getMaximumStates() {
		return Verifier.getMaximumStates();
	}
	
	public int getLogLevel() {
		return Logger.getLogLevel();
	}
	
	//Set log level LogEvent.DEBUG to LogEvent.CRITICAL
	public void setLogLevel(int level) {
		Logger.setLogLevel(level);
	}

	//Listener implementations
	@Override
	public void verificationEvent(VerificationEvent event) {
		//Use for user feedback
		//Event returns: specification id, formula, type, result, and specification itself
		if (userFriendly) {
//		    feedback.add(event.getUserFriendlyFeedback(groupMap));
			feedback.add("Specification " + event.getId() + " evaluated " + event.getVerificationResult() + " for " + event.getFormula().getSpecification().getType() + "(" + event.getFormula().getOriginalFormula() + "}");
		}
		else {
			feedback.add(event.toString());
		}
	}

    private BPMSpecification getBPMSpecification(File specificationFile) {
        SpecificationUnmarshaller unmarshaller;
        BPMSpecification spec = null;
        try {
            unmarshaller = new SpecificationUnmarshaller(specificationFile);
            spec = unmarshaller.getSpecification();
        }
        catch (Exception e) {
            e.printStackTrace();
            String[] erroRes = {"Invalid specification xml"};
            Logger.log("Invalid specification xml", LogEvent.CRITICAL);
        }

        return spec;
    }

    private BPMSpecification getBPMSpecification(String specification) {
        SpecificationUnmarshaller unmarshaller;
        BPMSpecification spec = null;
        try {
            unmarshaller = new SpecificationUnmarshaller(new ByteArrayInputStream(specification.getBytes("UTF-8")));
            spec = unmarshaller.getSpecification();
        }
        catch (Exception e) {
            e.printStackTrace();
            String[] erroRes = {"Invalid specification xml"};
            Logger.log("Invalid specification xml", LogEvent.CRITICAL);
        }

        return spec;
    }

	@Override
	public void verificationLogEvent(LogEvent event) {
		//Use for log and textual user feedback
		eventoutput += "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + event.toString() + "\n";
	}
}
