/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

import java.util.List;
import javax.inject.Inject;
import org.apromore.dao.ProcessRepository;
import org.apromore.service.DomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the DomainService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("domainService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
    rollbackFor = Exception.class)
public class DomainServiceImpl implements DomainService {

  private ProcessRepository pRepository;


  /**
   * Default Constructor allowing Spring to Autowire for testing and normal use.
   * 
   * @param processRepository Process Repository.
   */
  @Inject
  public DomainServiceImpl(final ProcessRepository processRepository) {
    pRepository = processRepository;
  }


  /**
   * @see org.apromore.service.DomainService#findAllDomains() {@inheritDoc}
   *      <p/>
   *      NOTE: This might need to convert (or allow for) to the models used in the webservices.
   */
  @Override
  public List<String> findAllDomains() {
    return pRepository.getAllDomains();
  }

}
