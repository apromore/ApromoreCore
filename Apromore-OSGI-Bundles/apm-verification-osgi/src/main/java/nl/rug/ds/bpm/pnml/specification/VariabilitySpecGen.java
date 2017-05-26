package nl.rug.ds.bpm.pnml.specification;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.rug.ds.bpm.variability.SpecificationToXML;
import nl.rug.ds.bpm.variability.VariabilitySpecification;

public class VariabilitySpecGen {
	
	public static void main(String[] args) {
		VariabilitySpecGen gen = new VariabilitySpecGen();
		gen.getSpecification();
		
		System.exit(0);
	}
	
	private void getSpecification() {
		FileDialog loadDialog = new FileDialog(new Frame(), "Select Petri Net files to include in the specification" , FileDialog.LOAD);
		loadDialog.setMultipleMode(true);
		loadDialog.setFile("*.pnml");
	    loadDialog.setVisible(true);
	    
	    List<String> filenames = getFileList(loadDialog.getFiles());
		String outputfolder = loadDialog.getDirectory();
		loadDialog.dispose();
	    
	    if (filenames.size() == 0) return;
	    
		FileDialog outputDialog = new FileDialog(new Frame(), "Select outputfile" , FileDialog.LOAD);
		outputDialog.setFile("*.xml");
		outputDialog.setMultipleMode(false);
		outputDialog.setVisible(true);
		
		String outputfile;
		if (!outputfolder.endsWith("/")) outputfolder += "/";
		
		if(outputDialog.getFile() != null) {
			outputfile = outputDialog.getDirectory() + outputDialog.getFile();
		}
		else {
			outputfile = outputfolder + "specification.xml";
			
			File f = new File(outputfile);
			int i = 2;
			while(f.exists()) {
				outputfile = outputfolder + "specification(" + i + ").xml";
				i++;
				f = new File(outputfile);
			}
		}
		
		outputDialog.dispose();
		
		VariabilitySpecification vs = new VariabilitySpecification(filenames, "silent");
		
//		write2File(outputfile, SpecificationToXML.getXML(vs, "silent"));
	}

	private List<String> getFileList(File[] files) {
		List<String> fList = new ArrayList<String>();
		
		for (File f: files) {
			fList.add(f.getAbsolutePath()); // + "/" + f.getName());
		}
		
		return fList;
	}
	
	private static void write2File(String filename, String filecontent) {
		try {
			File newfile = new File(filename);
				
	        FileWriter fileWriter = new FileWriter(newfile);
	        fileWriter.write(filecontent);
	    	fileWriter.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
