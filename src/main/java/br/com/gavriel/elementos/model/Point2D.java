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
}
