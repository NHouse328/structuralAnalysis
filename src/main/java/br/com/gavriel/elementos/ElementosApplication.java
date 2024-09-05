package br.com.gavriel.elementos;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.src.Utils;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class ElementosApplication {

	static Utils utils = new Utils();
	public static void main(String[] args) {

		double epsilon = 1e-11;

		double[] elementsAngle = {0.0, 126.87, 180.0, 233.13, 29.74, 150.26};
		double[] elementsLength = {5000.0, 2500.0, 2000.0, 2500.0, 4031.13, 4031.13};
		Object[] knotsForces = {null, null, 0.0, null, 1500.0, 1200.0, 0.0, -8000.0};
		int degreesOfFreedom = 8;

		int[][] elementsKnots =
			{
				{1, 2, 3, 4},
				{3, 4, 5, 6},
				{5, 6, 7, 8},
				{1, 2, 7, 8},
				{1, 2, 5, 6},
				{3, 4, 7, 8}
			};

		double[][] globalStiffnessMatrix = new double[degreesOfFreedom][degreesOfFreedom];

		Elemento[] elements = new Elemento[elementsAngle.length];

		for (int i = 0; i < elementsAngle.length; i++) {
			elements[i] = new Elemento(i, utils.degreesToRadians(elementsAngle[i]), utils.convertMillimeterToMeter(utils.inchToMillimeters(1)), utils.convertMillimeterToMeter(elementsLength[i]), utils.gpaToKgfPreMm2(206));
		}

		for (int i = 0; i < degreesOfFreedom; i++) {
			for (int j = 0; j < degreesOfFreedom; j++) {

				for (int knot = 0; knot < elementsKnots.length; knot++) {

					for (int knotX = 0; knotX < elementsKnots[knot].length; knotX++) {
						for (int knotY = 0; knotY < elementsKnots[knot].length; knotY++) {
							if (elementsKnots[knot][knotX] == i+1 && elementsKnots[knot][knotY] == j+1) {
								globalStiffnessMatrix[i][j] += elements[knot].getElementStiffnessMatrix()[knotX][knotY];
							}
						}
					}
				}
			}
		}

		List<Integer> forcesNotNullIndex = new ArrayList<>();

		for (int i = 0; i < knotsForces.length; i++) {
			if (knotsForces[i] != null) {
				forcesNotNullIndex.add(i);
			}
		}

		int forcesNotNullSize = forcesNotNullIndex.size();

		double[] forcesNotNull = new double[forcesNotNullSize];

		for (int i = 0; i < forcesNotNull.length; i++) {
			forcesNotNull[i] = (Double)knotsForces[forcesNotNullIndex.get(i)];
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

		double[] matrixU = new double[knotsForces.length];

		for (int i = 0; i < forcesNotNull.length; i++) {
			matrixU[forcesNotNullIndex.get(i)] = matrixUNotNull[i];
		}

		log.info("Matrix U : " + Arrays.toString(matrixU));

		double[] matrixSupportsReactions = new double[knotsForces.length];

		for (int i = 0; i < knotsForces.length; i++) {
			for (int j = 0; j < knotsForces.length; j++) {
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

		double[] elementsInternalForces = new double[elements.length];

		for (int i = 0; i < elementsInternalForces.length; i++) {
			double[] matrixDelta = new double[2];
			matrixDelta[0] = matrixU[elementsKnots[i][2]-1] - matrixU[elementsKnots[i][0]-1];
			matrixDelta[1] = matrixU[elementsKnots[i][3]-1] - matrixU[elementsKnots[i][1]-1];

			elementsInternalForces[i] =  elements[i].getAxialStiffness() * utils.multiplyArrays(elements[i].getTrigonometry(), matrixDelta);
		}

		log.info("Elements internal forces : " + Arrays.toString(elementsInternalForces));
	}
}
