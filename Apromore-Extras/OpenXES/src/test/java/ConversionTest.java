/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XSerializerRegistry;
import org.deckfour.xes.util.XTimer;


/**
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class ConversionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		JFileChooser openChooser = new JFileChooser();
		openChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		openChooser.showOpenDialog(null);
		File source = openChooser.getSelectedFile();
		// load source file
		Collection<XLog> logs = null;
		XTimer timer = new XTimer();
		for(XParser parser : XParserRegistry.instance().getAvailable()) {
			if(parser.canParse(source)) {
				System.out.println("Using input parser: " + parser.name());
				logs = parser.parse(source);
				break;
			}
		}
		System.out.println("Reading / building performance: " + timer.getDurationString());
		System.gc();
		System.gc();
		System.gc();
		timer.start();
		int events = 0;
		XLogInfo info = null;
		for(XLog log : logs) {
			info = XLogInfoFactory.createLogInfo(log);
			events += info.getNumberOfEvents();
		}
		System.out.println("Iteration performance: " + timer.getDurationString());
		System.out.println("# Events: " + events + ".");
		// ask user for target file
		XSerializer serializer = (XSerializer)JOptionPane.showInputDialog(null, "Pick output format", 
				"Output format", JOptionPane.QUESTION_MESSAGE, null, 
				XSerializerRegistry.instance().getAvailable().toArray(), 
				XSerializerRegistry.instance().currentDefault());
		JFileChooser saveChooser = new JFileChooser();
		saveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		saveChooser.showSaveDialog(null);
		File target = saveChooser.getSelectedFile();
		//OutputStream os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
		OutputStream os = new BufferedOutputStream(new FileOutputStream(target));
		timer.start();
		serializer.serialize(logs.iterator().next(), os);
		os.flush();
		os.close();
		System.out.println("Writing performance: " + timer.getDurationString());
	}

}
