package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Colors;
import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.extern.log4j.Log4j2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
    private final List<Double> matrixU;
    private List<Point2D> points;
    private List<Elemento> elements;
    private final double[] matrixSupportsReactions;
    private boolean showData = true;

    private double scale = 1.0; // Fator de escala inicial
    private double offsetX = 250; // Posição inicial do centro no eixo X
    private double offsetY = 250; // Posição inicial do centro no eixo Y
    private Point lastMousePosition; // Posição anterior do mouse para calcular o arrasto


    static final DecimalFormat df = new DecimalFormat("0.00");

    static final Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{5}, 0);

    public Plotter(List<Point2D> points, List<Elemento> elements, StructuralAnalysis analysis, boolean showData) {
        this.points = points;
        this.elements = elements;
        this.matrixSupportsReactions = analysis.getMatrixSupportsReactions();
        this.showData = showData;
        this.matrixU = analysis.getMatrixU();

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

        Font font = new Font("Serif", Font.PLAIN, (int) (160 * scale));
        g2d.setFont(font);

        Colors colors = new Colors();

        for (int i = 0; i < elements.size(); i++) {
            Elemento element = elements.get(i);

            g2d.setColor(Color.BLACK);

            int startX = (int) (offsetX + element.getStart().getX() * scale);
            int startY = (int) (offsetY - element.getStart().getY() * scale);
            int endX = (int) (offsetX + element.getEnd().getX() * scale);
            int endY = (int) (offsetY - element.getEnd().getY() * scale);

            int midXLine = (startX + endX) / 2;
            int midYLine = (startY + endY) / 2;

            int textOffsetY = 10;

            g2d.drawString(element.getName(), midXLine, midYLine - textOffsetY);

            int size = (int) ((500 + 10 * i) * scale);

            g2d.drawLine(startX, startY, endX, endY);

            g2d.setColor(colors.getColor(i));

            g2d.drawArc(startX - size / 2, startY - size / 2, size, size, 0, (int) element.getAngleDegree());

            double RelativeMidX = (size / 2) * Math.cos(element.getAngleRadian() / 2);
            double RelativeMidY = (size / 2) * Math.sin(element.getAngleRadian() / 2);

            double midX;
            double midY;
            if (element.getAngleDegree() <= 180) {
                midX = startX + RelativeMidX + 10* scale; // ponto médio em X no arco
                midY = startY - RelativeMidY + 10* scale; // ponto médio em Y no arco
            } else {
                midX = startX - RelativeMidX - 50* scale; // ponto médio em X no arco
                midY = startY + RelativeMidY + 20* scale; // ponto médio em Y no arco
            }
            if (this.showData) {
                g2d.drawString(element.getAngleName() + " ≅ " + df.format(element.getAngleDegree()) + "º", (int) midX, (int) midY);
            } else {
                g2d.drawString(element.getAngleName(), (int) midX, (int) midY);
            }
        }
    }


    private void drawPoints(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);

        int j = 0;

        Font font = new Font("Serif", Font.PLAIN, (int) (160 * scale));
        g2d.setFont(font);

        int diameter = (int) (30 * scale);
        int fontSize = g2d.getFontMetrics().getHeight();

        for (Point2D point : points) {
            g2d.setColor(Color.RED);
//            Point2D point = point2D;

            double x = offsetX + point.getX() * scale;
            double y = offsetY - point.getY() * scale;

            double forceY;
            double forceX;

            g2d.fillOval((int) x - diameter / 2, (int) y - diameter / 2, diameter, diameter);

            if (this.showData) {
                g2d.drawString(point.getName() + " (" + point.getX() + ", " + point.getY() + ")", (int) (x + 10 * scale), (int) (y + fontSize + 100 * scale));

                if (point.getForceX() != null) {
                    g2d.setColor(Color.BLUE);
                    forceX = point.getForceX();
                } else {
                    g2d.setColor(Color.RED);
                    forceX = matrixSupportsReactions[j];
                }

                if (forceX != 0) {
                    drawArrow(g2d, (int) x, (int) y, (int) (x + forceX * 0.1 * scale), (int) y);
                }

                g2d.drawString("Fx ≅ " + df.format(forceX), (int) (x + 300 * scale), (int) (y + fontSize / 2 + 50 * scale));

                if (point.getForceY() != null) {
                    g2d.setColor(Color.BLUE);
                    forceY = point.getForceY();
                } else {
                    g2d.setColor(Color.RED);
                    forceY = matrixSupportsReactions[j + 1];
                }

                if (forceY != 0) {
                    drawArrow(g2d, (int) x, (int) y, (int) x, (int) (y - forceY * 0.1 * scale));
                }

                g2d.drawString("Fy ≅ " + df.format(forceY), (int) x, (int) (y - 400 * scale));


            } else {
                g2d.drawString(point.getName(), (int) (x + 10 * scale), (int) (y + fontSize + 100 * scale));
            }
            j += 2;
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Desenha a linha da seta
        g2d.drawLine(x1, y1, x2, y2);

        // Calcula o ângulo da linha para posicionar as "asas" da seta
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = (int) (30 * scale);

        // Desenha as duas asas da seta
        int xArrow1 = x2 - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = y2 - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = x2 - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = y2 - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        g2d.drawLine(x2, y2, xArrow1, yArrow1);
        g2d.drawLine(x2, y2, xArrow2, yArrow2);
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

    public static void createAndShowPlot(List<Point2D> points, List<Elemento> elements, StructuralAnalysis analysis, boolean showData) {
        JFrame frame = new JFrame("Plotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new Plotter(points, elements, analysis, showData));
        frame.setVisible(true);
    }
}
