package com.processconfiguration.cmapper;

// Java 2 Standard classes
import java.awt.GridLayout;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 * View of a {@link Cmapper}.
 */
class CmapperView extends JPanel {

  private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.CmapperView");

  /**
   * @param cmapper  the model
   */
  CmapperView(final Cmapper cmapper) {
    super(new GridLayout(1, 0));

    // Layout
    JPanel vpView = new JPanel();
    vpView.setLayout(new GridLayout(cmapper.getVariationPoints().size(), 1));

    // Construct the list of variation points
    for (VariationPoint vp: cmapper.getVariationPoints()) {
      VariationPointView view = new VariationPointView(vp, cmapper);
      vpView.add(view);
    }

    if (vpView.getComponentCount() == 0) {
      JLabel label = new JLabel(bundle.getString("No_variation_points_present"));
      vpView.add(label);
    }

    JScrollPane scrollPane = new JScrollPane(vpView);
    scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
    add(scrollPane);
  }
}
