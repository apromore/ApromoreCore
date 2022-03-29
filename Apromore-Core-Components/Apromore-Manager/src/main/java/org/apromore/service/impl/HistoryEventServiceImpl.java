/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl;

import org.apromore.dao.HistoryEventRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.StatusEnum;
import org.apromore.dao.model.User;
import org.apromore.service.HistoryEventService;
import org.apromore.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the HistoryEventService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class HistoryEventServiceImpl implements HistoryEventService {

    @Inject
    private HistoryEventRepository historyRepo;
    @Inject
    private UserRepository userRepo;



    /**
     * @see org.apromore.service.HistoryEventService#addNewEvent(org.apromore.dao.model.StatusEnum, org.apromore.dao.model.HistoryEnum)
     * {@inheritDoc}
     */
    @Override
    public HistoryEvent addNewEvent(final StatusEnum status, final HistoryEnum historyType) {
        HistoryEvent historyEvent = new HistoryEvent();

        historyEvent.setStatus(status);
        historyEvent.setType(historyType);
        historyEvent.setOccurDate(new Date());

        User user = userRepo.findByUsername(SecurityUtils.getLoggedInUser());
        if (user != null) {
            historyEvent.setUser(user);
            user.addHistoryEvent(historyEvent);
        }

        return historyRepo.save(historyEvent);
    }

    /**
     * @see org.apromore.service.HistoryEventService#findLatestHistoryEventType(org.apromore.dao.model.HistoryEnum)
     * {@inheritDoc}
     */
    @Override
    public HistoryEvent findLatestHistoryEventType(HistoryEnum historyType) {
        HistoryEvent result = null;

        List<HistoryEvent> histories = historyRepo.findByStatusAndTypeOrderByOccurDateDesc(StatusEnum.FINISHED, historyType);
        if (histories != null && !histories.isEmpty()) {
            result = histories.get(0);
        } else {
            histories = historyRepo.findByStatusAndTypeOrderByOccurDateDesc(StatusEnum.START, historyType);
            if (histories != null && !histories.isEmpty()) {
                result = histories.get(0);
            }
        }

        return result;
    }

}
