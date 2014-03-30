import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class RubiksCube2DMouseListener implements MouseListener {
	// TODO: � supprimer et remplacer par les variables de RubiksCube
	private Point mouseRightPressedPoint;

	private Move2D getMoveInProgress(RubiksCube rc, Point mouseReleasedPoint) {
		// Identification du cube qui a �t� cliqu�
		if (RubiksCube2D.DEBUG)
			System.out.println("### Mouse released on : " + mouseReleasedPoint);
		
		// En cas de click gauche, on est sur un mouvement sp�cifique d'une face
		if (! rc.isFaceMove()) {
			return Move2D.DEFINED;
		}
		
		
		// Taille du mouvement n�cessaire pour d�clencher une rotation du cube
		// ici on part sur la largeur d'une face
		//final int MOVE_SIZE = rc.getSize() * RubiksCube2DFormat.CUBE_SIZE; 
		final int MOVE_SIZE = RubiksCube2DFormat.CUBE_SIZE; 
		
		// On s'assure que la souris a �t� press�e sur une des faces horizontales
		if (isPointOnHorizontalFaces(rc, mouseRightPressedPoint)) {
			
			// On s'assure que la souris a �t� relach�e sur une des faces horizontales
			if (isPointOnHorizontalFaces(rc, mouseReleasedPoint)) {
				double horizontalDelta = mouseReleasedPoint.getX() - this.mouseRightPressedPoint.getX();
				
				// Mouvement explicite de gauche � droite
				if (horizontalDelta >= MOVE_SIZE) {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Horizontal move from left to right detected !");						
					return Move2D.UNYAW;							
				}
				// Mouvement explicite de droite � gauche
				else if (horizontalDelta <= - MOVE_SIZE) {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Horizontal move from right to left detected !");
					return Move2D.YAW;							
				}
			}
		}
		
		// On s'assure que la souris a �t� press�e sur une des faces verticales
		if (isPointOnVerticalFaces(rc, mouseRightPressedPoint)) {
			
			// On s'assure que la souris a �t� relach�e sur une des faces verticales
			if (isPointOnVerticalFaces(rc, mouseReleasedPoint)) {
				double verticalDelta = mouseReleasedPoint.getY() - this.mouseRightPressedPoint.getY();
				
				// Mouvement explicite de haut en bas
				if (verticalDelta >= MOVE_SIZE) {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Vertical move from up to down detected !");
					return Move2D.UNPITCH;						
				}
				// Mouvement explicite de bas en haut
				else if (verticalDelta <= - MOVE_SIZE) {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Vertical move from down to up detected !");
					return Move2D.PITCH;
				}
			}
		}
		
		// On s'assure que la souris a �t� press�e sur une des faces lat�rales (left, right, top ou bottom)
		if (isPointOnLateralFaces(rc, mouseRightPressedPoint)) {
			
			// On s'assure que la souris a �t� relach�e sur une des faces lat�rales
			if (isPointOnLateralFaces(rc, mouseReleasedPoint)) {
				double verticalDelta   = mouseReleasedPoint.getY() - mouseRightPressedPoint.getY();
				double horizontalDelta = mouseReleasedPoint.getX() - mouseRightPressedPoint.getX();
				
				// Gestion du mouvement circulaire d'une face complete avec face de d�part = face d'arriv�e
				if (rc.getPressedFaceIdentified() == rc.getReleasedFaceIdentified()) {
					switch (rc.getPressedFaceIdentified()) {
						case LEFT:
							if (verticalDelta >= MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Anti hour circular move detected !");
								return Move2D.UNROLL;						
							}
							else if (verticalDelta <= - MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Hour circular move detected !");
								return Move2D.ROLL;						
							} 
							break;
							
						case RIGHT:
							if (verticalDelta >= MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Hour circular move detected !");
								return Move2D.ROLL;						
							}
							else if (verticalDelta <= - MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Anti hour circular move detected !");
								return Move2D.UNROLL;						
							} 
							break;
							
						case TOP:
							if (horizontalDelta >= MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Hour circular move detected !");
								return Move2D.ROLL;							
							}
							else if (horizontalDelta <= - MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Anti hour circular move detected !");
								return Move2D.UNROLL;						
							} 
							break;
							
						case BOTTOM:
							if (horizontalDelta >= MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Anti hour circular move detected !");
								return Move2D.UNROLL;
							}
							else if (horizontalDelta <= - MOVE_SIZE) {
								if (RubiksCube2D.DEBUG)
									System.out.println("### Hour circular move detected !");
								return Move2D.ROLL;
							} 
							break;
					}
				}
				// Gestion du mouvement circulaire d'une face complete avec face de d�part <> face d'arriv�e
				else {
					if (matchStartAndEndFaces(rc, Face.LEFT, Face.TOP)
							|| matchStartAndEndFaces(rc, Face.TOP, Face.RIGHT)
							|| matchStartAndEndFaces(rc, Face.RIGHT, Face.BOTTOM)
							|| matchStartAndEndFaces(rc, Face.BOTTOM, Face.LEFT)) {
						if (RubiksCube2D.DEBUG)
							System.out.println("### Hour circular move detected !");
						return Move2D.ROLL;
					}
					else if (matchStartAndEndFaces(rc, Face.LEFT, Face.BOTTOM)
							|| matchStartAndEndFaces(rc, Face.BOTTOM, Face.RIGHT)
							|| matchStartAndEndFaces(rc, Face.RIGHT, Face.TOP)
							|| matchStartAndEndFaces(rc, Face.TOP, Face.LEFT)) {
						if (RubiksCube2D.DEBUG)
							System.out.println("### Anti hour circular move detected !");
						return Move2D.UNROLL;
					}
				}
			}
		}
		
		return null;
	}

	private boolean matchStartAndEndFaces(RubiksCube rc, Face start, Face end) {
		return rc.getPressedFaceIdentified() == start && rc.getReleasedFaceIdentified() == end;
	}

	private boolean isPointOnLateralFaces(RubiksCube rc, Point point) {
		Face face = identifyFace(rc, point);
		boolean ok = face != null && face.isALateralFace();
		if (ok && RubiksCube2D.DEBUG)
			System.out.println("### Point " + point + " identified on Lateral faces (Left or Right or Top or Bottom)");					
		return ok;
	}

	private boolean isPointOnVerticalFaces(RubiksCube rc, Point point) {
		Face face = identifyFace(rc, point);
		boolean ok = face != null && face.isAVerticalFace();
		if (ok && RubiksCube2D.DEBUG)
			System.out.println("### Point " + point + " identified on Vertical faces (Back or Top or Front or Bottom)");					
		return ok;
	}

	private boolean isPointOnHorizontalFaces(RubiksCube rc, Point point) {
		Face face = identifyFace(rc, point);
		boolean ok = face != null && face.isAnHorizontalFace();
		if (ok && RubiksCube2D.DEBUG)
			System.out.println("### Point " + point + " identified on Horizontal faces (Left or Front or Right)");
		return ok;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("# Mouse clicked on : " + arg0.getPoint());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("# Mouse entered on : " + arg0.getPoint());
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (RubiksCube2D.DEBUG)
			System.out.println("# Mouse exited on : " + arg0.getPoint());
	}

	private boolean isPointOnRubiksCube(RubiksCube rc, Point point) {
		return isPointOnVerticalFaces(rc, point) || isPointOnHorizontalFaces(rc, point);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		RubiksCube rc = ((RubiksCube2D) arg0.getSource()).getRubiksCube();
		if (isPointOnRubiksCube(rc, arg0.getPoint())) {
			if (MouseEvent.BUTTON3 == arg0.getButton()) {
				rc.setIsFaceMove(true);
				this.mouseRightPressedPoint = arg0.getPoint();
				rc.setPressedFaceIdentified(identifyFace(rc, this.mouseRightPressedPoint));
				rc.setPointOnPressedFaceIdentified(identifyPointCoordOnFace(rc, rc.getPressedFaceIdentified(), arg0.getPoint()));
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
			
			if (RubiksCube2D.DEBUG)
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
			
		if (RubiksCube2D.DEBUG && identifiedFace != null)
			System.out.println("# Face identified : " + identifiedFace);
		
		return identifiedFace;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		RubiksCube rc = ((RubiksCube2D) arg0.getSource()).getRubiksCube();
		
		if (rc.hasPointOnPressedFaceIdentified()) {
			Point mouseReleasedPoint = arg0.getPoint();
			
			// On tourne une face d�finie suite � un click gauche ou droite
			rc.setReleasedFaceIdentified(identifyFace(rc, mouseReleasedPoint));
			rc.setPointOnReleasedFaceIdentified(identifyPointCoordOnFace(rc, rc.getReleasedFaceIdentified(), mouseReleasedPoint));
			
			// On ne prend en compte le click relach� que si il se trouve sur le Rubik's Cube
			if (isPointOnRubiksCube(rc, mouseReleasedPoint)) {
				Move2D move = getMoveInProgress(rc, mouseReleasedPoint);
				if (move != null) {
					switch (move) {
						case UNYAW:
							// On tourne le cube autour de l'axe Y globalement
							for (int i = 1; i <= rc.getSize(); i++) {
								rc.unyaw(i);
							}
							break;
							
						case YAW:
							// On tourne le cube autour de l'axe Y globalement
							for (int i = 1; i <= rc.getSize(); i++) {
								rc.yaw(i);
							}
							break;
							
						case UNPITCH:
							// On tourne le cube autour de l'axe X globalement
							for (int i = 1; i <= rc.getSize(); i++) {
								rc.unpitch(i);
							}
							break;
							
						case PITCH:
							// On tourne le cube autour de l'axe Y globalement
							for (int i = 1; i <= rc.getSize(); i++) {
								rc.pitch(i);
							}
							break;
							
						case ROLL:
							// On tourne le cube autour de l'axe Z globalement
							for (int i = 1; i <= rc.getSize(); i++) {
								rc.roll(i);
							}
							break;
							
						case UNROLL:
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
		}
		
		// On r�initialise le click droit de la souris
		this.mouseRightPressedPoint = null;
		
		if (rc.clearStuffIdentified()) {
			RubiksCube2D applet = ((RubiksCube2D) arg0.getSource());
			applet.repaint();
		}
	}

	private void moveRubiksCube(RubiksCube rc) {
		if (rc.getReleasedFaceIdentified() == null)
			return;
		
		if (RubiksCube2D.DEBUG) {
			System.out.println("Moving RubiksCube : startPoint " + rc.getPointOnPressedFaceIdentified().toString() + " on face " + rc.getPressedFaceIdentified() +
					           ", endPoint " + rc.getPointOnReleasedFaceIdentified().toString() + " on  face " + rc.getReleasedFaceIdentified());
		}
		
		// D�finir le mouvement 
		DefinedMove definedMove = getDefinedMoveInProgress(rc);
		
		if (definedMove.getMove() != null) {
			
			if (RubiksCube2D.DEBUG) {
				System.out.println("Move key : " + definedMove);
			}
			
			switch (definedMove.getMove()) {
				case UNYAW:
					rc.unyaw(definedMove.getIndex());
					break;
					
				case YAW:
					rc.yaw(definedMove.getIndex());
					break;
					
				case UNPITCH:
					rc.unpitch(definedMove.getIndex());
					break;
					
				case PITCH:
					rc.pitch(definedMove.getIndex());
					break;
					
				case ROLL:
					rc.roll(definedMove.getIndex());
					break;
					
				case UNROLL:
					rc.unroll(definedMove.getIndex());
					break;
			}
		}
		
	}

	class DefinedMove {
		private Move2D move;
		private int index;
		
		public void setMove(Move2D move) {this.move = move;}
		public Move2D getMove() {return this.move;}
		public void setIndex(int index) {this.index = index;}
		public int getIndex() {return this.index;}
		
		public String toString() {return move.name() + "@" + index;}
	}

	private DefinedMove getDefinedMoveInProgress(RubiksCube rc) {
		int size = rc.getSize();
		Face startFace   = rc.getPressedFaceIdentified();
		Point startPoint = rc.getPointOnPressedFaceIdentified();
		Face endFace     = rc.getReleasedFaceIdentified();
		Point endPoint   = rc.getPointOnReleasedFaceIdentified();
		
		DefinedMove definedMove = new DefinedMove();
		
		// On doit d'abord voir si les points sont coh�rents :
		// Sur une des faces horizontales
		if (startFace.isAnHorizontalFace()) {
			if (endFace.isAnHorizontalFace() && startPoint.getY() == endPoint.getY()) {
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
					definedMove.setMove(Move2D.UNYAW);
				}
				else if (move < 0) {
					definedMove.setMove(Move2D.YAW);
				}
				
				if (move != 0) {
					definedMove.setIndex(size - Double.valueOf(startPoint.getY()).intValue());
				}
			}
		}
		// Sur une des faces verticales
		if (startFace.isAVerticalFace()) {
			if (endFace.isAVerticalFace() && startPoint.getX() == endPoint.getX()) {
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
					definedMove.setMove(Move2D.UNPITCH);
				}
				else if (move < 0) {
					definedMove.setMove(Move2D.PITCH);
				}
				
				if (move != 0) {
					definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
				}
			}
		}
		// Sur une des faces lat�rales
		if (startFace.isALateralFace()) {
			if (endFace.isALateralFace()) {
				// Gestion du mouvement circulaire d'une face avec face de d�part = face d'arriv�e
				if (startFace == endFace) {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Circular face move : from a face to the same one");
				
					if (startFace.isAnHorizontalLateralFace() && startPoint.getX() == endPoint.getX()) {
						if (startFace == Face.LEFT) {
							definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);

							if (endPoint.getY() < startPoint.getY()) 
								definedMove.setMove(Move2D.ROLL);
							else if (endPoint.getY() > startPoint.getY())
								definedMove.setMove(Move2D.UNROLL);
						}
						else if (startFace == Face.RIGHT) {
							definedMove.setIndex(size - Double.valueOf(startPoint.getX()).intValue());
							
							if (endPoint.getY() > startPoint.getY())
								definedMove.setMove(Move2D.ROLL);
							else if (endPoint.getY() < startPoint.getY())
								definedMove.setMove(Move2D.UNROLL);
						}
					}
					else if (startFace.isAVerticalLateralFace() && startPoint.getY() == endPoint.getY()) {
						if (startFace == Face.TOP) {
							definedMove.setIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
							
							if (endPoint.getX() > startPoint.getX())
								definedMove.setMove(Move2D.ROLL);
							else if (endPoint.getX() < startPoint.getX())
								definedMove.setMove(Move2D.UNROLL);
						}
						else if (startFace == Face.BOTTOM) {
							definedMove.setIndex(size - Double.valueOf(startPoint.getY()).intValue());
							
							if (endPoint.getX() < startPoint.getX())
								definedMove.setMove(Move2D.ROLL);
							else if (endPoint.getX() > startPoint.getX())
								definedMove.setMove(Move2D.UNROLL);
						}
					}	
				}
				// Gestion du mouvement circulaire d'une face avec face de d�part <> face d'arriv�e
				else {
					if (RubiksCube2D.DEBUG)
						System.out.println("### Circular face move from a specific face to a different one");
					
					if (matchStartAndEndFaces(rc, Face.TOP, Face.RIGHT) && startPoint.getY() == (size - endPoint.getX() - 1)) {
						definedMove.setIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
						definedMove.setMove(Move2D.ROLL);
					}
					else if (matchStartAndEndFaces(rc, Face.RIGHT, Face.BOTTOM) && startPoint.getX() == endPoint.getY()) {
						definedMove.setIndex(Double.valueOf(size - startPoint.getX()).intValue());
						definedMove.setMove(Move2D.ROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.BOTTOM, Face.LEFT) && startPoint.getY() == (size - endPoint.getX() - 1)) {
						definedMove.setIndex(Double.valueOf(size - startPoint.getY()).intValue());
						definedMove.setMove(Move2D.ROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.LEFT, Face.TOP) && startPoint.getX() == endPoint.getY()) {
						definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
						definedMove.setMove(Move2D.ROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.TOP, Face.LEFT) && startPoint.getY() == endPoint.getX()) {
						definedMove.setIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
						definedMove.setMove(Move2D.UNROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.LEFT, Face.BOTTOM) && startPoint.getX() == (size - endPoint.getY() - 1)) {
						definedMove.setIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
						definedMove.setMove(Move2D.UNROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.BOTTOM, Face.RIGHT) && startPoint.getY() == endPoint.getX()) {
						definedMove.setIndex(Double.valueOf(size - startPoint.getY()).intValue());
						definedMove.setMove(Move2D.UNROLL);	
					}
					else if (matchStartAndEndFaces(rc, Face.RIGHT, Face.TOP) && startPoint.getX() == (size - endPoint.getY() - 1)) {
						definedMove.setIndex(Double.valueOf(size - startPoint.getX()).intValue());
						definedMove.setMove(Move2D.UNROLL);	
					}
				}
			}
		}
		
		return definedMove;
	}
}