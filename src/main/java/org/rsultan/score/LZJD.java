package org.rsultan.score;

import static java.util.Arrays.stream;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LZJD implements Distance {

  protected final byte[] s1;
  protected final byte[] s2;
  private final Map<Integer, Set<Byte>> cache = new HashMap<>();

  public LZJD(String s1, String s2) {
    this.s1 = s1.getBytes(Charset.defaultCharset());
    this.s2 = s2.getBytes(Charset.defaultCharset());
  }

  @Override
  public double compute() {
    var lzSet1 = getLZset(s1);
    var lzSet2 = getCachedLZset(s2);
    double size1 = lzSet1.size();
    double size2 = lzSet2.size();
    double intersection = intersection(lzSet1, lzSet2);
    return 1 - (intersection / union(size1, size2, intersection));
  }

  private Set<Byte> getCachedLZset(byte[] s2) {
    int key = Arrays.hashCode(s2);
    return Optional.ofNullable(cache.get(key)).orElseGet(() -> {
      Set<Byte> lZset = getLZset(s2);
      cache.put(key, lZset);
      return lZset;
    });
  }

  protected Set<Byte> getLZset(byte[] s1) {
    Set<Byte> values =  new HashSet<>();
    for (byte b : s1) {
      values.add(b);
    }
    return values;
  }

  private double union(double lzSet1, double lzSet2, double intersection) {
    return lzSet1 + lzSet2 - intersection;
  }

  private double intersection(Set<Byte> lzSet1, Set<Byte> lzSet2) {
    lzSet1.retainAll(lzSet2);
    return lzSet1.size();
  }
}
