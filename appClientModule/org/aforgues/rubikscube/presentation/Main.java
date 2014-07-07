package org.aforgues.rubikscube.presentation;
import org.aforgues.rubikscube.core.RubiksCube;


public class Main {
	public static void main(String[] args) {
		new Main();
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		RubiksCube r = new RubiksCube(3);
		RubiksCubeAsciiFormat asciiFormat = new RubiksCubeAsciiFormat(r);
		//RubiksCube2DFormat graphicFormat = new RubiksCube2DFormat(r);
		asciiFormat.show();
		//graphicFormat.show();
		
		r.shuffle(20);
		
		asciiFormat.show();
		//graphicFormat.show();
		
		/*System.out.println("Rotating face 1 of Rubik's Cube one time on Z axis");
		r.roll(1);
		RubiksCubeAsciiFomat.showAsciiFormat(r);
		
		System.out.println("Rotating face 1 of Rubik's Cube one time on X axis");
		r.pitch(1);
		RubiksCubeAsciiFomat.showAsciiFormat(r);

		System.out.println("Rotating face 2 of Rubik's Cube one time on Y axis");
		r.yaw(2);
		RubiksCubeAsciiFomat.showAsciiFormat(r);
		
		System.out.println("Rotating face 3 of Rubik's Cube one time on Z axis");
		r.roll(3);
		RubiksCubeAsciiFomat.showAsciiFormat(r);*/
	}

}