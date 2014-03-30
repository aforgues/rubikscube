import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class RubiksCubeAI {
	private RubiksCube initialRcConfig;
	
	public RubiksCubeAI(RubiksCube rc) {
		/*try {
			this.initialRcConfig = (RubiksCube) rc.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Unable to clone " + rc + " - " + e.getMessage());
		}*/
		// do not clone for test purpose so that we can see the effect of moves on cube directly
		this.initialRcConfig = rc;
		
		if (! isAiAvalaible())
			System.out.println("AI : only available for 3x3 Rubik's Cube !");
	}
	
	private boolean isAiAvalaible() {
		return this.initialRcConfig.getSize() == 3;
	}
	
	public void computeArtificialIntelligence() {
		if (! isAiAvalaible()) {
			System.out.println("AI : only available for 3x3 Rubik's Cube !");
			return;
		}
		
		if (this.initialRcConfig.isSolved()) {
			System.out.println("AI : RubiksCube is already solved !!!");
			return;
		}
		
		computeNextMoves(this.initialRcConfig);
	}

	private void computeNextMoves(RubiksCube rc) {
		List<Defined3DMove> path = new ArrayList<Defined3DMove>();
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI : starting to compute moves");
		
		/**
		 *  Implementing the seven step guide from http://www.chessandpoker.com/rubiks-cube-solution.html
		 */
		
		primeCube(rc, path);
		placeTopRowCorner(rc, path);
		placeTheEdgesOfTopLayer(rc, path);
		
		System.out.println("AI : final path (before optimization) is => " + path);
		
		// Finally we optimize moves in order to replace 3 PITCH with an UNPITCH for example
		optimizeMoves(path);
		
		System.out.println("AI : final path (after optimization) is => " + path);
	}

	/*
	 * Prerequisite algorithm
	 */
	private void primeCube(RubiksCube rc, List<Defined3DMove> path) {
		// Pre requisite : we must prime the cube
		// Identify the upper right cubie of the front facelet and especially the color on the top face
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		Facelet topFacelet = frontFaceUpperRightCubie.getTopFace();
		
		// We move the top middle row clockwise, until the central facelet has the same color as previous topFacelet 
		Cubie topFaceCenterCubie = rc.getCubie(2, 3, 2);
		int nbMove = 0;
		List<Defined3DMove> prereqPath = new ArrayList<Defined3DMove>();
		while (! topFaceCenterCubie.getTopFace().equals(topFacelet) && ++nbMove <= 3) {
			addLocalMove(rc, prereqPath, Move.ROLL, 2);
			topFaceCenterCubie = rc.getCubie(2, 3, 2);
		};
		
		if (nbMove == 4) {
			// We move the top center column, until the central facelet has the same color as previous topFacelet 
			nbMove = 0;
			prereqPath.clear();
			while (! topFaceCenterCubie.getTopFace().equals(topFacelet) && ++nbMove <= 3) {
				addLocalMove(rc, prereqPath, Move.PITCH, 2);
				topFaceCenterCubie = rc.getCubie(2, 3, 2);
			};
		}
		path.addAll(prereqPath);
		
		if (RubiksCube2D.DEBUG) {
			if (prereqPath.isEmpty())
				System.out.println("AI::prerequisite => already done !");
			else
				System.out.println("AI::prerequisite => " + path);
		}
	}

	/*
	 * Step One main algorithm
	 */
	private void placeTopRowCorner(RubiksCube rc, List<Defined3DMove> path) {
		if (matchesStepOneTopCross(rc)) {
			System.out.println("AI::stepOne => done !");
			return;
		}
		
		List<Defined3DMove> stepOnePath = new ArrayList<Defined3DMove>();

		// First we turn the top front row to the left so that our original corner cubie is at the upper left of the front face
		addLocalMove(rc, stepOnePath, Move.YAW, rc.getSize());
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepOne => Moving top front row to the left => " + stepOnePath);
		
		// Let's identify upper left cubie of front face
		Cubie frontFaceUpperLeftCubie = rc.getCubie(1, 3, 3);
		Facelet topColor   = frontFaceUpperLeftCubie.getTopFace();
		Facelet frontColor = frontFaceUpperLeftCubie.getFrontFace();
		
		// First we check if our target cubie is on the top row of front face but on the wrong side on the left
		Cubie topFaceUpperLeftCubie = rc.getCubie(1, 3, 1);
		if (matchesCornerCubieOnTwoFacelets(topFaceUpperLeftCubie, frontColor, topColor)) {
			addLocalMove(rc, stepOnePath, Move.ROLL, 1);
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the left => " + new Defined3DMove(Move.ROLL, 1));			
		}
		
		// Then we check if our target cubie is on the top row of front face but on the wrong side on the right
		Cubie topFaceUpperRightCubie = rc.getCubie(3, 3, 1);
		if (matchesCornerCubieOnTwoFacelets(topFaceUpperRightCubie, frontColor, topColor)) {
			addLocalMove(rc, stepOnePath, Move.UNPITCH, rc.getSize());
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the right => " + new Defined3DMove(Move.UNPITCH, rc.getSize()));			
		}
		
		// Our target cubie could already be at its good place (upper right of front face) but not facing the right way
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		if (matchesCornerCubieOnTwoFacelets(frontFaceUpperRightCubie, frontColor, topColor)) {
			// Let's find where is the topColor on this upperRight corner cubie of the front face
			List<Defined3DMove> stepOneTopRowMoves = null;
			
			// Then deduce step 1 algo when the match is front face : 4
			if (frontFaceUpperRightCubie.getFrontFace().equals(topColor)) {
				stepOneTopRowMoves = getStepOneAlgoFour();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepOne => about to apply step 1 algo 4 => " + stepOneTopRowMoves);
				
			}
			// Then deduce step 1 algo when the match is right face : 5
			else if (frontFaceUpperRightCubie.getRightFace().equals(topColor)) {
				stepOneTopRowMoves = getStepOneAlgoFive();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepOne => about to apply step 1 algo 5 => " + stepOneTopRowMoves);

			}
			
			// Finally apply algo
			addLocalMoves(rc, stepOnePath, stepOneTopRowMoves);
		}
		else {
		
			// Now we turn the front bottom row until we find at the bottom right corner cubie the 2 same facelets 
			Cubie frontFaceBottomRightCubie = rc.getCubie(3, 1, 3);
			int nbBottomRowMove = 0;
			while (! matchesCornerCubieOnTwoFacelets(frontFaceBottomRightCubie, topColor, frontColor) && ++nbBottomRowMove <= 3) {
				addLocalMove(rc, stepOnePath, Move.YAW, 1);
				frontFaceBottomRightCubie = rc.getCubie(3, 1, 3);
			}
			
			// We had no math => remove last three unuseful moves (YAW1)
			if (nbBottomRowMove == 4) {
				rc.move(new Defined3DMove(Move.YAW, 1));
				stepOnePath.remove(stepOnePath.size() - 1);
				stepOnePath.remove(stepOnePath.size() - 1);
				stepOnePath.remove(stepOnePath.size() - 1);
			}
			// We had a match !!
			else {
				if (RubiksCube2D.DEBUG && nbBottomRowMove > 0)
					System.out.println("AI::stepOne => Moving front bottom row to the left until we match the target corner cubie => " + (nbBottomRowMove) + " * YAW@1");
				
				// Let's find where is the topColor on this bottomRight corner cubie of the front face
				List<Defined3DMove> stepOneBottomRowMoves = null;
				
				// Then deduce step 1 algo when the match is right face : 1
				if (frontFaceBottomRightCubie.getRightFace().equals(topColor)) {
					stepOneBottomRowMoves = getStepOneAlgoOne();
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 1 => " + stepOneBottomRowMoves);
					
				}
				// Then deduce step 1 algo when the match is front face : 2
				else if (frontFaceBottomRightCubie.getFrontFace().equals(topColor)) {
					stepOneBottomRowMoves = getStepOneAlgoTwo();
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 2 => " + stepOneBottomRowMoves);
					
				}
				// Then deduce step 1 algo when the match is bottom face : 3
				else {
					stepOneBottomRowMoves = getStepOneAlgoThree();
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 3 => " + stepOneBottomRowMoves);
					
				}
				
				// Finally apply algo
				addLocalMoves(rc, stepOnePath, stepOneBottomRowMoves);
			}
		}
		
		path.addAll(stepOnePath);
		
		// Go on with this algo until step one is finished
		placeTopRowCorner(rc, path);
	}
	
	/*
	 * Step One utility methods
	 */
	private static boolean matchesStepOneTopCross(RubiksCube rc) {
		// Get top face center cubie
		Cubie topFaceCenterCubie = rc.getCubie(2, 3, 2);
		
		// Get upperRight corner Cubie of front face
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		
		// We are just after priming the cube so the top facelet already matches to the facelet of the center cubie of the top face
		// But we check whatever !
		if (! topFaceCenterCubie.getTopFace().equals(frontFaceUpperRightCubie.getTopFace()))
			return false;
		
		// Now we check the upperRight cubie of the right face
		Cubie rightFaceUpperRightCubie = rc.getCubie(3, 3, 1);
		if (! rightFaceUpperRightCubie.getTopFace().equals(frontFaceUpperRightCubie.getTopFace())
		 || ! rightFaceUpperRightCubie.getRightFace().equals(frontFaceUpperRightCubie.getRightFace()))
			return false;
		
		// Now we check the upperRight cubie of the back face
		Cubie backFaceUpperRightCubie = rc.getCubie(1, 3, 1);
		if (! backFaceUpperRightCubie.getTopFace().equals(frontFaceUpperRightCubie.getTopFace())
		 || ! backFaceUpperRightCubie.getBackFace().equals(rightFaceUpperRightCubie.getBackFace()))
			return false;
		
		// Now we check the upperRight cubie of the bottom face
		Cubie leftFaceUpperRightCubie = rc.getCubie(1, 3, 3);
		if (! leftFaceUpperRightCubie.getTopFace().equals(frontFaceUpperRightCubie.getTopFace())
		 || ! leftFaceUpperRightCubie.getLeftFace().equals(backFaceUpperRightCubie.getLeftFace()))
			return false;
		
		// Finally we check that front facelet of the upperRight cubie of the left face matches the front facelet of the upperRight cubie of the front face
		if (! leftFaceUpperRightCubie.getFrontFace().equals(frontFaceUpperRightCubie.getFrontFace()))
		 	return false;
		
		return true;
	}
	
	private static List<Defined3DMove> getStepOneAlgoOne() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	private static List<Defined3DMove> getStepOneAlgoTwo() {
		return Arrays.asList(new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	private static List<Defined3DMove> getStepOneAlgoThree() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}

	private static List<Defined3DMove> getStepOneAlgoFour() {
		return Arrays.asList(new Defined3DMove(Move.ROLL, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNROLL, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}

	private static List<Defined3DMove> getStepOneAlgoFive() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	/*
	 * Step Two main algorithm
	 */
	private void placeTheEdgesOfTopLayer(RubiksCube rc, List<Defined3DMove> path) {
		if (matchesStepTwo(rc)) {
			System.out.println("AI::stepTwo => done !");
			return;
		}
		
		List<Defined3DMove> stepTwoPath = new ArrayList<Defined3DMove>();
		
		// TODO
	}
	
	/*
	 * Step Two utility methods
	 */
	private static boolean matchesStepTwo(RubiksCube rc) {
		if (! matchesStepOneTopCross(rc))
			return false;
		
		// Get top face center cubie
		Cubie topFaceCenterCubie = rc.getCubie(2, 3, 2);
				
		// Check the edges
		Cubie frontFaceEdgeCubie       = rc.getCubie(2, 3, 3);
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		if (! frontFaceEdgeCubie.getTopFace().equals(topFaceCenterCubie.getTopFace())
		 ||	! frontFaceEdgeCubie.getFrontFace().equals(frontFaceUpperRightCubie.getFrontFace()))
			return false;
		
		Cubie rightFaceEdgeCubie       = rc.getCubie(3, 3, 2);
		Cubie rightFaceUpperRightCubie = rc.getCubie(3, 3, 1);
		if (! rightFaceEdgeCubie.getTopFace().equals(topFaceCenterCubie.getTopFace())
		 ||	! rightFaceEdgeCubie.getRightFace().equals(rightFaceUpperRightCubie.getRightFace()))
			return false;
		
		Cubie backFaceEdgeCubie        = rc.getCubie(2, 3, 1);
		Cubie backFaceUpperRightCubie  = rc.getCubie(1, 3, 1);
		if (! backFaceEdgeCubie.getTopFace().equals(topFaceCenterCubie.getTopFace())
		||	! backFaceEdgeCubie.getBackFace().equals(backFaceUpperRightCubie.getBackFace()))
			return false;
		
		Cubie leftFaceEdgeCubie        = rc.getCubie(1, 3, 2);
		Cubie leftFaceUpperRightCubie  = rc.getCubie(1, 3, 3);
		if (! leftFaceEdgeCubie.getTopFace().equals(topFaceCenterCubie.getTopFace())
		||	! leftFaceEdgeCubie.getLeftFace().equals(leftFaceUpperRightCubie.getLeftFace()))
			return false;
		
		return true;
	}
	
	private static List<Defined3DMove> getStepTwoAlgoOne() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	private static List<Defined3DMove> getStepTwoAlgoTwo() {
		return Arrays.asList(new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	private static List<Defined3DMove> getStepTwoAlgoThree() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}

	private static List<Defined3DMove> getStepTwoAlgoFour() {
		return Arrays.asList(new Defined3DMove(Move.ROLL, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNROLL, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}

	private static List<Defined3DMove> getStepTwoAlgoFive() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.UNPITCH, 3),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 3));
	}
	
	/*
	 * Global utility methods
	 */
	
	private static void addLocalMove(RubiksCube rc, List<Defined3DMove> localPath, Move move, int faceIndex) {
		Defined3DMove defined3DMove = new Defined3DMove(move, faceIndex); 
		rc.move(defined3DMove);
		localPath.add(defined3DMove);
	}

	private static void addLocalMoves(RubiksCube rc, List<Defined3DMove> localPath, List<Defined3DMove> moves) {
		if (moves != null) {
			rc.move(moves);
			localPath.addAll(moves);
		}
	}
	
	private static boolean matchesCornerCubieOnTwoFacelets(Cubie   cornerCubie,
														   Facelet firstTargetColor, 
														   Facelet secondTargetColor) {
		int count = 0;
		
		// retrieve the 3 facelets of the corner cubie
		Facelet firstColor  = ! cornerCubie.getFrontFace().equals(Facelet.NONE)
							? cornerCubie.getFrontFace()
						    : cornerCubie.getBackFace();
		Facelet secondColor = ! cornerCubie.getRightFace().equals(Facelet.NONE)
							? cornerCubie.getRightFace()
							: cornerCubie.getLeftFace();
		Facelet thirdColor  = ! cornerCubie.getBottomFace().equals(Facelet.NONE)
							? cornerCubie.getBottomFace()
							: cornerCubie.getTopFace();
		
		if (firstColor.equals(firstTargetColor) || firstColor.equals(secondTargetColor))
			count++;
		
		if (secondColor.equals(firstTargetColor) || secondColor.equals(secondTargetColor))
			count++;
		
		if (thirdColor.equals(firstTargetColor) || thirdColor.equals(secondTargetColor))
			count++;
		
		return count == 2;
	}
	
	private static void optimizeMoves(List<Defined3DMove> path) {
		Defined3DMove prevMove = null;
		int count = 1;
		for (ListIterator<Defined3DMove> it = path.listIterator(); it.hasNext(); ) {
			Defined3DMove move = it.next();
			
			/*
			 * Check if some move is repeated 3 times
			 */
			if (move.equals(prevMove))
				count++;
			else
				count = 1;
			
			if (count == 3) {
				// We remove current move
				it.remove();
				
				// Then we remove the 2 previous one
				it.previous();
				it.remove();
				it.previous();
				it.remove();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI : we should replace last three " + move + " with a " + Move.inverse(move.getMove()) + "@" + move.getFaceIndex());
				
				// Finally we replace with the inverse move
				move = new Defined3DMove(Move.inverse(move.getMove()), move.getFaceIndex());
				it.add(move);
				
				// We reinit the previous correctly then we reset the iterator at the current position
				prevMove = it.previous();
				it.next();
				
				count = 1;
			}
			
			/*
			 * Check if some move is followed by its opposite
			 */
			if (prevMove != null && move.equals(new Defined3DMove(Move.inverse(prevMove.getMove()), prevMove.getFaceIndex()))) {
				it.remove();
				it.previous();
				it.remove();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI : we should remove last " + move + " and its previous inverse " + prevMove);
			}
				
			prevMove = move;
		}
	}
}
