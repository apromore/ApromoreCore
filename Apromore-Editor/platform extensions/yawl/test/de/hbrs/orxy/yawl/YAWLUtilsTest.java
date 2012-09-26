/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.orxy.yawl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.util.YAWLUtils;

public class YAWLUtilsTest {

    @Test
    public void testIsElementVisible() {
        YTask task = new YAtomicTask("test", 0, 0, new YNet("test", new YSpecification()));
        assertTrue(YAWLUtils.isElementVisible(task));
        YCondition condition = new YCondition("test", new YNet("test", new YSpecification()));
        assertTrue(YAWLUtils.isElementVisible(condition));
        condition.setImplicit(true);
        assertFalse(YAWLUtils.isElementVisible(condition));
    }

    @Test
    public void testGetNextVisibleElement() {
        // TODO
    }

}
