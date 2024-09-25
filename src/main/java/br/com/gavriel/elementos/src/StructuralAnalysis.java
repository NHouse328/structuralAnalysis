package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Data
public class StructuralAnalysis {

	private final double[][] globalStiffnessMatrix;
	private final double[][] matrixElementsToInvert;
	private final double[][] matrixElementsInverse;
	private final List<Double> forces;
	private final ArrayList<Double> forcesNotNull;
	private final ArrayList<Integer> forcesNotNullIndex;
	private final ArrayList<Double> matrixU;
	private final double[] matrixSupportsReactions;
	private double sum;
	private List<Point2D> points;
	private List<Elemento> elements;

	static Utils utils = new Utils();
	static final DecimalFormat df = new DecimalFormat("0.000000");

	public StructuralAnalysis(List<Point2D> points, List<Elemento> elements) {
		this.points = points;
		this.elements = elements;

		int degreesOfFreedom = this.points.size() * 2;
		this.globalStiffnessMatrix = new double[degreesOfFreedom][degreesOfFreedom];

		for (Elemento element: elements) {
			element.getElementKnots().add(this.points.indexOf(element.getStart()) * 2);
			element.getElementKnots().add(this.points.indexOf(element.getStart()) * 2 + 1);
			element.getElementKnots().add(this.points.indexOf(element.getEnd()) * 2);
			element.getElementKnots().add(this.points.indexOf(element.getEnd()) * 2 + 1);

			for (int i = 0; i < element.getElementKnots().size(); i++) {
				for (int j = 0; j < element.getElementKnots().size(); j++) {
					this.globalStiffnessMatrix[element.getElementKnots().get(i)][element.getElementKnots().get(j)] += element.getElementStiffnessMatrix()[i][j];
				}
			}
		}

		this.forces = new ArrayList<>();
		this.forcesNotNull = new ArrayList<>();
		this.forcesNotNullIndex = new ArrayList<>();

		for (Point2D point: points) {
			forces.add(point.getForceX());
			forces.add(point.getForceY());
		}

		for (int i = 0; i < forces.size(); i++) {
			if (forces.get(i) != null) {
				forcesNotNull.add(forces.get(i));
				forcesNotNullIndex.add(i);
			}
		}

		this.matrixElementsToInvert = new double[forcesNotNull.size()][forcesNotNull.size()];

		for (int i = 0; i < forcesNotNullIndex.size(); i++) {
			for (int j = 0; j < forcesNotNullIndex.size(); j++) {
				this.matrixElementsToInvert[i][j] = this.globalStiffnessMatrix[forcesNotNullIndex.get(i)][forcesNotNullIndex.get(j)];
			}
		}

		this.matrixElementsInverse = utils.invertMatrix(this.matrixElementsToInvert);

		this.matrixU = new ArrayList<>();

		int j = 0;
		for (Double force : forces) {
			if (force != null) {
				this.matrixU.add(utils.multiplyArrays(this.matrixElementsInverse[j], this.forcesNotNull));
				j++;
			} else {
				this.matrixU.add(0.0);
			}
		}

		this.matrixSupportsReactions = new double[this.forces.size()];

		for (int i = 0; i < this.matrixSupportsReactions.length; i++) {
			this.matrixSupportsReactions[i] = utils.multiplyArrays(this.globalStiffnessMatrix[i], this.matrixU);
		}

		this.sum = 0.0;
		for (double value: this.matrixSupportsReactions){
			this.sum += value;
		}
		if (!Objects.equals(df.format(this.sum), df.format(0))) log.error("Soma de reações de apoio diferente de zero: " + df.format(this.sum));

		for (int i = 0; i < elements.size(); i++) {
			elements.get(i).getMatrixDeltaU().add(this.matrixU.get(elements.get(i).getElementKnots().get(2)) - this.matrixU.get(elements.get(i).getElementKnots().get(0)));
			elements.get(i).getMatrixDeltaU().add(this.matrixU.get(elements.get(i).getElementKnots().get(3)) - this.matrixU.get(elements.get(i).getElementKnots().get(1)));

			elements.get(i).setElementsInternalForces(this.elements.get(i).getAxialStiffness() * utils.multiplyArrays(this.elements.get(i).getTrigonometry(), elements.get(i).getMatrixDeltaU()));
		}
	}
}