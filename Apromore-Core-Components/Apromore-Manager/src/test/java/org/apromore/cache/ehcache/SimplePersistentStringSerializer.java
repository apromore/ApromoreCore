package org.apromore.cache.ehcache;

import org.ehcache.core.spi.service.FileBasedPersistenceContext;

import java.io.*;
import java.util.Map;


// tag::persistentSerializer[]
public class SimplePersistentStringSerializer extends SimpleTransientStringSerializer implements Closeable {

  private final File stateFile;

  public SimplePersistentStringSerializer(final ClassLoader loader, FileBasedPersistenceContext persistence) throws IOException, ClassNotFoundException {
    super(loader);
    stateFile = new File(persistence.getDirectory(), "serializer.data");
    if(stateFile.exists()) {
      restoreState();
    }
  }

  @Override
  public void close() throws IOException {
    persistState();
  }

  private void restoreState() throws IOException, ClassNotFoundException {
    FileInputStream fin = new FileInputStream(stateFile);
    try {
      ObjectInputStream oin = new ObjectInputStream(fin);
      try {
        idStringMap = (Map<Integer, String>) oin.readObject();
        stringIdMap = (Map<String, Integer>) oin.readObject();
        id = oin.readInt();
      } finally {
        oin.close();
      }
    } finally {
      fin.close();
    }
  }

  private void persistState() throws IOException {
    OutputStream fout = new FileOutputStream(stateFile);
    try {
      ObjectOutputStream oout = new ObjectOutputStream(fout);
      try {
        oout.writeObject(idStringMap);
        oout.writeObject(stringIdMap);
        oout.writeInt(id);
      } finally {
        oout.close();
      }
    } finally {
      fout.close();
    }
  }
}
// end::persistentSerializer[]
