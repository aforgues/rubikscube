package org.aforgues.rubikscube.presentation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

import org.aforgues.rubikscube.core.CubeExtractorUtility;
import org.aforgues.rubikscube.core.Cubie;
import org.aforgues.rubikscube.core.Face;
import org.aforgues.rubikscube.core.RubiksCube;

/**
 * 
 * 2D display tools
 * 
 * @author aforgues
 *
 */
public class RubiksCube2DFormat extends GenericRubiksCubeFormat {

	private final boolean DEBUG = false;

	public static final int CUBE_SIZE = 50;
	public static final int CUBE_MARGIN = CUBE_SIZE/10;
	
	private Graphics2D graphics;
	private double x_offset = 0;
	private double y_offset = 0;
	
	public RubiksCube2DFormat(RubiksCube rubiksCube, Graphics2D g) {
		super(rubiksCube);
		this.graphics = g;
	}
	
	public Point getFaceOffset(Face face) {
		switch (face) {
			case TOP:
				return this.getTopFaceOffset();
			case BOTTOM:
				return this.getBottomFaceOffset();
			case FRONT:
				return this.getFrontFaceOffset();
			case BACK:
				return this.getBackFaceOffset();
			case LEFT:
				return this.getLeftFaceOffset();
			case RIGHT:
				return this.getRightFaceOffset();
		}
		return null;
	}
	
	@Override
	public void show() {
		//if (this.rubiksCube == null)
			// TODO : effacer le cube actuallement affiché
			//this.graphics.clear ?
		
		super.show();
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showBackFace()
	 */  
	@Override
	protected void showBackFace() {
		List<Cubie> cubes = rubiksCube.getBackFaceCubes();
		
		if (DEBUG) {
			System.out.println("\nBack Face 2D :\n");
		}
			
		x_offset = getBackFaceOffset().getX();
		y_offset = getBackFaceOffset().getY();
		
		Collections.sort(cubes);
		for (int lineNumber = 1; lineNumber <= rubiksCube.getSize(); lineNumber++) {
			showSimpleLine2D(CubeExtractorUtility.extractLine(cubes, lineNumber, rubiksCube.getSize()), lineNumber, Face.BACK);
		}	
	}
	
	public Point getBackFaceOffset() {
		return new Point((1 + rubiksCube.getSize()) * CUBE_SIZE + CUBE_MARGIN,
				         1 * CUBE_SIZE);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showTopFace()
	 * Affichage en 2D via Awt de la face haute du Rubik's Cube
	 */
	@Override
	protected void showTopFace() {
		List<Cubie> cubes = rubiksCube.getTopFaceCubes();
		
		if (DEBUG) {
			System.out.println("\nTop Face 2D :\n");
		}

		x_offset = getTopFaceOffset().getX();
		y_offset = getTopFaceOffset().getY();
		
		Collections.sort(cubes);
		for (int lineNumber = 1; lineNumber <= rubiksCube.getSize(); lineNumber++) {
			showSimpleLine2D(CubeExtractorUtility.extractLine(cubes, lineNumber, rubiksCube.getSize()), lineNumber, Face.TOP);
		}	
	}
	
	public Point getTopFaceOffset() {
		return new Point((1 + rubiksCube.getSize()) * CUBE_SIZE + CUBE_MARGIN,
				         (1 + rubiksCube.getSize()) * CUBE_SIZE + CUBE_MARGIN);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showLeftFace()
	 */  
	@Override
	protected void showLeftFace() {
		List<Cubie> cubes = rubiksCube.getLeftFaceCubes();
		
		if (DEBUG){
			System.out.println("\nLeft Face 2D :\n");
		}

		x_offset = getLeftFaceOffset().getX();
		y_offset = getLeftFaceOffset().getY();
		
		Collections.sort(cubes);
		for (int lineNumber = rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			showSimpleLine2D(CubeExtractorUtility.extractColumn(cubes, lineNumber, rubiksCube.getSize()), rubiksCube.getSize() - lineNumber + 1, Face.LEFT);
		}	
	}

	public Point getLeftFaceOffset() {
		return new Point(1 * CUBE_SIZE,
				         (1 + 2 * rubiksCube.getSize()) * CUBE_SIZE + 2 * CUBE_MARGIN);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showFrontFace()
	 */
	@Override
	protected void showFrontFace() {
		List<Cubie> cubes = rubiksCube.getFrontFaceCubes();
		
		if (DEBUG) {
			System.out.println("\nFront Face 2D :\n");
		}

		x_offset = getFrontFaceOffset().getX();
		y_offset = getFrontFaceOffset().getY();
		
		Collections.sort(cubes);
		for (int lineNumber = rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			showSimpleLine2D(CubeExtractorUtility.extractLine(cubes, lineNumber, rubiksCube.getSize()), rubiksCube.getSize() - lineNumber + 1, Face.FRONT);
		}	
	}
	
	public Point getFrontFaceOffset() {
		return new Point((1 + rubiksCube.getSize()) * CUBE_SIZE + CUBE_MARGIN,
				         (1 + 2 * rubiksCube.getSize()) * CUBE_SIZE + 2 * CUBE_MARGIN);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showRightFace()
	 */  
	@Override
	protected void showRightFace() {
		List<Cubie> cubes = rubiksCube.getRightFaceCubes();
		
		if (DEBUG) {
			System.out.println("\nRight Face 2D :\n");
		}

		x_offset = getRightFaceOffset().getX();
		y_offset = getRightFaceOffset().getY();
		
		Collections.sort(cubes);
		Collections.reverse(cubes);
		for (int lineNumber = 1; lineNumber <= rubiksCube.getSize(); lineNumber++) {
			showSimpleLine2D(CubeExtractorUtility.extractColumn(cubes, lineNumber, rubiksCube.getSize()), lineNumber, Face.RIGHT);
		}	
	}
	
	public Point getRightFaceOffset() {
		return new Point((1 + 2 * rubiksCube.getSize()) * CUBE_SIZE + 2 * CUBE_MARGIN,
				         (1 + 2 * rubiksCube.getSize()) * CUBE_SIZE + 2 * CUBE_MARGIN);
	}
	
	/* (non-Javadoc)
	 * @see IRubiksCubeFormat#showBottomFace()
	 */  
	@Override
	protected void showBottomFace() {
		List<Cubie> cubes = rubiksCube.getBottomFaceCubes();
		
		if (DEBUG) {
			System.out.println("\nBottom Face 2D :\n");
		}

		x_offset = getBottomFaceOffset().getX();
		y_offset = getBottomFaceOffset().getY();
		
		Collections.sort(cubes);
		for (int lineNumber = rubiksCube.getSize(); lineNumber >= 1; lineNumber--) {
			showSimpleLine2D(CubeExtractorUtility.extractLine(cubes, lineNumber, rubiksCube.getSize()), rubiksCube.getSize() - lineNumber + 1, Face.BOTTOM);
		}	
	}
	
	public Point getBottomFaceOffset() {
		return new Point((1 + rubiksCube.getSize()) * CUBE_SIZE + CUBE_MARGIN,
				         (1 + 3 * rubiksCube.getSize()) * CUBE_SIZE + 3 * CUBE_MARGIN);
	}
	
	/**
	 * Permet d'afficher une ligne d'une face en 2D
	 * @param cubes
	 * @param lineNumber
	 * @param face
	 * @return L'affichage en 2D de la ligne
	 */
	private void showSimpleLine2D(List<Cubie> cubes, int lineNumber, Face face) {
		boolean showFaceIdentified = face.equals(this.rubiksCube.getPressedFaceIdentified())
				                  && this.rubiksCube.isFaceMove();
		
		for (int columnIndex = 0; columnIndex < this.rubiksCube.getSize(); columnIndex++) {
			double x = x_offset + columnIndex * CUBE_SIZE;
			double y = y_offset + (lineNumber - 1) * CUBE_SIZE;
			
			boolean showPointIdentified = (this.rubiksCube.hasPointOnPressedFaceIdentified()
				     && this.rubiksCube.getPointOnPressedFaceIdentified().equals(new Point(columnIndex, lineNumber - 1)))
				     && face.equals(this.rubiksCube.getPressedFaceIdentified());
			
			if (showFaceIdentified) {
				this.graphics.setPaint(java.awt.Color.BLACK);
				this.graphics.drawRect((int)x, (int)y, CUBE_SIZE, CUBE_SIZE);
			}
			else if (showPointIdentified) {
				this.graphics.setPaint(java.awt.Color.PINK);
				this.graphics.fill3DRect((int)x, (int)y, CUBE_SIZE, CUBE_SIZE, true);
			}
			else {
				this.graphics.setPaint(cubes.get(columnIndex).getFace(face).getAwtColor());
				this.graphics.fill3DRect((int)x, (int)y, CUBE_SIZE, CUBE_SIZE, true);
			}
			
			if (DEBUG) {
				System.out.println("x=" + (columnIndex + 1) + ", y=" + lineNumber + ", color=" + cubes.get(columnIndex).getFace(face));
			}
		}
	}
}

