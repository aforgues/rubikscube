package org.aforgues.rubikscube.core;
import java.util.ArrayList;
import java.util.List;


public class CubeExtractorUtility {

	public static List<Cubie> extractColumn(List<Cubie> cubes, int number, int rubiksCubeSize) {
		List<Cubie> columnCubes = new ArrayList<Cubie>();
		for (int columnNumber = 1; columnNumber <= rubiksCubeSize; columnNumber++) {
			columnCubes.add(cubes.get((columnNumber * rubiksCubeSize) - (rubiksCubeSize - number + 1)));
		}
		return columnCubes;
	}

	public static List<Cubie> extractLine(List<Cubie> cubes, int number, int rubiksCubeSize) {
		return cubes.subList((number - 1) * rubiksCubeSize, number * rubiksCubeSize);
	}


}