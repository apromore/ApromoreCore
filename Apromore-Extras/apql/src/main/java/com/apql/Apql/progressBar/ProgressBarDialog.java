package com.apql.Apql.progressBar;

/**
 * Created by corno on 15/08/2014.
 */

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ProgressBarDialog extends JPanel
        implements PropertyChangeListener {

    private JProgressBar progressBar;
    private Task task;
    private int progress = 0;
    private JDialog dialog;

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
//            Random random = new Random();

            //Initialize progress property.
            setProgress(99);
            while (progress < 100) {
//                //Sleep for up to one second.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
//                //Make random progress.
//                progress += random.nextInt(10);
//                setProgress(Math.min(progress, 99));
            }
            dialog.setModal(false);
            dialog.setVisible(false);
            dialog=null;
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
//            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }

    public void setProgress(int progress){
        System.out.println("Set Progress to " + progress);
        this.progress=progress;
    }

    public ProgressBarDialog(JDialog dialog) {
        super(new BorderLayout());
        this.dialog=dialog;
//        dialog.setModal(true);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        JPanel panel = new JPanel();
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }


    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.

    }
}