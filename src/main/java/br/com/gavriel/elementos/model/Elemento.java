package br.com.gavriel.elementos.model;

import br.com.gavriel.elementos.src.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class Elemento {

    public String name;
    public double length;

    public double angleSin;
    public double angleCos;

    public double sinTimesCos;
    public double angleSinSquered;
    public double angleCosSquered;

    public double modulusOfElasticity;
    public double radius;
    public double crossSection;
    public double axialStiffness;
    public double[][] elementStiffnessMatrix;

    public Point2D start;
    public Point2D end;

    private double deltaX;
    private double deltaY;

    public String angleName;
    public double angleRadian;
    public double angleDegree;

    private final double epsilon = 1e-15;
    private static final Utils utils = new Utils();

    public Elemento(String name, String angleName, double modulusOfElasticity, double radius, Point2D start, Point2D end) {
        this.name = name;
        this.angleName = angleName;

        this.start = start;
        this.end = end;

        this.deltaX = this.end.getX() - this.start.getX();
        this.deltaY = this.end.getY() - this.start.getY();

        this.length = Math.sqrt(Math.pow(this.deltaX, 2) + Math.pow(this.deltaY, 2));
        this.radius = radius;

        this.angleRadian = Math.atan2(deltaY, deltaX);
        this.angleDegree = Math.toDegrees(this.angleRadian);

        if (this.angleDegree < 0) {
//TODO Validar se regra pode ser usada pois inverteu a direção de uma dar forças internas
            this.angleDegree += 360;
        }

        this.modulusOfElasticity = modulusOfElasticity;

        this.angleCos = Math.cos(this.angleRadian);
        if (Math.abs(angleCos) < epsilon) {
            this.angleCos = 0;
        }

        this.angleSin = Math.sin(this.angleRadian);
        if (Math.abs(angleSin) < epsilon) {
            this.angleSin = 0;
        }

        this.angleCosSquered = Math.pow(this.angleCos, 2);
        this.angleSinSquered = Math.pow(this.angleSin, 2);
        this.sinTimesCos = this.angleSin * this.angleCos;
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
