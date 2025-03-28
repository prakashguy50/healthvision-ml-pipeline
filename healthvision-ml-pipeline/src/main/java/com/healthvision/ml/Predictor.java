package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;

public class Predictor {
    private Classifier model;
    
    public Predictor(String modelPath) throws Exception {
        this.model = (Classifier) weka.core.SerializationHelper.read(modelPath);
    }
    
    public String predict(double[] features) throws Exception {
        Instances dummy = new DataSource("datasets/iris.csv").getDataSet();
        dummy.setClassIndex(dummy.numAttributes() - 1);
        dummy.instance(0).setValue(0, features[0]);
        dummy.instance(0).setValue(1, features[1]);
        dummy.instance(0).setValue(2, features[2]);
        dummy.instance(0).setValue(3, features[3]);
        
        double pred = model.classifyInstance(dummy.instance(0));
        return dummy.classAttribute().value((int)pred);
    }
}