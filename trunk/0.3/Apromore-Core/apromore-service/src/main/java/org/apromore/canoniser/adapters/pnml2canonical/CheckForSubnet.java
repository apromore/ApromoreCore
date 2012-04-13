package org.apromore.canoniser.adapters.pnml2canonical;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.pnml.NetType;
import org.apromore.pnml.NetType.Page;
import org.apromore.pnml.PageType;
import org.apromore.pnml.PnmlType;

public class CheckForSubnet {
	File file;
	NetType net;
	PnmlType pnml;
	List<NetType> mainnet;
	List<NetType> subnet;
	List<Page> pages;
	File folder = new File("PNML_models/woped_cases_mapped_cpf_anf");

	public CheckForSubnet(PnmlType pnmlbase, String filename, long id) {

		mainnet = pnmlbase.getNet();
		for (Object obj : mainnet) {
			if (obj instanceof NetType) {
				NetType parent = (NetType) obj;
				pages = parent.getPage();
				for (Object obj1 : pages) {
					if (obj1 instanceof PageType) {
						PageType subpage = (PageType) obj1;
						subnet = subpage.getNet();
						for (Object obj2 : subnet) {
							if (obj2 instanceof NetType) {
								NetType lastnet = (NetType) obj2;
								net = lastnet;
							}
						}
					}
				}
			}
		}
		if (net != null) {

			try {
				String subfilename = filename + "_subnet";
				TranslateSubnet ts = new TranslateSubnet(pnml, net, id,
						subfilename);

				JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				JAXBElement<CanonicalProcessType> cprocRootElem = new org.apromore.cpf.ObjectFactory()
						.createCanonicalProcess(ts.getCPF());
				m.marshal(cprocRootElem, new File(folder, subfilename + ".cpf"));

				jc = JAXBContext.newInstance("org.apromore.anf");
				m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory()
						.createAnnotations(ts.getANF());
				m.marshal(annsRootElem, new File(folder, subfilename + ".anf"));

			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public CheckForSubnet(PnmlType pnmlbase, String filename, long id,
			NetType sub) {

		NetType secondsubnet = sub;
		pages = secondsubnet.getPage();
		for (Object obj : pages) {
			if (obj instanceof PageType) {
				PageType subpage = (PageType) obj;
				subnet = subpage.getNet();
				for (Object obj1 : subnet) {
					if (obj1 instanceof NetType) {
						NetType lastnet = (NetType) obj1;
						net = lastnet;
					}
				}
			}
		}
		if (net != null) {

			try {
				String subfilename = filename + "_subnet";
				TranslateSubnet ts = new TranslateSubnet(pnml, net, id,
						subfilename);

				JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				JAXBElement<CanonicalProcessType> cprocRootElem = new org.apromore.cpf.ObjectFactory()
						.createCanonicalProcess(ts.getCPF());
				m.marshal(cprocRootElem, new File(folder, subfilename + ".cpf"));

				jc = JAXBContext.newInstance("org.apromore.anf");
				m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory()
						.createAnnotations(ts.getANF());
				m.marshal(annsRootElem, new File(folder, subfilename + ".anf"));

			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public NetType getNet() {

		return net;
	}

}
