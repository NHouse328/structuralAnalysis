package br.com.gavriel.elementos.model;

import br.com.gavriel.elementos.src.Utils;

public enum ModulusOfElasticity {
    AISI_ACO_1045(206.0),
    ASTM_A_36(0.4);

    private final double modulusValue;

    ModulusOfElasticity(double modulusValue) {
        this.modulusValue = modulusValue;
    }

    private double modulusOfElasticity() {
        return modulusValue;
    }

    public double getKgfPreMm2() {
        return Utils.gpaToKgfPreMm2(modulusOfElasticity());
    }
}
