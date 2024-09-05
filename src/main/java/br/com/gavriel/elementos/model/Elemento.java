package br.com.gavriel.elementos.model;

import br.com.gavriel.elementos.src.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Elemento {

    public int id;
    public double length;
    public double relativeAngle;
    public double angleSin;
    public double angleCos;
    public double angleSinSquered;
    public double angleCosSquered;
    public double sinTimesCos;
    public double modulusOfElasticity;
    public double radius;
    public double crossSection;
    public double axialStiffness;
    public double[][] elementStiffnessMatrix;

    private double epsilon = 1e-15;
    private static Utils utils = new Utils();


    public Elemento(int id, double relativeAngle, double radius, double length, double modulusOfElasticity) {
        this.id = id;
        this.length = length;
        this.modulusOfElasticity = modulusOfElasticity;
        this.relativeAngle = relativeAngle;

        this.angleCos = Math.cos(relativeAngle);
        if (Math.abs(angleCos) < epsilon) {
            this.angleCos = 0;
        }

        this.angleSin = Math.sin(relativeAngle);
        if (Math.abs(angleSin) < epsilon) {
            this.angleSin = 0;
        }

        this.angleCosSquered = Math.pow(this.angleCos, 2);
        this.angleSinSquered = Math.pow(this.angleSin, 2);
        this.sinTimesCos = this.angleSin * this.angleCos;
        this.radius = radius;
        this.crossSection = Math.PI * Math.pow(this.radius, 2);
        this.axialStiffness = (this.crossSection * this.modulusOfElasticity) / this.length;
        this.elementStiffnessMatrix = calculeMatrixElement();
    }

    private double[][] calculeMatrixElement() {
        double[][] matrix =
            new double[][]{
                    {this.angleCosSquered, this.sinTimesCos, -this.angleCosSquered, -this.sinTimesCos},
                    {this.sinTimesCos, this.angleSinSquered, -this.sinTimesCos, -this.angleSinSquered},
                    {-this.angleCosSquered, -this.sinTimesCos, this.angleCosSquered,this.sinTimesCos},
                    {-this.sinTimesCos, -this.angleSinSquered, this.sinTimesCos, this.angleSinSquered}
            };

        return utils.multiplyMatrixByScalar(matrix, this.axialStiffness);
    }

    public double[] getTrigonometry() {
        return new double[]{this.angleCos, this.angleSin};
    }

}
