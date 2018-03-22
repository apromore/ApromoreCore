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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

// Java 2 Enterprise Edition
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// Third party packages
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Local classes
import org.apromore.service.dataflow.Dataflow;
import org.apromore.service.dataflow.Processor;

@Entity
@Table(name = "dataflow",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
        }
)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="discriminator",
    discriminatorType=DiscriminatorType.STRING
)
//@DiscriminatorValue("PredictorDO")
//@Configurable("predictor")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class DataflowImpl implements Dataflow {

    private static Logger LOGGER = LoggerFactory.getLogger(DataflowImpl.class.getCanonicalName());

    private DataflowRepository dataflowRepository;
    private AdminClient adminClient;

    private Integer id;
    private Set<Topic> topics = new HashSet<>();
    private Set<Processor> processors = new HashSet<>();

    // Database field accessors

    /** @return primary key */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /** @param newId  the new primary key */
    public void setId(final Integer newId) {
        this.id = newId;
    }

    @OneToMany(mappedBy = "dataflow", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> newTopics) {
        topics = newTopics;
    }

    @OneToMany(mappedBy = "dataflow", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Processor> getProcessors() {
        return processors;
    }

    public void setProcessors(Set<Processor> newProcessors) {
        processors = newProcessors;
    }

    // Implementation of Dataflow

    /**
     * This should only be accessed by {@link DataflowServiceImpl.createDataflow}.
     *
     * @param repository never <code>null</code>
     * @throws IllegalArgumentException if <var>repository</var> is <code>null</code>
     */
    void open(DataflowRepository newDataflowRepository, AdminClient newAdminClient) {
        assert newDataflowRepository != null;
        assert newAdminClient != null;

        this.dataflowRepository = newDataflowRepository;
        this.adminClient = newAdminClient;

        // Create Kafka topics
        List<NewTopic> newTopics = new ArrayList<>();
        for (Topic topic: topics) { newTopics.add(new NewTopic(topic.getName(), 1, (short) 1)); }
        CreateTopicsResult result = adminClient.createTopics(newTopics);
        try {
            LOGGER.info("Create topics result: " + result.all().get());
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.warn("Unable to confirm create topics", e);
        }

        // Create Kafka processors
        for (Processor processor: processors) { processor.open(); }
    }

    public void start() {
        if (dataflowRepository == null) { throw new IllegalStateException("Cannot start, already closed"); }
        for (Processor processor: processors) { processor.start(); }
    }

    public void stop() {
        if (dataflowRepository == null) { throw new IllegalStateException("Cannot stop, already closed"); }
        for (Processor processor: processors) { processor.stop(); }
    }

    public void close() {
        for (Processor processor: processors) { processor.close(); }

        List<String> exTopics = new ArrayList<>();
        for (Topic topic: topics) { exTopics.add(topic.getName()); }
        LOGGER.info("Deleting topics");
        DeleteTopicsResult result = adminClient.deleteTopics(exTopics);
        try {
            LOGGER.info("Deleted topics result: " + result.all().get());
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.warn("Unable to confirm delete topics", e);
        }

        dataflowRepository.deleteInBatch(Collections.singleton(this));

        dataflowRepository = null;
        adminClient = null;
    }
}
