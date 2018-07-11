package org.aforgues.rubikscube.presentation.ascii;
import java.util.Collections;
import java.util.List;

import org.aforgues.rubikscube.core.Axis;
import org.aforgues.rubikscube.core.CubeExtractorUtility;
import org.aforgues.rubikscube.core.Cubie;
import org.aforgues.rubikscube.core.Face;
import org.aforgues.rubikscube.core.RubiksCube;
import org.aforgues.rubikscube.presentation.common.GenericRubiksCubeFormat;

/**
 * 
 * Ascii display tools
 * 
 * @author aforgues
 *
 */
public class RubiksCubeAsciiFormat extends GenericRubiksCubeFormat {

	private static final boolean DEBUG = false;
	
	public RubiksCubeAsciiFormat(RubiksCube rubiksCube) {
		super(rubiksCube);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showBackFace()
	 */  
	@Override
	protected void showBackFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(1, Axis.Z);
		
		StringBuilder sb = new StringBuilder("\nBack Face :\n");
		Collections.sort(cubes);
		for (int lineNumber = 1; lineNumber <= this.rubiksCube.getSize(); lineNumber++) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractLine(cubes, lineNumber, this.rubiksCube.getSize()), lineNumber, Face.BACK));
		}	
		System.out.println(sb.toString());
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showTopFace()
	 */
	@Override
	protected void showTopFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(this.rubiksCube.getSize(), Axis.Y);
		
		StringBuilder sb = new StringBuilder("\nTop Face :\n");
		Collections.sort(cubes);
		for (int lineNumber = 1; lineNumber <= this.rubiksCube.getSize(); lineNumber++) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractLine(cubes, lineNumber, this.rubiksCube.getSize()), lineNumber, Face.TOP));
		}	
		System.out.println(sb.toString());
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showLeftFace()
	 */  
	@Override
	protected void showLeftFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(1, Axis.X);
		
		StringBuilder sb = new StringBuilder("\nLeft Face :\n");
		Collections.sort(cubes);
		for (int lineNumber = this.rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractColumn(cubes, lineNumber, this.rubiksCube.getSize()), this.rubiksCube.getSize() - lineNumber + 1, Face.LEFT));
		}	
		System.out.println(sb.toString());
	}

	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showFrontFace()
	 */
	@Override
	protected void showFrontFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(this.rubiksCube.getSize(), Axis.Z);
		
		StringBuilder sb = new StringBuilder("\nFront Face :\n");
		Collections.sort(cubes);
		for (int lineNumber = this.rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractLine(cubes, lineNumber, this.rubiksCube.getSize()), this.rubiksCube.getSize() - lineNumber + 1, Face.FRONT));
		}	
		System.out.println(sb.toString());
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showRightFace()
	 */  
	@Override
	protected void showRightFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(this.rubiksCube.getSize(), Axis.X);
		
		StringBuilder sb = new StringBuilder("\nRight Face :\n");
		Collections.sort(cubes);
		Collections.reverse(cubes);
		for (int lineNumber = 1; lineNumber <= this.rubiksCube.getSize(); lineNumber++) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractColumn(cubes, lineNumber, this.rubiksCube.getSize()), lineNumber, Face.RIGHT));
		}	
		System.out.println(sb.toString());
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat2#showBottomFace()
	 */  
	@Override
	protected void showBottomFace() {
		List<Cubie> cubes = this.rubiksCube.getCubies(1, Axis.Y);
		
		StringBuilder sb = new StringBuilder("\nBottom Face :\n");
		Collections.sort(cubes);
		for (int lineNumber = this.rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			sb.append(showSimpleLine(CubeExtractorUtility.extractLine(cubes, lineNumber, this.rubiksCube.getSize()), this.rubiksCube.getSize() - lineNumber + 1, Face.BOTTOM));
		}	
		System.out.println(sb.toString());
	}
	
	/**
	 * Permet d'afficher une ligne d'une face en format ASCII
	 * @param cubes
	 * @param lineNumber
	 * @param face
	 * @return La representation ASCII de la ligne
	 */
	private String showSimpleLine(List<Cubie> cubes, int lineNumber, Face face) {
		StringBuilder sb = new StringBuilder();
		int rubiksCubeSize = this.rubiksCube.getSize();
		for (int columnIndex = 0; columnIndex < rubiksCubeSize; columnIndex++) {
			// En cas de 1ere colonne
			if (columnIndex == 0)
				sb.append("-");
			sb.append("--");
		}
		sb.append("\n");
		for (int columnIndex = 0; columnIndex < rubiksCubeSize; columnIndex++) {
			// En cas de 1ere colonne
			if (columnIndex == 0)
				sb.append("|");
			if (! DEBUG)
				sb.append(cubes.get(columnIndex).getFace(face) + "|");
			else
				sb.append(cubes.get(columnIndex).getCoordinates().simpleFormat() + "|");
		}
		sb.append("\n");
		// En cas de dernière ligne seulement
		if (lineNumber == rubiksCubeSize) {
			for (int columnIndex = 0; columnIndex < rubiksCubeSize; columnIndex++) {
				// En cas de 1ere colonne
				if (columnIndex == 0)
					sb.append("-");
				sb.append("--");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
