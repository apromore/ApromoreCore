/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.predictivemonitor.impl;

// Java 2 Standard Editions
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

// Java 2 Enterprise Edition
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;

// Third party packages
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

// Local classes
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorEvent;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

@Service("predictiveMonitorService")
public class PredictiveMonitorServiceImpl implements PredictiveMonitorService {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorServiceImpl.class.getCanonicalName());

    private AdminClient adminClient;
    private String kafkaHost;
    private String controlTopic;
    private String eventsTopic;
    private String prefixesTopic;
    private String predictionsTopic;
    private PredictiveMonitorRepository predictiveMonitorRepository;
    private PredictiveMonitorEventRepository predictiveMonitorEventRepository;
    private PredictorRepository predictorRepository;
    private JdbcTemplate jdbcTemplate;

    final private Consumer<String, String> consumer;
    final private Thread                   consumerThread;
    final private AtomicBoolean            closed = new AtomicBoolean(false);
    final private Map<Observer, Object>    observerMap = new WeakHashMap<>();

    @Inject public PredictiveMonitorServiceImpl(
        @Named("kafkaHost") String        kafkaHost,
        @Named("eventsTopic") String      eventsTopic,
        @Named("prefixesTopic") String    prefixesTopic,
        @Named("predictionsTopic") String predictionsTopic,
        @Named("controlTopic") String     controlTopic,
        PredictiveMonitorRepository       predictiveMonitorRepository,
        PredictiveMonitorEventRepository  predictiveMonitorEventRepository,
        PredictorRepository               predictorRepository,
        JdbcTemplate                      jdbcTemplate)
    {
        assert kafkaHost != null;
        assert eventsTopic != null;
        assert prefixesTopic != null;
        assert predictionsTopic != null;
        assert controlTopic != null;
        assert predictiveMonitorRepository != null;
        assert predictiveMonitorEventRepository != null;
        assert predictorRepository != null;
        assert jdbcTemplate != null;

        this.predictiveMonitorRepository = predictiveMonitorRepository;
        this.predictiveMonitorEventRepository = predictiveMonitorEventRepository;
        this.predictorRepository = predictorRepository;
        this.jdbcTemplate = jdbcTemplate;

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHost);

        this.adminClient = AdminClient.create(props);
        this.kafkaHost = kafkaHost;
        this.eventsTopic = eventsTopic;
        this.prefixesTopic = prefixesTopic;
        this.predictionsTopic = predictionsTopic;
        this.controlTopic = controlTopic;

        props.put("group.id", "apromore");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", StringDeserializer.class);
        consumer = new KafkaConsumer(props);
        consumerThread = new Thread(new Runnable() {
            public void run() {
                LOGGER.info("Running prediction consumer");
                try {
                    consumer.subscribe(Arrays.asList(prefixesTopic, predictionsTopic));
                    while (!closed.get()) {
                        LOGGER.info("Polling prediction consumer");
                        ConsumerRecords<String, String> records = consumer.poll(60000);
                        LOGGER.info("Polled prediction consumer, obtained " + records.count() + " records");
                        for (ConsumerRecord<String, String> record: records) {
                            try {
                                if (record.topic().equals(prefixesTopic)) {
                                    JSONObject event = new JSONObject(record.value());
                                    JSONArray prefix = event.getJSONArray("prefix");
                                    JSONObject lastEvent = prefix.getJSONObject(prefix.length() - 1);
                                    LOGGER.info("Prefix: " + lastEvent);
                                    PredictiveMonitor predictiveMonitor = findPredictiveMonitorById(event.getInt("log_id"));
                                    if (predictiveMonitor != null) {
                                        upsertEvent(predictiveMonitor, event.getString("case_id"), prefix.length(), lastEvent);
                                    } else {
                                        LOGGER.warn("Discarding prediction for nonexistent log " + event.optInt("log_id"));
                                    }
                                    
                                } else if (record.topic().equals(predictionsTopic)) {
                                    JSONObject json = new JSONObject(record.value());
                                    LOGGER.info("Prediction: " + json);
                                    PredictiveMonitor predictiveMonitor = findPredictiveMonitorById(json.getInt("log_id"));
                                    if (predictiveMonitor != null) {
                                        JSONObject jsonPredictions = new JSONObject();
                                        jsonPredictions.put("predictions", json.getJSONObject("predictions"));
                                        upsertEvent((PredictiveMonitorImpl) predictiveMonitor, json.getString("case_id"), json.getInt("event_nr"), jsonPredictions);

                                    } else {
                                        LOGGER.warn("Discarding prediction for nonexistent log " + json.getInt("log_id"));
                                    }

                                } else {
                                    LOGGER.warn("Discarding consumer record from unrecognized topic " + record.topic());
                                }

                            } catch (JSONException e) {
                                LOGGER.warn("Unable to parse consumer record as JSON object: " + record.value(), e);
                            }
                        }
                    }
                } catch (WakeupException e) {
                    LOGGER.error("Unexpected failure in Kafka consumer thread", e);
                    if (!closed.get()) { 
                        LOGGER.error("Wakeup exception while Kafka consumer thread wasn't closed", e);
                    }   
                } catch (Throwable e) {
                    LOGGER.error("Unexpected failure in Kafka consumer thread", e);
                } finally {
                    LOGGER.info("Closing prediction consumer");
                    consumer.close();
                    LOGGER.info("Closed prediction consumer");
                }   
                LOGGER.info("Ran prediction consumer - thread now terminated");
            }   
        }); 
        LOGGER.info("Starting prediction consumer");
        consumerThread.start();
        LOGGER.info("Started prediction consumer");
    }

    @Override
    public void finalize() {
        LOGGER.info("Signaling prediction consumer thread to terminate");
        closed.set(true);
        consumer.wakeup();
        LOGGER.info("Signaled prediction consumer thread to terminate");
    }

    public void addObserver(Observer observer) {
        observerMap.put(observer, null);
    }


    // Predictor management

    public Predictor createPredictor(String name, String type, InputStream pklFile) {
        LOGGER.info("Create predictor " + name + " of type " + type);

        try {
            //Blob blob = jdbcTemplate.getDataSource().getConnection().createBlob();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //int bytesCopied = IOUtils.copy(pklFile, blob.setBinaryStream(1));
            int bytesCopied = IOUtils.copy(pklFile, baos);

            PredictorImpl predictor = new PredictorImpl();
            predictor.setName(name);
            predictor.setType(type);
            //predictor.setPkl(blob);
            predictor.setPkl(baos.toByteArray());
            predictorRepository.saveAndFlush(predictor);

            //blob.free();
            return predictor;

        } catch (IOException /*| SQLException*/ e) {
            LOGGER.error("Unable to create predictor " + name + " of type " + type, e);
            return null;
        }

    }

    public void deletePredictors(Iterable<Predictor> predictors) {
        List<PredictorImpl> list = new LinkedList<>();
        for (Predictor predictor: predictors) {
            list.add((PredictorImpl) predictor);
        }
        predictorRepository.deleteInBatch(list);
    }

    public Predictor findPredictorById(Integer id) {
        try {
            return predictorRepository.findById(id);

        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public Predictor findPredictorByName(String name) {
        try {
            return predictorRepository.findByName(name);

        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public List<Predictor> getPredictors() {
        return new ArrayList<>(predictorRepository.findAll());
    }


    // PredictiveMonitor management

    public PredictiveMonitor createPredictiveMonitor(String name, List<Predictor> predictors) {
        LOGGER.info("Create predictive monitor " + name);

        PredictiveMonitorImpl predictiveMonitor = new PredictiveMonitorImpl();
        predictiveMonitor.setName(name);
        predictiveMonitor.setPredictorImpls(toPredictorImplSet(predictors));
        predictiveMonitorRepository.saveAndFlush(predictiveMonitor);

        notifyPredictiveMonitorsChange();

        return predictiveMonitor;
    }

    private static Set<PredictorImpl> toPredictorImplSet(Iterable<Predictor> iterable) {
        Set<PredictorImpl> predictorImpls = new HashSet<>();
        for (Predictor predictor: iterable) {
            predictorImpls.add((PredictorImpl) predictor);
        }
        return predictorImpls;
    }

    public void deletePredictiveMonitors(Iterable<PredictiveMonitor> predictiveMonitors) {
        List<PredictiveMonitorImpl> list = new LinkedList<>();
        for (PredictiveMonitor predictiveMonitor: predictiveMonitors) {
            list.add((PredictiveMonitorImpl) predictiveMonitor);
        }
        predictiveMonitorRepository.deleteInBatch(list);

        notifyPredictiveMonitorsChange();
    }

    public PredictiveMonitor findPredictiveMonitorById(Integer id) {
        try {
            return predictiveMonitorRepository.findById(id);

        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public PredictiveMonitor findPredictiveMonitorByName(String name) {
        return predictiveMonitorRepository.findByName(name);
    }

    public List<PredictiveMonitor> getPredictiveMonitors() {
        return new ArrayList<>(predictiveMonitorRepository.findAll());
    }

    /** This method cannot be used due to an unsolved Virgo compatibility issue. */
    public void exportLogToPredictiveMonitor(XLog log, PredictiveMonitor predictiveMonitor) throws Exception {
        LOGGER.info("Exporting log to " + predictiveMonitor.getName());

        // Configuration
        Properties props = new Properties();
        props.put("bootstrap.servers",  kafkaHost);
        props.put("acks",               "all");
        props.put("retries",            0);
        props.put("batch.size",         16384);
        props.put("linger.ms",          1);
        //props.put("timeout.ms",         5000);
        props.put("max.block.ms",       5000);
        //props.put("metadata.fetch.timeout.ms", 5000);
        //props.put("request.timeout.ms", 5000);
        props.put("buffer.memory",      33554432);
        props.put("key.serializer",     StringSerializer.class);
        props.put("value.serializer",   StringSerializer.class);

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {

            // Merge the XES events of all traces into a single list of JSON objects
            List<JSONObject> jsons = new ArrayList<>();
            for (XTrace trace: log) {
                for (XEvent event: trace) {
                    JSONObject json = new JSONObject();
                    json.put("log_id", predictiveMonitor.getId());

                    JSONObject logProperties = new JSONObject();
                    addAttributes(log, logProperties);
                    json.put("log_attributes", logProperties);

                    JSONObject traceProperties = new JSONObject();
                    addAttributes(trace, traceProperties);
                    json.put("case_attributes", traceProperties);

                    JSONObject eventProperties = new JSONObject();
                    addAttributes(event, eventProperties);
                    json.put("event_attributes", eventProperties);

                    jsons.add(json);
                }
            }

            // Sort the events by time
            final DatatypeFactory f = DatatypeFactory.newInstance();
            /*
            The existence of the Comparator class will cause Virgo to fail to load this plugin.
            See: https://jira.spring.io/browse/SPR-11719 as a best guess at why this might be

            Collections.sort(jsons, new Comparator<JSONObject>() {
                public int compare(JSONObject a, JSONObject b) {
                    return f.newXMLGregorianCalendar(a.optJSONObject("event_attributes").optString("time:timestamp"))
                  .compare(f.newXMLGregorianCalendar(b.optJSONObject("event_attributes").optString("time:timestamp")));
                }
            });
            */
            if (true) { throw new Error("Uncomment lines 371-380 of PredictiveMonitorServiceImpl and recompile to re-enable the export method."); }

            // Export to the Kafka topic
            JSONArray columns = new JSONArray();
            for (Predictor predictor: predictiveMonitor.getPredictors()) {
                columns.put(predictor.getId());
            }
            for (JSONObject json: jsons) {
                json.put("predictors", columns);
                producer.send(new ProducerRecord<String,String>(eventsTopic, json.toString()));
            }
            LOGGER.info("Exported " + jsons.size() + " log events");

        } catch (DatatypeConfigurationException | JSONException e) {
            throw new Exception("Unable to export log", e);
        }
        LOGGER.info("Exported log to " + predictiveMonitor.getName());
    }

    private static void addAttributes(XAttributable attributable, JSONObject json) throws JSONException {
        for (Map.Entry<String, XAttribute> entry: attributable.getAttributes().entrySet()) {
            json.put(entry.getKey(), entry.getValue().toString());
        }
    }

    /**
     * Broadcast the current list of predictive monitors via the control topic.
     *
     * This allows Kafka processors to discard work for deleted predictive monitors.
     */
    private void notifyPredictiveMonitorsChange() {
        Properties props = new Properties();
        props.put("bootstrap.servers",  kafkaHost);
        props.put("acks",               "all");
        props.put("retries",            0);
        props.put("batch.size",         16384);
        props.put("linger.ms",          1);
        //props.put("timeout.ms",         5000);
        props.put("max.block.ms",       5000);
        //props.put("metadata.fetch.timeout.ms", 5000);
        //props.put("request.timeout.ms", 5000);
        props.put("buffer.memory",      33554432);
        props.put("key.serializer",     StringSerializer.class);
        props.put("value.serializer",   StringSerializer.class);

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            JSONArray json = new JSONArray();
            for (PredictiveMonitorImpl predictiveMonitor: predictiveMonitorRepository.findAll()) {
                json.put(predictiveMonitor.getId());
            }
            producer.send(new ProducerRecord<String,String>(controlTopic, json.toString()));
        }
    }


    // PredictiveMonitorEvent management

    public PredictiveMonitorEventImpl upsertEvent(PredictiveMonitor predictiveMonitor, String caseId, Integer eventNr, JSONObject json) {
        LOGGER.info("Populate event monitor=" + predictiveMonitor.getName() + " case=" + caseId + " event=" + eventNr);

        PredictiveMonitorEventImpl predictiveMonitorEvent = predictiveMonitorEventRepository.findByPredictiveMonitorAndCaseIdAndEventNr((PredictiveMonitorImpl) predictiveMonitor, caseId, eventNr);
        if (predictiveMonitorEvent == null) {
            predictiveMonitorEvent = new PredictiveMonitorEventImpl();
            predictiveMonitorEvent.setPredictiveMonitor((PredictiveMonitorImpl) predictiveMonitor);
            predictiveMonitorEvent.setCaseId(caseId);
            predictiveMonitorEvent.setEventNr(eventNr);
            predictiveMonitorEvent.setJson(json.toString());

        } else {
            try {
                JSONObject oldJson = new JSONObject(predictiveMonitorEvent.getJson());
                JSONObject mergedJson = deepMerge(json, oldJson);
                predictiveMonitorEvent.setJson(mergedJson.toString());

            } catch (JSONException e) {
                LOGGER.error("Unable to merge existing event JSON data with new data", e);
                return predictiveMonitorEvent;
            }
        }
        
        predictiveMonitorEventRepository.saveAndFlush(predictiveMonitorEvent);

        for (Observer observer: observerMap.keySet()) {
            try {
                observer.update(null, null);;
            } catch (Throwable e) {
                LOGGER.warn("Observer update failed", e);
            }
        }

        return predictiveMonitorEvent;
    }

    public static JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
        for (String key: JSONObject.getNames(source)) {
            Object value = source.get(key);
            if (!target.has(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject)value;
                    deepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }

    public List<PredictiveMonitorEvent> findPredictiveMonitorEvents(PredictiveMonitor predictiveMonitor) {
        return new ArrayList<>(predictiveMonitorEventRepository.findByPredictiveMonitor((PredictiveMonitorImpl) predictiveMonitor));
    }

    public PredictiveMonitorEvent findPredictiveMonitorEvent(PredictiveMonitor predictiveMonitor, String caseId, Integer eventNr) {
        return predictiveMonitorEventRepository.findByPredictiveMonitorAndCaseIdAndEventNr((PredictiveMonitorImpl) predictiveMonitor, caseId, eventNr);
    }
}
