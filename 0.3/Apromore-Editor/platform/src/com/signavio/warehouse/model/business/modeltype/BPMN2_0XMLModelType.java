package com.signavio.warehouse.model.business.modeltype;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.xml.sax.SAXException;

import com.signavio.platform.util.fsbackend.FileSystemUtil;
import com.signavio.warehouse.business.BPMN20XMLFileUtil;
import com.signavio.warehouse.model.business.ModelTypeFileExtension;
import com.signavio.warehouse.model.business.ModelTypeRequiredNamespaces;
import com.signavio.warehouse.revision.business.RepresentationType;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;

@ModelTypeRequiredNamespaces(namespaces={"http://b3mn.org/stencilset/bpmn2.0#", "http://b3mn.org/stencilset/bpmn2.0conversation#", "http://b3mn.org/stencilset/bpmn2.0choreography#"})
public class BPMN2_0XMLModelType extends SignavioModelType {

	@Override
	public void storeRepresentationInfoToModelFile(RepresentationType type, byte[] content, String path) {
		super.storeRepresentationInfoToModelFile(type, content, path);
		if (RepresentationType.JSON == type){
			try {
				String bpmn20Path = path.substring(0,path.lastIndexOf(this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension())) + ".bpmn20.xml";
				String jsonRep = new String(content, "UTF-8");
                BPMN20XMLFileUtil.storeBPMN20XMLFile(bpmn20Path, jsonRep);
            } catch (IOException e) {
                    throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
            } catch (JSONException e) {
            	throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			} catch (BpmnConverterException e) {
				throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			} catch (JAXBException e) {
				throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			} catch (SAXException e) {
				throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			} catch (ParserConfigurationException e) {
				throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			} catch (TransformerException e) {
				throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
			}
		}
	}

	@Override
	public void storeRevisionToModelFile(String jsonRep, String svgRep,String path) {
		super.storeRevisionToModelFile(jsonRep, svgRep, path);
		try {
			String bpmn20Path = path.substring(0,path.lastIndexOf(this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension())) + ".bpmn20.xml";
            BPMN20XMLFileUtil.storeBPMN20XMLFile(bpmn20Path, jsonRep);
        } catch (IOException e) {
                throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
        } catch (JSONException e) {
        	throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (BpmnConverterException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (JAXBException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (SAXException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (TransformerException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		}
	}
	
	@Override
	public boolean acceptUsageForTypeName(String namespace) {
		for(String ns : this.getClass().getAnnotation(ModelTypeRequiredNamespaces.class).namespaces()) {
			if(ns.equals(namespace)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public File storeModel(String path, String id, String name, String description,
			String type, String jsonRep, String svgRep) {
		File file = super.storeModel(path, id, name, description, type, jsonRep, svgRep);
		try {
			String bpmn20Path = path.substring(0,path.lastIndexOf(this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension())) + ".bpmn20.xml";
            BPMN20XMLFileUtil.storeBPMN20XMLFile(bpmn20Path, jsonRep);
        } catch (IOException e) {
                throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
        } catch (JSONException e) {
        	throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (BpmnConverterException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (JAXBException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (SAXException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		} catch (TransformerException e) {
			throw new IllegalStateException("Cannot save BPMN2.0 XML", e);
		}
        return file;
	}

	@Override
	public boolean renameFile(String parentPath, String oldName, String newName) {
		if(super.renameFile(parentPath, oldName, newName)) {
			if(parentPath != "") {
				parentPath += File.separator;
			}
			return FileSystemUtil.renameFile(parentPath + File.separator + oldName + ".bpmn20.xml", parentPath + File.separator + newName + ".bpmn20.xml");
		} else {
			return false;
		}
	}
	
	@Override
	public void deleteFile(String parentPath, String name) {
		super.deleteFile(parentPath, name);
		FileSystemUtil.deleteFileOrDirectory(parentPath + File.separator + name + ".bpmn20.xml");
	}
}
