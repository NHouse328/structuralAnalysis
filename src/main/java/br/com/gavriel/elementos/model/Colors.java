package br.com.gavriel.elementos.model;

import java.awt.Color;
public class Colors {
    private final Color[] colors = {
            new Color(255, 99, 132),   // Rosa
            new Color(54, 162, 235),   // Azul
            new Color(255, 206, 86),   // Amarelo
            new Color(75, 192, 192),   // Verde Água
            new Color(153, 102, 255),  // Roxo
            new Color(255, 159, 64),   // Laranja
            new Color(201, 203, 207),  // Cinza Claro
            new Color(0, 128, 128),    // Verde Teal
            new Color(220, 20, 60),    // Vermelho Crimson
            new Color(34, 139, 34),    // Verde Floresta
            new Color(255, 69, 0),     // Laranja Avermelhado
            new Color(255, 215, 0),    // Ouro
            new Color(106, 90, 205),   // Azul Ardósia Médio
            new Color(199, 21, 133),   // Rosa Médio
            new Color(60, 179, 113),   // Verde Primavera Médio
            new Color(255, 228, 196),  // Bege
            new Color(123, 104, 238),  // Azul Royal Médio
            new Color(139, 69, 19),    // Marrom Saddle
            new Color(70, 130, 180),   // Azul Steel
            new Color(210, 105, 30)    // Chocolate
    };

    public Color getColor(int index) {
        return this.colors[index % colors.length];
    }
}
