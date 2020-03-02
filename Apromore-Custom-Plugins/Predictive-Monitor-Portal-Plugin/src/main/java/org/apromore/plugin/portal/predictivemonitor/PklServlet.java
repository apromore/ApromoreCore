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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

// Java 2 Enterprise Edition
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Third party packages
import org.apache.commons.io.IOUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

// Local packages
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * Allow {@link Predictor} pkl to be accessed via HTTP GET.
 */
public class PklServlet extends HttpServlet {

    @Inject
    private PredictiveMonitorService predictiveMonitorService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log("GET " + request.getPathInfo() + " " + request.getQueryString());

        Predictor predictor = null;
        switch (request.getPathInfo()) {
        case "/id":
            int pklId;
            try {
                pklId = Integer.parseUnsignedInt(request.getQueryString());

            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query part must be an unsigned integer; was " + request.getQueryString());
                return;
            }

            predictor = predictiveMonitorService.findPredictorById(pklId);
            if (predictor == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No pkl with id " + pklId);
                return;
            }
            break;

        case "/name":
            predictor = predictiveMonitorService.findPredictorByName(request.getQueryString());
            if (predictor == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No pkl with name " + request.getQueryString());
                return;
            }
            break;

        default:
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Unsupported path info; was " + request.getPathInfo());
            return;
        }
        assert predictor != null;

        try (InputStream in = new ByteArrayInputStream(predictor.getPkl()) /*.getBinaryStream()*/) {
            response.setStatus(HttpServletResponse.SC_OK);
            IOUtils.copy(in, response.getOutputStream());

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            return;
        }
    }
}
