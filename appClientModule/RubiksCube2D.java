import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;


public class RubiksCube2D extends JApplet {


	private static final long serialVersionUID = 1L;
	static final boolean DEBUG = true;
	
	private RubiksCube rubiksCube;
	
	public void initRubiksCube(int size) {
		this.rubiksCube = new RubiksCube(size);
	}

	public RubiksCube getRubiksCube() {
		return this.rubiksCube;
	}
	
	public void init() {
		setBackground(Color.NONE.getAwtColor());
		
		// FIXME : ca ne semble pas avoir d'effet
		setForeground(Color.NONE.getAwtColor());
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
				
				// TODO : recentrer le rubik's cube en cas de redimentionnement de la fenêtre
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