package nl.rug.ds.bpm.test;

import nl.rug.ds.bpm.variability.SpecificationToXML;
import nl.rug.ds.bpm.variability.VariabilitySpecification;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by armascer on 19/05/2017.
 */
public class TestMain {

    public static void main(String[] args){
        List<String> files = new LinkedList<>();
        files.add("nets/MunA.pnml");
        files.add("nets/MunB.pnml");
        files.add("nets/MunC.pnml");
        files.add("nets/MunD.pnml");

        VariabilitySpecification vs = new VariabilitySpecification(files, "silent");
        String[] output = SpecificationToXML.getOutput(vs, "silent");
        System.out.println(output[1]);
    }
}
