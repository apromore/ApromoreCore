/**
 * Copyright © 2006-2009, Marcello La Rosa (marcello.larosa@gmail.com)
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *      Marcello La Rosa - initial API and implementation
 */
package com.processconfiguration.cmap.rcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.TreeMap;

import javax.swing.JFrame;

public class ExecBDDC extends JFrame {
	private PrintWriter writer;
	private BufferedReader reader;
	private Process p;
	// private String cos;//this is the command related to the condition to be
	// verified
	private String cos_v;// this is the command related to the condition, to
							// which variable 'c' refers
	private boolean first;
	private TreeMap<String, Boolean> valuation;
	private static boolean ValuationSet;

	public ExecBDDC(TreeMap<String, Boolean> valuation) {
		ValuationSet = false;
		this.valuation = valuation;
		Runtime rt = Runtime.getRuntime();
		try {
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows"))
				osName = "./bddc/bddc.exe";
			else if (osName.equals("Linux"))
				osName = "./bddc/bddcL";
			else if (osName.equals("Solaris"))
				osName = "./bddc/bddcS";
			else if (osName.equals("Mac"))
				osName = "./bddc/bddcM";
			else {
				System.err
						.println("Operating System not supported or os.name protected. Assume Windows architecture.");
				osName = "./bddc/bddc.exe";
			}
			p = rt.exec(osName);
			writer = new PrintWriter(p.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void init(String init) {// initialises a process by setting all the
									// facts (variables) to arguments

		setCommand(init);
		consumeOutput();
		first = true;// sets first to true, that means to load cos_v into memory
	}

	public void setCommand(String line) {// passes a command as input to the
											// process

		if (writer != null) {
			writer.print(line + "\n");// set the process input
			writer.flush();
		}
	}

	public void consumeOutput() {// simply consumes the process output. Useful
									// for initialization
		try {
			reader.read();
			while (reader.ready())
				// empties the buffer
				reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isViolated(String cond) {
		String value;
		String condition;
		char[] data = new char[1];
		if (ValuationSet == false) {
			for (String fID : valuation.keySet()) {// first, it sets the
													// variables to the ones of
													// the configuration or
													// partial configuration to
													// be verified
				value = valuation.get(fID).toString();
				if (value.equals("true")) {
					setCommand(fID + " := 1;");
					consumeOutput();// consumes the output from the previous
									// command as it is not needed
				} else if (value.equals("false")) {
					setCommand(fID + " := 0;");
					consumeOutput();// consumes the output from the previous
									// command as it is not needed
				}
			}
			ValuationSet = true;
		}
		condition = cond + ";";
		// cos_v="c := "+condition;
		setCommand(condition);// second, it verifies if the constraints are
								// still met
		try {
			reader.read(data, 0, 1);// '0' if constraints are violated,
									// something else otherwise
			while (reader.ready())
				// empties the buffer
				reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * for (String fID : valuation.keySet()){//third, it resets the
		 * configuration (all the facts are UNSET). This is necessary if later
		 * an XOR question is checked with isXOR() method
		 * value=valuation.get(fID).toString(); if (value.equals("true") ||
		 * value.equals("false")){ setFact(fID, "u"+fID.substring(1)); } }
		 */

		if (data[0] == '1')
			return true;
		else
			return false;
	}
}
