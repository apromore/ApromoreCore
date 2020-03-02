/*
 * This file is part of "Apromore".
 *
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

package cs.ut.util

enum class Columns(val value: String) {
    STATIC_CAT_COLS("static_cat_cols"),
    DYNAMIC_CAT_COLS("dynamic_cat_cols"),
    STATIC_NUM_COLS("static_num_cols"),
    DYNAMIC_NUM_COLS("dynamic_num_cols")
}

enum class ColumnPart(val value: String) {
    DYNAMIC("dynamic"),
    STATIC("static"),
    CAT_COLS("_cat_cols"),
    NUM_COLS("_num_cols")
}

enum class IdentColumns(val value: String) {
    CASE_ID("case_id_col"),
    ACTIVITY("activity_col"),
    TIMESTAMP("timestamp_col"),
    RESOURCE("resource_col")
}

enum class Algorithm(val value: String) {
    LAST_STATE("last_state"),
    PREFIX("prefix_length_based"),
    FREQUENCY("frequency"),
    COMBINED("combined"),
    INDEX("index_based"),
    ZERO("zero"),
    STATE("state_based"),
    CLUSTERING("clustering"),
    RANDOM_FOREST("random_forest"),
    GRADIENT_BOOST("gradient_boosting"),
    DEC_TREE("decision_tree"),
    XGBOOST("xgboost"),
    REMTIME("remtime"),
    OUTCOME("outcome"),
    NEXT_ACTIVITY("next")
}

enum class Page(val value: String) {
    LANDING("landing"),
    UPLOAD("upload"),
    TRAINING("training"),
    VALIDATION("validation"),
    MODEL_OVERVIEW("jobs")
}

enum class Field(val value: String) {
    ENCODING("encoding"),
    BUCKETING("bucketing"),
    LEARNER("learner"),
    PREDICTION("predictiontype")
}

enum class GridColumns(val value: String) {
    SORTABLE("sortableColumns"),
    HIDDEN("hiddenColumns"),
    TIMESTAMP("timestampFormat")
}

enum class ZipDirs(val value: String) {
    FEATURES("features"),
    DETAILED("detailed"),
    ACCURACY("accuracy"),
    SCHEME("scheme")
}

enum class Node(val value: String) {
    EVENT_NUMBER("n_of_events")
}