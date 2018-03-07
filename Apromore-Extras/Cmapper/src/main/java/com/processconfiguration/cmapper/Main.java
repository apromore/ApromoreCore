/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import com.processconfiguration.quaestio.ApromoreProcessModel;
import com.processconfiguration.quaestio.FileProcessModel;

/**
 * Present the Cmapper as a desktop application.
 */
class Main extends JFrame {

  private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.Main");

  /**
   * Main frame for the desktop application.
   *
   * @param cmapper  a configuration mapper
   */
  private Main(final Cmapper cmapper) {

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(createMenuBar(cmapper));
    setLocation(new java.awt.Point(100, 100));
    //setMinimumSize(new java.awt.Dimension(900, 600));
    setTitle(bundle.getString("title"));

    generateUI(cmapper);
  }

  private void generateUI(final Cmapper cmapper) {
    setContentPane(new CmapperView(cmapper));
    pack();
    setVisible(true);
  }

  /**
   * @param cmapper  a configuration mapper
   * @return the application menu bar
   */
  private JMenuBar createMenuBar(final Cmapper cmapper) {
    final File RESOURCES_DIRECTORY = new File("/Users/raboczi/Project/apromore/Apromore-Extras/bpmncmap/src/test/resources");
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu(bundle.getString("File"));
    menuBar.add(fileMenu);

    JMenuItem openModel = fileMenu.add(new AbstractAction(bundle.getString("Open_model")) {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("BPMN_process_model"), "bpmn"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setModel(new FileProcessModel(chooser.getSelectedFile()));
            generateUI(cmapper);
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, bundle.getString("Unable_to_open_model") + e, bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem openQml = fileMenu.add(new AbstractAction(bundle.getString("Open_qml")) {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("QML_questionnaire"), "qml"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setQml(new FileQml(chooser.getSelectedFile()));
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, bundle.getString("Unable_to_open_qml") + e, bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem openCmap = fileMenu.add(new AbstractAction(bundle.getString("Open_cmap")) {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("Configuration_mapping"), "cmap"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setCmap(new FileCmap(chooser.getSelectedFile()));
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, bundle.getString("Unable_to_open_cmap") + e, bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem saveCmap = fileMenu.add(new AbstractAction(bundle.getString("Save_cmap")) {
      public void actionPerformed(ActionEvent event) {
        try {
          // Has a questionnaire been selected?
          if (!cmapper.isQmlSet()) {
            // Confirm whether the genuine intention is to save without a QML file
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(
              null,
              bundle.getString("Save_cmap_without_qml?"),
              bundle.getString("Warning"),
              JOptionPane.OK_CANCEL_OPTION)) { return;  /* user cancelled the save */ }
          }

          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("Configuration_mapping"), "cmap"));
          if (chooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            cmapper.save(new FileCmap(file));
            JOptionPane.showMessageDialog(
              null,
              bundle.getString("Saved_cmap_as") + chooser.getSelectedFile(),
              bundle.getString("Saved"),
              JOptionPane.INFORMATION_MESSAGE
            );
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(
            null,
            bundle.getString("Unable_to_save_cmap") + e,
            bundle.getString("Error"),
            JOptionPane.ERROR_MESSAGE
          );
        }
      }
    });

    JMenuItem quit = fileMenu.add(new AbstractAction(bundle.getString("Quit")) {
      public void actionPerformed(ActionEvent event) {
        Main.this.dispose();
      }
    });

    return menuBar;
  }

  /**
   * Entry point for running the configuration mapper from the command line.
   *
   * @param argv  command line arguments
   */
  public static void main(String[] argv) throws Exception {

    final Cmapper cmapper = new Cmapper();

    // Parse command line arguments, initializing the model
    int j = -1;  // the index within args in which "-apromore_model" occurs
    String user = null;
    for (int i = 0; i < argv.length; i++) {
      switch (argv[i]) {
      case "-apromore_model":
        if (i+3 >= argv.length) {
          throw new IllegalArgumentException("-apromore_model without id/branch/version");
        }
        j = i;
        i += 3;
        break;

      case "-cmap":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-cmap without filename");
        }
        cmapper.setCmap(new FileCmap(new File(argv[i])));
        break;

      case "-model":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-model without filename");
        }
        cmapper.setModel(new FileProcessModel(new File(argv[i])));
        break;

      case "-qml":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-qml without filename");
        }
        cmapper.setQml(new FileQml(new File(argv[i])));
        break;

      case "-user":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-user without user name");
        }
        user = argv[i];
        break;

      default:
        throw new IllegalArgumentException("Unknown parameter: " + argv[i]);
      }
    }

    // -apromore_model and -user can occur in either order, so we've deferred processing them until here
    if (j != -1) {
      cmapper.setModel(new ApromoreProcessModel(
        new URI("http://localhost:80/manager/services"),  // manager SOAP endpoint
        Integer.valueOf(argv[j+1]),  // process ID
        argv[j+2],                   // branch
        argv[j+3],                   // version number
        null,                        // Swing parent component
        user                         // user name
      ));
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          new Main(cmapper);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
