package org.rsultan.score;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.rsultan.encoder.GzipEncoder;

public class NCD implements Distance {

  private final int s1;
  private final int s2;
  private final int s1s2;

  private static final Map<Integer, Integer> encoded = new HashMap<>();

  public NCD(String s1, String s2) {
    this.s1 = GzipEncoder.encode(s1).length;
    this.s2 = getCachedEncode(s2);
    this.s1s2 = GzipEncoder.encode(s1 + " " + s2).length;
  }

  private static int getCachedEncode(String s2) {
    int key = s2.hashCode();
    return Optional.ofNullable(encoded.get(key)).orElseGet(() -> {
      int length = GzipEncoder.encode(s2).length;
      encoded.put(key, length);
      return length;
    });
  }

  public double compute() {
    return (double) (s1s2 - Math.min(s1, s2)) / (double) Math.max(s1, s2);
  }
}
