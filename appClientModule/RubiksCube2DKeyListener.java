import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;


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
		List<Defined3DMove> moves = rc.generateShuffleMoves(nbMove);
		
		// On va effectuer <code>moves</code> mouvements aléatoires à la suite
		for (int i = 1; i <= moves.size(); i++) {
			if (RubiksCube2D.DEBUG)
				System.out.println("Shuffle move number " + i);
			
			Defined3DMove definedMove = moves.get(i - 1);
			
			// On récupère un des 9 mouvements possibles aléatoirement
			switch (definedMove.getMove()) {
				case PITCH:
					rc.pitch(definedMove.getFaceIndex());
					break;
				case DOUBLE_PITCH: // a virer ??
					rc.pitch(definedMove.getFaceIndex());
					repaintSmoothly(applet);
					rc.pitch(definedMove.getFaceIndex());
					break;
				case UNPITCH:
					rc.unpitch(definedMove.getFaceIndex());
					break;
				case YAW:
					rc.yaw(definedMove.getFaceIndex());
					break;
				case DOUBLE_YAW:// a virer ??
					rc.yaw(definedMove.getFaceIndex());
					repaintSmoothly(applet);
					rc.yaw(definedMove.getFaceIndex());
					break;
				case UNYAW:
					rc.unyaw(definedMove.getFaceIndex());
					break;
				case ROLL:
					rc.roll(definedMove.getFaceIndex());
					break;
				case DOUBLE_ROLL:// a virer ??
					rc.roll(definedMove.getFaceIndex());
					repaintSmoothly(applet);
					rc.roll(definedMove.getFaceIndex());
					break;
				case UNROLL:
					rc.unroll(definedMove.getFaceIndex());
					break;	
			}
			repaintSmoothly(applet);
		}
		
	}

	private void repaintSmoothly(RubiksCube2D applet) {
		try {
			// FIXME : marche pô !!
			Thread.sleep(200);
			applet.getContentPane().setSize(applet.getWidth(), applet.getHeight());
			applet.invalidate();
			applet.validate();
			applet.repaint();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}