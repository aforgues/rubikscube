import java.util.ArrayList;
import java.util.List;

public class CubeExtractorUtility {

	public static List<Cube> extractColumn(List<Cube> cubes, int number, int rubiksCubeSize) {
		List<Cube> columnCubes = new ArrayList<Cube>();
		for (int columnNumber = 1; columnNumber <= rubiksCubeSize; columnNumber++) {
			columnCubes.add(cubes.get((columnNumber * rubiksCubeSize) - (rubiksCubeSize - number + 1)));
		}
		return columnCubes;
	}

	public static List<Cube> extractLine(List<Cube> cubes, int number, int rubiksCubeSize) {
		return cubes.subList((number - 1) * rubiksCubeSize, number * rubiksCubeSize);
	}


}