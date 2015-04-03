package org.aforgues.rubikscube.presentation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Facelet;
import org.aforgues.rubikscube.core.RubiksCube;


public class RubiksCube2D extends JApplet {


	private static final long serialVersionUID = 1L;
	public static final boolean DEBUG = false;
	
	private RubiksCube rubiksCube;
	
	// Champs spéciaux pour le calcul de l'IA
	private List<DefinedMove> moves;
	private int currentMoveIndex;
	
	public void initRubiksCube(int size) {
		this.rubiksCube = new RubiksCube(size);
	}

	public RubiksCube getRubiksCube() {return this.rubiksCube;}
	public List<DefinedMove> getMoves() {return this.moves;}
	public DefinedMove getNextMove() {
		if (this.moves == null || this.moves.isEmpty())
			return null;
		
		if (this.currentMoveIndex == this.moves.size())
			return null;
		
		return this.moves.get(currentMoveIndex++);
	}
	
	public void setDefinedMoves(List<DefinedMove> moves) {
		this.moves = moves;
		this.currentMoveIndex = 0;
	}
	
	// TODO : utiliser cette méthode lors de n'importe quel mouvement autre que l'appui sur la touche 'N'
	public void clearDefinedMoves() {
		this.moves = null;
		this.currentMoveIndex = 0;
	}
	
	public void init() {
		setBackground(Facelet.NONE.getAwtColor());
		
		// FIXME : ca ne semble pas avoir d'effet
		setForeground(Facelet.NONE.getAwtColor());
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		RubiksCube2DFormat graphicFormat = new RubiksCube2DFormat(this.getRubiksCube(), g2);
		graphicFormat.show();
	}
	
	public static void main(String... args) {
		// Get params for Rubiks Cube size
		int size = 3;
		if (args.length != 0) {
			try {
				size = Integer.valueOf(args[0]);
			}
			catch (NumberFormatException e) {
				System.out.println("Bad parameter format : you must provide a numeric size for the Rubik's Cube");
			}
		}
		
		JFrame f = new JFrame("Rubik's Cube 2D");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Exiting Rubik's Cube program...");
				System.exit(0);
			}
		});
		
		f.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				if (DEBUG)
					System.out.println("# Component hidden : ### " + arg0.getComponent());
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				if (DEBUG)
					System.out.println("# Component moved : ### " + arg0.getComponent());
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				if (DEBUG)
					System.out.println("# Component resized : ### " + arg0.getComponent());
				
				// TODO : recentrer le rubik's cube en cas de redimentionnement de la fenètre
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				if (DEBUG)
					System.out.println("# Component shown : ### " + arg0.getComponent());
			}
		
		});
		
		RubiksCube2D applet = new RubiksCube2D();
		applet.initRubiksCube(size);
		
		applet.addKeyListener(new RubiksCube2DKeyListener());
		applet.addMouseListener(new RubiksCube2DMouseListener());
		// TODO: ajouter un MouseMotionListener pour afficher les cubes survoler lors d'un clic gauche
		
		f.getContentPane().add("Center", applet);
		applet.init();
		
		
		f.pack();
		
		int width  = RubiksCube2DFormat.CUBE_SIZE * (1 + 3 * size + 1) + 2 * RubiksCube2DFormat.CUBE_MARGIN;
		int height = RubiksCube2DFormat.CUBE_SIZE * (1 + 4 * size + 1) + 3 * RubiksCube2DFormat.CUBE_MARGIN;
		f.setMinimumSize(new Dimension(width, height));
		f.setSize(new Dimension(width, height));
		
		// FIXME : je n'arrive pas à initialiser la taille de la fenetre !
		f.setMaximumSize(new Dimension(width, height));
		
		f.setVisible(true);
	}
	

}