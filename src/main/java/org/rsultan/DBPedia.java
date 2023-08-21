package org.rsultan;

import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.shade.guava.io.Files;
import org.rsultan.eval.ConfusionMatrix;
import org.rsultan.eval.MulticlassConfusionMatrix;
import org.rsultan.knn.KNN;
import org.rsultan.knn.KNN.SentenceCategory;

public class DBPedia {

  private static final Map<String, Integer> labelsToInt = new HashMap<>();

  public static final Pattern PATTERN = Pattern.compile("((\".+\"),(.+),(.+),(.+))");

  //The dataset https://www.kaggle.com/datasets/amananandrai/ag-news-classification-dataset
  // args[0] is the training dataset
  // args[1] is the test dataset
  public static void main(String[] args) throws IOException {
    int k = 5;
    var trainSet = getDatasetSet(args, 0);
    Collections.shuffle(trainSet);
    trainSet = trainSet.subList(0, 20000);
    var testSet = getDatasetSet(args, 1);
    Collections.shuffle(testSet);
    testSet = trainSet.subList(0, 10000);

    int nbOfLabels = labelsToInt.size();
    KNN knn = new KNN(k);
    INDArray predict = knn.predict(trainSet, testSet, nbOfLabels);
    var confusionMatrix = new MulticlassConfusionMatrix(nbOfLabels);

    for (int i = 0; i < predict.rows(); i++) {
      int response = testSet.get(i).category();
      int prediction = predict.getInt(i, 0);
      confusionMatrix.add(response, prediction);
    }

    System.out.println(confusionMatrix);

    for (int i = 0; i < nbOfLabels; i++) {
      ConfusionMatrix cm = confusionMatrix.getConfusionMatrix(i);
      System.out.println("Label :" + i);
      System.out.println("f1Score: " + cm.f1Score());
      System.out.println("phiCoefficient: " + cm.phiCoefficient());
    }
  }

  private static List<SentenceCategory> getDatasetSet(String[] args, int x) throws IOException {
    return Files.readLines(new File(args[x]), Charset.defaultCharset()).stream().skip(1)
        .map(PATTERN::matcher)
        .filter(Matcher::matches)
        .map(array -> {
          String group = array.group(4);
          labelsToInt.putIfAbsent(group, labelsToInt.size());
          return new SentenceCategory(
              array.group(1).replaceAll("[\"\\-'().\\,\\\\]", " ").replaceAll(" +", " ")
                  .toLowerCase(),
              labelsToInt.get(group));
        })
        .collect(Collectors.toList());
  }
}
