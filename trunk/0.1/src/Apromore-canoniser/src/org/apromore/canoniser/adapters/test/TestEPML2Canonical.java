package org.apromore.canoniser.adapters.test;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.canoniser.adapters.Canonical2XPDL;
import org.apromore.canoniser.adapters.EPML2Canonical;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.cpf.CanonicalProcessType;
import org.wfmc._2008.xpdl2.PackageType;

import de.epml.TypeEPML;

public class TestEPML2Canonical {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean de_flag = true;
		
		File folder = new File("EPML_models");
		FileFilter fileFilter = new FileFilter() { 
			public boolean accept(File file) { 
				return file.isFile(); 
			} 
		}; 
		File[] folderContent = folder.listFiles (fileFilter);
		int n =0;
		for (int i=0;i<folderContent.length;i++) {
			File file = folderContent[i];
			String filename = file.getName();
			StringTokenizer tokenizer = new StringTokenizer(filename, ".");
			String filename_without_path = tokenizer.nextToken();
			String extension = filename.split("\\.")[filename.split("\\.").length-1];
			if (extension.compareTo("epml1")==0) {
				System.out.println ("Analysing " + filename);
				n++;
				try{
					JAXBContext jc = JAXBContext.newInstance("de.epml");
					Unmarshaller u = jc.createUnmarshaller();
					JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(file);
					TypeEPML epml = rootElement.getValue();

					EPML2Canonical epml2canonical = new EPML2Canonical(epml);

					jc = JAXBContext.newInstance("org.apromore.cpf");
					Marshaller m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<CanonicalProcessType> cprocRootElem = 
						new org.apromore.cpf.ObjectFactory().createCanonicalProcess(epml2canonical.getCPF());
					m.marshal(cprocRootElem, new File(folder,filename_without_path + ".cpf"));

					jc = JAXBContext.newInstance("org.apromore.anf");
					m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(epml2canonical.getANF());
					m.marshal(annsRootElem, new File (folder,filename_without_path + ".anf"));

					if(de_flag)
					{
						Canonical2XPDL c1 = new Canonical2XPDL(epml2canonical.getCPF());
						Canonical2XPDL c2 = new Canonical2XPDL(epml2canonical.getCPF(),epml2canonical.getANF());
						
						XPDL2Canonical c3 = new XPDL2Canonical(c1.getXpdl());
						XPDL2Canonical c4 = new XPDL2Canonical(c2.getXpdl());
						
						Canonical2EPML c5 = new Canonical2EPML(c3.getCpf());
						 c5 = new Canonical2EPML(c3.getCpf(),c3.getAnf());
						
						 c5 = new Canonical2EPML(c4.getCpf());
						 c5 = new Canonical2EPML(c4.getCpf(),c4.getAnf());
					}
					
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				System.out.println ("Skipping " + filename);
			}	
		}
		System.out.println ("Analysed " + n + " files.");
	} 

}
