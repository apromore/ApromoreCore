package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * View of a {@link Cmapper}.
 */
class CmapperView extends JPanel {

  /**
   * @param cmapper  the model
   */
  CmapperView(final Cmapper cmapper) {

    // Layout
    JPanel vpView = new JPanel();
    vpView.setLayout(new GridLayout(cmapper.getVariationPoints().size(), 1));

    // Construct the list of variation points
    for (VariationPoint vp: cmapper.getVariationPoints()) {
      vpView.add(new VariationPointView(vp));
    }

    if (vpView.getComponentCount() == 0) {
      vpView.add(new JLabel("No variation points present"));
    }

    add(new JScrollPane(vpView));
  }
}
