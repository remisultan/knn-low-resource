package org.rsultan.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

public class GzipEncoder {

  public static byte[] encode(String source) {
    try {
      var bos = new ByteArrayOutputStream();
      var gos = new GZIPOutputStream(bos);
      gos.write(source.getBytes(Charset.defaultCharset()));
      gos.close();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
