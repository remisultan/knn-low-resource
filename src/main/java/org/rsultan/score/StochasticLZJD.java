package org.rsultan.score;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class StochasticLZJD extends LZJD {

  private final double falseSeenProbability;

  public StochasticLZJD(String s1, String s2, double falseSeenProbability) {
    super(s1, s2);
    this.falseSeenProbability = falseSeenProbability;
  }

  @Override
  protected Set<Byte> getLZset(byte[] bytes) {
    var random = new Random();
    Set<Byte> values = new HashSet<>();
    for (byte b : bytes) {
      if (!values.contains(b) && random.nextDouble() > falseSeenProbability) {
        values.add(b);
      }
    }
    return values;
  }

}
