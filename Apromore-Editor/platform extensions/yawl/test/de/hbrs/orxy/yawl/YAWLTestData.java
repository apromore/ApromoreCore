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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;

public class YAWLTestData {

    public static String orderFulfillmentSource;
    public static YSpecification orderFulfillmentSpecification;

    public static String testSource;
    public static YSpecification testSpecification;

    public static String filmProduction;

    static {
        try {
            orderFulfillmentSource = new Scanner(new File("resources/orderfulfillment.yawl"), "UTF-8").useDelimiter("\\A").next();

            List<YSpecification> specificationList = YMarshal.unmarshalSpecifications(YAWLTestData.orderFulfillmentSource, false);
            orderFulfillmentSpecification = specificationList.get(0);

            filmProduction = new Scanner(new File("resources/filmproduction.yawl"), "UTF-8").useDelimiter("\\A").next();

            testSource = new Scanner(new File("resources/test.yawl"), "UTF-8").useDelimiter("\\A").next();

            List<YSpecification> testSpecificationList = YMarshal.unmarshalSpecifications(YAWLTestData.testSource, false);
            testSpecification = testSpecificationList.get(0);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (YSyntaxException e) {
            e.printStackTrace();
        }
    }

}
