/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2011, 2012, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.portal.client;

import org.apromore.model.Detail;
import org.apromore.model.ResultPQL;

import java.util.List;

/**
 * Created by corno on 9/07/2014.
 */
public interface PortalService {
    void addNewTab(List<ResultPQL> results, String userID, List<Detail> details, String query, String nameQuery);
}
