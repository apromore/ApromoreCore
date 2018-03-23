package nl.rug.ds.bpm.verification.checker.nuxmv;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.verification.checker.nusmv2.NuSMVChecker;

import java.io.File;

public class NuXMVChecker extends NuSMVChecker {

    public NuXMVChecker(EventHandler eventHandler, File checker) {
        super(eventHandler, checker);
    }
}
