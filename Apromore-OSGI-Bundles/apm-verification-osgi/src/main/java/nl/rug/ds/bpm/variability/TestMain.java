package nl.rug.ds.bpm.variability;

import ee.ut.pnml.PNMLReader;
import hub.top.petrinet.PetriNet;
import nl.rug.ds.bpm.event.VerificationLogEvent;
import nl.rug.ds.bpm.pnml.verifier.PnmlVerifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by armascer on 19/05/2017.
 */
public class TestMain {

    public static void main(String[] args){
        TestMain test = new TestMain();
//        String[] result = test.extractSpecification();
//
        String xmlSpec = test.readXML();
        test.verify(xmlSpec);
    }

    private String readXML() {
        String fileName = "nets/verification.xml";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return new String(encoded, StandardCharsets.UTF_8);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void verify(String xml){
        PnmlVerifier pnmlVerifier = new PnmlVerifier();
        pnmlVerifier.setLogLevel(VerificationLogEvent.INFO);
        try {
            PetriNet net = PNMLReader.parse(new File("nets/booking_c.pnml"));

//            String[] results = pnmlVerifier.verify(net, xml);
            String[] results = {};

            for(int i =0; i < results.length; i++)
                System.out.println(results[i]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String[] extractSpecification(){
        List<String> files = new LinkedList<>();
        files.add("nets/MunA.pnml");
        files.add("nets/MunB.pnml");
        files.add("nets/MunC.pnml");
        files.add("nets/MunD.pnml");

        VariabilitySpecification vs = new VariabilitySpecification(files, "silent");
        String[] output = SpecificationToXML.getOutput(vs, "silent");
        System.out.println(output[1]);

        return output;
    }
}