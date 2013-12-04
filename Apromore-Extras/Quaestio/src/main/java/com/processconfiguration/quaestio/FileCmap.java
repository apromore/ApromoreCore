package com.processconfiguration.quaestio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.processconfiguration.cmap.CMAP;
import com.processconfiguration.utils.schemaValidation;

class FileCmap implements Cmap {

        /** A file in C-BPMN format. */
	private File file;

	/**
         * Read a process model from the filesystem. 
	 */
	FileCmap(final File file) throws Exception {
		String str, cond;
                int fStr, lStr, cStr;

                // schema verification
                schemaValidation.validate(getClass().getResource("/xsd/CMAP.xsd"), file);

                // validate CMAP format
                //checks that conditions in the CMAP file are in the correct format, i.e. they don't contain symbols like \u2227
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while ((str = reader.readLine()) != null) {
                        fStr = str.indexOf("condition=");
                        lStr = str.indexOf("/", fStr);
                        if (fStr != -1 && lStr != -1) {
                                cond = str.substring(fStr + 11, lStr - 1);
                                cStr = cond.length();
                                if (cond.indexOf("\u2228") == 1
                                                || cond.indexOf("\u2227") == 1
                                                || cond.indexOf("\u00ac") == 1
                                                || cond.indexOf("\u22bb") == 1) {
                                        throw new IllegalArgumentException("Wrong condition format, please use \"+,-,.,xor,nor,=,=>\"");
                                }
                        }
                }
                reader.close();

		this.file = file;
	}

	public CMAP getCMAP() throws JAXBException {
		return (CMAP) JAXBContext.newInstance("com.processconfiguration.cmap").createUnmarshaller().unmarshal(file);
	}

	public String getText() {
		return file.getAbsolutePath();
	}
}

