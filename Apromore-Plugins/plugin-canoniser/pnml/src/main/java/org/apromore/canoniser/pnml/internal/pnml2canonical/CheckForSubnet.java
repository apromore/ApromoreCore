/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

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

                JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf", org.apromore.cpf.ObjectFactory.class.getClassLoader());
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                        Boolean.TRUE);
                JAXBElement<CanonicalProcessType> cprocRootElem = new org.apromore.cpf.ObjectFactory()
                        .createCanonicalProcess(ts.getCPF());
                m.marshal(cprocRootElem, new File(folder, subfilename + ".cpf"));

                jc = JAXBContext.newInstance("org.apromore.anf", org.apromore.anf.ObjectFactory.class.getClassLoader());
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
