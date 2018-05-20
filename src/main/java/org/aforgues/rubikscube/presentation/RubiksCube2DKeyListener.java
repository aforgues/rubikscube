package org.aforgues.rubikscube.presentation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.aforgues.rubikscube.core.RubiksCube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;

final class RubiksCube2DKeyListener implements KeyListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube2DKeyListener.class);

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("# Key pressed : {}", arg0.getKeyChar());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("# Key released : {}", arg0.getKeyChar());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("### Key Typed : #{}", arg0.getKeyChar());
		
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
                applet.repaint();
				break;
			case '3':
				applet.initRubiksCube(3);
                applet.repaint();
				break;
			case '4':
				applet.initRubiksCube(4);
                applet.repaint();
				break;
			case '5':
				applet.initRubiksCube(5);
                applet.repaint();
				break;
			case '6':
				applet.initRubiksCube(6);
                applet.repaint();
				break;
			case '7':
				applet.initRubiksCube(7);
                applet.repaint();
				break;
			
			default:
				LOGGER.warn("### Key typed : no action defined on this key : {}", arg0.getKeyChar());
		}

		
	}

	/**
	 * Méthode permettant de mélanger le Rubik's Cube
	 */
	private void smoothShuffle(RubiksCube2D applet, int nbMove) {
		RubiksCube rc = applet.getRubiksCube();
		rc.shuffle(nbMove);
		applet.repaint();
	}
}