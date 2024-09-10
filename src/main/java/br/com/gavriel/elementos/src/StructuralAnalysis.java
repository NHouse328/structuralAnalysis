package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.gavriel.elementos.model.Alphabet.getLetter;
import static br.com.gavriel.elementos.model.Alphabet.getUpperLetter;
import static br.com.gavriel.elementos.model.GreekAlphabet.getGreekLetter;

@Log4j2
@Data
public class StructuralAnalysis {

	private List<Point2D> points;
	private List<Elemento> elements;

	private double[] matrixSupportsReactions;
	private double[] elementsInternalForces;
	private double[][] globalStiffnessMatrix;
	private double[][] matrixElementsToInvert;
	private double[][] matrixElementsInverse;
	private double[] matrixU;
	private double[] matrixUNotNull;
	private double[] forcesNotNull;;
	private List<Object> knotsForces;
	private List<List<Integer>> elementsKnots;
	private double sum;

	static Utils utils = new Utils();
	static final double epsilon = 1e-10;

	static final DecimalFormat df = new DecimalFormat("0.##########");

	public StructuralAnalysis(List<Point2D> points, List<Elemento> elements) {
		this.points = points;
		this.elements = elements;

		this.elementsKnots = new ArrayList<>();

		for (int i = 0; i < this.elements.size(); i++) {
			Elemento element = this.elements.get(i);

			if (element.getName() == null) {
				element.setName(getLetter(i));
			}

			if (element.getAngleName() == null) {
				element.setAngleName(getGreekLetter(i));
			}

			List<Integer> knots = new ArrayList<>();

			knots.add(this.points.indexOf(element.getStart()) * 2 + 1);
			knots.add(this.points.indexOf(element.getStart()) * 2 + 2);

			knots.add(this.points.indexOf(element.getEnd()) * 2 + 1);
			knots.add(this.points.indexOf(element.getEnd()) * 2 + 2);

			this.elementsKnots.add(knots);
		}

		this.knotsForces = new ArrayList<>();

		for (int i = 0; i < this.points.size(); i++) {
			Point2D point = this.points.get(i);

			if (point.getName() == null) {
				point.setName(getUpperLetter(i));
			}

			this.knotsForces.add(point.getForceX());
			this.knotsForces.add(point.getForceY());

		}

		int degreesOfFreedom = this.points.size() * 2;
		this.globalStiffnessMatrix = new double[degreesOfFreedom][degreesOfFreedom];

		for (int i = 0; i < degreesOfFreedom; i++) {
			for (int j = 0; j < degreesOfFreedom; j++) {

				for (int knot = 0; knot < this.elementsKnots.size(); knot++) {

					for (int knotX = 0; knotX < this.elementsKnots.get(knot).size(); knotX++) {
						for (int knotY = 0; knotY < this.elementsKnots.get(knot).size(); knotY++) {
							if (this.elementsKnots.get(knot).get(knotX) == i+1 && this.elementsKnots.get(knot).get(knotY) == j+1) {
								this.globalStiffnessMatrix[i][j] += this.elements.get(knot).getElementStiffnessMatrix()[knotX][knotY];
							}
						}
					}
				}
			}
		}

		List<Integer> forcesNotNullIndex = new ArrayList<>();

		for (int i = 0; i < this.knotsForces.size(); i++) {
			if (this.knotsForces.get(i) != null) {
				forcesNotNullIndex.add(i);
			}
		}

		int forcesNotNullSize = forcesNotNullIndex.size();

		this.forcesNotNull = new double[forcesNotNullSize];

		for (int i = 0; i < this.forcesNotNull.length; i++) {
			this.forcesNotNull[i] = (Double) this.knotsForces.get(forcesNotNullIndex.get(i));
		}

		this.matrixElementsToInvert = new double[forcesNotNullSize][forcesNotNullSize];

		for (int i = 0; i < forcesNotNullIndex.size(); i++) {
			for (int j = 0; j < forcesNotNullIndex.size(); j++) {
				this.matrixElementsToInvert[i][j] = this.globalStiffnessMatrix[forcesNotNullIndex.get(i)][forcesNotNullIndex.get(j)];
			}
		}

		this.matrixElementsInverse = utils.invertMatrix(this.matrixElementsToInvert);

		this.matrixUNotNull = new double[forcesNotNullSize];

		for (int i = 0; i < forcesNotNullIndex.size(); i++) {
			for (int j = 0; j < forcesNotNullIndex.size(); j++) {
				this.matrixUNotNull[i] = utils.multiplyArrays(this.matrixElementsInverse[i], this.forcesNotNull);
			}
		}

		this.matrixU = new double[this.knotsForces.size()];

		for (int i = 0; i < this.forcesNotNull.length; i++) {
			this.matrixU[forcesNotNullIndex.get(i)] = this.matrixUNotNull[i];
		}

		this.matrixSupportsReactions = new double[this.knotsForces.size()];

		for (int i = 0; i < this.knotsForces.size(); i++) {
			for (int j = 0; j < this.knotsForces.size(); j++) {
				this.matrixSupportsReactions[i] = utils.multiplyArrays(this.globalStiffnessMatrix[i], this.matrixU);
				if (Math.abs(this.matrixSupportsReactions[i]) < epsilon) {
					this.matrixSupportsReactions[i] = 0;
				}
			}
		}

		this.sum = 0.0;
		for (double value: this.matrixSupportsReactions){
			this.sum += value;
		}
		if (Math.abs(this.sum) < epsilon) {
			this.sum = 0;
		} else log.error("Soma de reações de apoio diferente de zero: " + this.sum);

		this.elementsInternalForces = new double[this.elements.size()];

		for (int i = 0; i < this.elementsInternalForces.length; i++) {
			double[] matrixDelta = new double[2];
			matrixDelta[0] = this.matrixU[this.elementsKnots.get(i).get(2)-1] - this.matrixU[this.elementsKnots.get(i).get(0)-1];
			matrixDelta[1] = this.matrixU[this.elementsKnots.get(i).get(3)-1] - this.matrixU[this.elementsKnots.get(i).get(1)-1];

			this.elementsInternalForces[i] =  this.elements.get(i).getAxialStiffness() * utils.multiplyArrays(this.elements.get(i).getTrigonometry(), matrixDelta);
		}

		log.info(Arrays.toString(this.elementsInternalForces));
	}
}
