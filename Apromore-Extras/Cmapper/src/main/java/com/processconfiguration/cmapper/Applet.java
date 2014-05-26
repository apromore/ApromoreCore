package com.processconfiguration.cmapper;

// Java 2 Standard classes
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Execute the Cmapper as an applet.
 */
public class Applet extends JApplet {

    public void init() {
        super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Cmapper cmapper = new Cmapper();
		        //add(new View(cmapper));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
