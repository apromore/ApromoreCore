package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

// Local classes
import com.processconfiguration.qml.FactType;
import com.processconfiguration.qml.QMLType;
import com.processconfiguration.qml.QuestionType;

/**
 * View for a {@link QMLType} document.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class QmlFrame extends JFrame {

    private static Logger LOGGER = Logger.getLogger(QmlFrame.class.getCanonicalName());

    /**
     * Sole constructor.
     *
     * @param qml  the questionnaire to view
     */
     QmlFrame(final QMLType qml) {

         // We'll need to look up questionnaire facts by their ids later
         Map<String, FactType> factMap = new HashMap<>();
         for (FactType fact: qml.getFact()) {
             factMap.put(fact.getId(), fact);
         }

         // Create the tree
         DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(qml.getName());
         for (QuestionType question: qml.getQuestion()) {
             DefaultMutableTreeNode questionTreeNode = new DefaultMutableTreeNode(question.getId() + " - " + question.getDescription());
             List<String> mapQFL = question.getMapQFL();
             StringTokenizer st = new StringTokenizer(question.getMapQF(), " ");
             while(st.hasMoreTokens()){
                 String token = st.nextToken();
                 if (token.startsWith("#") && token.length() > 1) {
                     String answerId = token.substring(1);
                     String answerLabel = answerId;
                     if (factMap.containsKey(answerId)) {
                         answerLabel = answerLabel + " - " + factMap.get(answerId).getDescription();
                     }
                     DefaultMutableTreeNode answerTreeNode = new DefaultMutableTreeNode(answerLabel);
                     questionTreeNode.add(answerTreeNode);
                 } else {
                     LOGGER.warning("Skipping malformed fact link \"" + token + "\" in question " + question.getId());
                 }
             }
             rootTreeNode.add(questionTreeNode);
         }

         add(new JScrollPane(new JTree(rootTreeNode)));
         pack();
     }
}
