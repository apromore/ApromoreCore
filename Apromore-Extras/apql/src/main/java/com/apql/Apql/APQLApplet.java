/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.xml.soap.SOAPException;

import com.apql.Apql.controller.ViewController;
import org.apromore.model.UserType;

public class APQLApplet extends JApplet {
	private static final long serialVersionUID = 8477785817089483519L;


  	public void init() {
		super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run(){
                    ViewController controller = ViewController.getController();
                    controller.setUsername(APQLApplet.this.getParameter("user"));
                    controller.setIdSession(APQLApplet.this.getParameter("idSession"));
                    controller.setApplet(APQLApplet.this);
                    try {
                        final Main main = new Main(new URI(APQLApplet.this.getParameter("manager_endpoint")),
                                             new URI(APQLApplet.this.getParameter("portal_endpoint")),
                                             new URI(APQLApplet.this.getParameter("pql_logic_endpoint"))) {
                            @Override
                            protected void browse(final URL url) throws Exception {
                                getAppletContext().showDocument(url, "target");
                            }
                        };
                        APQLApplet.this.add(main);
                        APQLApplet.this.setMinimumSize(new Dimension(1000, 700));
                    } catch (SOAPException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                    try {
                        String OS = System.getProperty("os.name").toLowerCase();
                        if(OS.indexOf("mac") >= 0) {
                            for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
                                if (lafInfo.getName().toLowerCase().contains("mac")) {
                                    UIManager.setLookAndFeel(lafInfo.getClassName());
                                    break;
                                }
                            }
                        }else {
                            UIManager.setLookAndFeel(new ToolTipLookAndFeel());
                        }
                    } catch(Throwable ex){
			ex.printStackTrace();
                    }

                    /*
                    System.out.println("Spawning independent frame");

                    try {
                        JFrame frame = new JFrame();
                        frame.setTitle("Independent Frame");
                        frame.setLocationRelativeTo(null);
                        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                        //Create and set up the content pane.
                        Main main2 = new Main();
                        frame.setContentPane(main2);

                        //Display the window.
                        frame.setMinimumSize(new Dimension(1000, 700));
                        frame.pack();
                        frame.setVisible(true);

                        System.out.println("Spawning independent frame");
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    */
                }
            });
        } catch (Exception ex){
	    ex.printStackTrace();
        }

//            try
//
//            {
//
//            }
//
//            catch(
//            Exception ex
//            )
//
//            {
//                System.err.println("ToolTipLookAndFeel exception!");
//                System.err.println(ex.getMessage());
//            }
//
//            setMinimumSize(new Dimension(1000, 500)
//
//            );
//
//            setLocation(200,200);
//
//            m=new
//
//            Main();
//
//            add(m);

        }

    public void destroy(){
        super.destroy();
    }

    class ToolTipLookAndFeel extends MetalLookAndFeel
	{
	    protected void initSystemColorDefaults(UIDefaults table)
	    {        
	        super.initSystemColorDefaults(table);        
	        table.put("info", new ColorUIResource(255, 247, 200));    
	    }

	    protected void initComponentDefaults(UIDefaults table) {
	        super.initComponentDefaults(table);

	    Border border = BorderFactory.createLineBorder(new Color(76,79,83));
	    table.put("ToolTip.border", border);
	    }
	}

}
