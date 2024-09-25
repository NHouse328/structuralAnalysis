package br.com.gavriel.elementos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point2D {
    private String name;
    private double x;
    private double y;
    private Double forceX;
    private Double forceY;

    public Point2D(String name, double x, double y, Double forceX, Double forceY) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.forceX = forceX;
        this.forceY = forceY;
    }

    private Double deslocamentX;
    private Double deslocamentY;

}
