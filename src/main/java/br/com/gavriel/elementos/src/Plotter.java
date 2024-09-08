package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.extern.log4j.Log4j2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.List;

@Log4j2
public class Plotter extends JPanel implements MouseWheelListener {
    private List<Point2D> points;
    private List<Elemento> elements;

    private double scale = 1.0; // Fator de escala inicial
    private double offsetX = 250; // Posição inicial do centro no eixo X
    private double offsetY = 250; // Posição inicial do centro no eixo Y
    private Point lastMousePosition; // Posição anterior do mouse para calcular o arrasto


    static final DecimalFormat df = new DecimalFormat("0.00");

    public Plotter(List<Point2D> points, List<Elemento> elements) {
        this.points = points;
        this.elements = elements;

        addMouseWheelListener(this); // Listener para zoom
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint(); // Salva a posição do mouse quando clicado
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Calcula o deslocamento do mouse e ajusta o offset do gráfico
                Point currentMousePosition = e.getPoint();
                double deltaX = currentMousePosition.getX() - lastMousePosition.getX();
                double deltaY = currentMousePosition.getY() - lastMousePosition.getY();
                offsetX += deltaX;
                offsetY += deltaY;
                lastMousePosition = currentMousePosition; // Atualiza a última posição do mouse
                repaint(); // Redesenha o gráfico com o novo centro
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Detecta duplo clique
                if (e.getClickCount() == 2) {
                    // Centraliza o gráfico
                    offsetX = getWidth() / 2.0;
                    offsetY = getHeight() / 2.0;
                    repaint(); // Re-desenha o gráfico com o centro ajustado
                }
            }
        };

        addMouseMotionListener(mouseHandler); // Listener para arrastar
        addMouseListener(mouseHandler); // Listener para clicar e detectar duplo clique
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawCartesianPlan(g2d);
        drawElements(g2d);
        drawPoints(g2d);
    }

    private void drawCartesianPlan(Graphics2D g2d) {
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{5}, 0);

        g2d.setStroke(dashed);

        g2d.drawLine((int) offsetX - 200, (int) offsetY, (int) offsetX + 200, (int) offsetY); // Eixo X
        g2d.drawLine((int) offsetX, (int) offsetY - 200, (int) offsetX, (int) offsetY + 200); // Eixo Y
    }

    private void drawElements(Graphics2D g2d) {
        BasicStroke stroke = new BasicStroke((float) (2.0f * scale),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                10.0f, null, 0.0f);

        g2d.setStroke(stroke);

        for (int i = 0; i < elements.size(); i++) {
            Elemento element = elements.get(i);

            g2d.setColor(Color.BLACK);

            int startX = (int) (offsetX + element.getStart().getX() * scale);
            int startY = (int) (offsetY - element.getStart().getY() * scale);
            int endX = (int) (offsetX + element.getEnd().getX() * scale);
            int endY = (int) (offsetY - element.getEnd().getY() * scale);

            int size = (int) ((500 + 10 * i) * scale);

            g2d.drawLine(startX, startY, endX, endY);

            g2d.setColor(Color.BLUE);

            g2d.drawArc(startX - size / 2, startY - size / 2, size, size, 0, (int) element.getAngleDegree());

            double midX = startX + (size / 2) * Math.cos(element.getAngleRadian() / 2) + 10; // ponto médio em X no arco
            double midY = startY - (size / 2) * Math.sin(element.getAngleRadian() / 2) - 10; // ponto médio em Y no arco

            g2d.drawString(element.getAngleName() + " ≅ " + df.format(element.getAngleDegree()) + "º", (int) midX, (int) midY);
        }
    }


    private void drawPoints(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        
        for (Point2D point : points) {
            
            double x = offsetX + point.getX() * scale;
            double y = offsetY - point.getY() * scale;
            
            g2d.fillOval((int) x - 5, (int) y - 5, 10, 10);
            g2d.drawString("(" + point.getX() + ", " + point.getY() + ")", (int) (x + 5), (int) y - 5);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Pega a posição do mouse
        double mouseX = e.getX();
        double mouseY = e.getY();

        // Calcula a posição atual do mouse em termos de coordenadas no gráfico
        double relativeX = (mouseX - offsetX) / scale;
        double relativeY = (mouseY - offsetY) / scale;

        // Aplica o zoom
        if (e.getPreciseWheelRotation() < 0) {
            scale *= 1.1; // Zoom in
        } else {
            scale *= 0.9; // Zoom out
        }

        // Ajusta o offset para manter o ponto do mouse fixo
        offsetX = mouseX - relativeX * scale;
        offsetY = mouseY - relativeY * scale;

        repaint(); // Re-desenha o gráfico após o zoom
    }

    public static void createAndShowPlot(List<Point2D> points, List<Elemento> elements) {
        JFrame frame = new JFrame("Plotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new Plotter(points, elements));
        frame.setVisible(true);
    }
}
