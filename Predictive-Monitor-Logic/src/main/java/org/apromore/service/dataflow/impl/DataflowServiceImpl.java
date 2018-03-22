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

package org.apromore.service.dataflow.impl;

// Java 2 Standard Edition
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

// Java 2 Enterprise Edition
import javax.inject.Inject;
import javax.inject.Named;

// Third party packages
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// Local classes
import org.apromore.service.dataflow.Dataflow;
import org.apromore.service.dataflow.DataflowService;
import org.apromore.service.dataflow.Processor;

@Service("dataflowService")
public class DataflowServiceImpl implements DataflowService {

    private static Logger LOGGER = LoggerFactory.getLogger(DataflowServiceImpl.class.getCanonicalName());

    private AdminClient adminClient;
    private DataflowRepository dataflowRepository;

    @Inject public DataflowServiceImpl(@Named("kafkaHost") String kafkaHost, DataflowRepository dataflowRepository) {
        assert kafkaHost != null;
        assert dataflowRepository != null;

        this.dataflowRepository = dataflowRepository;

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHost);
        this.adminClient = AdminClient.create(props);
    }

    public Dataflow createDataflow(Collection<String> topicNames, Collection<Processor> processors) {

        // Record the dataflow and its topics in the DB
        DataflowImpl result = new DataflowImpl();
        result.setTopics(toTopics(topicNames, result));
        for (Processor processor: processors) { processor.setDataflow(result); }
        result.setProcessors(new HashSet<Processor>(processors));
        result = dataflowRepository.saveAndFlush(result);

        // Create the Kafka topics and processors
        result.open(dataflowRepository, adminClient);

        return result;
    }

    private static Set<Topic> toTopics(Collection<String> topicNames, DataflowImpl dataflow) {
        Set<Topic> topics = new HashSet<>();
        for (String topicName: topicNames) {
            Topic topic = new Topic();
            topic.setDataflow(dataflow);
            topic.setName(topicName);
            topics.add(topic);
        }
        return topics;
    }
}
