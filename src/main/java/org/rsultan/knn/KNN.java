package org.rsultan.knn;

import static java.util.stream.IntStream.range;
import static org.nd4j.linalg.factory.Nd4j.sortWithIndices;

import java.util.List;
import java.util.concurrent.Executors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.rsultan.score.LZJD;
import org.rsultan.score.StochasticLZJD;

public class KNN {

  private static long sum;
  private static int count;
  private final int k;

  public KNN(int k) {
    this.k = k;
  }

  public INDArray predict(List<SentenceCategory> trainSet, List<SentenceCategory> testSet,
      int nbOfLabels) {
    var matrix = Nd4j.create(testSet.stream().map(x1 -> createVector(trainSet, x1)).toList(), testSet.size(), trainSet.size());
    var argSort = sortWithIndices(matrix, 0, true)[0].getColumns(range(0, k).toArray());
    var categoryMatrix = Nd4j.zeros(testSet.size(), nbOfLabels);
    for (int i = 0; i < argSort.rows(); i++) {
      for (int j = 0; j < argSort.columns(); j++) {
        int rowIdx = argSort.getInt(i, j);
        int colIdx = trainSet.get(rowIdx).category;
        categoryMatrix.putScalar(rowIdx, colIdx, categoryMatrix.getDouble(rowIdx, colIdx) + 1D);
      }
    }

    return sortWithIndices(categoryMatrix, 1, false)[0];
  }

  private static INDArray createVector(List<SentenceCategory> trainSet, SentenceCategory x1) {
    var vector = Nd4j.zeros(1, trainSet.size());

    long t0 = System.currentTimeMillis();

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      range(0, trainSet.size()).forEach(idx -> executor.submit(() -> {
            vector.putScalar(idx,
                new LZJD(x1.sentence, trainSet.get(idx).sentence).compute());
          }
      ));
    }

    sum += System.currentTimeMillis() - t0;
    count += 1;

    if (count % 100 == 0) {
      System.out.println(
          " Average row processing time (" + count + " rows): " + (sum / count) / 1000d + "s");
      count = 0;
      sum = 0;
    }
    return vector;
  }

  public record SentenceCategory(String sentence, int category) {

  }

}
