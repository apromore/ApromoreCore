/*
 * Copyright  2009-2017 The Apromore Initiative.
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
package org.apromore.prodrift.main;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apromore.prodrift.driftcharacterization.CharacterizationAccuracyResult;


public class Main {
	
	public static List<String> logNameList = new ArrayList<>();
	
	public static Map<String, CharacterizationAccuracyResult> CharacterizationAccuracyMap = new LinkedHashMap<>();
	
	public static boolean completeCharacterizationExperiment = false;
	public static boolean completeGoodnessOfKSPTExperiment = false;
	
	public static boolean isLogGenerationForNick = false;	
	public static String startActivity1 = "START";
	public static String startActivity2 = "START";
	public static String endActivity1 = "END";
	
	public static boolean isStandAlone = true;

	public static void main(final String[] args) throws Exception 
	{
		
	}

}
	 