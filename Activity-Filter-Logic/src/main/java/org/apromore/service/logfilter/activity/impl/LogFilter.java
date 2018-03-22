package org.apromore.service.logfilter.activity.impl;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.collection.AlphanumComparator;
import org.processmining.plugins.log.logfilters.impl.DefaultLogFilter;
import org.processmining.plugins.log.logfilters.impl.EventLogFilter;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 25/4/17.
 */
public class LogFilter {

    private int percentage;
    private int nofSteps;
    private int eventTypeStep;
    private int eventFilterStep;
    private SimpleStep[] mySteps;
    private String[] classes_to_remove;

    /**
     * Runs the simple log filter on the given log.
     *
     * @param log
     *            The given log.
     * @return The filtered log (depends on the settings chosen by the user).
     */
    public XLog filter(XLog log, String[] classes_to_remove, int percentage) {
        this.percentage = percentage;
        nofSteps = 0;
        eventTypeStep = nofSteps++;
        eventFilterStep = nofSteps++;
        this.classes_to_remove = classes_to_remove;

        mySteps = new SimpleStep[nofSteps];

        mySteps[eventTypeStep] = new EventTypeStep();
        mySteps[eventFilterStep] = new EventFilterStep();

        for(int i = 0; i < 2; i++) {
            mySteps[i].initComponents(log);
            log = mySteps[i].getLog();
        }
        return log;
    }

    private class EventClassComparator implements Comparator<XEventClass> {

        public int compare(XEventClass o1, XEventClass o2) {
            // TODO Auto-generated method stub
            return (new AlphanumComparator().compare(o1.toString(), o2.toString()));
        }
    }


    /**
     * Simple step class. All steps belong to this class, but in the future
     * additional (non-simple) steps may be added.
     *
     * @author hverbeek
     *
     */
    private abstract class SimpleStep {

        protected XLog log;
        protected XEventClassifier classifier;
        protected List<String> listKeep = new ArrayList<>();
        protected List<String> listRemove = new ArrayList<>();
        protected XLogInfo logInfo;
        protected XEventClasses eventClasses;
        protected List<XEventClass> sortedEventClasses;

        /**
         * Creates the simple step.
         *
         * @param classifier
         *            The classifier to use in this step.
         */
        public SimpleStep(XEventClassifier classifier) {
            this.classifier = classifier;
        }

        public abstract XLog getLog();

        public void setLog(XLog log) {
            this.log = log;
        }

        /**
         /**
         * Initializes the component, given the log to filter.
         */
        public void initComponents(XLog log) {
            setLog(log);
            logInfo = XLogInfoImpl.create(log, classifier);
            eventClasses = logInfo.getEventClasses(classifier);
            sortedEventClasses = new ArrayList<XEventClass>(eventClasses.getClasses());
            Collections.sort(sortedEventClasses, new EventClassComparator());

            /**
             * Initialize the event classes.
             */

            // TODO Auto-generated method stub
            int size = 0;
            TreeSet<Integer> eventSizes = new TreeSet<Integer>();
            for (XEventClass event : sortedEventClasses) {
                size += event.size();
                eventSizes.add(event.size());
            }

            int treshold = size * percentage;
            int value = 0;
            while (100 * value < treshold) {
                int eventSize = eventSizes.last();
                eventSizes.remove(eventSize);
                for (XEventClass event : sortedEventClasses) {
                    if (event.size() == eventSize) {
                        value += eventSize;
                        listKeep.add(event.toString());
                    }else {
                        listRemove.add(event.toString());
                    }
                }
            }
        }
    }

    private class EventTypeStep extends SimpleStep {
        /**
         *
         */
        private static final long serialVersionUID = 1266880064535493470L;

        public EventTypeStep() {
            super(XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER);
        }

        public XLog getLog() {
            String[] toSkip = new String[0];
            DefaultLogFilter filter = new DefaultLogFilter();
            return filter.filter(null, log, classes_to_remove, toSkip);
        }

        public void initComponents(XLog log) {
            setLog(log);
        }
    }

    private class EventFilterStep extends SimpleStep {
        /**
         *
         */
        private static final long serialVersionUID = 2295002325162718535L;

        public EventFilterStep() {
            super(XLogInfoImpl.STANDARD_CLASSIFIER);
        }

        public XLog getLog() {
            String[] selectedIds = listKeep.toArray(new String[listKeep.size()]);
            EventLogFilter filter = new EventLogFilter();
            return filter.filterWithClassifier(null, log, classifier, selectedIds);
        }

    }
}

