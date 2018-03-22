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

package org.apromore.service.predictivemonitor.impl;

// Java 2 Standard Edition
import java.io.File;
import java.io.IOException;

// Java 2 Enterprise Edition
import javax.persistence.Entity;

// Third party packages
import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Local classes
import org.apromore.service.dataflow.Processor;

/**
 * A Kafka {@link Processor} implemented as a shell command launched in a separate operating system {@link Process}.
 */
@Entity
public class CommandProcessor extends Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandProcessor.class.getCanonicalName());

    private String directory;
    private String command;
    private Process process;

    @Column(name = "command", nullable = false)
    public String getCommand() {
        return command;
    }

    /** @param newCommand  the new primary key */
    public void setCommand(final String newCommand) {
        this.command = newCommand;
    }

    @Column(name = "directory", nullable = false)
    public String getDirectory() {
        return directory;
    }

    /** @param newCommand  the new primary key */
    public void setDirectory(final String newDirectory) {
        this.directory = newDirectory;
    }

    @Override
    public void open() {
        File file = new File(directory);
        String[] args = command.split(" ");

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(file);
        pb.redirectError(new File("/tmp/error.txt") /*File.createTempFile("error", ".txt")*/);
        pb.redirectOutput(new File("/tmp/out.txt") /*File.createTempFile("output", ".txt")*/);
        try {
            process = pb.start();
            LOGGER.info("Started process for command: " + command);
        } catch (IOException e) {
            LOGGER.error("Unable to create process", e);
        }
    }

    @Override
    public void close() {
        process.destroy();
        try {
            int code = process.waitFor();
            LOGGER.info("Killed process with return code " + code + " for command: " + command);

        } catch (InterruptedException | RuntimeException e) {
            LOGGER.warn("Unable to kill process for command: " + command, e);
        }
    }
}
