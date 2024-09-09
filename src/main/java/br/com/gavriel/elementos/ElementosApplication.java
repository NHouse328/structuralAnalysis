package br.com.gavriel.elementos;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import br.com.gavriel.elementos.src.Plotter;
import br.com.gavriel.elementos.src.Utils;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.gavriel.elementos.model.Alphabet.getLetter;
import static br.com.gavriel.elementos.model.Alphabet.getUpperLetter;
import static br.com.gavriel.elementos.model.GreekAlphabet.getGreekLetter;
import static br.com.gavriel.elementos.model.ModulusOfElasticity.AISI_ACO_1045;

@Log4j2
public class ElementosApplication {

	static Utils utils = new Utils();

	public static void main(String[] args) {

		double epsilon = 1e-10;

		double radius = utils.convertMillimeterToMeter(utils.inchToMillimeters(1));
		double modulusOfElasticity = AISI_ACO_1045.getKgfPreMm2();

		List<Point2D> points = Arrays.asList(
			new Point2D("1"	,0.0	 	,0.0		, null, null),
			new Point2D("2"	,5000.0	,0.0		, +0.0, +0.0),
			new Point2D("3"	,10000.0	,1500.0	, +0.0, null),
			new Point2D("4"	,15000.0	,3000.0	, -4000.0, +0.0),
			new Point2D("5"	,20000.0	,3000.0	, -4000.0, +0.0),
			new Point2D("6"	,20000.0	,6000.0	, +0.0, +0.0),
			new Point2D("7"	,15000.0	,6000.0	, +0.0, +0.0),
			new Point2D("8"	,10000.0	,4500.0	, +0.0, +0.0),
			new Point2D("9"	,5000.0	,3000.0	, +0.0, +0.0),
			new Point2D("10"	,0.0		,3000.0	, null, +0.0)
		);

		List<Elemento> elements = Arrays.asList(
				new Elemento("1"	, null, modulusOfElasticity, radius, points.get(1-1)	, points.get(2-1))	,
				new Elemento("2"	, null, modulusOfElasticity, radius, points.get(2-1)	, points.get(3-1))	,
				new Elemento("3"	, null, modulusOfElasticity, radius, points.get(3-1)	, points.get(4-1))	,
				new Elemento("4"	, null, modulusOfElasticity, radius, points.get(4-1)	, points.get(5-1))	,
				new Elemento("5"	, null, modulusOfElasticity, radius, points.get(5-1)	, points.get(6-1))	,
				new Elemento("6"	, null, modulusOfElasticity, radius, points.get(6-1)	, points.get(7-1))	,
				new Elemento("7"	, null, modulusOfElasticity, radius, points.get(7-1)	, points.get(8-1))	,
				new Elemento("8"	, null, modulusOfElasticity, radius, points.get(8-1)	, points.get(9-1))	,
				new Elemento("9"	, null, modulusOfElasticity, radius, points.get(9-1)	, points.get(10-1))	,
				new Elemento("10"	, null, modulusOfElasticity, radius, points.get(10-1)	, points.get(1-1))	,
				new Elemento("11"	, null, modulusOfElasticity, radius, points.get(1-1)	, points.get(9-1))	,
				new Elemento("12"	, null, modulusOfElasticity, radius, points.get(9-1)	, points.get(2-1))	,
				new Elemento("13"	, null, modulusOfElasticity, radius, points.get(9-1)	, points.get(3-1))	,
				new Elemento("14"	, null, modulusOfElasticity, radius, points.get(8-1)	, points.get(3-1))	,
				new Elemento("15"	, null, modulusOfElasticity, radius, points.get(7-1)	, points.get(3-1))	,
				new Elemento("16"	, null, modulusOfElasticity, radius, points.get(7-1)	, points.get(4-1))	,
				new Elemento("17"	, null, modulusOfElasticity, radius, points.get(7-1)	, points.get(5-1))
		);

		List<List<Integer>> elementsKnots = new ArrayList<>();

		for (int i = 0; i < elements.size(); i++) {
			Elemento element = elements.get(i);

			if (element.getName() == null) {
				element.setName(getLetter(i));
			}

			if (element.getAngleName() == null) {
				element.setAngleName(getGreekLetter(i));
			}

			List<Integer> knots = new ArrayList<>();

			knots.add(points.indexOf(element.getStart()) * 2 + 1);
			knots.add(points.indexOf(element.getStart()) * 2 + 2);

			knots.add(points.indexOf(element.getEnd()) * 2 + 1);
			knots.add(points.indexOf(element.getEnd()) * 2 + 2);

			elementsKnots.add(knots);
		}

//		log.info("ELEMENTO;α;λ;μ;λ²;μ²;λ.μ;A[m²];L[m];E[kgf/mm²];EA/L");
//		for (Elemento elemento : elements) {
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

		for (int i = 0; i < points.size(); i++) {
			Point2D point = points.get(i);

			if (point.getName() == null) {
				point.setName(getUpperLetter(i));
			}

			knotsForces.add(point.forceX);
			knotsForces.add(point.forceY);

		}

		int degreesOfFreedom = points.size() * 2;
		double[][] globalStiffnessMatrix = new double[degreesOfFreedom][degreesOfFreedom];

		for (int i = 0; i < degreesOfFreedom; i++) {
			for (int j = 0; j < degreesOfFreedom; j++) {

				for (int knot = 0; knot < elementsKnots.size(); knot++) {

					for (int knotX = 0; knotX < elementsKnots.get(knot).size(); knotX++) {
						for (int knotY = 0; knotY < elementsKnots.get(knot).size(); knotY++) {
							if (elementsKnots.get(knot).get(knotX) == i+1 && elementsKnots.get(knot).get(knotY) == j+1) {
								globalStiffnessMatrix[i][j] += elements.get(knot).getElementStiffnessMatrix()[knotX][knotY];
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

		double[] elementsInternalForces = new double[elements.size()];

		for (int i = 0; i < elementsInternalForces.length; i++) {
			double[] matrixDelta = new double[2];
			matrixDelta[0] = matrixU[elementsKnots.get(i).get(2)-1] - matrixU[elementsKnots.get(i).get(0)-1];
			matrixDelta[1] = matrixU[elementsKnots.get(i).get(3)-1] - matrixU[elementsKnots.get(i).get(1)-1];

			elementsInternalForces[i] =  elements.get(i).getAxialStiffness() * utils.multiplyArrays(elements.get(i).getTrigonometry(), matrixDelta);
		}

		log.info("Elements internal forces : " + Arrays.toString(elementsInternalForces));

		Plotter.createAndShowPlot(points, elements, matrixSupportsReactions);
	}
}
