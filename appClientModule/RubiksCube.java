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

public class RubiksCube implements Cloneable {
	
	private static final boolean VERBAL = false;
	
	private final static int MIN_SIZE = 2;
	
	private int size;
    private List<Cubie> config;

    private Face identifiedPressedFace;
	private Point pointOnPressedFaceIdentified;
	private Face identifiedReleasedFace;
	private Point pointOnReleasedFaceIdentified;

	private boolean isFaceMove;
	
	public int getSize() {
		return size;
	}
	
	// Only for clone method
	private RubiksCube(int size, List<Cubie> config) {
		this.size = size;
		this.config = new ArrayList<Cubie>(config);
	}
	
	public RubiksCube(int size) {
		if (size < MIN_SIZE) {
			System.out.println("RubiksCube size cannot be less than " + MIN_SIZE + " (actual : " + size + ") => forcing size to " + MIN_SIZE);
			size = MIN_SIZE;
		}
		this.size = size;
		initConfig();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		List<Cubie> configClone = new ArrayList<Cubie>();
		for (Cubie cubie : this.config) {
			configClone.add((Cubie) cubie.clone());
		}
		return new RubiksCube(this.size, configClone);
	}
	    
	/**
	 * Initialisation de la configuration d'un Rubik's Cube termin�
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
		 * Face arri�re : orange 
		 */
		
		config = new ArrayList<Cubie>();
		
		for (int z = 1; z <= getSize(); z++) {
			// Construction des N niveaux centraux de la face : axe y
			for (int y = 1; y <= getSize(); y++) {
				Facelet frontColor  = z == getSize() ? Facelet.RED    : Facelet.NONE;				
				Facelet bottomColor = y == 1         ? Facelet.WHITE  : Facelet.NONE;	
				Facelet topColor    = y == getSize() ? Facelet.YELLOW : Facelet.NONE;
				Facelet backColor   = z == 1         ? Facelet.ORANGE : Facelet.NONE;
				
				int x = 1;
				
				ThreeDimCoordinate coord = new ThreeDimCoordinate(x, y, z);
				Cubie cube = new Cubie(getSize(), coord, frontColor, Facelet.BLUE, Facelet.NONE, bottomColor, topColor, backColor);
				config.add(cube);
				
				for (x = 2; x < getSize(); x++) {
					ThreeDimCoordinate centerCoord = new ThreeDimCoordinate(x, y, z);
					Cubie centerCube = new Cubie(getSize(), centerCoord, frontColor, Facelet.NONE, Facelet.NONE, bottomColor, topColor, backColor);
					config.add(centerCube);
				}
				
				ThreeDimCoordinate lastCoord = new ThreeDimCoordinate(x, y, z);
				Cubie lastCube = new Cubie(getSize(), lastCoord, frontColor, Facelet.NONE, Facelet.GREEN, bottomColor, topColor, backColor);
				config.add(lastCube);
			}
		}		
		
	}
	
	public void reset() {
		this.initConfig();
	}
	
	/**
	 * Rotation selon l'axe X de 90� vers l'avant (comme une roulade avant ou un frontflip en snowboard) : tangage
	 */
	public void pitch(int index) {
		if (VERBAL)
			System.out.println("Pitching (rotation on X axis) Rubik's Cube on face " + index);
		
		List<Cubie> cubes = getCubes(index, Axis.X);
		
		for (Cubie cube : cubes) {
			cube.pitch();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe X de 90� vers l'avant (comme une roulade arri�re ou un backflip en snowboard) : tangage
	 */
	public void unpitch(int index) {
		if (VERBAL)
			System.out.println("Unpitching (inverse rotation on X axis) Rubik's Cube on face " + index + " through 3 pitches");
		
		pitch(index);
		pitch(index);
		pitch(index);
	}
	
	/**
	 * Rotation selon l'axe Y de 90� vers la droite (comme un toupis dans le sens des aiguilles d'une montre vue de haut) : lacet
	 */
	public void yaw(int index) {
		if (VERBAL)
			System.out.println("Yawing (rotation on Y axis) Rubik's Cube on face " + index);
		
		List<Cubie> cubes = getCubes(index, Axis.Y);
		
		for (Cubie cube : cubes) {
			cube.yaw();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe Y de 90� vers la gauche (comme un toupis de gaucher dans le sens inverse des aiguilles d'une montre vue de haut) : lacet
	 */
	public void unyaw(int index) {
		if (VERBAL)
			System.out.println("Unyawing (inverse rotation on Y axis) Rubik's Cube on face " + index + " through 3 yaws");
		
		yaw(index);
		yaw(index);
		yaw(index);
	}

	/**
	 * Rotation selon l'axe Z de 90� vers la droite (comme pour fermer une porte � clef dans le sens des aiguilles d'une montre vue de face)  : roulis
	 */
	public void roll(int index) {
		if (VERBAL)
			System.out.println("Rolling (rotation on Z axis) Rubik's Cube on face " + index);
		
		List<Cubie> cubes = getCubes(index, Axis.Z);
		
		for (Cubie cube : cubes) {
			cube.roll();
		}
		
		Collections.sort(config);
	}

	/**
	 * Rotation selon l'axe Z de 90� vers la gauche (comme pour ouvrir une porte � clef dans le sens inverse des aiguilles d'une montre vue de face)  : roulis
	 */
	public void unroll(int index) {
		if (VERBAL)
			System.out.println("Unrolling (inverse rotation on Z axis) Rubik's Cube on face " + index + " through 3 rolls");
		
		roll(index);
		roll(index);
		roll(index);
	}
	
	public List<Cubie> getCubes(int index, Axis axis) {
		if (VERBAL)
			System.out.println("Retrieving cubes of axis " + axis.name() + " on face " + index);
		
		if (index < 1 || index > getSize()) {
			System.out.println("### ERROR : Cannot rotate RubiksCube face n�" + index + " on " + axis.name() + " axis => allowed indexes are in [1-" + getSize() + "] range");
			return Collections.emptyList();
		}
		
		List<Cubie> cubes = new ArrayList<Cubie>();
		for (Cubie cube : config) {
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
		
		// On va effectuer <code>moves</code> mouvements al�atoires � la suite
		for (int i = 1; i <= moves.size(); i++) {
			if (VERBAL)
				System.out.println("Shuffle move number " + i);
			
			Defined3DMove definedMove = moves.get(i - 1);
			
			// On r�cup�re un des 9 mouvements possibles al�atoirement
			move(definedMove);
		}
	}


	public void move(Defined3DMove definedMove) {
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
	
	public void move(List<Defined3DMove> moves) {
		if (moves != null) {
			for (Defined3DMove move : moves) {
				this.move(move);
			}
		}
	}
	
	/**
	 * M�thode permettant de m�langer le Rubik's Cube
	 */
	public List<Defined3DMove> generateShuffleMoves(int nbMove) {
		if (VERBAL)
			System.out.println("Starting shuffling Rubik's Cube in " + nbMove + " moves ...");
		
		List<Defined3DMove> moves = new ArrayList<Defined3DMove>(nbMove);
		
		Random moveRandomGenerator = new Random();
		Random faceRandomGenerator = new Random();
		
		// On va effectuer <code>moves</code> mouvements al�atoires � la suite
		for (int i = 1; i <= nbMove; i++) {
			if (VERBAL)
				System.out.println("Shuffle move number " + i);
			
			// On r�cup�re un des 9 mouvements possibles al�atoirement
			int move = moveRandomGenerator.nextInt(Move.values().length - 1);
			moves.add(new Defined3DMove(Defined3DMove.getMove(move), faceRandomGenerator.nextInt(getSize()) + 1));
		}
		return moves;
	}
	
	/*
	 * Methods to handle face or point identification while mouse clicking
	 */
	
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
	
	public void setIsFaceMove(boolean b) {
		this.isFaceMove = b;
	}
	
	public boolean isFaceMove() {
		return this.isFaceMove;
	}
	
	public boolean clearStuffIdentified() {
		// On r�initialise les face et point identifi�e
		if (this.hasPressedFaceIdentified()) {
			this.setPressedFaceIdentified(null);
			
			if (this.hasPointOnPressedFaceIdentified())
				this.setPointOnPressedFaceIdentified(null);
			
			this.setIsFaceMove(false);
			
			return true;
		}
		
		if (this.hasReleasedFaceIdentified()) {
			this.setReleasedFaceIdentified(null);
			
			if (this.hasPointOnReleasedFaceIdentified()) {
				this.setPointOnReleasedFaceIdentified(null);
			}
			
			return true;
		}
		
		return false;
	}

	/**
	 * Indique si le RubiksCube est r�solu : toutes les faces sont correctement termin�es
	 * @return
	 */
	public boolean isSolved() {
		// On regarde face apr�s face, si tous les cubes ont bien la m�me couleur
		for (Face face : Face.values()) {
			if (! this.checkFaceSolved(face))
				return false;
		}
		return true;
	}
	
	private boolean checkFaceSolved(Face face) {
		Facelet faceColor = null;
		for (Cubie cube : getFaceCubies(face)) {
			if (faceColor == null || faceColor.equals(cube.getFace(face)))
				faceColor = cube.getFace(face);
			else
				return false;
		}
		if (RubiksCube2D.DEBUG)
			System.out.println("Checking RubiksCube solved : " + face + " Face matched !");
		
		return true;
	}

	private List<Cubie> getFaceCubies(Face face) {
		List<Cubie> cubes = null;
		switch(face) {
			case FRONT:
				cubes = this.getFrontFaceCubes();
				break;
			case LEFT:
				cubes = this.getLeftFaceCubes();
				break;
			case TOP:
				cubes = this.getTopFaceCubes();
				break;
			case RIGHT:
				cubes = this.getRightFaceCubes();
				break;
			case BOTTOM:
				cubes = this.getBottomFaceCubes();
				break;
			case BACK:
				cubes = this.getBackFaceCubes();
				break;
		}
		
		return cubes;
	}

	public List<Cubie> getBackFaceCubes() {
		return this.getCubes(1, Axis.Z);
	}

	public List<Cubie> getTopFaceCubes() {
		return this.getCubes(this.getSize(), Axis.Y);
	}

	public List<Cubie> getLeftFaceCubes() {
		return this.getCubes(1, Axis.X);
	}

	public List<Cubie> getFrontFaceCubes() {
		return this.getCubes(this.getSize(), Axis.Z);
	}

	public List<Cubie> getRightFaceCubes() {
		return this.getCubes(this.getSize(), Axis.X);
	}

	public List<Cubie> getBottomFaceCubes() {
		return this.getCubes(1, Axis.Y);
	}

	public Cubie getCubie(int x, int y, int z) {
		if (x < 1 || x > getSize()
		 || y < 1 || y > getSize()
		 || z < 1 || z > getSize()) {
			System.out.println("### ERROR : Cannot get Cubie on coord x=" + x + ", y=" + y + ", z=" + z + " => allowed coords are in [1-" + getSize() + "] range");
			return null;
		}
		
		for (Cubie cubie : this.config) {
			ThreeDimCoordinate coord = cubie.getCoordinates();
			
			if (coord.getX() == x && coord.getY() == y && coord.getZ() == z)
				return cubie;
		}
		return null;
	}
	
	// Dump
	//-----
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("RubiksCube [size=" + this.size + ", config=\n");
		for (Cubie cube : config) {
			s.append(cube).append("\n");
		}
		s.append("]");
		
		return s.toString();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result + size;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RubiksCube other = (RubiksCube) obj;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

}
