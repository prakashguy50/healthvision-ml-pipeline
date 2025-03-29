package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.classifiers.trees.J48;
import java.io.File;

public class Trainer {
    public static void main(String[] args) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("src/main/resources/datasets/iris.csv"));
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        
        J48 classifier = new J48();
        classifier.buildClassifier(data);
        
        String modelPath = "iris-model.j48";
        weka.core.SerializationHelper.write(modelPath, classifier);
        
        new GCSClient().uploadModel(
            modelPath,
            "gs://healthvision-ml-20250328-23525/models/iris-model.j48"
        );
    }
}