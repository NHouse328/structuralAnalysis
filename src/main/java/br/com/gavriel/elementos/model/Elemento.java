package br.com.gavriel.elementos.model;

import br.com.gavriel.elementos.src.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class Elemento {

    private String name;
    private double length;

    private double angleSin;
    private double angleCos;

    private double sinTimesCos;
    private double angleSinSquered;
    private double angleCosSquered;

    private double modulusOfElasticity;
    private double radius;
    private double crossSection;
    private double axialStiffness;
    private double[][] elementStiffnessMatrix;

    private Point2D start;
    private Point2D end;

    private double deltaX;
    private double deltaY;

    private String angleName;
    private double angleRadian;
    private double angleDegree;

    private final double epsilon = 1e-15;
    private static final Utils utils = new Utils();

    public void setName(String name) {
        this.name = name;
    }
    public void setAngleName(String angleName) {
        this.angleName = angleName;
    }

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
