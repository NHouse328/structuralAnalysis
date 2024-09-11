package br.com.gavriel.elementos.src;

import lombok.extern.log4j.Log4j2;

import java.awt.Color;
import java.util.Random;

@Log4j2
public class Utils {

    public double radiansToDegrees (double radian) {
        return Math.toDegrees(radian);
    }

    public double degreesToRadians (double degree) {
        return Math.toRadians(degree);
    }

    public static double inchToMillimeters(double Inches) {
        return 25.4 * Inches;
    }

    public static double gpaToKgfPreMm2(double gpa) {
        double conversionFactor = 101.971621;

        return gpa * conversionFactor;
    }

//    Perfil "T" aluminio com abas iguais 1.1/2 x 1/8 (3,81cm x 3,17mm)

    public static void main(String[] args) {
        double B = inchToMillimeters(1.5);
        log.info(B);
        double h = inchToMillimeters((double) 1 /8);
        log.info(h);
        double b = inchToMillimeters((double) 1 /8);
        log.info(b);
        double H = inchToMillimeters(1.5);
        log.info(H);

        log.info(tCrossSectionMm(B, h, b, H));
    }

    public static double tCrossSectionMm(double B, double h, double b, double H) {
        return (B * h) + (b * (H - h));
    }

    public double[][] multiplyMatrixByScalar(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }

        return result;
    }

    // Função para calcular a inversa de uma matriz
    public double[][] invertMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] augmentedMatrix = new double[n][2 * n];

        // Criando a matriz aumentada [matrix | identidade]
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = matrix[i][j];
            }
            augmentedMatrix[i][i + n] = 1;
        }

        // Aplicar eliminação de Gauss-Jordan
        for (int i = 0; i < n; i++) {
            // Verificar se o elemento pivô é zero
            if (augmentedMatrix[i][i] == 0) {
                boolean found = false;
                for (int j = i + 1; j < n; j++) {
                    if (augmentedMatrix[j][i] != 0) {
                        // Troca de linhas
                        double[] temp = augmentedMatrix[i];
                        augmentedMatrix[i] = augmentedMatrix[j];
                        augmentedMatrix[j] = temp;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // A matriz não é invertível
                    return null;
                }
            }

            // Normalizar a linha pivô
            double pivot = augmentedMatrix[i][i];
            for (int j = 0; j < 2 * n; j++) {
                augmentedMatrix[i][j] /= pivot;
            }

            // Eliminar as outras entradas da coluna atual
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    double factor = augmentedMatrix[j][i];
                    for (int k = 0; k < 2 * n; k++) {
                        augmentedMatrix[j][k] -= factor * augmentedMatrix[i][k];
                    }
                }
            }
        }

        // Extrair a matriz inversa da matriz aumentada
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = augmentedMatrix[i][j + n];
            }
        }

        return inverse;
    }

    public double multiplyArrays(double[] arrayA, double[] arrayB) {
        if (arrayA.length != arrayB.length) {
            throw new IllegalArgumentException("As arrays devem ter o mesmo tamanho.");
        }

        double result = 0;

        // Multiplicar elemento por elemento e somar
        for (int i = 0; i < arrayA.length; i++) {
            result += arrayA[i] * arrayB[i];
        }

        return result;
    }

    public static double convertMillimeterToMeter(double millimeter) {
        return millimeter / 1000;
    }

    public static Color getRandomColor() {
        Random rand = new Random();

        // Vermelho e Verde são menores para manter o azul dominante
        int red = rand.nextInt(255); // Pequena quantidade de vermelho (0-50)
        int green = rand.nextInt(255); // Pequena quantidade de verde (0-50)
        int blue = rand.nextInt(255); // Azul mais forte (100-255)

        return new Color(red, green, blue);
    }
}
