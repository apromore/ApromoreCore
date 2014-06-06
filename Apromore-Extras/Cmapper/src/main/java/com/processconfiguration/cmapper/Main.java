package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
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
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("BPMN_process_models"), "bpmn"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setBpmn(new FileProcessModel(chooser.getSelectedFile()));
            generateUI(cmapper);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, bundle.getString("Unable_to_open_model") + e, bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem openQml = fileMenu.add(new AbstractAction(bundle.getString("Open_questionnaire")) {
      public void actionPerformed(ActionEvent event) {
        try {
          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("QML_questionnaires"), "qml"));
          if (chooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.setQml(new FileInputStream(chooser.getSelectedFile()));
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, bundle.getString("Unable_to_open_questionnaire") + e, bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem saveCmap = fileMenu.add(new AbstractAction(bundle.getString("Save_configuration_mapping")) {
      public void actionPerformed(ActionEvent event) {
        try {
          // Has a questionnaire been selected?
          if (!cmapper.isQmlSet()) {
            // Confirm whether the genuine intention is to save without a QML file
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(
              null,
              "No questionnaire has been selected.  Save cmap anyway?",
              "Warning",
              JOptionPane.OK_CANCEL_OPTION)) { return;  /* user cancelled the save */ }
          }

          JFileChooser chooser = new JFileChooser(RESOURCES_DIRECTORY);
          chooser.setFileFilter(new FileNameExtensionFilter(bundle.getString("Configuration_mapping"), "cmap"));
          if (chooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
            cmapper.writeCmap(chooser.getSelectedFile());
            JOptionPane.showMessageDialog(
              null,
              bundle.getString("Saved_configuration_mapping_as") + chooser.getSelectedFile(),
              bundle.getString("Saved"),
              JOptionPane.INFORMATION_MESSAGE
            );
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
            null,
            bundle.getString("Unable_to_save_configuration_mapping") + e,
            bundle.getString("Error"),
            JOptionPane.ERROR_MESSAGE
          );
          e.printStackTrace();
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
    for (int i = 0; i < argv.length; i++) {
      switch (argv[i]) {
      case "-apromore_model":
        if (i+3 >= argv.length) {
          throw new IllegalArgumentException("-apromore_model without id/branch/version");
        }
        cmapper.setBpmn(
          new ApromoreProcessModel(Integer.valueOf(argv[i+1]),  // process ID
                                                   argv[i+2],   // branch
                                                   argv[i+3],   // version number
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
        cmapper.setQml(new FileInputStream(argv[i]));
        break;

      default:
        throw new IllegalArgumentException("Unknown parameter: " + argv[i]);
      }
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
