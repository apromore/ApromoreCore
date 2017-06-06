package nl.rug.ds.bpm.variability.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Nick van Beest
 * @date 09/01/2017
 * 
 * This class switches the commandline output on or off
 */

public class BlackHole {

	private PrintStream stdout;
	private PrintStream blackhole;
	
	public BlackHole(PrintStream stdout) {
		this.stdout = stdout;
		this.blackhole = new PrintStream(new ByteArrayOutputStream());
	}
		
	public void hideOutput() {
		System.setOut(blackhole);
	}
	
	public void showOutput() {
		System.setOut(stdout);
	}
}
