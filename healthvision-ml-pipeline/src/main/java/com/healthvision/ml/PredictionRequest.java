package com.healthvision.ml;

public class PredictionRequest {
    private double pregnancies;
    private double glucose;
    private double bloodPressure;
    private double skinThickness;
    private double insulin;
    private double bmi;
    private double diabetesPedigreeFunction;
    private double age;

    // Getters and Setters
    public double getPregnancies() { return pregnancies; }
    public void setPregnancies(double pregnancies) { this.pregnancies = pregnancies; }

    public double getGlucose() { return glucose; }
    public void setGlucose(double glucose) { this.glucose = glucose; }

    public double getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(double bloodPressure) { this.bloodPressure = bloodPressure; }

    public double getSkinThickness() { return skinThickness; }
    public void setSkinThickness(double skinThickness) { this.skinThickness = skinThickness; }

    public double getInsulin() { return insulin; }
    public void setInsulin(double insulin) { this.insulin = insulin; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public double getDiabetesPedigreeFunction() { return diabetesPedigreeFunction; }
    public void setDiabetesPedigreeFunction(double diabetesPedigreeFunction) { this.diabetesPedigreeFunction = diabetesPedigreeFunction; }

    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }
}
