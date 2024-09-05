package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Coordinate;
import lombok.extern.log4j.Log4j2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

@Log4j2
public class Plotter  extends JPanel {
    private List<Coordinate> coordinates;

    public Plotter(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Desenha o plano cartesiano
        g2d.drawLine(50, 250, 450, 250); // Eixo X
        g2d.drawLine(250, 50, 250, 450); // Eixo Y

        // Desenha os pontos
        g2d.setColor(Color.RED);
        for (Coordinate coord : coordinates) {
            int x = 250 + coord.getX(); // Ajusta o ponto para o centro do gráfico
            int y = 250 - coord.getY(); // Inverte o eixo Y para manter o sentido "positivo" para cima
            g2d.fillOval(x - 5, y - 5, 10, 10); // Desenha o ponto (círculo)
            g2d.drawString("(" + coord.getX() + ", " + coord.getY() + ")", x + 5, y - 5); // Exibe as coordenadas ao lado do ponto
        }
    }

    public static void createAndShowPlot(List<Coordinate> coordinates) {
        JFrame frame = new JFrame("Plotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new Plotter(coordinates));
        frame.setVisible(true);
    }
}
