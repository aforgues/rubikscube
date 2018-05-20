package org.aforgues.rubikscube.presentation;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.aforgues.rubikscube.core.RubiksCube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RubiksCube2D extends JApplet {

	private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube2D.class);

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_ZOOM_OFFSET = 5;

	private RubiksCube rubiksCube;

	private RubiksCube2DFormat graphicFormat;
    public RubiksCube2DFormat getGraphicFormat() {return this.graphicFormat;}

    public RubiksCube2D(int size) throws HeadlessException {
        JFrame f = new JFrame("Rubik's Cube 2D");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                LOGGER.info("Exiting Rubik's Cube program...");
                System.exit(0);
            }
        });

        f.addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent arg0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("# Component hidden : ### {}", arg0.getComponent());
                }
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("# Component moved : ### {}", arg0.getComponent());
                }
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("# Component resized : ### {}", arg0.getComponent());
                }

                // TODO : recentrer le rubik's cube en cas de redimentionnement de la fenètre
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("# Component shown : ### {}", arg0.getComponent());
                }
            }

        });
        f.addKeyListener(new RubiksCube2DKeyListener());

        this.initRubiksCube(size);

        addMouseListener(new RubiksCube2DMouseListener(this));

        // Ajout d'un MouseMotionListener pour gérer le scroll avec le bouton du milieu et/ou afficher les cubes survolés lors d'un clic gauche
        addMouseWheelListener(new RubiksCube2DMouseWheelListener());

        f.getContentPane().add("Center", this);
        this.init();


        f.pack();

        Graphics2D g2 = (Graphics2D) this.getGraphics();;
        graphicFormat = new RubiksCube2DFormat(this.getRubiksCube(), g2);

        int width  = graphicFormat.getCubeSize() * (1 + 3 * size + 1) + 2 * graphicFormat.getCubeMargin();
        int height = graphicFormat.getCubeSize() * (1 + 4 * size + 1) + 3 * graphicFormat.getCubeMargin();
        f.setMinimumSize(new Dimension(width, height));
        f.setSize(new Dimension(width, height));

        // FIXME : je n'arrive pas à initialiser la taille de la fenetre !
        f.setMaximumSize(new Dimension(width, height));

        f.setVisible(true);
    }

    public void initRubiksCube(int size) {
		this.rubiksCube = new RubiksCube(size);
		if (this.graphicFormat != null)
		    this.graphicFormat.updateRubiksCube(this.rubiksCube);
	}

	public RubiksCube getRubiksCube() {return this.rubiksCube;}
	
	public void init() {
		setBackground(Color.BLACK);
		
		// FIXME : ca ne semble pas avoir d'effet
		setForeground(Color.BLACK);
	}
	
	@Override
	public void paint(Graphics g) {
		// FIXME : on perd le fond noir, et la sélection en clic droit perd les couleurs de la face sélectionnée
        // FIXME : mais on gagne le rafraichissement lors du zoom ... par contre ça fait plus saccadé !
        //super.paint(g);
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
				LOGGER.error("Bad parameter format : you must provide a numeric size for the Rubik's Cube => {}", args[0]);
				System.exit(0);
			}
		}
		
		new RubiksCube2D(size);
	}

    public void zoomIn() {
	    graphicFormat.zoom(DEFAULT_ZOOM_OFFSET);
    }

    public void zoomOut() {
        graphicFormat.zoom(-DEFAULT_ZOOM_OFFSET);
    }
}