/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.deckfour.xes;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.buffered.XAttributeMapSerializer;
import org.deckfour.xes.model.buffered.XAttributeMapSerializerImpl;
import org.deckfour.xes.model.buffered.XAttributeMapSerializerKyroImpl;
import org.deckfour.xes.model.impl.*;
import org.deckfour.xes.nikefs2.NikeFS2RandomAccessStorage;
import org.deckfour.xes.nikefs2.NikeFS2StorageProvider;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.util.XTimer;
import org.deckfour.xes.xstream.XesXStreamPersistency;

import com.thoughtworks.xstream.XStream;


public class SerializationTest {

    public static XFactory factory = XFactoryRegistry.instance().currentDefault();

    public static void walkLog(XLog log) {
        walkAttributes(log);
        for(XTrace trace : log) {
            walkTrace(trace);
        }
    }

    public static void walkTrace(XTrace trace) {
        walkAttributes(trace);
        for(XEvent event : trace) {
            walkAttributes(event);
        }
    }

    public static void walkAttributes(XAttributable attributable) {
        XAttributeMap attributeMap = attributable.getAttributes();
        for(XAttribute attribute : attributeMap.values()) {
            String key = attribute.getKey();
            String value = attribute.toString();
            key.trim();
            value.trim();
            walkAttributes(attribute);
        }
    }

    public static byte[] encode(XEvent event, XAttributeMapSerializer attributeMapSerializer) throws IOException {
        // prepare output stream for encoding
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // write event id to output stream
        XID.write(event.getID(), dos);
        // encode attributes
        attributeMapSerializer.serialize(event.getAttributes(), dos);
        // flush and serialize output stream result
        dos.flush();
        return baos.toByteArray();
    }

    private static final ThreadLocal<Kryo> kryoThreadLocal
            = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(XAttributeMap.class);
        return kryo;
    });


    public static void runTest() {
        Thread testRunner = new Thread() {

            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                XTimer timer = new XTimer();
                XLog log;
                List<XLog> parsedLog = null;
                // create log
                // XLog log = createLog(20, 3000, 100, 2000);//createLog(150, 151, 500, 1000);

                // import log
//				XFactory factory = new XFactoryNaiveImpl();
                XFactory factory = new XFactoryBufferedImpl();
                XesXmlParser parser = new XesXmlParser(factory);
                try {
//					parsedLog = parser.parse(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/samplePoke.xes"));
                    parsedLog = parser.parse(new GZIPInputStream(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/procmin20180612_F2_5M.xes.gz")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log = parsedLog.iterator().next();
                timer.stop();
                System.out.println("Imported log:");
//				System.out.println("Created log:");
//				System.out.println("  Traces: " + NUM_TRACES);
//				System.out.println("  Events: " + NUM_EVENTS);
//				System.out.println("  Attributes: " + NUM_ATTRIBUTES);
                System.out.println("Duration: " + timer.getDurationString());
                System.gc();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                System.out.println("Memory Used: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
                // walk log
                timer.start();
                walkLog(log);
                timer.stop();
                System.out.println("Walked log.");
                System.out.println("Duration: " + timer.getDurationString());
                // serializing to GZIPped file

//				System.out.println("Serializing log.");
//				timer.start();
//				try {
//					OutputStream os = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(new File("/Users/frank/Desktop/stresstest.xes.gz"))));
//					(new XesXmlSerializer()).serialize(log, os);
//					timer.stop();
//					os.flush();
//					os.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println("Duration: " + timer.getDurationString());
//
//				File sFile = new File("/Users/frank/Desktop/testxstream.xml");
//				if(sFile.exists()) {
//					sFile.delete();
//				}
//				try {
//					sFile.createNewFile();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}

//				XStream xstream = new XStream();
//				XesXStreamPersistency.register(xstream);
//				try {
//					System.out.println("Serializing log with XStream");
//					timer.start();
//					OutputStream oStream = new BufferedOutputStream(new FileOutputStream(sFile));
//					xstream.toXML(log, oStream);
//					timer.stop();
//					System.out.println("Duration: " + timer.getDurationString());
//					System.out.println("Deserializing log with XStream");
//					timer.start();
//					XLog log2 = (XLog)xstream.fromXML(new BufferedInputStream(new FileInputStream(sFile)));
//					System.out.println("Duration: " + timer.getDurationString());
//					// walk log
//					timer.start();
//					walkLog(log2);
//					timer.stop();
//					System.out.println("Walked deserialized log.");
//					System.out.println("Duration: " + timer.getDurationString());
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

//				XLog log2 = (XLog) log.clone();

                factory = new XFactoryNaiveImpl();
                System.out.println("Creating with " + factory.getClass().getSimpleName() + ": ");
                timer.start();

                XLog log2 = factory.createLog();

                for(XTrace trace : log) {

                    XTrace t = factory.createTrace();

                    for(XEvent event : trace) {

                        XEvent e = factory.createEvent();
                        for (String key : event.getAttributes().keySet()) {
                            XAttribute attr = event.getAttributes().get(key);

                            e.getAttributes().put(key, attr);
//							assertNotNull(attr);
//							if (attr instanceof XAttributeBoolean) {
//								assertNotNull(((XAttributeBoolean) attr).getValue());
//							} else if (attr instanceof XAttributeLiteral) {
//								assertNotNull(((XAttributeLiteral) attr).getValue());
//							} else if (attr instanceof XAttributeContinuous) {
//								assertNotNull(((XAttributeContinuous) attr).getValue());
//							} else if (attr instanceof XAttributeDiscrete) {
//								assertNotNull(((XAttributeDiscrete) attr).getValue());
//							}
//							attributeCounter++;
                        }
                        t.add(e);
//						for (Map.Entry<String, XAttribute> attrEntry : event.getAttributes().entrySet()) {
//							if (attrEntry.getValue() instanceof XAttributeBoolean) {
//								attrEntry.setValue(new XAttributeBooleanImpl(attrEntry.getKey(),
//										!((XAttributeBoolean) attrEntry.getValue()).getValue(),
//										attrEntry.getValue().getExtension()));
//							} else if (attrEntry.getValue() instanceof XAttributeLiteral) {
//								if (attrEntry.getKey().equals(XConceptExtension.KEY_NAME)) {
//									attrEntry.setValue(new XAttributeLiteralImpl(attrEntry.getKey(),
//											((XAttributeLiteral) attrEntry.getValue()).getValue().concat(" NEW"),
//											attrEntry.getValue().getExtension()));
//								} else {
//									attrEntry.setValue(new XAttributeLiteralImpl(attrEntry.getKey(), getRandomString(),
//											attrEntry.getValue().getExtension()));
//								}
//							} else if (attrEntry.getValue() instanceof XAttributeContinuous) {
//								attrEntry.setValue(new XAttributeContinuousImpl(attrEntry.getKey(), random.nextDouble(),
//										attrEntry.getValue().getExtension()));
//							} else if (attrEntry.getValue() instanceof XAttributeDiscrete) {
//								attrEntry.setValue(new XAttributeDiscreteImpl(attrEntry.getKey(), random.nextInt(),
//										attrEntry.getValue().getExtension()));
//							} else if (attrEntry.getValue() instanceof XAttributeTimestamp) {
//								attrEntry.setValue(new XAttributeTimestampImpl(attrEntry.getKey(), Math.abs(random.nextLong()),
//										attrEntry.getValue().getExtension()));
//							}
//							attributeCounter++;
//						}
                    }

//					t.addAll(trace);

                    log2.add(t);
                }
                timer.stop();
                System.out.println("Cloned log.");
                System.out.println("Duration: " + timer.getDurationString());
                System.gc();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                System.out.println("Memory Used: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB ");

                timer.start();
                walkLog(log2);
                timer.stop();
                System.out.println("Walked log.");
                System.out.println("Duration: " + timer.getDurationString());



            }

        };

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        Thread testRunner1 = new Thread(() -> {


            /**
             * Encoding for non-existent extension (generic attributes)
             */
            final int EXTENSION_GENERIC = -1;

            /**
             * The number of events contained in a buffer.
             */
            int size = 0;
            /**
             * The current logical index (in number of events) in the buffer.
             */
            int index = 0;
            /**
             * The current actual position in the backing buffer storage, in bytes
             * offset from the beginning of the storage.
             */
            long position = 0;
            /**
             * The position at which the last event entry was inserted into the backing
             * buffer storage. Initialized with -1.
             */
            long lastInsertPosition = -1;

            /**
             * Storage provider which is used to allocate new buffer storages.
             */
            NikeFS2StorageProvider provider = NikeFS2VirtualFileSystem
                    .instance();

            /**
             * The random access storage to back the buffer of events.
             */

            NikeFS2RandomAccessStorage storage = null;
            try {
                storage = provider.createStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * Attribute map serializer.
             */
            XAttributeMapSerializer attributeMapSerializer = new XAttributeMapSerializerImpl();
            /**
             * Factory for model elements.
             */
            XFactory factory = XFactoryRegistry.instance().currentDefault();

            XTimer timer = new XTimer();
            XLog log;
            List<XLog> parsedLog = null;

            // import log
            XesXmlParser parser = new XesXmlParser(factory);
            try {
//				parsedLog = parser.parse(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/samplePoke.xes"));
                parsedLog = parser.parse(new GZIPInputStream(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/procmin20180612_F2_5M.xes.gz")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            log = parsedLog.iterator().next();
            timer.stop();
            System.out.println("Imported log:");
            System.out.println("Duration: " + timer.getDurationString());


            // remember insert position
            timer.start();
            long insertPosition = 0;
            try {
                insertPosition = storage.length();

                // position storage pointer at end of storage
                storage.seek(insertPosition);

                storage.writeInt(log.size());

                for(XTrace trace : log) {

                    storage.writeInt(trace.size());

                    for(XEvent event : trace) {

                        // encode event to byte array
                        byte evtEnc[] = encode(event, attributeMapSerializer);
//						// compute segment length: add some slack to accomodate for later,
//						// larger versions of this entry
//						int segmentPaddingSize = evtEnc.length / 4;
//						int segmentSize = evtEnc.length + segmentPaddingSize;
//						byte segmentPadding[] = new byte[segmentPaddingSize];
//						Arrays.fill(segmentPadding, (byte) 0);
//						// record offset to subsequent audit trail entry for forward
//						// skips; as size of event encoding + 12 bytes (for forward and
//						// backward offset marker and payload size)
//						storage.writeInt(segmentSize + 12);
//						// record offset to previous event (for backward skips)
//						storage.writeInt((int) (insertPosition - lastInsertPosition));
                        // record actual payload size
                        storage.writeInt(evtEnc.length);
                        // record event encoding data
                        storage.write(evtEnc);
                        // record padding data
//						storage.write(segmentPadding);
//						// update last position pointer to this entry
//						lastInsertPosition = insertPosition;
                        // update collection size
                        size++;
                    }
                }

                timer.stop();
                System.out.println("Persisted log into disk");
                System.out.println("Duration: " + timer.getDurationString());

                //Start to construct XLog using XFactoryNaiveImpl
                timer.start();
                factory = new XFactoryNaiveImpl();
                System.out.println("Creating with " + factory.getClass().getSimpleName() + ": ");
                timer.start();

                XLog log2 = factory.createLog();

                storage.seek(0);
                int logSize = storage.readInt();
                System.out.println("Read the size of log: " + logSize);

                for (int traceIndex = 0; traceIndex < logSize; traceIndex++) {

                    XTrace t = factory.createTrace();

                    int traceSize = storage.readInt();
//					System.out.println("Read the size of trace: " + traceSize);

                    for (int eventIndex = 0; eventIndex < traceSize; eventIndex++) {

//						// reset file pointer position
//						storage.seek(position);
//						// compute next position from forward offset
//						long nextPosition = position + storage.readInt();
                        // skip backward offset (4 bytes)
//						storage.skipBytes(4);
                        // read payload size
                        int eventSize = storage.readInt();
                        // buffered implementation: reads the byte array representing the
                        // event and interprets it from that buffer subsequently.
                        byte[] eventData = new byte[eventSize];
                        storage.readFully(eventData);
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
                                eventData));
                        // read event data attributes in specified order from file
                        XID id = XID.read(dis);
                        // read event attribute set
                        XAttributeMap attributes = attributeMapSerializer.deserialize(dis);
                        // assemble event
                        XEvent event = factory.createEvent(id, attributes);
                        // adjust position of data access layer
//						position = nextPosition;
                        index++;
                        t.add(event);
                    }
                    log2.add(t);

                }


                timer.stop();
                System.out.println("Swapped back into memory");
                System.out.println("Duration: " + timer.getDurationString());
                System.out.println("Read the size of log: " + log2.size());

            } catch (IOException e) {
                e.printStackTrace();
            }



        });

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        Thread testRunner2 = new Thread(() -> {


            /**
             * Encoding for non-existent extension (generic attributes)
             */
            final int EXTENSION_GENERIC = -1;

            /**
             * The number of events contained in a buffer.
             */
            int size = 0;
            /**
             * The current logical index (in number of events) in the buffer.
             */
            int index = 0;
            /**
             * The current actual position in the backing buffer storage, in bytes
             * offset from the beginning of the storage.
             */
            long position = 0;
            /**
             * The position at which the last event entry was inserted into the backing
             * buffer storage. Initialized with -1.
             */
            long lastInsertPosition = -1;

            /**
             * Storage provider which is used to allocate new buffer storages.
             */
            NikeFS2StorageProvider provider = NikeFS2VirtualFileSystem
                    .instance();

            /**
             * The random access storage to back the buffer of events.
             */

            NikeFS2RandomAccessStorage storage = null;
            try {
                storage = provider.createStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * Attribute map serializer.
             */
            XAttributeMapSerializerKyroImpl attributeMapSerializer = new XAttributeMapSerializerKyroImpl();
            /**
             * Factory for model elements.
             */
            XFactory factory = XFactoryRegistry.instance().currentDefault();

            Kryo kryo = new Kryo();
            kryo.register(XAttributeMap.class);

            XTimer timer = new XTimer();
            XLog log;
            List<XLog> parsedLog = null;

            // import log
            XesXmlParser parser = new XesXmlParser(factory);
            try {
//				parsedLog = parser.parse(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/samplePoke.xes"));
                parsedLog = parser.parse(new GZIPInputStream(new FileInputStream("/Users/frank/Projects/xeslite/TestLogs/procmin20180612_F2_5M.xes.gz")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            log = parsedLog.iterator().next();
            timer.stop();
            System.out.println("Imported log:");
            System.out.println("Duration: " + timer.getDurationString());


            // remember insert position
            timer.start();
            long insertPosition = 0;
            try {
                insertPosition = storage.length();

                // position storage pointer at end of storage
                storage.seek(insertPosition);

                storage.writeInt(log.size());

                for(XTrace trace : log) {

                    storage.writeInt(trace.size());

                    for(XEvent event : trace) {

                        // encode event to byte array
//						byte evtEnc[] = encode(event, attributeMapSerializer);

                        // prepare output stream for encoding
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(baos);
                        // write event id to output stream
                        XID.write(event.getID(), dos);
                        // encode attributes
                        Output output = new Output(dos);
                        kryo.writeObject(output, event.getAttributes());
                        // flush and serialize output stream result
                        output.close();
                        byte evtEnc[] = output.getBuffer();

//						System.out.println("Kryo size: " + output.getBuffer().length);

//						// compute segment length: add some slack to accomodate for later,
//						// larger versions of this entry
//						int segmentPaddingSize = evtEnc.length / 4;
//						int segmentSize = evtEnc.length + segmentPaddingSize;
//						byte segmentPadding[] = new byte[segmentPaddingSize];
//						Arrays.fill(segmentPadding, (byte) 0);
//						// record offset to subsequent audit trail entry for forward
//						// skips; as size of event encoding + 12 bytes (for forward and
//						// backward offset marker and payload size)
//						storage.writeInt(segmentSize + 12);
//						// record offset to previous event (for backward skips)
//						storage.writeInt((int) (insertPosition - lastInsertPosition));
                        // record actual payload size
                        storage.writeInt(evtEnc.length);
                        // record event encoding data
                        storage.write(evtEnc);
                        // record padding data
//						storage.write(segmentPadding);
//						// update last position pointer to this entry
//						lastInsertPosition = insertPosition;
                        // update collection size
                        size++;
                    }
                }

                timer.stop();
                System.out.println("Persisted log into disk");
                System.out.println("Duration: " + timer.getDurationString());

                //Start to construct XLog using XFactoryNaiveImpl
                timer.start();
                factory = new XFactoryNaiveImpl();
                System.out.println("Creating with " + factory.getClass().getSimpleName() + ": ");
                timer.start();

                XLog log2 = factory.createLog();

                storage.seek(0);
                int logSize = storage.readInt();
                System.out.println("Read the size of log: " + logSize);

                for (int traceIndex = 0; traceIndex < logSize; traceIndex++) {

                    XTrace t = factory.createTrace();

                    int traceSize = storage.readInt();
                    System.out.println("Read the size of trace: " + traceSize);

                    for (int eventIndex = 0; eventIndex < traceSize; eventIndex++) {

//						// reset file pointer position
//						storage.seek(position);
//						// compute next position from forward offset
//						long nextPosition = position + storage.readInt();
                        // skip backward offset (4 bytes)
//						storage.skipBytes(4);
                        // read payload size
                        int eventSize = storage.readInt();
                        // buffered implementation: reads the byte array representing the
                        // event and interprets it from that buffer subsequently.
                        byte[] eventData = new byte[eventSize];

                        storage.readFully(eventData);
                        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
                                eventData));
                        // read event data attributes in specified order from file
                        XID id = XID.read(dis);
                        // read event attribute set
                        Input input = new Input();
                        input.setBuffer(eventData, 16, eventSize-16);
                        XAttributeMap attributes = kryo.readObject(input, XAttributeMap.class);
                        input.close();
                        // assemble event
                        XEvent event = factory.createEvent(id, attributes);
                        // adjust position of data access layer
//						position = nextPosition;
                        index++;
                        t.add(event);
                    }
                    log2.add(t);

                }


                timer.stop();
                System.out.println("Swapped back into memory");
                System.out.println("Duration: " + timer.getDurationString());
                System.out.println("Read the size of log: " + log2.size());

            } catch (IOException e) {
                e.printStackTrace();
            }



        });

        testRunner1.start();
    }

    public static void main(String[] args) {
        runTest();
    }

}
