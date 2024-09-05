package br.com.gavriel.elementos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point2D {
    public String name;
    public double x;
    public double y;
    public Double forceX;
    public Double forceY;
}
