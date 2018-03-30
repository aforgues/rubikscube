package org.aforgues.rubikscube.presentation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import org.aforgues.rubikscube.ai.RubiksCubeAI;
import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Move;
import org.aforgues.rubikscube.core.RubiksCube;

import javax.swing.JFrame;

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

		JFrame f = (JFrame) arg0.getSource();
		RubiksCube2D applet = (RubiksCube2D) f.getContentPane().getComponent(0);

		/*
		 * L'appui sur la touche 't' permet de générer une solution à partir d'une position donnée du Rubik's Cube
		 * Ensuite il suffit d'appuyer sur la touche 'n' pour appliquer les mouvements de la solution un par un
		 *
		 */

		switch (arg0.getKeyChar()) {
			case 't':
				applet.getRubiksCube().solve(false);
				break;
			case 'n':
				applet.getRubiksCube().moveToNextPosition();
				applet.repaint();
				break;
			case 'y':
				applet.getRubiksCube().solve(true);
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