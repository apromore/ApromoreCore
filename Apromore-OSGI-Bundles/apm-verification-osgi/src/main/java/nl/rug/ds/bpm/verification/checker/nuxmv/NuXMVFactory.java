package nl.rug.ds.bpm.verification.checker.nuxmv;

import nl.rug.ds.bpm.verification.checker.Checker;
import nl.rug.ds.bpm.verification.checker.CheckerFactory;

import java.io.File;

/**
 * Created by Heerko Groefsema on 09-Jun-17.
 */
public class NuXMVFactory extends CheckerFactory {

	public NuXMVFactory(File executable) {
		super(executable);
	}

	@Override
	public Checker getChecker() {
		return new NuXMVChecker(eventHandler, executable);
	}
}
