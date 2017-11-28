package au.ltl.main;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import au.ltl.domain.Constraint;
import au.ltl.utils.ModelAbstractions;


public class Main {

	public static void main(String[] args) throws Exception {

		String modelName="data"+File.separator+"model1.bpmn";
		byte[] modelArray=getFileAsArray(modelName);
		ModelAbstractions model = new ModelAbstractions(modelArray);
		File XmlFileDeclareRules=new File("data"+File.separator+"declare.xml");;
		LinkedList<Constraint> LTLConstraintList=null;
		int addActionCost=1;
		int deleteActionCost=1;

		ModelChecker checker = new ModelChecker(model,new FileInputStream(XmlFileDeclareRules), LTLConstraintList, addActionCost, deleteActionCost);
		HashMap<String, List<RuleVisualization>> results = checker.checkNet();

//		System.out.println(new Gson().toJson(results));
		System.out.println("Check completed");
	}

	public static byte[] getFileAsArray(String fileName) {
		FileInputStream fileInputStream = null;
		File file = new File(fileName);

		try {
			byte[] bFile = new byte[(int) file.length()];

			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();

			return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	private static void st(Object x){
		System.out.println(x.toString());
	}
}
