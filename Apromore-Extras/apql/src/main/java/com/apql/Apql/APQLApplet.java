package com.apql.Apql;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import org.apromore.model.UserType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class APQLApplet extends JApplet {
	private static final long serialVersionUID = 8477785817089483519L;


  	public void init() {
		super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                private Main m;
                private QueryText query;
                private UserType user;
                private ViewController controller;
                private QueryController queryController;

                public void run(){
                    controller=ViewController.getController();
                    queryController=QueryController.getQueryController();
//                    controller.clear();
//                    queryController.clearQueryController();
                    controller.setUsername(APQLApplet.this.getParameter("user"));
                    controller.setIdSession(APQLApplet.this.getParameter("idSession"));
                    controller.setApplet(APQLApplet.this);
                    m=new Main();
                    APQLApplet.this.add(m);
                    APQLApplet.this.setMinimumSize(new Dimension(1000, 700));
                    /*
                    APQLApplet.this.add(new JButton(new AbstractAction("Hide") {
                        public void actionPerformed(java.awt.event.ActionEvent event) {
                            System.err.println("The Hide button in the applet was pressed.");
                        }
                    }));
                    */

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
                        frame.setLocation(100, 100);
                        //frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                        //Create and set up the content pane.
                        Main main2 = new Main();
                        main2.setMinimumSize(new Dimension(1000, 700));
                        frame.setContentPane(main2);

                        //Display the window.
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
