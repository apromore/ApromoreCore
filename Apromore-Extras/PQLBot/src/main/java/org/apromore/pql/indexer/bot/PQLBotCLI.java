/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.pql.indexer.bot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jbpt.persist.MySQLConnection;
import org.pql.core.PQLBasicPredicatesMC;
import org.pql.index.IndexType;
import org.pql.index.PQLIndexMySQL;
import org.pql.label.ILabelManager;
import org.pql.label.LabelManagerLevenshtein;
import org.pql.label.LabelManagerLuceneVSM;
import org.pql.label.LabelManagerType;
import org.pql.label.LabelManagerThemisVSM;
import org.pql.mc.LoLA2ModelChecker;

import org.pql.bot.AbstractPQLBot;
import org.pql.bot.PQLBot;

/**
 * Reimplementation of the PQL Bot command line interface.
 */
public class PQLBotCLI {
    final private static String    version    = "1.2";
    
    public static void main(String[] args) throws AbstractPQLBot.NameInUseException, InterruptedException, ClassNotFoundException, SQLException, IOException {
        // read parameters from the CLI
        CommandLineParser parser = new DefaultParser();
        
        Connection connection = null;
        
        String botName = null;
        int    sleepTime = 0;
        int    indexTime = 0;
        String labelSimilaritySearch = "LUCENE";
        String labelSimilarityConfig = null;
        String lola = null;;
        double defaultLabelSimilarityThreshold = 0;
        String indexedLabelSimilarityThresholds = null;
        
        try {
            // create Options object
            Options options = new Options();
            
            // create options
            Option helpOption    = Option.builder("h").longOpt("help").optionalArg(true).desc("print this message").hasArg(false).build();
            Option versionOption = Option.builder("v").longOpt("version").optionalArg(true).desc("get version of this tool").hasArg(false).build();
            Option nameOption    = Option.builder("n").longOpt("name").hasArg().optionalArg(true).desc("name of this bot (maximum 36 characters)").valueSeparator().argName("string").build();
            Option sleepOption   = Option.builder("s").longOpt("sleep").hasArg().optionalArg(true).desc("time to sleep between indexing jobs (in seconds)").valueSeparator().argName("number").build();
            Option indexOption   = Option.builder("i").longOpt("index").hasArg().optionalArg(true).desc("maximal indexing time (in seconds)").valueSeparator().argName("number").build();
            
            // add options
            options.addOption(helpOption);
            options.addOption(versionOption);
            options.addOption(nameOption);
            options.addOption(sleepOption);
            options.addOption(indexOption);
            
            options.addOption(Option.builder("d").longOpt("database").hasArg().desc("MySQL database (a JDBC URL)").valueSeparator().argName("URL").build());
            options.addOption(Option.builder("u").longOpt("user").hasArg().desc("MySQL user name").valueSeparator().argName("string").build());
            options.addOption(Option.builder("p").longOpt("password").hasArg().desc("MySQL password").valueSeparator().argName("string").build());
            options.addOption(Option.builder("a").longOpt("labeltype").hasArg().desc("Label similarity algorithm (LEVENSHTEIN, LUCENE, THEMIS_VSM)").valueSeparator().argName("string").build());
            options.addOption(Option.builder("r").longOpt("labelrepo").hasArg().optionalArg(true).desc("Lucene repository").valueSeparator().argName("path").build());
            options.addOption(Option.builder("l").longOpt("lola").hasArg().desc("LoLA2 executable path").valueSeparator().argName("path").build());
            options.addOption(Option.builder("t").longOpt("threshold").hasArg().desc("default similarity threshold (comma-separated numbers)").valueSeparator().argName("numbers").build());
            options.addOption(Option.builder("T").longOpt("thresholds").hasArg().desc("similarity thresholds").valueSeparator().argName("string").build());

            // parse the command line arguments
            CommandLine cmd = parser.parse(options, args);
            
            // handle version
            if(cmd.hasOption("v")) {
                System.out.println(PQLBotCLI.version);
                return;
            }
            
            System.out.println("===============================================================================");
            System.out.println(String.format(" Process Query Language (PQL) Bot ver. %s by Artem Polyvyanyy", PQLBotCLI.version));
            System.out.println("===============================================================================");
            
            // handle help
            if(cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("PQL",options);
                System.out.println("===============================================================================");
                return;
            }
            
            // read parameters
            connection = (new MySQLConnection(cmd.getOptionValue("d"), cmd.getOptionValue("u"), cmd.getOptionValue("p"))).getConnection();
            sleepTime = 15;
            indexTime = 86400;
            labelSimilaritySearch = cmd.getOptionValue("a");
            labelSimilarityConfig = cmd.getOptionValue("r");
            lola = cmd.getOptionValue("l");
            defaultLabelSimilarityThreshold = Double.valueOf(cmd.getOptionValue("t"));
            indexedLabelSimilarityThresholds = cmd.getOptionValue("T");
            
            // handle name
            botName = cmd.getOptionValue("n");
            if (botName==null) botName = UUID.randomUUID().toString();
            else if (botName.length()>36) {
                System.out.println("ERROR: Bot name exceeds maximum allowed length of 36 characters. PQL Bot stopped.");
                System.out.println("===============================================================================");
                return;
            }
            // handle sleep
            try { sleepTime = Integer.parseInt(cmd.getOptionValue("s")); } catch (NumberFormatException e) {}
            
            // handle index
            try { indexTime = Integer.parseInt(cmd.getOptionValue("i")); } catch (NumberFormatException e) {}
        }
        catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("CLI parsing failed. Reason: " + exp.getMessage());
        }
        
        // output final parameters
        System.out.println(String.format("Bot name:\t\t%s", botName));
        System.out.println(String.format("Sleep time:\t\t%ss", sleepTime));
        System.out.println(String.format("Max. index time:\t%ss", indexTime));
        System.out.println("===============================================================================");
        
        LoLA2ModelChecker    mc = new LoLA2ModelChecker(lola);
        PQLBasicPredicatesMC bp = new PQLBasicPredicatesMC(mc);

        Set<Double> indexedLabelSimilarityThresholdsSet = new HashSet<>();
        for (String indexedLabelSimilarityThreshold: Arrays.asList(indexedLabelSimilarityThresholds.split(","))) {
            try {
                indexedLabelSimilarityThresholdsSet.add(Double.parseDouble(indexedLabelSimilarityThreshold));
            } catch (NumberFormatException e) {
                System.err.println("Misconfigured pql.indexedLabelSimilarityThresholds: " + indexedLabelSimilarityThresholds);
                return;
            }
        }

        ILabelManager labelManager;
        try {
            switch (LabelManagerType.valueOf(labelSimilaritySearch)) {
            case LEVENSHTEIN:
                labelManager = new LabelManagerLevenshtein(connection,
                                                           defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet);
                break;
            case LUCENE:
                labelManager = new LabelManagerLuceneVSM(connection,
                                                         defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet,
                                                         labelSimilarityConfig);
                break;
            /*
            case THEMIS_VSM: 
                labelManager = new LabelManagerThemisVSM(connection,
                                                         postgresHost, postgresName, postgresUser, postgresPassword,
                                                         defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet);
                break;
            */
            default:
                System.err.println("Misconfigured pql.labelSimilaritySearch " + labelSimilaritySearch);
                return;
            }
        } catch (ClassNotFoundException | IOException | SQLException e) {
            System.err.println("Unable to create label manager: " + e.getMessage());
            return;
        }
        assert labelManager != null;

        PQLIndexMySQL index = new PQLIndexMySQL(connection, bp, labelManager, mc, defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet, IndexType.PREDICATES, indexTime, sleepTime);
        
        PQLBot bot = new PQLBot(connection, botName, index, mc, IndexType.PREDICATES, indexTime, sleepTime);
        bot.run();
    }
}
