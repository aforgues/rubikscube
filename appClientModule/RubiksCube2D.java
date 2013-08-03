import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Point;

import javax.swing.JApplet;
import javax.swing.JFrame;


public class RubiksCube2D extends JApplet {


	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG = true;
	
	private RubiksCube rubiksCube;
	private int rubiksCubeSize = 3;
	
	public void initRubiksCube(int size) {
		this.rubiksCube = null;
		this.repaint();
		this.rubiksCubeSize = size;
		this.rubiksCube = new RubiksCube(rubiksCubeSize);
	}

	private RubiksCube getRubiksCube() {
		if (this.rubiksCube == null) {
			this.initRubiksCube(3);
		}
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
		//rubiksCube.shuffle(20);
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
		
		applet.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (DEBUG)
					System.out.println("# Key pressed : " + arg0.getKeyChar());
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (DEBUG)
					System.out.println("# Key released : " + arg0.getKeyChar());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (DEBUG)
					System.out.println("### Key Typed : #" + arg0.getKeyChar());
				
				RubiksCube2D applet = ((RubiksCube2D) arg0.getSource());
				
				switch (arg0.getKeyChar()) {
					case 'a':
						applet.rubiksCube.pitch(1);
						applet.repaint();
						break;
					case 'z':
						applet.rubiksCube.pitch(2);
						applet.repaint();
						break;
					case 'e':
						applet.rubiksCube.pitch(3);
						applet.repaint();
						break;
					case 'r':
						applet.rubiksCube.unpitch(1);
						applet.repaint();
						break;
					case 'q':
						applet.rubiksCube.yaw(1);
						applet.repaint();
						break;
					case 's':
						applet.rubiksCube.yaw(2);
						applet.repaint();
						break;
					case 'd':
						applet.rubiksCube.yaw(3);
						applet.repaint();
						break;
					case 'f':
						applet.rubiksCube.unyaw(1);
						applet.repaint();
						break;
					case 'w':
						applet.rubiksCube.roll(1);
						applet.repaint();
						break;
					case 'x':
						applet.rubiksCube.roll(2);
						applet.repaint();
						break;
					case 'c':
						applet.rubiksCube.roll(3);
						applet.repaint();
						break;
					case 'v':
						applet.rubiksCube.unroll(1);
						applet.repaint();
						break;
					case 'm':
						applet.rubiksCube.shuffle(20);
						applet.repaint();
						break;
					case 'i':
						applet.rubiksCube.reset();
						applet.repaint();
						break;
					case '1':
						applet.initRubiksCube(1);
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
						if (DEBUG)
							System.out.println("### Key typed : no action defined on this key");
				}

				
			}
			
		});
		
		// TODO: ajouter un MouseMotionListener pour afficher les cubes survoler lors d'un clic gauche
		
		applet.addMouseListener(new MouseListener() {
			
			private Point mouseRightPressedPoint;
			
			private Move getMoveInProgress(RubiksCube rc, Point mouseReleasedPoint) {
				// Identification du cube qui a été cliqué
				if (DEBUG)
					System.out.println("### Mouse released on : " + mouseReleasedPoint);
				
				// En cas de click gauche, on est sur un mouvement spécifique d'une face
				if (rc.hasPointOnPressedFaceIdentified()) {
					return Move.DEFINED;
				}
				
				
				// Taille du mouvement nécessaire pour déclencher une rotation du cube
				// ici on part sur la largeur d'une face
				//final int MOVE_SIZE = rc.getSize() * RubiksCube2DFormat.CUBE_SIZE; 
				final int MOVE_SIZE = RubiksCube2DFormat.CUBE_SIZE; 
				
				// On s'assure que la souris a été pressée sur une des faces horizontales
				if (isPointOnHorizontalFaces(rc, mouseRightPressedPoint)) {
					
					// On s'assure que la souris a été relachée sur une des faces horizontales
					if (isPointOnHorizontalFaces(rc, mouseReleasedPoint)) {
						double horizontalDelta = mouseReleasedPoint.getX() - this.mouseRightPressedPoint.getX();
						
						// Mouvement explicite de gauche à droite
						if (horizontalDelta >= MOVE_SIZE) {
							if (DEBUG)
								System.out.println("### Horizontal move from left to right detected !");						
							return Move.LEFT_TO_RIGHT;							
						}
						// Mouvement explicite de droite à gauche
						else if (horizontalDelta <= - MOVE_SIZE) {
							if (DEBUG)
								System.out.println("### Horizontal move from right to left detected !");
							return Move.RIGHT_TO_LEFT;							
						}
					}
				}
				
				// On s'assure que la souris a été pressée sur une des faces verticales
				if (isPointOnVerticalFaces(rc, mouseRightPressedPoint)) {
					
					// On s'assure que la souris a été relachée sur une des faces verticales
					if (isPointOnVerticalFaces(rc, mouseReleasedPoint)) {
						double verticalDelta = mouseReleasedPoint.getY() - this.mouseRightPressedPoint.getY();
						
						// Mouvement explicite de haut en bas
						if (verticalDelta >= MOVE_SIZE) {
							if (DEBUG)
								System.out.println("### Vertical move from up to down detected !");
							return Move.UP_TO_DOWN;						
						}
						// Mouvement explicite de bas en haut
						else if (verticalDelta <= - MOVE_SIZE) {
							if (DEBUG)
								System.out.println("### Vertical move from down to up detected !");
							return Move.DOWN_TO_UP;
						}
					}
				}
				
				// On s'assure que la souris a été pressée sur une des faces latérales (left, right, top ou bottom)
				if (isPointOnLateralFaces(rc, mouseRightPressedPoint)) {
					
					// On s'assure que la souris a été relachée sur une des faces latérales
					if (isPointOnLateralFaces(rc, mouseReleasedPoint)) {
						double verticalDelta   = mouseReleasedPoint.getY() - mouseRightPressedPoint.getY();
						double horizontalDelta = mouseReleasedPoint.getX() - mouseRightPressedPoint.getX();
						
						// Gestion du mouvement circulaire d'une face complete avec face de départ = face d'arrivée
						if (rc.getPressedFaceIdentified() == rc.getReleasedFaceIdentified()) {
							switch (rc.getPressedFaceIdentified()) {
								case LEFT:
									if (verticalDelta >= MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Anti hour circular move detected !");
										return Move.CIRCULAR_ANTI_HOUR;						
									}
									else if (verticalDelta <= - MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Hour circular move detected !");
										return Move.CIRCULAR_HOUR;						
									} 
									break;
									
								case RIGHT:
									if (verticalDelta >= MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Hour circular move detected !");
										return Move.CIRCULAR_HOUR;						
									}
									else if (verticalDelta <= - MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Anti hour circular move detected !");
										return Move.CIRCULAR_ANTI_HOUR;						
									} 
									break;
									
								case TOP:
									if (horizontalDelta >= MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Hour circular move detected !");
										return Move.CIRCULAR_HOUR;							
									}
									else if (horizontalDelta <= - MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Anti hour circular move detected !");
										return Move.CIRCULAR_ANTI_HOUR;						
									} 
									break;
									
								case BOTTOM:
									if (horizontalDelta >= MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Anti hour circular move detected !");
										return Move.CIRCULAR_ANTI_HOUR;
									}
									else if (horizontalDelta <= - MOVE_SIZE) {
										if (DEBUG)
											System.out.println("### Hour circular move detected !");
										return Move.CIRCULAR_HOUR;
									} 
									break;
							}
						}
						// Gestion du mouvement circulaire d'une face complete avec face de départ <> face d'arrivée
						else {
							if (matchStartAndEndFaces(rc, Face.LEFT, Face.TOP)
									|| matchStartAndEndFaces(rc, Face.TOP, Face.RIGHT)
									|| matchStartAndEndFaces(rc, Face.RIGHT, Face.BOTTOM)
									|| matchStartAndEndFaces(rc, Face.BOTTOM, Face.LEFT)) {
								if (DEBUG)
									System.out.println("### Hour circular move detected !");
								return Move.CIRCULAR_HOUR;
							}
							else if (matchStartAndEndFaces(rc, Face.LEFT, Face.BOTTOM)
									|| matchStartAndEndFaces(rc, Face.BOTTOM, Face.RIGHT)
									|| matchStartAndEndFaces(rc, Face.RIGHT, Face.TOP)
									|| matchStartAndEndFaces(rc, Face.TOP, Face.LEFT)) {
								if (DEBUG)
									System.out.println("### Anti hour circular move detected !");
								return Move.CIRCULAR_ANTI_HOUR;
							}
						}
					}
				}
				
				return Move.NONE;
			}
			
			private boolean matchStartAndEndFaces(RubiksCube rc, Face start, Face end) {
				return rc.getPressedFaceIdentified() == start && rc.getReleasedFaceIdentified() == end;
			}

			private boolean isPointOnLateralFaces(RubiksCube rc, Point point) {
				Face face = identifyFace(rc, point);
				boolean ok = Face.LEFT == face || Face.RIGHT == face || Face.TOP == face || Face.BOTTOM == face;
				if (ok && DEBUG)
					System.out.println("### Point " + point + " identified on circular faces (Left or Right or Top or Bottom)");					
				return ok;
			}

			private boolean isPointOnVerticalFaces(RubiksCube rc, 
												   Point point) {
				RubiksCube2DFormat format = new RubiksCube2DFormat(rc, null);
				boolean ok = point.getX() >= format.getBackFaceOffset().getX()
				          && point.getX() < format.getBackFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
				          && point.getY() >= format.getBackFaceOffset().getY()
				          && point.getY() < format.getBottomFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE;
				if (ok && DEBUG)
					System.out.println("### Point " + point + " identified on Vertical faces (Back or Top or Front or Bottom)");					
				return ok;
			}

			private boolean isPointOnHorizontalFaces(RubiksCube rc,
													 Point point) {
				RubiksCube2DFormat format = new RubiksCube2DFormat(rc, null);
				boolean ok = point.getX() >= format.getLeftFaceOffset().getX()
				          && point.getX() < format.getRightFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
				          && point.getY() >= format.getRightFaceOffset().getY()
				          && point.getY() < format.getRightFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE;
				if (ok && DEBUG)
					System.out.println("### Point " + point + " identified on Horizontal faces (Left or Front or Right)");
				return ok;
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (DEBUG)
					System.out.println("# Mouse clicked on : " + arg0.getPoint());
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (DEBUG)
					System.out.println("# Mouse entered on : " + arg0.getPoint());
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (DEBUG)
					System.out.println("# Mouse exited on : " + arg0.getPoint());
			}

			private boolean isPointOnRubiksCube(RubiksCube rc, Point point) {
				return isPointOnVerticalFaces(rc, point) || isPointOnHorizontalFaces(rc, point);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				RubiksCube rc = ((RubiksCube2D) arg0.getSource()).rubiksCube;
				if (isPointOnRubiksCube(rc, arg0.getPoint())) {
					if (MouseEvent.BUTTON3 == arg0.getButton()) {
						this.mouseRightPressedPoint = arg0.getPoint();
						rc.setPressedFaceIdentified(identifyFace(rc, this.mouseRightPressedPoint));
						((RubiksCube2D) arg0.getSource()).repaint();
					}
					else if (MouseEvent.BUTTON1 == arg0.getButton()) {
						rc.setPressedFaceIdentified(identifyFace(rc, arg0.getPoint()));
						rc.setPointOnPressedFaceIdentified(identifyPointCoordOnFace(rc, rc.getPressedFaceIdentified(), arg0.getPoint()));
						((RubiksCube2D) arg0.getSource()).repaint();
					}
				}
			}

			private Point identifyPointCoordOnFace(RubiksCube rc, Face faceIdentified, Point point) {				
				Point pointCoordOnFace = null;
				if (faceIdentified != null) {
					RubiksCube2DFormat format = new RubiksCube2DFormat(rc, null);
					
					pointCoordOnFace = new Point(
							(int) Math.floor((point.getX() - format.getFaceOffset(faceIdentified).getX()) / (RubiksCube2DFormat.CUBE_SIZE)),
					        (int) Math.floor((point.getY() - format.getFaceOffset(faceIdentified).getY()) / (RubiksCube2DFormat.CUBE_SIZE)));
					
					if (DEBUG)
						System.out.println("# Point identified on face : " + pointCoordOnFace);
				}
				return pointCoordOnFace;
			}

			private Face identifyFace(RubiksCube rc, Point point) {
				Face identifiedFace = null;
				
				RubiksCube2DFormat format = new RubiksCube2DFormat(rc, null);
				if (point.getX() >= format.getBackFaceOffset().getX()
		         && point.getX() < format.getBackFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
		         && point.getY() >= format.getBackFaceOffset().getY()
    	         && point.getY() < format.getBackFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.BACK;
				}
				else if (point.getX() >= format.getBottomFaceOffset().getX()
  		  	          && point.getX() < format.getBottomFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
			          && point.getY() >= format.getBottomFaceOffset().getY()
	    	          && point.getY() < format.getBottomFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.BOTTOM;
				}
				else if (point.getX() >= format.getFrontFaceOffset().getX()
			          && point.getX() < format.getFrontFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
		 	          && point.getY() >= format.getFrontFaceOffset().getY()
		              && point.getY() < format.getFrontFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.FRONT;
				}
				else if (point.getX() >= format.getLeftFaceOffset().getX()
  			          && point.getX() < format.getLeftFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
			          && point.getY() >= format.getLeftFaceOffset().getY()
		              && point.getY() < format.getLeftFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.LEFT;
				}
				else if (point.getX() >= format.getRightFaceOffset().getX()
	 	 	          && point.getX() < format.getRightFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
			          && point.getY() >= format.getRightFaceOffset().getY()
		              && point.getY() < format.getRightFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.RIGHT;
				}
				else if (point.getX() >= format.getTopFaceOffset().getX()
			          && point.getX() < format.getTopFaceOffset().getX() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE
			          && point.getY() >= format.getTopFaceOffset().getY()
		              && point.getY() < format.getTopFaceOffset().getY() + rc.getSize() * RubiksCube2DFormat.CUBE_SIZE) {
					identifiedFace = Face.TOP;
				}
					
				if (DEBUG)
					System.out.println("# Face identified : " + identifiedFace);
				
				return identifiedFace;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				RubiksCube rc = ((RubiksCube2D) arg0.getSource()).rubiksCube;
				
				// TODO: améliorer ce test et homogénéiser la gestion des variables affectées en cas de click gauche ou droite
				// De plus, on pré-suppose qu'on ne sera dans le cas ou mouseRightPressedPoint est not null seulement dans les case <> DEFINED
				if (this.mouseRightPressedPoint != null || rc.hasPointOnPressedFaceIdentified()) {
					Point mouseReleasedPoint = arg0.getPoint();
					
					// On tourne une face définie suite à un click gauche
					rc.setReleasedFaceIdentified(identifyFace(rc, mouseReleasedPoint));
					rc.setPointOnReleasedFaceIdentified(identifyPointCoordOnFace(rc, rc.getReleasedFaceIdentified(), mouseReleasedPoint));
					
					// On ne prend en compte le click relaché que si il se trouve sur le Rubik's Cube
					if (isPointOnRubiksCube(rc, mouseReleasedPoint)) {
						switch (getMoveInProgress(rc, mouseReleasedPoint)) {
							case LEFT_TO_RIGHT:
								// On tourne le cube autour de l'axe Y globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.unyaw(i);
								}
								break;
								
							case RIGHT_TO_LEFT:
								// On tourne le cube autour de l'axe Y globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.yaw(i);
								}
								break;
								
							case UP_TO_DOWN:
								// On tourne le cube autour de l'axe X globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.unpitch(i);
								}
								break;
								
							case DOWN_TO_UP:
								// On tourne le cube autour de l'axe Y globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.pitch(i);
								}
								break;
								
							case CIRCULAR_HOUR:
								// On tourne le cube autour de l'axe Z globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.roll(i);
								}
								break;
								
							case CIRCULAR_ANTI_HOUR:
								// On tourne le cube autour de l'axe Z globalement
								for (int i = 1; i <= rc.getSize(); i++) {
									rc.unroll(i);
								}
								break;
								
							case DEFINED:
								moveRubiksCube(rc);
								break;
						}
						
						RubiksCube2D applet = ((RubiksCube2D) arg0.getSource());
						applet.repaint();
					}
				}
				
				// On réinitialise le click droit de la souris
				this.mouseRightPressedPoint = null;
				
				// On réinitialise les face et point identifiée
				if (rc.hasPressedFaceIdentified()) {
					rc.setPressedFaceIdentified(null);
					
					if (rc.hasPointOnPressedFaceIdentified())
						rc.setPointOnPressedFaceIdentified(null);
					
					RubiksCube2D applet = ((RubiksCube2D) arg0.getSource());
					applet.repaint();
				}
				
				if (rc.hasReleasedFaceIdentified()) {
					rc.setReleasedFaceIdentified(null);
					
					if (rc.hasPointOnReleasedFaceIdentified()) {
						rc.setPointOnReleasedFaceIdentified(null);
					}
				}
			}

			private void moveRubiksCube(RubiksCube rc) {
				if (rc.getReleasedFaceIdentified() == null)
					return;
				
				if (DEBUG) {
					System.out.println("Moving RubiksCube : startPoint " + rc.getPointOnPressedFaceIdentified().toString() + " on face " + rc.getPressedFaceIdentified() +
							           ", endPoint " + rc.getPointOnReleasedFaceIdentified().toString() + " on  face " + rc.getReleasedFaceIdentified());
				}
				
				// Définir le mouvement 
				DefinedMove definedMove = getDefinedMoveInProgress(rc);
				
				if (definedMove.getMove() != Move.NONE) {
					
					switch (definedMove.getMove()) {
						case LEFT_TO_RIGHT:
							rc.unyaw(definedMove.getIndex());
							break;
							
						case RIGHT_TO_LEFT:
							rc.yaw(definedMove.getIndex());
							break;
							
						case UP_TO_DOWN:
							rc.unpitch(definedMove.getIndex());
							break;
							
						case DOWN_TO_UP:
							rc.pitch(definedMove.getIndex());
							break;
							
						case CIRCULAR_HOUR:
							rc.roll(definedMove.getIndex());
							break;
							
						case CIRCULAR_ANTI_HOUR:
							rc.unroll(definedMove.getIndex());
							break;
					}
				}
				
			}
			
			class DefinedMove {
				private Move move;
				private int index;
				
				public void setMove(Move move) {this.move = move;}
				public Move getMove() {return this.move;}
				public void setIndex(int index) {this.index = index;}
				public int getIndex() {return this.index;}
				
				public DefinedMove() {
					this.move = Move.NONE;
				}
			}
			
			private DefinedMove getDefinedMoveInProgress(RubiksCube rc) {
				int size = rc.getSize();
				Face startFace   = rc.getPressedFaceIdentified();
				Point startPoint = rc.getPointOnPressedFaceIdentified();
				Face endFace     = rc.getReleasedFaceIdentified();
				Point endPoint   = rc.getPointOnReleasedFaceIdentified();
				
				DefinedMove definedMove = new DefinedMove();
				
				// On doit d'abord voir si les points sont cohérents :
				// Sur une des faces horizontales
				if (isAnHorizontalFace(startFace)) {
					if (isAnHorizontalFace(endFace) && startPoint.getY() == endPoint.getY()) {
						int endPointOffset = 0;
						if (endFace == Face.FRONT)
							endPointOffset += size * 1;
						else if (endFace == Face.RIGHT)
							endPointOffset += size * 2;
						
						int startPointOffset = 0;
						if (startFace == Face.FRONT)
							startPointOffset += size * 1;
						else if (startFace == Face.RIGHT)
							startPointOffset += size * 2;
						
						double move = endPoint.getX() + endPointOffset - startPoint.getX() - startPointOffset;
						
						if (move > 0) {
							definedMove.setMove(Move.LEFT_TO_RIGHT);
						}
						else if (move < 0) {
							definedMove.setMove(Move.RIGHT_TO_LEFT);
						}
						
						if (move != 0) {
							definedMove.setIndex(size - Double.valueOf(startPoint.getY()).intValue());
						}
					}
				}
				// Sur une des faces verticales
				if (isAVerticalFace(startFace)) {
					if (isAVerticalFace(endFace) && startPoint.getX() == endPoint.getX()) {
						int endPointOffset = 0;
						if (endFace == Face.TOP)
							endPointOffset += size * 1;
						else if (endFace == Face.FRONT)
							endPointOffset += size * 2;
						else if (endFace == Face.BOTTOM)
							endPointOffset += size * 3;
						
						int startPointOffset = 0;
						if (startFace == Face.TOP)
							startPointOffset += size * 1;
						else if (startFace == Face.FRONT)
							startPointOffset += size * 2;
						else if (startFace == Face.BOTTOM)
							startPointOffset += size * 3;
						
						double move = endPoint.getY() + endPointOffset - startPoint.getY() - startPointOffset;
						
						if (move > 0) {
							definedMove.setMove(Move.UP_TO_DOWN);
						}
						else if (move < 0) {
							definedMove.setMove(Move.DOWN_TO_UP);
						}
						
						if (move != 0) {
							definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
						}
					}
				}
				// Sur une des faces latérales
				if (isALateralFace(startFace)) {
					if (isALateralFace(endFace)) {
						// Gestion du mouvement circulaire d'une face avec face de départ = face d'arrivée
						if (startFace == endFace) {
							if (isAnHorizontalLateralFace(startFace) && startPoint.getX() == endPoint.getX()) {
								if (startFace == Face.LEFT) {
									definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);

									if (endPoint.getY() < startPoint.getY()) 
										definedMove.setMove(Move.CIRCULAR_HOUR);
									else if (endPoint.getY() > startPoint.getY())
										definedMove.setMove(Move.CIRCULAR_ANTI_HOUR);
								}
								else if (startFace == Face.RIGHT) {
									definedMove.setIndex(size - Double.valueOf(startPoint.getX()).intValue());
									
									if (endPoint.getY() > startPoint.getY())
										definedMove.setMove(Move.CIRCULAR_HOUR);
									else if (endPoint.getY() < startPoint.getY())
										definedMove.setMove(Move.CIRCULAR_ANTI_HOUR);
								}
							}
							else if (isAVerticalLateralFace(startFace) && startPoint.getY() == endPoint.getY()) {
								if (startFace == Face.TOP) {
									definedMove.setIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
									
									if (endPoint.getX() > startPoint.getX())
										definedMove.setMove(Move.CIRCULAR_HOUR);
									else if (endPoint.getX() < startPoint.getX())
										definedMove.setMove(Move.CIRCULAR_ANTI_HOUR);
								}
								else if (startFace == Face.BOTTOM) {
									definedMove.setIndex(size - Double.valueOf(startPoint.getY()).intValue());
									
									if (endPoint.getX() < startPoint.getX())
										definedMove.setMove(Move.CIRCULAR_HOUR);
									else if (endPoint.getX() > startPoint.getX())
										definedMove.setMove(Move.CIRCULAR_ANTI_HOUR);
								}
							}	
						}
						// Gestion du mouvement circulaire d'une face avec face de départ <> face d'arrivée
						else {
							if (DEBUG)
								System.out.println("### Circular face move a face to a different one : not yet implemented !");
						}
					}
				}
				
				return definedMove;
			}
			
			// FIXME: déplacer cela dans RubiksCube2DFormat ?
			private boolean isAnHorizontalFace(Face face) {
				return Face.LEFT == face || Face.FRONT == face || Face.RIGHT == face;
			}
			
			// FIXME: déplacer cela dans RubiksCube2DFormat ?
			private boolean isAVerticalFace(Face face) {
				return Face.BACK == face || Face.TOP == face || Face.FRONT == face || Face.BOTTOM == face;
			}
			
			// FIXME: déplacer cela dans RubiksCube2DFormat ?
			private boolean isALateralFace(Face face) {
				return isAnHorizontalLateralFace(face) || isAVerticalLateralFace(face);
			}
			
			// FIXME: déplacer cela dans RubiksCube2DFormat ?
			private boolean isAnHorizontalLateralFace(Face face) {
				return Face.LEFT == face || Face.RIGHT == face;
			}
			
			// FIXME: déplacer cela dans RubiksCube2DFormat ?
			private boolean isAVerticalLateralFace(Face face) {
				return Face.TOP == face || Face.BOTTOM == face;
			}
			
		});
		
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