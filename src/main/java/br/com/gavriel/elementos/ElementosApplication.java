package br.com.gavriel.elementos;

import br.com.gavriel.elementos.model.Elemento;
import br.com.gavriel.elementos.model.Point2D;
import br.com.gavriel.elementos.src.FileLogger;
import br.com.gavriel.elementos.src.Plotter;
import br.com.gavriel.elementos.src.StructuralAnalysis;
import br.com.gavriel.elementos.src.Utils;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

import static br.com.gavriel.elementos.model.ModulusOfElasticity.AISI_ACO_1045;

@Log4j2
public class ElementosApplication {

	public static void main(String[] args) {

		double B = Utils.inchToMillimeters(1.5);
		double h = Utils.inchToMillimeters((double) 1 /8);
		double b = Utils.inchToMillimeters((double) 1 /8);
		double H = Utils.inchToMillimeters(1.5);

//		double crossSection = Math.PI * Math.pow(Utils.convertMillimeterToMeter(Utils.inchToMillimeters(1)), 2);
		double crossSection = Utils.tCrossSectionMm(B, h, b, H);
		double modulusOfElasticity = AISI_ACO_1045.getKgfPreMm2();

//		List<Point2D> points = Arrays.asList(
//				new Point2D(null, 0.0	 ,0.0	, null	, null),
//				new Point2D(null, 5000.0,0.0	, +0.0	, null),
//				new Point2D(null, 3500.0,2000.0	, +1500.0, +1200.0),
//				new Point2D(null, 1500.0,2000.0	, +0.0	, -8000.0)
//		);
//
//		List<Elemento> elements = Arrays.asList(
//				new Elemento(null, modulusOfElasticity, radius, points.get(0), points.get(1)),
//				new Elemento(null, modulusOfElasticity, radius, points.get(1), points.get(2)),
//				new Elemento(null, modulusOfElasticity, radius, points.get(2), points.get(3)),
//				new Elemento(null, modulusOfElasticity, radius, points.get(3), points.get(0)),
//				new Elemento(null, modulusOfElasticity, radius, points.get(2), points.get(0)),
//				new Elemento(null, modulusOfElasticity, radius, points.get(3), points.get(1))
//		);

		List<Point2D> points = Arrays.asList(
			new Point2D("1"	,0.0	 	,0.0		, null, null),
			new Point2D("2"	,5000.0	,0.0		, +0.0, +0.0),
			new Point2D("3"	,10000.0	,1500.0	, +0.0, null),
			new Point2D("4"	,15000.0	,3000.0	, +0.0, +0.0),
			new Point2D("5"	,20000.0	,3000.0	, +0.0, +0.0),
			new Point2D("6"	,20000.0	,6000.0	, +0.0, +0.0),
			new Point2D("7"	,15000.0	,6000.0	, +0.0, +0.0),
			new Point2D("8"	,10000.0	,4500.0	, +0.0, +0.0),
			new Point2D("9"	,5000.0	,3000.0	, +0.0, -8826.0),
			new Point2D("10"	,0.0		,3000.0	, null, -8826.0),
			new Point2D("Massa"	,17500.0		,-4000.0	, +0.0, -83300.0)
		);

		List<Elemento> elements = Arrays.asList(
				new Elemento("1"	, modulusOfElasticity, crossSection, points.get(1-1)	, points.get(2-1))	,
				new Elemento("2"	, modulusOfElasticity, crossSection, points.get(2-1)	, points.get(3-1))	,
				new Elemento("3"	, modulusOfElasticity, crossSection, points.get(3-1)	, points.get(4-1))	,
				new Elemento("4"	, modulusOfElasticity, crossSection, points.get(4-1)	, points.get(5-1))	,
				new Elemento("5"	, modulusOfElasticity, crossSection, points.get(5-1)	, points.get(6-1))	,
				new Elemento("6"	, modulusOfElasticity, crossSection, points.get(6-1)	, points.get(7-1))	,
				new Elemento("7"	, modulusOfElasticity, crossSection, points.get(7-1)	, points.get(8-1))	,
				new Elemento("8"	, modulusOfElasticity, crossSection, points.get(8-1)	, points.get(9-1))	,
				new Elemento("9"	, modulusOfElasticity, crossSection, points.get(9-1)	, points.get(10-1))	,
				new Elemento("10"	, modulusOfElasticity, crossSection, points.get(10-1)	, points.get(1-1))	,
				new Elemento("11"	, modulusOfElasticity, crossSection, points.get(1-1)	, points.get(9-1))	,
				new Elemento("12"	, modulusOfElasticity, crossSection, points.get(9-1)	, points.get(2-1))	,
				new Elemento("13"	, modulusOfElasticity, crossSection, points.get(9-1)	, points.get(3-1))	,
				new Elemento("14"	, modulusOfElasticity, crossSection, points.get(8-1)	, points.get(3-1))	,
				new Elemento("15"	, modulusOfElasticity, crossSection, points.get(7-1)	, points.get(3-1))	,
				new Elemento("16"	, modulusOfElasticity, crossSection, points.get(7-1)	, points.get(4-1))	,
				new Elemento("17"	, modulusOfElasticity, crossSection, points.get(7-1)	, points.get(5-1))	,
				new Elemento("18"	, modulusOfElasticity, crossSection, points.get(11-1)	, points.get(4-1))	,
				new Elemento("19"	, modulusOfElasticity, crossSection, points.get(11-1)	, points.get(5-1))
		);

		StructuralAnalysis analysis = new StructuralAnalysis(points, elements);

		new FileLogger("A1_Solved", points, elements, analysis);
		Plotter.createAndShowPlot(points, elements, analysis, false);
	}
}
