package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Log4j2
public class FileLogger {

    static final DecimalFormat df = new DecimalFormat("0.##########");

    private static final String fileRoot = "src/main/resources/";
    private String filePath;
    private final List<Point2D> points;
    private final List<Elemento> elements;
    private final StructuralAnalysis analysis;
    private BufferedWriter bw;;

    public FileLogger(String fileName, List<Point2D> points, List<Elemento> elements, StructuralAnalysis analysis) {
        this.filePath = fileRoot + fileName + ".md";
        this.points = points;
        this.elements = elements;
        this.analysis = analysis;

        try {
            File file = new File(filePath);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            
            this.bw = new BufferedWriter(fw);

            this.bw.write("![a1 solved](img/a1.png)" + "\n\n");

            writeElementsStiffnessMatrix();
            writeMatrixByElement();
            writeGlobalStiffnessMatrix();
            writeFirstInverceMatrix();
            writeSecondInverceMatrix();
            writeMatrixSupportsReactions();
            writeElementsInternalForces();
            writeDeslocamentos();


            this.bw.close();
            log.info("Informações foram gravadas no arquivo " + filePath);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void writeDeslocamentos() throws IOException {
        this.bw.write("## Definir a matriz de deslocamentos." + "\n");

        this.bw.write("|Deslocamento");
        this.bw.write("| |" + "\n");
        this.bw.write("|---|---|" + "\n");

        for (int i = 0; i < analysis.getMatrixU().length; i++) {
            this.bw.write("|U" + i);
            this.bw.write("|" + df.format(analysis.getMatrixU()[i]));
            this.bw.write("|" + "\n");
        }
    }

    private void writeFirstInverceMatrix() throws IOException {

        this.bw.write("## Definir a matriz inversa para encontrar os deslocamentos." + "\n");

        this.bw.write("|Deslocamento");
        for (int i = 0; i < analysis.getMatrixElementsToInvert().length; i++) {
            this.bw.write("| ");
        }
        this.bw.write("|Forças|" + "\n");

        for (int i = 0; i < analysis.getMatrixElementsToInvert().length+2; i++) {
            this.bw.write("|---");
        }
        this.bw.write("|" + "\n");

        for (int i = 0; i < analysis.getMatrixElementsToInvert().length; i++) {
            int u = analysis.getKnotsForces().indexOf(analysis.getForcesNotNull()[i]) + 1;
            this.bw.write("|U" + u);

            for (int j = 0; j < analysis.getMatrixElementsToInvert().length; j++) {
                this.bw.write("|" + analysis.getMatrixElementsToInvert()[i][j]);
            }
            this.bw.write("|" + analysis.getForcesNotNull()[i]);
            this.bw.write("|" + "\n");
        }
    }

    private void writeSecondInverceMatrix() throws IOException {
        this.bw.write("## Definir a matriz inversa para encontrar os deslocamentos." + "\n");

        this.bw.write("|Deslocamento");
        for (int i = 0; i < analysis.getMatrixElementsInverse().length; i++) {
            this.bw.write("| ");
        }
        this.bw.write("|Forças|Mult. Matrizes|" + "\n");

        for (int i = 0; i < analysis.getMatrixElementsInverse().length+3; i++) {
            this.bw.write("|---");
        }
        this.bw.write("|" + "\n");

        for (int i = 0; i < analysis.getMatrixElementsInverse().length; i++) {
            int u = analysis.getKnotsForces().indexOf(analysis.getForcesNotNull()[i]) + 1;
            this.bw.write("|U" + u);

            for (int j = 0; j < analysis.getMatrixElementsInverse().length; j++) {
                this.bw.write("|" + analysis.getMatrixElementsInverse()[i][j]);
            }

            this.bw.write("|" + analysis.getForcesNotNull()[i]);
            this.bw.write("|" + analysis.getMatrixUNotNull()[i]);
            this.bw.write("|" + "\n");
        }
    }

    private void writeElementsInternalForces() throws IOException {
        this.bw.write("![elements Internal Forces](img/elementInternalForces.png)" + "\n");

        this.bw.write("## Forças internas e Característica." + "\n");
        this.bw.write("|Elemento|EA/L|λ|μ|Força|Característica|" + "\n");
        this.bw.write("|---|---|---|---|---|---|" + "\n");
        for (int i = 0; i < this.elements.size(); i++) {
            Elemento elemento = this.elements.get(i);

            this.bw.write("|" );
            this.bw.write( elemento.getName() + "|");
            this.bw.write( df.format(elemento.getAxialStiffness()) + "|");
            this.bw.write( df.format(elemento.getAngleCos()) + "|");
            this.bw.write( df.format(elemento.getAngleSin()) + "|");
            this.bw.write( df.format(analysis.getElementsInternalForces()[i]) + "|");
            if (analysis.getElementsInternalForces()[i] > 0 ){
                this.bw.write("TRAÇÃO" + "|");
            } else {
                this.bw.write("COMPRESSÃO" + "|");
            }
            this.bw.write("\n");

        }
    }

    private void writeMatrixSupportsReactions() throws IOException {
        this.bw.write("## Encontrar as reações nos apoios." + "\n");

        this.bw.write("|Deslocamento|Valor (kN)| Resultado");
        this.bw.write("|" + "\n");

        for (int i = 0; i < 3; i++) {
            this.bw.write("|---");
        }
        this.bw.write("|" + "\n");

        for (int i = 0; i < this.analysis.getMatrixSupportsReactions().length; i++) {
            this.bw.write("|F" + (i+1));

            if (analysis.getKnotsForces().get(i) == null) {
                this.bw.write("|0");
            } else {
                this.bw.write("|" + analysis.getMatrixU()[i]);
            }

            this.bw.write("|" + df.format(this.analysis.getMatrixSupportsReactions()[i]));


            this.bw.write("|" + "\n");
        }
        this.bw.write("| |SOMA:|" + df.format(analysis.getSum()) + "|\n");
    }

    private void writeGlobalStiffnessMatrix() throws IOException {
        this.bw.write("## Matriz de Rigidez Global" + "\n");

        this.bw.write("|F|Valor (kN)");
        for (int i = 0; i < this.analysis.getGlobalStiffnessMatrix().length; i++) {
            this.bw.write("|");
            this.bw.write(String.valueOf(i+1));
        }
        this.bw.write("||" + "\n");

        for (int i = 0; i < this.analysis.getGlobalStiffnessMatrix().length +3; i++) {
            this.bw.write("|---");
        }
        this.bw.write("|" + "\n");

        for (int i = 0; i < this.analysis.getGlobalStiffnessMatrix().length; i++) {
            this.bw.write("|F" + (i+1));
            if (analysis.getKnotsForces().get(i) == null) {
                this.bw.write("|F" + (i+1));
            } else {
                this.bw.write("|" + analysis.getKnotsForces().get(i));
            }

            for (int j = 0; j < this.analysis.getGlobalStiffnessMatrix()[i].length; j++) {
                this.bw.write("|" + df.format(this.analysis.getGlobalStiffnessMatrix()[i][j]));
            }

            this.bw.write("|" + (i+1));
            this.bw.write("|" + "\n");
        }
    }

    private void writeElementsStiffnessMatrix() throws IOException {
        this.bw.write("## Matriz de Rigidez de cada elemento:" + "\n");
        this.bw.write("|ELEMENTO|α|λ|μ|λ²|μ²|λ.μ|A[mm²]|L[mm]|E[kgf/mm²]|EA/L|" + "\n");
        this.bw.write("|---|---|---|---|---|---|---|---|---|---|---|" + "\n");
        for (Elemento elemento : this.elements) {
            this.bw.write(								       "|" +
                elemento.getName()					            + "|" +
                df.format(elemento.getAngleDegree()) 			+ "|" +
                df.format(elemento.getAngleCos()) 				+ "|" +
                df.format(elemento.getAngleSin()) 				+ "|" +
                df.format(elemento.getAngleCosSquered()) 		+ "|" +
                df.format(elemento.getAngleSinSquered()) 		+ "|" +
                df.format(elemento.getSinTimesCos()) 			+ "|" +
                df.format(elemento.getCrossSection()) 			+ "|" +
                df.format(elemento.getLength()) 				+ "|" +
                df.format(elemento.getModulusOfElasticity()) 	+ "|" +
                df.format(elemento.getAxialStiffness()) 		+ "|" + "\n"
            );
        }
    }
    private void writeMatrixByElement() throws IOException {

        this.bw.write("![elements Stiffness Matrix](img/elementStiffnessMatrix.png)" + "\n");

        this.bw.write("## Elementos" + "\n");
        for (int i = 0; i < this.elements.size(); i++) {
            Elemento element = this.elements.get(i);

            this.bw.write("### Elemento " + element.getName() + "\n");

            this.bw.write(						  "|" +
                    this.analysis.getElementsKnots().get(i).get(0)	+ "|" +
                    this.analysis.getElementsKnots().get(i).get(1)	+ "|" +
                    this.analysis.getElementsKnots().get(i).get(2) + "|" +
                    this.analysis.getElementsKnots().get(i).get(3)	+ "| |\n"
            );
            this.bw.write("|---|---|---|---|---|\n");

            for (int j = 0; j < element.getElementStiffnessMatrix().length; j++) {
                StringBuilder text = new StringBuilder("|");
                double[][] elementStiffnessMatrix = element.getElementStiffnessMatrix();

                for (int k = 0; k < elementStiffnessMatrix.length; k++) {
                    text.append(df.format(elementStiffnessMatrix[j][k]));
                    text.append("|");
                }

                text.append(this.analysis.getElementsKnots().get(i).get(j));
                text.append("|\n");

                this.bw.write(String.valueOf(text));
            }
            this.bw.write("\n");
        }
    }
}

