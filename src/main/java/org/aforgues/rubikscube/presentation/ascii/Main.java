package org.aforgues.rubikscube.presentation.ascii;
import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Move;
import org.aforgues.rubikscube.core.RubiksCube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		new Main();
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		LOGGER.info("Init 3x3 Rubik's Cube");
		RubiksCube r = new RubiksCube(3);
		RubiksCubeAsciiFormat asciiFormat = new RubiksCubeAsciiFormat(r);
		asciiFormat.show();

		LOGGER.info("Shuffling Rubik's Cube 20 times");
		r.shuffle(20);
		asciiFormat.show();
		
		LOGGER.info("Rotating face 1 of Rubik's Cube one time on Z axis");
		r.move(new DefinedMove(Move.ROLL, 1));
		asciiFormat.show();

		LOGGER.info("Rotating face 1 of Rubik's Cube one time on X axis");
		r.move(new DefinedMove(Move.PITCH, 1));
		asciiFormat.show();

		LOGGER.info("Rotating face 2 of Rubik's Cube one time on Y axis");
		r.move(new DefinedMove(Move.YAW,2));
		asciiFormat.show();
		
		LOGGER.info("Rotating face 3 of Rubik's Cube one time on Z axis");
		r.move(new DefinedMove(Move.ROLL,3));
		asciiFormat.show();
	}

}