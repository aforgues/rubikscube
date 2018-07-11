package org.aforgues.rubikscube.presentation.basic2d;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Face;
import org.aforgues.rubikscube.core.Move;
import org.aforgues.rubikscube.core.RubiksCube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class RubiksCube2DMouseListener implements MouseListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube2DMouseListener.class);

	private RubiksCube2D applet;

    public RubiksCube2DMouseListener(RubiksCube2D applet) {
        this.applet = applet;
    }

    private int getRubiksCube2DCubeSize() {
        return this.applet.getGraphicFormat().getCubeSize();
    }

    // Pour identifier le mouvement souhaité, on sauvegarde temporairement la localisation du click droit
    private Point mouseRightPressedPoint;

	private DefinedMove getMoveInProgress(RubiksCube rc, Point mouseReleasedPoint) {
		// Identification du cube qui a été cliqué
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("### Mouse released on : {}", mouseReleasedPoint);
		
		// En cas de click gauche, on est sur un mouvement spécifique d'une face
		if (! this.applet.isFaceMove()) {
			if (this.applet.getReleasedFaceIdentified() == null)
				return null;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Moving RubiksCube : startPoint {} on face {}, endPoint {} on  face {}", this.applet.getPointOnPressedFaceIdentified().toString(), this.applet.getPressedFaceIdentified(), this.applet.getPointOnReleasedFaceIdentified().toString(), this.applet.getReleasedFaceIdentified());
			}
			return getDefinedMoveInProgress(rc);
		}
		
		// Taille du mouvement nécessaire pour déclencher une rotation du cube
		// ici on part sur la largeur d'une face
		//final int MOVE_SIZE = rc.getSize() * getRubiksCube2DCubeSize();
		final int MOVE_SIZE = getRubiksCube2DCubeSize();
		
		// On s'assure que la souris a été pressée sur une des faces horizontales
		if (isPointOnHorizontalFaces(rc, mouseRightPressedPoint)) {
			
			// On s'assure que la souris a été relachée sur une des faces horizontales
			if (isPointOnHorizontalFaces(rc, mouseReleasedPoint)) {
				double horizontalDelta = mouseReleasedPoint.getX() - this.mouseRightPressedPoint.getX();
				
				// Mouvement explicite de gauche à droite
				if (horizontalDelta >= MOVE_SIZE) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Horizontal move from left to right detected !");
					return new DefinedMove(Move.UNYAW);
				}
				// Mouvement explicite de droite à gauche
				else if (horizontalDelta <= - MOVE_SIZE) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Horizontal move from right to left detected !");
					return new DefinedMove(Move.YAW);
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
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Vertical move from up to down detected !");
					return new DefinedMove(Move.UNPITCH);
				}
				// Mouvement explicite de bas en haut
				else if (verticalDelta <= - MOVE_SIZE) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Vertical move from down to up detected !");
					return new DefinedMove(Move.PITCH);
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
				if (this.applet.getPressedFaceIdentified() == this.applet.getReleasedFaceIdentified()) {
					switch (this.applet.getPressedFaceIdentified()) {
						case LEFT:
							if (verticalDelta >= MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Anti hour circular move detected !");
								return new DefinedMove(Move.UNROLL);
							}
							else if (verticalDelta <= - MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Hour circular move detected !");
								return new DefinedMove(Move.ROLL);
							} 
							break;
							
						case RIGHT:
							if (verticalDelta >= MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Hour circular move detected !");
								return new DefinedMove(Move.ROLL);
							}
							else if (verticalDelta <= - MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Anti hour circular move detected !");
								return new DefinedMove(Move.UNROLL);
							} 
							break;
							
						case TOP:
							if (horizontalDelta >= MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Hour circular move detected !");
								return new DefinedMove(Move.ROLL);
							}
							else if (horizontalDelta <= - MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Anti hour circular move detected !");
								return new DefinedMove(Move.UNROLL);
							} 
							break;
							
						case BOTTOM:
							if (horizontalDelta >= MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Anti hour circular move detected !");
								return new DefinedMove(Move.UNROLL);
							}
							else if (horizontalDelta <= - MOVE_SIZE) {
								if (LOGGER.isDebugEnabled())
									LOGGER.debug("### Hour circular move detected !");
								return new DefinedMove(Move.ROLL);
							} 
							break;
					
						default:
							break;
					}
				}
				// Gestion du mouvement circulaire d'une face complete avec face de départ <> face d'arrivée
				else {
					if (matchStartAndEndFaces(Face.LEFT, Face.TOP)
							|| matchStartAndEndFaces(Face.TOP, Face.RIGHT)
							|| matchStartAndEndFaces(Face.RIGHT, Face.BOTTOM)
							|| matchStartAndEndFaces(Face.BOTTOM, Face.LEFT)) {
						if (LOGGER.isDebugEnabled())
							LOGGER.debug("### Hour circular move detected !");
						return new DefinedMove(Move.ROLL);
					}
					else if (matchStartAndEndFaces(Face.LEFT, Face.BOTTOM)
							|| matchStartAndEndFaces(Face.BOTTOM, Face.RIGHT)
							|| matchStartAndEndFaces(Face.RIGHT, Face.TOP)
							|| matchStartAndEndFaces(Face.TOP, Face.LEFT)) {
						if (LOGGER.isDebugEnabled())
							LOGGER.debug("### Anti hour circular move detected !");
						return new DefinedMove(Move.UNROLL);
					}
				}
			}
		}
		
		return null;
	}

	private boolean matchStartAndEndFaces(Face start, Face end) {
		return this.applet.getPressedFaceIdentified() == start && this.applet.getReleasedFaceIdentified() == end;
	}

	private boolean isPointOnLateralFaces(RubiksCube rc, Point mouseClickedPoint) {
		Face face = identifyFace(rc, mouseClickedPoint);
		boolean ok = face != null && face.isALateralFace();
		if (LOGGER.isDebugEnabled()) {
			if (ok)
				LOGGER.debug("### Point {} identified on Lateral faces (Left or Right or Top or Bottom)", mouseClickedPoint);
		}
		return ok;
	}

	private boolean isPointOnVerticalFaces(RubiksCube rc, Point mouseClickedPoint) {
		Face face = identifyFace(rc, mouseClickedPoint);
		boolean ok = face != null && face.isAVerticalFace();
		if (LOGGER.isDebugEnabled()) {
			if (ok)
				LOGGER.debug("### Point {} identified on Vertical faces (Back or Top or Front or Bottom)", mouseClickedPoint);
		}
		return ok;
	}

	private boolean isPointOnHorizontalFaces(RubiksCube rc, Point mouseClickedPoint) {
		Face face = identifyFace(rc, mouseClickedPoint);
		boolean ok = face != null && face.isAnHorizontalFace();
		if (LOGGER.isDebugEnabled()) {
			if (ok)			
				LOGGER.debug("### Point {} identified on Horizontal faces (Left or Front or Right)", mouseClickedPoint);
		}
		return ok;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("# Mouse clicked on : {}", arg0.getPoint());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("# Mouse entered on : {}", arg0.getPoint());
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("# Mouse exited on : {}", arg0.getPoint());
	}

	private boolean isPointOnRubiksCube(RubiksCube rc, Point mouseClickedPoint) {
		return isPointOnVerticalFaces(rc, mouseClickedPoint) || isPointOnHorizontalFaces(rc, mouseClickedPoint);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("# Mouse pressed on : {}", arg0.getPoint());

	    RubiksCube rc = ((RubiksCube2D) arg0.getSource()).getRubiksCube();
		if (isPointOnRubiksCube(rc, arg0.getPoint())) {
			if (MouseEvent.BUTTON3 == arg0.getButton()) {
				this.applet.setIsFaceMove(true);
				this.mouseRightPressedPoint = arg0.getPoint();
				this.applet.setPressedFaceIdentified(identifyFace(rc, this.mouseRightPressedPoint));
				this.applet.setPointOnPressedFaceIdentified(identifyPointCoordOnFace(this.applet.getPressedFaceIdentified(), arg0.getPoint()));
				this.applet.repaint();
			}
			else if (MouseEvent.BUTTON1 == arg0.getButton()) {
				this.applet.setPressedFaceIdentified(identifyFace(rc, arg0.getPoint()));
				this.applet.setPointOnPressedFaceIdentified(identifyPointCoordOnFace(this.applet.getPressedFaceIdentified(), arg0.getPoint()));
				this.applet.repaint();
			}
		}
	}

	private Point identifyPointCoordOnFace(Face faceIdentified, Point mouseClickedPoint) {
		Point pointCoordOnFace = null;
		if (faceIdentified != null) {
			RubiksCube2DFormat format = this.applet.getGraphicFormat();
			
			pointCoordOnFace = new Point(
					(int) Math.floor((mouseClickedPoint.getX() - format.getFaceOffset(faceIdentified).getX()) / (getRubiksCube2DCubeSize())),
			        (int) Math.floor((mouseClickedPoint.getY() - format.getFaceOffset(faceIdentified).getY()) / (getRubiksCube2DCubeSize())));
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("# Point identified on face : {}", pointCoordOnFace);
		}
		return pointCoordOnFace;
	}

	private Face identifyFace(RubiksCube rc, Point mouseClickedPoint) {
		Face identifiedFace = null;
		
		RubiksCube2DFormat format = this.applet.getGraphicFormat();
		if (mouseClickedPoint.getX() >= format.getBackFaceOffset().getX()
	     && mouseClickedPoint.getX() < format.getBackFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	     && mouseClickedPoint.getY() >= format.getBackFaceOffset().getY()
	     && mouseClickedPoint.getY() < format.getBackFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.BACK;
		}
		else if (mouseClickedPoint.getX() >= format.getBottomFaceOffset().getX()
	          && mouseClickedPoint.getX() < format.getBottomFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	          && mouseClickedPoint.getY() >= format.getBottomFaceOffset().getY()
	          && mouseClickedPoint.getY() < format.getBottomFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.BOTTOM;
		}
		else if (mouseClickedPoint.getX() >= format.getFrontFaceOffset().getX()
	          && mouseClickedPoint.getX() < format.getFrontFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	          && mouseClickedPoint.getY() >= format.getFrontFaceOffset().getY()
	          && mouseClickedPoint.getY() < format.getFrontFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.FRONT;
		}
		else if (mouseClickedPoint.getX() >= format.getLeftFaceOffset().getX()
	          && mouseClickedPoint.getX() < format.getLeftFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	          && mouseClickedPoint.getY() >= format.getLeftFaceOffset().getY()
	          && mouseClickedPoint.getY() < format.getLeftFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.LEFT;
		}
		else if (mouseClickedPoint.getX() >= format.getRightFaceOffset().getX()
	          && mouseClickedPoint.getX() < format.getRightFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	          && mouseClickedPoint.getY() >= format.getRightFaceOffset().getY()
	          && mouseClickedPoint.getY() < format.getRightFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.RIGHT;
		}
		else if (mouseClickedPoint.getX() >= format.getTopFaceOffset().getX()
	          && mouseClickedPoint.getX() < format.getTopFaceOffset().getX() + rc.getSize() * getRubiksCube2DCubeSize()
	          && mouseClickedPoint.getY() >= format.getTopFaceOffset().getY()
	          && mouseClickedPoint.getY() < format.getTopFaceOffset().getY() + rc.getSize() * getRubiksCube2DCubeSize()) {
			identifiedFace = Face.TOP;
		}
			
		if (LOGGER.isDebugEnabled()) {
			if (identifiedFace != null)
				LOGGER.debug("# Face identified : {}", identifiedFace);
		}
		
		return identifiedFace;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("# Mouse released on : {}", arg0.getPoint());

	    RubiksCube rc = ((RubiksCube2D) arg0.getSource()).getRubiksCube();
		
		if (this.applet.hasPointOnPressedFaceIdentified()) {
			Point mouseReleasedPoint = arg0.getPoint();
			
			// On tourne une face définie suite à un click gauche ou droite
			this.applet.setReleasedFaceIdentified(identifyFace(rc, mouseReleasedPoint));
			this.applet.setPointOnReleasedFaceIdentified(identifyPointCoordOnFace(this.applet.getReleasedFaceIdentified(), mouseReleasedPoint));
			
			// On ne prend en compte le click relaché que si il se trouve sur le Rubik's Cube
			if (isPointOnRubiksCube(rc, mouseReleasedPoint)) {
				DefinedMove definedMove = getMoveInProgress(rc, mouseReleasedPoint);
				if (definedMove != null) {
					rc.manualMove(definedMove);
					this.applet.repaint();
				}
			}
		}
		
		// On réinitialise le click droit de la souris
		this.mouseRightPressedPoint = null;
		
		if (this.applet.clearStuffIdentified()) {
			this.applet.repaint();
		}
	}

	private DefinedMove getDefinedMoveInProgress(RubiksCube rc) {
		int size = rc.getSize();
		Face startFace   = this.applet.getPressedFaceIdentified();
		Point startPoint = this.applet.getPointOnPressedFaceIdentified();
		Face endFace     = this.applet.getReleasedFaceIdentified();
		Point endPoint   = this.applet.getPointOnReleasedFaceIdentified();
		
		DefinedMove definedMove = new DefinedMove();
		
		// On doit d'abord voir si les points sont cohérents :
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
					definedMove.setMove(Move.UNYAW);
				}
				else if (move < 0) {
					definedMove.setMove(Move.YAW);
				}
				
				if (move != 0) {
					definedMove.setFaceIndex(size - Double.valueOf(startPoint.getY()).intValue());
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
					definedMove.setMove(Move.UNPITCH);
				}
				else if (move < 0) {
					definedMove.setMove(Move.PITCH);
				}
				
				if (move != 0) {
					definedMove.setFaceIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
				}
			}
		}
		// Sur une des faces latérales
		if (startFace.isALateralFace()) {
			if (endFace.isALateralFace()) {
				// Gestion du mouvement circulaire d'une face avec face de départ = face d'arrivée
				if (startFace == endFace) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Circular face move : from a face to the same one");
				
					if (startFace.isAnHorizontalLateralFace() && startPoint.getX() == endPoint.getX()) {
						if (startFace == Face.LEFT) {
							definedMove.setFaceIndex(Double.valueOf(startPoint.getX()).intValue() + 1);

							if (endPoint.getY() < startPoint.getY()) 
								definedMove.setMove(Move.ROLL);
							else if (endPoint.getY() > startPoint.getY())
								definedMove.setMove(Move.UNROLL);
						}
						else if (startFace == Face.RIGHT) {
							definedMove.setFaceIndex(size - Double.valueOf(startPoint.getX()).intValue());
							
							if (endPoint.getY() > startPoint.getY())
								definedMove.setMove(Move.ROLL);
							else if (endPoint.getY() < startPoint.getY())
								definedMove.setMove(Move.UNROLL);
						}
					}
					else if (startFace.isAVerticalLateralFace() && startPoint.getY() == endPoint.getY()) {
						if (startFace == Face.TOP) {
							definedMove.setFaceIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
							
							if (endPoint.getX() > startPoint.getX())
								definedMove.setMove(Move.ROLL);
							else if (endPoint.getX() < startPoint.getX())
								definedMove.setMove(Move.UNROLL);
						}
						else if (startFace == Face.BOTTOM) {
							definedMove.setFaceIndex(size - Double.valueOf(startPoint.getY()).intValue());
							
							if (endPoint.getX() < startPoint.getX())
								definedMove.setMove(Move.ROLL);
							else if (endPoint.getX() > startPoint.getX())
								definedMove.setMove(Move.UNROLL);
						}
					}	
				}
				// Gestion du mouvement circulaire d'une face avec face de départ <> face d'arrivée
				else {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("### Circular face move from a specific face to a different one");
					
					if (matchStartAndEndFaces(Face.TOP, Face.RIGHT) && startPoint.getY() == (size - endPoint.getX() - 1)) {
						definedMove.setFaceIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
						definedMove.setMove(Move.ROLL);
					}
					else if (matchStartAndEndFaces(Face.RIGHT, Face.BOTTOM) && startPoint.getX() == endPoint.getY()) {
						definedMove.setFaceIndex(Double.valueOf(size - startPoint.getX()).intValue());
						definedMove.setMove(Move.ROLL);	
					}
					else if (matchStartAndEndFaces(Face.BOTTOM, Face.LEFT) && startPoint.getY() == (size - endPoint.getX() - 1)) {
						definedMove.setFaceIndex(Double.valueOf(size - startPoint.getY()).intValue());
						definedMove.setMove(Move.ROLL);	
					}
					else if (matchStartAndEndFaces(Face.LEFT, Face.TOP) && startPoint.getX() == endPoint.getY()) {
						definedMove.setFaceIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
						definedMove.setMove(Move.ROLL);	
					}
					else if (matchStartAndEndFaces(Face.TOP, Face.LEFT) && startPoint.getY() == endPoint.getX()) {
						definedMove.setFaceIndex(Double.valueOf(startPoint.getY()).intValue() + 1);
						definedMove.setMove(Move.UNROLL);	
					}
					else if (matchStartAndEndFaces(Face.LEFT, Face.BOTTOM) && startPoint.getX() == (size - endPoint.getY() - 1)) {
						definedMove.setFaceIndex(Double.valueOf(startPoint.getX()).intValue() + 1);
						definedMove.setMove(Move.UNROLL);	
					}
					else if (matchStartAndEndFaces(Face.BOTTOM, Face.RIGHT) && startPoint.getY() == endPoint.getX()) {
						definedMove.setFaceIndex(Double.valueOf(size - startPoint.getY()).intValue());
						definedMove.setMove(Move.UNROLL);	
					}
					else if (matchStartAndEndFaces(Face.RIGHT, Face.TOP) && startPoint.getX() == (size - endPoint.getY() - 1)) {
						definedMove.setFaceIndex(Double.valueOf(size - startPoint.getX()).intValue());
						definedMove.setMove(Move.UNROLL);	
					}
				}
			}
		}
		
		return definedMove;
	}
}