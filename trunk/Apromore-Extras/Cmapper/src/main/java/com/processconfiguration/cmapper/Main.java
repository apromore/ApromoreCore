package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    setTitle("Synergia - Cmapper v. 2.0");

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

    JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);

    JMenuItem openModel = fileMenu.add(new AbstractAction("Open model...") {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter("BPMN process models", "bpmn"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setBpmn(new FileProcessModel(chooser.getSelectedFile()));
            generateUI(cmapper);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Unable to open model: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem openQml = fileMenu.add(new AbstractAction("Open questionnaire...") {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter("QML questionnaires", "qml"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setQml(chooser.getSelectedFile());
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Unable to open questionnaire: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem saveCmap = fileMenu.add(new AbstractAction("Save configuration mapping...") {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter("Configuration mapping", "cmap"));
          if (chooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.writeCmap(chooser.getSelectedFile());
            JOptionPane.showMessageDialog(
              null,
              "Saved configuration mapping as " + chooser.getSelectedFile(),
              "Saved",
              JOptionPane.INFORMATION_MESSAGE
            );
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Unable to save configuration mapping: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem quit = fileMenu.add(new AbstractAction("Quit") {
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
    for (int i = 0; i < argv.length; i++) {
      switch (argv[i]) {
      case "-apromore_model":
        if (i+3 >= argv.length) {
          throw new IllegalArgumentException("-apromore_model without id/branch/version");
        }
        cmapper.setBpmn(
          new ApromoreProcessModel(Integer.valueOf(argv[i+1]),  // process ID
                                                   argv[i+2],                   // branch
                                                   argv[i+3],                   // version number
                                                   null)
        );
        i += 3;
        break;

      case "-cmap":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-cmap without filename");
        }
        cmapper.setCmap(new File(argv[i]));
        break;

      case "-model":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-model without filename");
        }
        cmapper.setBpmn(new FileProcessModel(new File(argv[i])));
        break;

      case "-qml":
        if (++i >= argv.length) {
          throw new IllegalArgumentException("-qml without filename");
        }
        cmapper.setQml(new File(argv[i]));
        break;

      default:
        throw new IllegalArgumentException("Unknown parameter: " + argv[i]);
      }
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          //createAndShowGUI(cmapper);
          new Main(cmapper);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
