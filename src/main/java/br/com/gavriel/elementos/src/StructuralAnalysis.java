package br.com.gavriel.elementos.src;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.gavriel.elementos.model.Alphabet.getLetter;
import static br.com.gavriel.elementos.model.Alphabet.getUpperLetter;
import static br.com.gavriel.elementos.model.GreekAlphabet.getGreekLetter;

@Log4j2
public class StructuralAnalysis {

	private List<Point2D> points;
	private List<Elemento> elements;

	static Utils utils = new Utils();
	static final double epsilon = 1e-10;

	public StructuralAnalysis(List<Point2D> points, List<Elemento> elements) {
		this.points = points;
		this.elements = elements;

		List<List<Integer>> elementsKnots = new ArrayList<>();

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

			elementsKnots.add(knots);
		}

//		log.info("ELEMENTO;α;λ;μ;λ²;μ²;λ.μ;A[m²];L[m];E[kgf/mm²];EA/L");
//		for (Elemento elemento : this.elements) {
//			log.info(
//					elemento.getName() + ";" +
//							elemento.getAngleDegree() + ";" +
//							elemento.getAngleCos() + ";" +
//							elemento.getAngleSin() + ";" +
//							elemento.getAngleCosSquered() + ";" +
//							elemento.getAngleSinSquered() + ";" +
//							elemento.getSinTimesCos() + ";" +
//							elemento.getCrossSection() + ";" +
//							elemento.getLength() + ";" +
//							elemento.getModulusOfElasticity() + ";" +
//							elemento.getAxialStiffness()
//			);
//		}

		List<Object> knotsForces = new ArrayList<>();

		for (int i = 0; i < this.points.size(); i++) {
			Point2D point = this.points.get(i);

			if (point.getName() == null) {
				point.setName(getUpperLetter(i));
			}

			knotsForces.add(point.forceX);
			knotsForces.add(point.forceY);

		}

		int degreesOfFreedom = this.points.size() * 2;
		double[][] globalStiffnessMatrix = new double[degreesOfFreedom][degreesOfFreedom];

		for (int i = 0; i < degreesOfFreedom; i++) {
			for (int j = 0; j < degreesOfFreedom; j++) {

				for (int knot = 0; knot < elementsKnots.size(); knot++) {

					for (int knotX = 0; knotX < elementsKnots.get(knot).size(); knotX++) {
						for (int knotY = 0; knotY < elementsKnots.get(knot).size(); knotY++) {
							if (elementsKnots.get(knot).get(knotX) == i+1 && elementsKnots.get(knot).get(knotY) == j+1) {
								globalStiffnessMatrix[i][j] += this.elements.get(knot).getElementStiffnessMatrix()[knotX][knotY];
							}
						}
					}
				}
			}
		}

		List<Integer> forcesNotNullIndex = new ArrayList<>();

		for (int i = 0; i < knotsForces.size(); i++) {
			if (knotsForces.get(i) != null) {
				forcesNotNullIndex.add(i);
			}
		}

		int forcesNotNullSize = forcesNotNullIndex.size();

		double[] forcesNotNull = new double[forcesNotNullSize];

		for (int i = 0; i < forcesNotNull.length; i++) {
			forcesNotNull[i] = (Double) knotsForces.get(forcesNotNullIndex.get(i));
		}

		double[][] matrixElementsToInvert = new double[forcesNotNullSize][forcesNotNullSize];

		for (int i = 0; i < forcesNotNullIndex.size(); i++) {
			for (int j = 0; j < forcesNotNullIndex.size(); j++) {
				matrixElementsToInvert[i][j] = globalStiffnessMatrix[forcesNotNullIndex.get(i)][forcesNotNullIndex.get(j)];
			}
		}

		double[][] matrixElementsInverse = utils.invertMatrix(matrixElementsToInvert);

		double[] matrixUNotNull = new double[forcesNotNullSize];

		for (int i = 0; i < forcesNotNullIndex.size(); i++) {
			for (int j = 0; j < forcesNotNullIndex.size(); j++) {
				matrixUNotNull[i] = utils.multiplyArrays(matrixElementsInverse[i], forcesNotNull);
			}
		}

		double[] matrixU = new double[knotsForces.size()];

		for (int i = 0; i < forcesNotNull.length; i++) {
			matrixU[forcesNotNullIndex.get(i)] = matrixUNotNull[i];
		}

		log.info("Matrix U : " + Arrays.toString(matrixU));

		double[] matrixSupportsReactions = new double[knotsForces.size()];

		for (int i = 0; i < knotsForces.size(); i++) {
			for (int j = 0; j < knotsForces.size(); j++) {
				matrixSupportsReactions[i] = utils.multiplyArrays(globalStiffnessMatrix[i], matrixU);
				if (Math.abs(matrixSupportsReactions[i]) < epsilon) {
					matrixSupportsReactions[i] = 0;
				}
			}
		}

		log.info("Matrix supports reactions : " + Arrays.toString(matrixSupportsReactions));

		double sum = 0.0;
		for (double value: matrixSupportsReactions){
			sum += value;
		}
		if (Math.abs(sum) < epsilon) {
			sum = 0;
		} else log.error("Soma de reações de apoio diferente de zero: " + sum);

		double[] elementsInternalForces = new double[this.elements.size()];

		for (int i = 0; i < elementsInternalForces.length; i++) {
			double[] matrixDelta = new double[2];
			matrixDelta[0] = matrixU[elementsKnots.get(i).get(2)-1] - matrixU[elementsKnots.get(i).get(0)-1];
			matrixDelta[1] = matrixU[elementsKnots.get(i).get(3)-1] - matrixU[elementsKnots.get(i).get(1)-1];

			elementsInternalForces[i] =  this.elements.get(i).getAxialStiffness() * utils.multiplyArrays(this.elements.get(i).getTrigonometry(), matrixDelta);
		}

		log.info("Elements internal forces : " + Arrays.toString(elementsInternalForces));

		Plotter.createAndShowPlot(this.points, this.elements, matrixSupportsReactions);
	}
}
