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

package org.apromore.common.converters.xpdl.interceptor;

import de.hpi.bpmn2xpdl.XPDLPackage;
import de.hpi.bpmn2xpdl.XPDLPool;
import de.hpi.bpmn2xpdl.XPDLWorkflowProcess;

public class BizageInterceptor {

    private XPDLPackage xpdlPackage;

    public BizageInterceptor(XPDLPackage newPackage) {
        this.xpdlPackage = newPackage;
    }

    public void intercept() {
        // remove the Main process empty pool
        String MAIN_PROCESS = "Main Process";
        for (XPDLWorkflowProcess workflowProcess : xpdlPackage.getWorkflowProcesses().getWorkflowProcesses()) {
            if (MAIN_PROCESS.equals(workflowProcess.getName())) {
                removeEmptyPool(workflowProcess.getId());
                xpdlPackage.getWorkflowProcesses().getWorkflowProcesses().remove(workflowProcess);
                break; // to avoid cuncurrent modification exception
            }
        }

        // remove the Main process empty pool
        for (XPDLPool pool : xpdlPackage.getPools().getPools()) {
            if (MAIN_PROCESS.equals(pool.getName())) {
                removeEmptyWorkflowProcess(pool.getProcess());
                xpdlPackage.getPools().getPools().remove(pool);
                break;
            }
        }
    }

    private void removeEmptyPool(String id) {
        for (XPDLPool pool : xpdlPackage.getPools().getPools()) {
            if (id.equals(pool.getId())) {
                xpdlPackage.getPools().getPools().remove(pool);
                break;
            }
        }
    }

    private void removeEmptyWorkflowProcess(String processId) {
        for (XPDLWorkflowProcess process : xpdlPackage.getWorkflowProcesses().getWorkflowProcesses()) {
            if (processId.equals(process.getId())) {
                xpdlPackage.getWorkflowProcesses().getWorkflowProcesses().remove(process);
                break;
            }
        }
    }

}
