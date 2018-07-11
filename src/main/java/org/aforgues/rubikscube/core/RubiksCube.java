package org.aforgues.rubikscube.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.aforgues.rubikscube.ai.RubiksCubeAI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube.class);
	
	private final static int MIN_SIZE = 2;
	
	private int size;
    private List<Cubie> config;

    private RubiksCubeAI ai;

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
			LOGGER.warn("RubiksCube size cannot be less than {} (actual : {}) => forcing size to {}", MIN_SIZE, size, MIN_SIZE);
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
	 * Initialisation de la configuration d'un Rubik's Cube terminé
	 */
	private void initConfig() {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("Initializing Rubik's Cube configuration");
		
		/**
		 * Face avant   : rouge
		 * Face gauche  : bleue
		 * Face droite  : verte
		 * Face basse   : blanche
		 * Face haute   : jaune
		 * Face arrière : orange 
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
		this.resetSolvingPath();
	}
	
	/**
	 * Rotation selon l'axe X de 90° vers l'avant (comme une roulade avant ou un frontflip en snowboard) : tangage
	 */
	private void pitch(int index) {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("Pitching (rotation on X axis) Rubik's Cube on face {}", index);
		
		List<Cubie> cubes = getCubies(index, Axis.X);
		
		for (Cubie cube : cubes) {
			cube.pitch();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe X de 90° vers l'avant (comme une roulade arrière ou un backflip en snowboard) : tangage
	 */
	private void unpitch(int index) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Unpitching (inverse rotation on X axis) Rubik's Cube on face {} through 3 pitches", index);

		pitch(index);
		pitch(index);
		pitch(index);
	}
	
	/**
	 * Rotation selon l'axe Y de 90° vers la droite (comme un toupis dans le sens des aiguilles d'une montre vue de haut) : lacet
	 */
	private void yaw(int index) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Yawing (rotation on Y axis) Rubik's Cube on face {}", index);
		
		List<Cubie> cubes = getCubies(index, Axis.Y);
		
		for (Cubie cube : cubes) {
			cube.yaw();
		}
		
		Collections.sort(config);
	}
	
	/**
	 * Rotation selon l'axe Y de 90° vers la gauche (comme un toupis de gaucher dans le sens inverse des aiguilles d'une montre vue de haut) : lacet
	 */
	private void unyaw(int index) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Unyawing (inverse rotation on Y axis) Rubik's Cube on face {} through 3 yaws", index);
		
		yaw(index);
		yaw(index);
		yaw(index);
	}

	/**
	 * Rotation selon l'axe Z de 90° vers la droite (comme pour fermer une porte à clef dans le sens des aiguilles d'une montre vue de face)  : roulis
	 */
	private void roll(int index) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Rolling (rotation on Z axis) Rubik's Cube on face {}", index);
		
		List<Cubie> cubes = getCubies(index, Axis.Z);
		
		for (Cubie cube : cubes) {
			cube.roll();
		}
		
		Collections.sort(config);
	}

	/**
	 * Rotation selon l'axe Z de 90° vers la gauche (comme pour ouvrir une porte à clef dans le sens inverse des aiguilles d'une montre vue de face)  : roulis
	 */
	private void unroll(int index) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Unrolling (inverse rotation on Z axis) Rubik's Cube on face {} through 3 rolls", index);
		
		roll(index);
		roll(index);
		roll(index);
	}

	public List<Cubie> getAllCubies() {
	    return this.config;
    }

	public List<Cubie> getCubies(int index, Axis axis) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Retrieving cubes of axis {} on face n°{}", axis.name(), index);
		
		if (index < 1 || index > getSize()) {
			LOGGER.error("### ERROR : Cannot rotate RubiksCube face n°{} on {} axis => allowed indexes are in [1-{}] range", index, axis.name(), getSize());
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
	
	public void shuffle(int nbMove) {
		List<DefinedMove> moves = generateShuffleMoves(nbMove);
		
		// On va effectuer <code>moves</code> mouvements aléatoires à la suite
		for (int i = 1; i <= moves.size(); i++) {
            if (LOGGER.isTraceEnabled())
				LOGGER.trace("Shuffle move number {}", i);
			
			DefinedMove definedMove = moves.get(i - 1);
			
			// On récupère un des 9 mouvements possibles aléatoirement
			internalMove(definedMove);
		}

		this.resetSolvingPath();
	}

    public boolean moveToNextPosition() {
		DefinedMove nextMove = getNextMove();
		internalMove(nextMove);
		return (nextMove != null);
	}

	public void move(DefinedMove move) {
		internalMove(move);
	}

	public void manualMove(DefinedMove move) {
	    internalMove(move);

        // Comme on a tourné manuellement le RubiksCube, on réinitialise l'éventuelle solution calculée par l'AI
        this.resetSolvingPath();
    }

	private void internalMove(DefinedMove definedMove) {
		if (definedMove != null && definedMove.getMove() != null) {
			// On définit la liste d'index à déplacer
			List<Integer> indexes = new ArrayList<Integer>();
			if (definedMove.isFaceMove()) {
				for (int i = 1; i <= getSize(); i++) {
					indexes.add(Integer.valueOf(i));
				}
                if (LOGGER.isTraceEnabled())
					LOGGER.trace("Moving all face with {}", definedMove.getMove().name());
			}
			else {
				indexes.add(definedMove.getFaceIndex());
                if (LOGGER.isTraceEnabled())
					LOGGER.trace("Moving {}", definedMove);
			}
			
			// Ensuite on boucle sur ces indexes pour déplacer la ou les faces du cube souhaitées
			for (int index : indexes) {
				switch (definedMove.getMove()) {
					// On tourne le cube autour de l'axe X 
					case PITCH:
						pitch(index);
						break;
					case DOUBLE_PITCH:
						pitch(index);
						pitch(index);
						break;
					// On tourne le cube autour de l'axe X 
					case UNPITCH:
						unpitch(index);
						break;
					// On tourne le cube autour de l'axe Y 
					case YAW:
						yaw(index);
						break;
					case DOUBLE_YAW:
						yaw(index);
						yaw(index);
						break;
					// On tourne le cube autour de l'axe Y 
					case UNYAW:
						unyaw(index);
						break;
					// On tourne le cube autour de l'axe Z globalement
					case ROLL:
						roll(index);
						break;
					case DOUBLE_ROLL:
						roll(index);
						roll(index);
						break;
					// On tourne le cube autour de l'axe Z globalement
					case UNROLL:
						unroll(index);
						break;	
				}
			}
		}
	}
	
	public void move(List<DefinedMove> moves) {
		if (moves != null) {
			for (DefinedMove move : moves) {
				this.internalMove(move);
			}
		}
	}
	
	/**
	 * Méthode permettant de mélanger le Rubik's Cube
	 */
	private List<DefinedMove> generateShuffleMoves(int nbMove) {
        if (LOGGER.isTraceEnabled())
			LOGGER.trace("Starting shuffling Rubik's Cube in {} moves ...", nbMove);
		
		List<DefinedMove> moves = new ArrayList<DefinedMove>(nbMove);
		
		Random moveRandomGenerator = new Random();
		Random faceRandomGenerator = new Random();
		
		// On va effectuer <code>moves</code> mouvements aléatoires à la suite
		for (int i = 1; i <= nbMove; i++) {
            if (LOGGER.isTraceEnabled())
				LOGGER.trace("Shuffle move number {}", i);
			
			// On récupère un des 9 mouvements possibles aléatoirement
			int move = moveRandomGenerator.nextInt(Move.values().length - 1);
			moves.add(new DefinedMove(DefinedMove.getMove(move), faceRandomGenerator.nextInt(getSize()) + 1));
		}
		return moves;
	}

	/**
	 *  Using artificial intelligence to solve the RubiksCube
	 */
	public void solve(boolean isSimulation) {
		this.ai = new RubiksCubeAI(this, isSimulation);
		ai.computeArtificialIntelligence();
	}

	private DefinedMove getNextMove() {
		if (this.ai != null) {
		    DefinedMove nextMove = this.ai.getNextMove();
		    if (nextMove != null)
		        return nextMove;
		    else {
		        this.resetSolvingPath();
		        return null;
            }

		}
		else {
            if (LOGGER.isTraceEnabled())
				LOGGER.trace("No path already computed by the RubiksCube AI => solving it !");
			this.solve(false);
			return getNextMove();
		}
	}

	public void resetSolvingPath() {
		if (this.ai != null) {
			this.ai.reset();
			this.ai = null;
		}
	}

	/**
	 * Indique si le RubiksCube est résolu : toutes les faces sont correctement terminées
	 * @return
	 */
	public boolean isSolved() {
		// On regarde face après face, si tous les cubes ont bien la même couleur
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
        if (LOGGER.isDebugEnabled())
			LOGGER.debug("Checking RubiksCube solved : {} Face matched !", face);
		
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
		return this.getCubies(1, Axis.Z);
	}

	public List<Cubie> getTopFaceCubes() {
		return this.getCubies(this.getSize(), Axis.Y);
	}

	public List<Cubie> getLeftFaceCubes() {
		return this.getCubies(1, Axis.X);
	}

	public List<Cubie> getFrontFaceCubes() {
		return this.getCubies(this.getSize(), Axis.Z);
	}

	public List<Cubie> getRightFaceCubes() {
		return this.getCubies(this.getSize(), Axis.X);
	}

	public List<Cubie> getBottomFaceCubes() {
		return this.getCubies(1, Axis.Y);
	}

	public Cubie getCubie(int x, int y, int z) {
		if (x < 1 || x > getSize()
		 || y < 1 || y > getSize()
		 || z < 1 || z > getSize()) {
			LOGGER.error("### ERROR : Cannot get Cubie on coord x={}, y={}, z={} => allowed coords are in [1-{}] range", x, y, z, getSize());
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
