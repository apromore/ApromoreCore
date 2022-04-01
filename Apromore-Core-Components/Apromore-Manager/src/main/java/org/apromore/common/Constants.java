/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.common;

public interface Constants {

    public static final String XML_MIMETYPE = "text/xml";
    public static final String GZ_MIMETYPE = "application/x-gzip";

    public static final String INITIAL_ANNOTATION = "Initial";
    public static final String INITIAL_FORMAT = "IntialFormat";
    public static final String CANONICAL = "Canonical";
    public static final String NATIVE_TYPE = "BPMN 2.0";

    public static final String TRUNK_NAME = "MAIN";
    public static final String DRAFT_BRANCH_NAME = "DRAFT";
    public static final String TYPE = "type";
    public static final String CONNECTOR = "Connector";
    public static final String FUNCTION = "Function";
    public static final String EVENT = "Event";

    public static final String PROCESS_NAME = "ProcessName";
    public static final String BRANCH_NAME = "BranchName";
    public static final String BRANCH_ID = "BranchID";
    public static final String VERSION_NUMBER = "VersionNumber";
    public static final String PROCESS_MODEL_VERSION_ID = "PMVID";
    public static final String ROOT_FRAGMENT_ID = "RootFragmentId";
    public static final String ORIGINAL_FRAGMENT_ID = "OriginalFragmentId";
    public static final String LOCK_STATUS = "LockStatus";
    public static final String LOCKED = "Locked";
    public static final String UNLOCKED = "Unlocked";

    public static final String CPF_CONTEXT = "org.apromore.cpf";
    public static final String XPDL2_CONTEXT = "org.wfmc._2009.xpdl2";

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public static final int NO_LOCK = 0;
    public static final int INDIRECT_LOCK = 1;
    public static final int DIRECT_LOCK = 2;

    public static final String PHASE1 = "Phase_1";
    public static final String PHASE2 = "Phase_2";
    public static final int ROUND_OFF_AMOUNT = 1000000;
}
