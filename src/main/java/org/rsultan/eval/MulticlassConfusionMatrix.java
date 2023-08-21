package org.rsultan.eval;


import static java.util.stream.IntStream.range;

import org.nd4j.linalg.factory.Nd4j;

public class MulticlassConfusionMatrix {

  private final long[][] matrix;

  public MulticlassConfusionMatrix(int nbOfLabels) {
    this.matrix = new long[nbOfLabels][nbOfLabels];
  }

  public void add(int expected, int predicted) {
    matrix[expected][predicted] += 1L;
  }

  public ConfusionMatrix getConfusionMatrix(int label) {
    double tp = getTp(label);
    double tn = getTn(label);
    double fn = getFn(label);
    double fp = getFp(label);
    return new ConfusionMatrix(tp, tn, fp, fn);
  }

  private double getTn(int label) {
    double tn = 0.0;
    for (int i = 0; i < matrix.length; i++) {
      if (i != label) {
        for (int j = 0; j < matrix.length; j++) {
          if (j != label) {
            tn += matrix[i][j];
          }
        }
      }
    }
    return tn;
  }

  private double getTp(int label) {
    return matrix[label][label];
  }

  private double getFn(int label) {
    return range(0, matrix.length).filter(idx -> label != idx)
        .mapToDouble(idx -> matrix[label][idx])
        .sum();
  }

  private double getFp(int label) {
    var fp = 0.0;
    int bound = matrix.length;
    for (int idx = 0; idx < bound; idx++) {
      if (label != idx) {
        double v = matrix[idx][label];
        fp += v;
      }
    }
    return fp;
  }

  public String toString() {
    return Nd4j.create(matrix).toString();
  }
}
