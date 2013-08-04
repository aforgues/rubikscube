import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe de configuration du Rubik's Cube
 * 
 *        ^ 
 *     y  |      
 *        |    
 *        |  
 *        |
 *        ------------> x
 *      /
 *    /
 *  /
 * v 
 *  z  
 *       
 * @author Arnaud Forgues
 */

public class RubiksCube {
	
	private static final boolean VERBAL = false;
	
	private final static int MIN_SIZE = 2;
	
	private int size;
    private List<Cube> config;

    private Face identifiedPressedFace;
	private Point pointOnPressedFaceIdentified;
	private Face identifiedReleasedFace;
	private Point pointOnReleasedFaceIdentified;
	
	public int getSize() {
		return size;
	}

	
	public RubiksCube(int size) {
		if (size < MIN_SIZE) {
			System.out.println("RubiksCube size cannot be less than " + MIN_SIZE + " (actual : " + size + ") => forcing size to " + MIN_SIZE);
			size = MIN_SIZE;
		}
		this.size = size;
		initConfig();
	}
	    
	/**
	 * Initialisation de la configuration d'un Rubik's Cube terminé
	 */
	private void initConfig() {
		if (VERBAL)
			System.out.println("Initializing Rubik's Cube configuration");
		
		/**
		 * Face avant   : rouge
		 * Face gauche  : bleue
		 * Face droite  : verte
		 * Face basse   : blanche
		 * Face haute   : jaune
		 * Face arrière : orange 
		 */
		
		config = new ArrayList<Cube>();
		
		for (int z = 1; z <= getSize(); z++) {
			// Construction des N niveaux centraux de la face : axe y
			for (int y = 1; y <= getSize(); y++) {
				Color frontColor  = z == getSize() ? Color.RED    : Color.NONE;				
				Color bottomColor = y == 1         ? Color.WHITE  : Color.NONE;	
				Color topColor    = y == getSize() ? Color.YELLOW : Color.NONE;
				Color backColor   = z == 1         ? Color.ORANGE : Color.NONE;
				
				int x = 1;
				
				ThreeDimCoordinate coord = new ThreeDimCoordinate(x, y, z);
				Cube cube = new Cube(getSize(), coord, frontColor, Color.BLUE, Color.NONE, bottomColor, topColor, backColor);
				config.add(cube);
				
				for (x = 2; x < getSize(); x++) {
					ThreeDimCoordinate centerCoord = new ThreeDimCoordinate(x, y, z);
					Cube centerCube = new Cube(getSize(), centerCoord, frontColor, Color.NONE, Color.NONE, bottomColor, topColor, backColor);
					config.add(centerCube);
				}
				
				ThreeDimCoordinate lastCoord = new ThreeDimCoordinate(x, y, z);
				Cube lastCube = new Cube(getSize(), lastCoord, frontColor, Color.NONE, Color.GREEN, bottomColor, topColor, backColor);
				config.add(lastCube);
			}
		}		
		
	}
	
	public void reset() {
		this.initConfig();
	}
	
	/**
	 * Rotation selon l'axe X de 90° vers l'avant (comme une roulade avant ou un frontflip en snowboard) : tangage
	 */
	public void pitch(int index) {
		if (VERBAL)
			System.out.println("Pitching (rotation on X axis) Rubik's Cube on face " + index);
		
		List<Cube> cubes = getCubes(index, Axis.X);
		
		for (Cube cube : cubes) {
			cube.pitch();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe X de 90° vers l'avant (comme une roulade arrière ou un backflip en snowboard) : tangage
	 */
	public void unpitch(int index) {
		if (VERBAL)
			System.out.println("Unpitching (inverse rotation on X axis) Rubik's Cube on face " + index + " through 3 pitches");
		
		pitch(index);
		pitch(index);
		pitch(index);
	}
	
	/**
	 * Rotation selon l'axe Y de 90° vers la droite (comme un toupis dans le sens des aiguilles d'une montre vue de haut) : lacet
	 */
	public void yaw(int index) {
		if (VERBAL)
			System.out.println("Yawing (rotation on Y axis) Rubik's Cube on face " + index);
		
		List<Cube> cubes = getCubes(index, Axis.Y);
		
		for (Cube cube : cubes) {
			cube.yaw();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe Y de 90° vers la gauche (comme un toupis de gaucher dans le sens inverse des aiguilles d'une montre vue de haut) : lacet
	 */
	public void unyaw(int index) {
		if (VERBAL)
			System.out.println("Unyawing (inverse rotation on Y axis) Rubik's Cube on face " + index + " through 3 yaws");
		
		yaw(index);
		yaw(index);
		yaw(index);
	}

	/**
	 * Rotation selon l'axe Z de 90° vers la droite (comme pour fermer une porte à clef dans le sens des aiguilles d'une montre vue de face)  : roulis
	 */
	public void roll(int index) {
		if (VERBAL)
			System.out.println("Rolling (rotation on Z axis) Rubik's Cube on face " + index);
		
		List<Cube> cubes = getCubes(index, Axis.Z);
		
		for (Cube cube : cubes) {
			cube.roll();
		}
		
		Collections.sort(config);
	}

	/**
	 * Rotation selon l'axe Z de 90° vers la gauche (comme pour ouvrir une porte à clef dans le sens inverse des aiguilles d'une montre vue de face)  : roulis
	 */
	public void unroll(int index) {
		if (VERBAL)
			System.out.println("Unrolling (inverse rotation on Z axis) Rubik's Cube on face " + index + " through 3 rolls");
		
		roll(index);
		roll(index);
		roll(index);
	}
	
	public List<Cube> getCubes(int index, Axis axis) {
		if (VERBAL)
			System.out.println("Retrieving cubes of axis " + axis.name() + " on face " + index);
		
		if (index < 1 || index > getSize()) {
			System.out.println("### ERROR : Cannot rotate RubiksCube face n°" + index + " on " + axis.name() + " axis => allowed indexes are in [1-" + getSize() + "] range");
			return Collections.emptyList();
		}
		
		List<Cube> cubes = new ArrayList<Cube>();
		for (Cube cube : config) {
			ThreeDimCoordinate coord = cube.getCoordinates();
			
			int i;
			switch (axis) {
				case X:
					i = coord.getX();
					break;
				case Y:
					i = coord.getY();
					break;
				case Z:
					i = coord.getZ();
					break;	
				default:
					continue;
			}
			
			if (i == index) {
				cubes.add(cube);
			}
		}
		return cubes;
	}
	
	// Utiliser uniquement par le main pour le test Ascii
	public void shuffle(int nbMove) {
		List<Defined3DMove> moves = generateShuffleMoves(nbMove);
		
		// On va effectuer <code>moves</code> mouvements aléatoires à la suite
		for (int i = 1; i <= moves.size(); i++) {
			if (VERBAL)
				System.out.println("Shuffle move number " + i);
			
			Defined3DMove definedMove = moves.get(i - 1);
			
			// On récupère un des 9 mouvements possibles aléatoirement
			switch (definedMove.getMove()) {
				case PITCH:
					pitch(definedMove.getFaceIndex());
					break;
				case DOUBLE_PITCH: // a virer ??
					pitch(definedMove.getFaceIndex());
					pitch(definedMove.getFaceIndex());
					break;
				case UNPITCH:
					unpitch(definedMove.getFaceIndex());
					break;
				case YAW:
					yaw(definedMove.getFaceIndex());
					break;
				case DOUBLE_YAW:// a virer ??
					yaw(definedMove.getFaceIndex());
					yaw(definedMove.getFaceIndex());
					break;
				case UNYAW:
					unyaw(definedMove.getFaceIndex());
					break;
				case ROLL:
					roll(definedMove.getFaceIndex());
					break;
				case DOUBLE_ROLL:// a virer ??
					roll(definedMove.getFaceIndex());
					roll(definedMove.getFaceIndex());
					break;
				case UNROLL:
					unroll(definedMove.getFaceIndex());
					break;	
			}
		}
	}
	
	/**
	 * Méthode permettant de mélanger le Rubik's Cube
	 */
	public List<Defined3DMove> generateShuffleMoves(int nbMove) {
		if (VERBAL)
			System.out.println("Starting shuffling Rubik's Cube in " + nbMove + " moves ...");
		
		List<Defined3DMove> moves = new ArrayList<Defined3DMove>(nbMove);
		
		Random moveRandomGenerator = new Random();
		Random faceRandomGenerator = new Random();
		
		// On va effectuer <code>moves</code> mouvements aléatoires à la suite
		for (int i = 1; i <= nbMove; i++) {
			if (VERBAL)
				System.out.println("Shuffle move number " + i);
			
			// On récupère un des 9 mouvements possibles aléatoirement
			int move = moveRandomGenerator.nextInt(Move.values().length - 1);
			moves.add(new Defined3DMove(Defined3DMove.getMove(move), faceRandomGenerator.nextInt(getSize()) + 1));
		}
		return moves;
	}
	
	public void setPressedFaceIdentified(Face identifiedFace) {
		this.identifiedPressedFace = identifiedFace;	
	}
	
	public Face getPressedFaceIdentified() {
		return this.identifiedPressedFace;
	}

	public boolean hasPressedFaceIdentified() {
		return this.getPressedFaceIdentified() != null;
	}

	public void setPointOnPressedFaceIdentified(Point pointCoordOnFace) {
		this.pointOnPressedFaceIdentified = pointCoordOnFace;	
	}
	
	public Point getPointOnPressedFaceIdentified() {
		return this.pointOnPressedFaceIdentified;
	}

	public boolean hasPointOnPressedFaceIdentified() {
		return getPointOnPressedFaceIdentified() != null;
	}
	
	public void setReleasedFaceIdentified(Face identifyFace) {
		this.identifiedReleasedFace = identifyFace;
	}

	public Face getReleasedFaceIdentified() {
		return this.identifiedReleasedFace;
	}
	
	public void setPointOnReleasedFaceIdentified(Point identifyPointCoordOnFace) {
		this.pointOnReleasedFaceIdentified = identifyPointCoordOnFace;
	}

	public Point getPointOnReleasedFaceIdentified() {
		return this.pointOnReleasedFaceIdentified;
	}

	public boolean hasReleasedFaceIdentified() {
		return getReleasedFaceIdentified() != null;
	}
	
	
	public boolean hasPointOnReleasedFaceIdentified() {
		return getPointOnReleasedFaceIdentified() != null;
	}
	
	// Dump
	//-----
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("RubiksCube [size=" + this.size + ", config=\n");
		for (Cube cube : config) {
			s.append(cube).append("\n");
		}
		s.append("]");
		
		return s.toString();
	}

}
