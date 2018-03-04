package org.aforgues.rubikscube.presentation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import org.aforgues.rubikscube.ai.RubiksCubeAI;
import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.RubiksCube;


final class RubiksCube2DKeyListener implements KeyListener {
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("# Key pressed : " + arg0.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("# Key released : " + arg0.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("### Key Typed : #" + arg0.getKeyChar());
		
		// FIXME: this does not work..
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
			return;
		}	

		RubiksCube2D applet = ((RubiksCube2D) arg0.getSource());
			
		switch (arg0.getKeyChar()) {
			case 'a':
				applet.getRubiksCube().pitch(1);
				applet.repaint();
				break;
			case 'z':
				applet.getRubiksCube().pitch(2);
				applet.repaint();
				break;
			case 'e':
				applet.getRubiksCube().pitch(3);
				applet.repaint();
				break;
			case 'r':
				applet.getRubiksCube().unpitch(1);
				applet.repaint();
				break;
			case 't':
				RubiksCubeAI ai = new RubiksCubeAI(applet.getRubiksCube(), false);
				List<DefinedMove> moves = ai.computeArtificialIntelligence();
				applet.setDefinedMoves(moves);
				break;
			case 'n':
				DefinedMove move = applet.getNextMove();
				if (move != null) {
					System.out.println("Move : " + move);
					applet.getRubiksCube().move(move);
					applet.repaint();
				}
				break;
			case 'y':
				RubiksCubeAI ai2 = new RubiksCubeAI(applet.getRubiksCube(), true);
				ai2.computeArtificialIntelligence();
				applet.repaint();
				break;	
			case 'q':
				applet.getRubiksCube().yaw(1);
				applet.repaint();
				break;
			case 's':
				applet.getRubiksCube().yaw(2);
				applet.repaint();
				break;
			case 'd':
				applet.getRubiksCube().yaw(3);
				applet.repaint();
				break;
			case 'f':
				applet.getRubiksCube().unyaw(1);
				applet.repaint();
				break;
			case 'w':
				applet.getRubiksCube().roll(1);
				applet.repaint();
				break;
			case 'x':
				applet.getRubiksCube().roll(2);
				applet.repaint();
				break;
			case 'c':
				applet.getRubiksCube().roll(3);
				applet.repaint();
				break;
			case 'v':
				applet.getRubiksCube().unroll(1);
				applet.repaint();
				break;
			case 'm':
				smoothShuffle(applet, 20);
				break;
			case 'i':
				applet.getRubiksCube().reset();
				applet.repaint();
				break;
			case '2':
				applet.initRubiksCube(2);
				repaintSmoothly(applet);
				break;
			case '3':
				applet.initRubiksCube(3);
				repaintSmoothly(applet);
				break;
			case '4':
				applet.initRubiksCube(4);
				repaintSmoothly(applet);
				break;
			case '5':
				applet.initRubiksCube(5);
				repaintSmoothly(applet);
				break;
			case '6':
				applet.initRubiksCube(6);
				repaintSmoothly(applet);
				break;
			case '7':
				applet.initRubiksCube(7);
				repaintSmoothly(applet);
				break;
			
			default:
				if (RubiksCube2D.DEBUG)
					System.out.println("### Key typed : no action defined on this key");
		}

		
	}

	/**
	 * Méthode permettant de mélanger le Rubik's Cube
	 */
	private void smoothShuffle(RubiksCube2D applet, int nbMove) {
		RubiksCube rc = applet.getRubiksCube();
		rc.shuffle(nbMove);
		repaintSmoothly(applet);
	}

	private void repaintSmoothly(RubiksCube2D applet) {
//		try {
//			// FIXME : marche pô !!
//			Thread.sleep(200);
//			applet.getContentPane().setSize(applet.getWidth(), applet.getHeight());
//			applet.invalidate();
//			applet.validate();
			applet.repaint();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}