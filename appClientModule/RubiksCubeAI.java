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
		
		// Pre requisite
		primeCube(rc, path);
		
		// Step One
		placeTopRowCorner(rc, path);
		
		// Step Two
		placeTheEdgesOfTopLayer(rc, path);
		
		// Step Three
		alignTheCenters(rc, path);
		placeTheMiddleLayerEdges(rc, path, 0);
		
		// Step four
		turnTheCubeOver(rc, path);
		arrangeTheLastLayerCorners(rc, path);
		
		// TODO : step five to seven
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI : final path (before optimization) is => " + path);
		
		// Finally we optimize moves in order to replace 3 PITCH with an UNPITCH for example
		optimizeMoves(path);
		
		System.out.println("AI : final path is => " + path);
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
		if (matchesCubieOnTwoFacelets(topFaceUpperLeftCubie, frontColor, topColor)) {
			addLocalMove(rc, stepOnePath, Move.ROLL, 1);
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the left => " + new Defined3DMove(Move.ROLL, 1));			
		}
		
		// Then we check if our target cubie is on the top row of front face but on the wrong side on the right
		Cubie topFaceUpperRightCubie = rc.getCubie(3, 3, 1);
		if (matchesCubieOnTwoFacelets(topFaceUpperRightCubie, frontColor, topColor)) {
			addLocalMove(rc, stepOnePath, Move.UNPITCH, rc.getSize());
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the right => " + new Defined3DMove(Move.UNPITCH, rc.getSize()));			
		}
		
		// Our target cubie could already be at its good place (upper right of front face) but not facing the right way
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		if (matchesCubieOnTwoFacelets(frontFaceUpperRightCubie, frontColor, topColor)) {
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
			while (! matchesCubieOnTwoFacelets(frontFaceBottomRightCubie, topColor, frontColor) && ++nbBottomRowMove <= 3) {
				addLocalMove(rc, stepOnePath, Move.YAW, 1);
				frontFaceBottomRightCubie = rc.getCubie(3, 1, 3);
			}
			
			// We had no match => remove last three unuseful moves (YAW1)
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
		
		// First we turn the top front row to the left for recursive purpose
		addLocalMove(rc, stepTwoPath, Move.YAW, rc.getSize());
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepTwo => Moving top front row to the left => " + stepTwoPath);
		
		List<Defined3DMove> stepTwoMoves = null;
		
		// Let's identify upper left cubie of front face
		Cubie frontFaceUpperLeftCubie = rc.getCubie(1, 3, 3);
		Facelet topColor   = frontFaceUpperLeftCubie.getTopFace();
		Facelet frontColor = frontFaceUpperLeftCubie.getFrontFace();
		
		// First we check if our target edge cubie is on the top row of front face but on the wrong side
		Cubie frontFaceMiddleEdgeCubie = rc.getCubie(2, 3, 3);
		if (matchesCubieOnTwoFacelets(frontFaceMiddleEdgeCubie, topColor, frontColor)) {
			// Let's find where is the topColor on this topCenter edge cubie of the front face
			// Then deduce step 2 algo when the match is front face : 5
			if (frontFaceMiddleEdgeCubie.getFrontFace().equals(topColor)) {
				stepTwoMoves = getStepTwoAlgoFive();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepTwo => target edge cubie is on top row of front face but on the wrong side => about to apply step 2 algo 5 => " + stepTwoMoves);				
			}
			else {
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepTwo => target edge cubie is already on top row of front face and also on the good side");
			}
			
		}
		else {
			// Now we will turn the middle row of front face to the left until we match target edge cubie on the right of this middle row or not
			Cubie frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3);
			int nbMiddleRowMove = 0;
			while (! matchesCubieOnTwoFacelets(frontFaceMiddleRightCubie, topColor, frontColor) && ++nbMiddleRowMove <= 3) {
				addLocalMove(rc, stepTwoPath, Move.YAW, 2);
				frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3);
			}
			
			// We had no match => remove last three unusefull moves (YAW2) and check on bottom row of front face
			if (nbMiddleRowMove == 4) {
				rc.move(new Defined3DMove(Move.YAW, 2));
				stepTwoPath.remove(stepTwoPath.size() - 1);
				stepTwoPath.remove(stepTwoPath.size() - 1);
				stepTwoPath.remove(stepTwoPath.size() - 1);
				
				// Before checking on bottom row, we deal with specific cases : if our target edge cubie is on the top row on the right, back or left face
				// We check for right edge cubie of top layer
				if (matchesCubieOnTwoFacelets(rc.getCubie(3, 3, 2), topColor, frontColor)) {
					addLocalMove(rc, stepTwoPath, Move.ROLL, 2);
					addLocalMove(rc, stepTwoPath, Move.YAW, 1);
					addLocalMove(rc, stepTwoPath, Move.UNROLL, 2);
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of right face => moving the middle column of right face clockwise to match the bottom row => ROLL@2, YAW@1, UNROLL@2");				
				}
				// Then we check for back edge cubie of top layer
				else if (matchesCubieOnTwoFacelets(rc.getCubie(2, 3, 1), topColor, frontColor)) {
					addLocalMove(rc, stepTwoPath, Move.PITCH, 2);
					addLocalMove(rc, stepTwoPath, Move.YAW, 1);
					addLocalMove(rc, stepTwoPath, Move.UNPITCH, 2);
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of back face => moving the middle column of back face clockwise to match the bottom row => PITCH@2, YAW@1, UNPITCH@2");
				}
				// Finally we check for left edge cubie of top layer
				else if (matchesCubieOnTwoFacelets(rc.getCubie(1, 3, 2), topColor, frontColor)) {
					addLocalMove(rc, stepTwoPath, Move.UNROLL, 2);
					addLocalMove(rc, stepTwoPath, Move.YAW, 1);
					addLocalMove(rc, stepTwoPath, Move.ROLL, 2);
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of left face => moving the middle column of left face counter-clockwise to match the bottom row => UNROLL@2, YAW@1, ROLL@2");
				}
				
				// Now we can check on bottom row of front face
				Cubie frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
				int nbBottomRowMove = 0;
				while (! matchesCubieOnTwoFacelets(frontFaceBottomCenterCubie, topColor, frontColor) && ++nbBottomRowMove <= 3) {
					addLocalMove(rc, stepTwoPath, Move.YAW, 1);
					frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
				}
				
				// We had no match => remove last three unusefull moves (YAW1)
				if (nbBottomRowMove == 4) {
					rc.move(new Defined3DMove(Move.YAW, 1));
					stepTwoPath.remove(stepTwoPath.size() - 1);
					stepTwoPath.remove(stepTwoPath.size() - 1);
					stepTwoPath.remove(stepTwoPath.size() - 1);
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => No match for target edge cubie on bottom row !! That should not be the case ;-)");
				}
				// We had a match !!
				else {
					if (RubiksCube2D.DEBUG) {
						String suffix = "";
						if (nbBottomRowMove > 0)
							suffix = " by moving front bottom row to the left => " + (nbBottomRowMove) + " * YAW@1";
						System.out.println("AI::stepTwo => We matched the target center edge cubie of bottom row" + suffix);
					}
					
					// Let's find where is the topColor on this bottomCenter edge cubie of the front face
					
					// Then deduce step 2 algo when the match is bottom face : 1
					if (frontFaceBottomCenterCubie.getBottomFace().equals(topColor)) {
						stepTwoMoves = getStepTwoAlgoOne();
						
						if (RubiksCube2D.DEBUG)
							System.out.println("AI::stepTwo => about to apply step 2 algo 1 => " + stepTwoMoves);
						
					}
					// Then deduce step 2 algo when the match is front face : 2
					else if (frontFaceBottomCenterCubie.getFrontFace().equals(topColor)) {
						stepTwoMoves = getStepTwoAlgoTwo();
						
						if (RubiksCube2D.DEBUG)
							System.out.println("AI::stepTwo => about to apply step 2 algo 2 => " + stepTwoMoves);
					}
				}
			}
			// We had a match !!
			else {
				if (RubiksCube2D.DEBUG) {
					String suffix = "";
					if (nbMiddleRowMove > 0)
						suffix = " by moving front middle row to the left => " + (nbMiddleRowMove) + " * YAW@2";
					System.out.println("AI::stepTwo => We matched the target right edge cubie of middle row" + suffix);
				}
				
				// Let's find where is the topColor on this middleRight edge cubie of the front face
				
				// Then deduce step 2 algo when the match is right face : 3
				if (frontFaceMiddleRightCubie.getRightFace().equals(topColor)) {
					stepTwoMoves = getStepTwoAlgoThree();
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => about to apply step 2 algo 3 => " + stepTwoMoves);
					
				}
				// Then deduce step 2 algo when the match is front face : 4
				else if (frontFaceMiddleRightCubie.getFrontFace().equals(topColor)) {
					stepTwoMoves = getStepTwoAlgoFour();
					
					if (RubiksCube2D.DEBUG)
						System.out.println("AI::stepTwo => about to apply step 2 algo 4 => " + stepTwoMoves);
					
				}
			}
		}
		
		// Finally apply algo
		addLocalMoves(rc, stepTwoPath, stepTwoMoves);
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepTwo => " + stepTwoPath);
		
		path.addAll(stepTwoPath);
		
		// Go on with this algo until step two is finished
		placeTheEdgesOfTopLayer(rc, path);
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
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 2),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.PITCH, 2));
	}
	
	private static List<Defined3DMove> getStepTwoAlgoTwo() {
		return Arrays.asList(new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.UNPITCH, 2),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 2));
	}
	
	private static List<Defined3DMove> getStepTwoAlgoThree() {
		return Arrays.asList(new Defined3DMove(Move.UNYAW, 2),
				             new Defined3DMove(Move.ROLL, 3),
				             new Defined3DMove(Move.YAW, 2),
				             new Defined3DMove(Move.UNROLL, 3));
	}

	private static List<Defined3DMove> getStepTwoAlgoFour() {
		return Arrays.asList(new Defined3DMove(Move.UNYAW, 2),
				             new Defined3DMove(Move.UNROLL, 3),
				             new Defined3DMove(Move.YAW, 2),
				             new Defined3DMove(Move.YAW, 2),
				             new Defined3DMove(Move.ROLL, 3));
	}

	private static List<Defined3DMove> getStepTwoAlgoFive() {
		return Arrays.asList(new Defined3DMove(Move.UNPITCH, 2),
							 new Defined3DMove(Move.YAW, 1),
							 new Defined3DMove(Move.YAW, 1),
							 new Defined3DMove(Move.PITCH, 2),
				             new Defined3DMove(Move.YAW, 1),
				             new Defined3DMove(Move.UNPITCH, 2),
				             new Defined3DMove(Move.UNYAW, 1),
				             new Defined3DMove(Move.PITCH, 2));
	}
	
	
	/*
	 * Step Three main algorithm
	 */	
	
	// Forming the Half-T
	private void alignTheCenters(RubiksCube rc, List<Defined3DMove> path) {
		if (matchesStepThreeAlignTheCenters(rc)) {
			System.out.println("AI::stepThree::AlignTheCenters => done !");
			return;
		}
		
		List<Defined3DMove> stepThreePath = new ArrayList<Defined3DMove>();
		
		// First we turn the top front row to the left for recursive purpose
		addLocalMove(rc, stepThreePath, Move.YAW, 2);
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepThree::AlignTheCenters => Moving middle front row to the left => " + stepThreePath);
		
		path.addAll(stepThreePath);
		
		// Go on with this algo until step two is finished
		alignTheCenters(rc, path);
	}
	
	// Place the remaining edges
	private void placeTheMiddleLayerEdges(RubiksCube rc, List<Defined3DMove> path, int nbConsecutiveFaceWithoutFullTFound) {
		if (matchesStepThreePlaceTheMiddleLayerEdges(rc)) {
			System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => done !");
			return;
		}
		
		List<Defined3DMove> stepThreePath = new ArrayList<Defined3DMove>();

		// First we turn the top and middle front row to the left for recursive purpose
		addLocalMove(rc, stepThreePath, Move.YAW, rc.getSize());
		addLocalMove(rc, stepThreePath, Move.YAW, 2);
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => Moving top and middle front row to the left => " + stepThreePath);
		
		// Compute usefull cubie
		Cubie frontFaceUpperLeftCornerCubie  = rc.getCubie(1, 3, 3);
		Cubie frontFaceUpperRightCornerCubie = rc.getCubie(3, 3, 3);
		Cubie frontFaceMiddleLeftEdgeCubie   = rc.getCubie(1, 2, 3);
		Cubie frontFaceMiddleRightEdgeCubie  = rc.getCubie(3, 2, 3);
		
		// No Full-T found 4 consecutive times => apply left or right algo
		if (nbConsecutiveFaceWithoutFullTFound >= 4) {
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T found " + nbConsecutiveFaceWithoutFullTFound + " consecutive times");
			
			// apply left algo only if middle left edge cubie of front face is not already correct
			if (! frontFaceMiddleLeftEdgeCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace())
			 || ! frontFaceMiddleLeftEdgeCubie.getLeftFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())) {
				addLocalMoves(rc, stepThreePath, getStepThreeAlgoLeft());
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => apply left algo");
			}
			// apply right algo only if middle right edge cubie of front face is not already correct
			else if (! frontFaceMiddleRightEdgeCubie.getFrontFace().equals(frontFaceUpperRightCornerCubie.getFrontFace())
			 || ! frontFaceMiddleRightEdgeCubie.getRightFace().equals(frontFaceUpperRightCornerCubie.getRightFace())) {
				addLocalMoves(rc, stepThreePath, getStepThreeAlgoRight());
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => apply right algo");
			}
			else {
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => all left and right middle edge cubie of front face are correct");
			}
			
			
		}
		
		// First check if the correct edge on the left side is in the proper position but is turned around
		List<Defined3DMove> stepThreePreLeftMoves = null;
		if (frontFaceMiddleLeftEdgeCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())
		 && frontFaceMiddleLeftEdgeCubie.getLeftFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace())) {
			stepThreePreLeftMoves = getStepThreeAlgoLeft();
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => middle front left edge cubie is in the proper position but is turned around => apply step 3 algo left to force the proper cubie to the bottom => " + stepThreePreLeftMoves);
		}
		addLocalMoves(rc, stepThreePath, stepThreePreLeftMoves);
		
		// First check if the correct edge on the left side is in the proper position but is turned around
		List<Defined3DMove> stepThreePreRightMoves = null;
		if (frontFaceMiddleRightEdgeCubie.getFrontFace().equals(frontFaceUpperRightCornerCubie.getRightFace())
		 && frontFaceMiddleRightEdgeCubie.getRightFace().equals(frontFaceUpperRightCornerCubie.getFrontFace())) {
			stepThreePreRightMoves = getStepThreeAlgoRight();
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => middle front right edge cubie is in the proper position but is turned around => apply step 3 algo right to force the proper cubie to the bottom => " + stepThreePreRightMoves);
		}
		addLocalMoves(rc, stepThreePath, stepThreePreRightMoves);
		
		// Trying to form the Full-T by turning the front bottom row to the left
		// Then check that we match one correct edge on the middle row (left or right cubie)
		Cubie frontFaceBottomCenterCubie     = rc.getCubie(2, 1, 3);
		int nbBottomRowMove = 0;
		while (! (frontFaceBottomCenterCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace()) 
			   && (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace()) || frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperRightCornerCubie.getRightFace()))) && ++nbBottomRowMove <= 3) {
			addLocalMove(rc, stepThreePath, Move.YAW, 1);
			frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
		}
		
		// We had no match => remove last three unuseful moves (YAW1)
		if (nbBottomRowMove == 4) {
			rc.move(new Defined3DMove(Move.YAW, 1));
			stepThreePath.remove(stepThreePath.size() - 1);
			stepThreePath.remove(stepThreePath.size() - 1);
			stepThreePath.remove(stepThreePath.size() - 1);
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => no match found on bottom row for front face color : " + frontFaceUpperLeftCornerCubie.getFrontFace());
			
			nbConsecutiveFaceWithoutFullTFound++;
		}
		// We had a match !!
		else {
			nbConsecutiveFaceWithoutFullTFound = 0;
			
			if (RubiksCube2D.DEBUG && nbBottomRowMove > 0)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => Moving front bottom row to the left until we match the target center edge cubie => " + (nbBottomRowMove) + " * YAW@1");
			
			// Let's find where is the bottomColor : on left or right edge cubie ?
			List<Defined3DMove> stepThreeMoves = null;
			
			// Then deduce step 3 algo when the match is on the left
			if (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())) {
				stepThreeMoves = getStepThreeAlgoLeft();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => about to apply step 3 algo left => " + stepThreeMoves);
				
			}
			// Then deduce step 3 algo when the match is on the right
			else if (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperRightCornerCubie.getRightFace())) {
				stepThreeMoves = getStepThreeAlgoRight();
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => about to apply step 3 algo right => " + stepThreeMoves);
			}
			else {
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No match on bottom color " + frontFaceBottomCenterCubie.getBottomFace() + " on left edge cubie : " + frontFaceUpperLeftCornerCubie.getLeftFace() + " or on right edge cubie : " + frontFaceUpperRightCornerCubie.getRightFace());
			}

			// Finally apply algo
			addLocalMoves(rc, stepThreePath, stepThreeMoves);
		}
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => " + stepThreePath);
		
		path.addAll(stepThreePath);
		
		// Go on with this algo until step three is finished
		placeTheMiddleLayerEdges(rc, path, nbConsecutiveFaceWithoutFullTFound);
	}

	/*
	 * Step three utility methods
	 */
	private static boolean matchesStepThreeAlignTheCenters(RubiksCube rc) {
		if (! matchesStepTwo(rc))
			return false;
				
		// Check each lateral face
		Cubie frontFaceEdgeCubie   = rc.getCubie(2, 3, 3);
		Cubie frontFaceCenterCubie = rc.getCubie(2, 2, 3);
		if (! frontFaceEdgeCubie.getFrontFace().equals(frontFaceCenterCubie.getFrontFace()))
			return false;
		
		Cubie rightFaceEdgeCubie   = rc.getCubie(3, 3, 2);
		Cubie rightFaceCenterCubie = rc.getCubie(3, 2, 2);
		if (! rightFaceEdgeCubie.getRightFace().equals(rightFaceCenterCubie.getRightFace()))
			return false;
		
		Cubie backFaceEdgeCubie    = rc.getCubie(2, 3, 1);
		Cubie backFaceCenterCubie  = rc.getCubie(2, 2, 1);
		if (! backFaceEdgeCubie.getBackFace().equals(backFaceCenterCubie.getBackFace()))
			return false;
		
		Cubie leftFaceEdgeCubie    = rc.getCubie(1, 3, 2);
		Cubie leftFaceCenterCubie  = rc.getCubie(1, 2, 2);
		if (! leftFaceEdgeCubie.getLeftFace().equals(leftFaceCenterCubie.getLeftFace()))
			return false;
		
		return true;
	}
	
	private static boolean matchesStepThreePlaceTheMiddleLayerEdges(RubiksCube rc) {
		if (! matchesStepThreeAlignTheCenters(rc))
			return false;
				
		// Check each lateral face
		Cubie frontFaceEdgeCubie        = rc.getCubie(2, 3, 3);
		Cubie frontFaceCenterLeftCubie  = rc.getCubie(1, 2, 3);
		Cubie frontFaceCenterRightCubie = rc.getCubie(3, 2, 3);
		if (! frontFaceEdgeCubie.getFrontFace().equals(frontFaceCenterLeftCubie.getFrontFace())
		 || ! frontFaceEdgeCubie.getFrontFace().equals(frontFaceCenterRightCubie.getFrontFace()))
			return false;
		
		Cubie rightFaceEdgeCubie        = rc.getCubie(3, 3, 2);
		Cubie rightFaceCenterLeftCubie  = rc.getCubie(3, 2, 3);
		Cubie rightFaceCenterRightCubie = rc.getCubie(3, 2, 1);
		if (! rightFaceEdgeCubie.getRightFace().equals(rightFaceCenterLeftCubie.getRightFace())
		 || ! rightFaceEdgeCubie.getRightFace().equals(rightFaceCenterRightCubie.getRightFace()))
			return false;
		
		Cubie backFaceEdgeCubie         = rc.getCubie(2, 3, 1);
		Cubie backFaceCenterLeftCubie   = rc.getCubie(3, 2, 1);
		Cubie backFaceCenterRightCubie  = rc.getCubie(1, 2, 1);
		if (! backFaceEdgeCubie.getBackFace().equals(backFaceCenterLeftCubie.getBackFace())
		 || ! backFaceEdgeCubie.getBackFace().equals(backFaceCenterRightCubie.getBackFace()))
			return false;
		
		Cubie leftFaceEdgeCubie         = rc.getCubie(1, 3, 2);
		Cubie leftFaceCenterLeftCubie   = rc.getCubie(1, 2, 1);
		Cubie leftFaceCenterRightCubie  = rc.getCubie(1, 2, 3);
		if (! leftFaceEdgeCubie.getLeftFace().equals(leftFaceCenterLeftCubie.getLeftFace())
		 || ! leftFaceEdgeCubie.getLeftFace().equals(leftFaceCenterRightCubie.getLeftFace()))
			return false;
		
		return true;
	}
	
	private static List<Defined3DMove> getStepThreeAlgoLeft() {
		return Arrays.asList(new Defined3DMove(Move.UNYAW, 1),
	             			 new Defined3DMove(Move.UNPITCH, 1),
	             			 new Defined3DMove(Move.YAW, 1),
	             			 new Defined3DMove(Move.PITCH, 1),
	             			 new Defined3DMove(Move.YAW, 1),
	             			 new Defined3DMove(Move.UNROLL, 3),
	             			 new Defined3DMove(Move.UNYAW, 1),
	             			 new Defined3DMove(Move.ROLL, 3));
	}

	private static List<Defined3DMove> getStepThreeAlgoRight() {
		return Arrays.asList(new Defined3DMove(Move.YAW, 1),
    			 			 new Defined3DMove(Move.UNPITCH, 3),
    			 			 new Defined3DMove(Move.UNYAW, 1),
    			 			 new Defined3DMove(Move.PITCH, 3),
    			 			 new Defined3DMove(Move.UNYAW, 1),
    			 			 new Defined3DMove(Move.ROLL, 3),
    			 			 new Defined3DMove(Move.YAW, 1),
    			 			 new Defined3DMove(Move.UNROLL, 3));
	}
	
	/*
	 * Step Four main algorithm
	 */
	
	// Here we turn the entire cube upside down to prepare next step algorithm
	private void turnTheCubeOver(RubiksCube rc, List<Defined3DMove> path) {
		for (int i=1; i <= rc.getSize(); i++) {
			addLocalMove(rc, path, Move.DOUBLE_ROLL, i);
		}
		
		if (RubiksCube2D.DEBUG)
			System.out.println("AI::stepFour::TurnTheCubeOver => done !");
	}
	
	// Arrange the corners of last layer (on top now) without good facelets on good place
	private void arrangeTheLastLayerCorners(RubiksCube rc, List<Defined3DMove> path) {
		if (matchesStepFour(rc)) {
			System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => done !");
			return;
		}
		
		List<Defined3DMove> stepFourPath = new ArrayList<Defined3DMove>();
		
		// First we get the top color
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// First check if the two corner cubie are already side-by-side
		// Check front right corner cubie
		Cubie frontRightCornerCubie     = rc.getCubie(3, 3, 3);
		Cubie frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3); 
		Facelet frontColor = frontFaceMiddleRightCubie.getFrontFace();
		Facelet rightColor = frontFaceMiddleRightCubie.getRightFace();
		
		// Then check front left corner cubie
		Cubie frontLeftCornerCubie      = rc.getCubie(1, 3, 3);
		Cubie frontFaceMiddleLeftCubie  = rc.getCubie(1, 2, 3); 
		Facelet leftColor = frontFaceMiddleLeftCubie.getLeftFace();
		
		int nbTopRowMove = 0;
		while (! (matchesCornerCubieOnFacelets(frontRightCornerCubie, topColor, frontColor, rightColor)
			   && matchesCornerCubieOnFacelets(frontLeftCornerCubie, topColor, frontColor, leftColor)) && ++nbTopRowMove <= 3) {
			addLocalMove(rc, stepFourPath, Move.YAW, rc.getSize());
			frontRightCornerCubie = rc.getCubie(3, 3, 3);
			frontLeftCornerCubie  = rc.getCubie(1, 3, 3);
		}
		
		// We had no match => remove last three unusefull moves (YAW3)
		if (nbTopRowMove == 4) {
			rc.move(new Defined3DMove(Move.YAW, rc.getSize()));
			stepFourPath.remove(stepFourPath.size() - 1);
			stepFourPath.remove(stepFourPath.size() - 1);
			stepFourPath.remove(stepFourPath.size() - 1);
			
			if (RubiksCube2D.DEBUG)
				System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => no match found on top row for side-by-side front face color : " + frontColor);
			
			// Then we search where are our two target corner cubie
			Cubie backRightCornerCubie = rc.getCubie(3, 3, 1);
			Cubie backLeftCornerCubie  = rc.getCubie(1, 3, 1);
			
			// We begin with the left corner cubie
			boolean isLeftOnFirstPosition  = matchesCornerCubieOnFacelets(frontRightCornerCubie, topColor, frontColor, leftColor);
			boolean isLeftOnSecondPosition = matchesCornerCubieOnFacelets(frontLeftCornerCubie, topColor, frontColor, leftColor);
			boolean isLeftOnThirdPosition  = matchesCornerCubieOnFacelets(backRightCornerCubie, topColor, frontColor, leftColor);
			boolean isLeftOnFourthPosition = matchesCornerCubieOnFacelets(backLeftCornerCubie, topColor, frontColor, leftColor);
			
			// We follow with the right corner cubie
			boolean isRightOnFirstPosition  = matchesCornerCubieOnFacelets(frontRightCornerCubie, topColor, frontColor, rightColor);
			boolean isRightOnSecondPosition = matchesCornerCubieOnFacelets(frontLeftCornerCubie, topColor, frontColor, rightColor);
			boolean isRightOnThirdPosition  = matchesCornerCubieOnFacelets(backRightCornerCubie, topColor, frontColor, rightColor);
			boolean isRightOnFourthPosition = matchesCornerCubieOnFacelets(backLeftCornerCubie, topColor, frontColor, rightColor);
			
			if (isLeftOnFirstPosition && isRightOnSecondPosition) {
				addLocalMoves(rc, stepFourPath, getStepFourAlgoSwitchOneAndTwo());
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => apply algo step 4 switch 1 and 2 : " + getStepFourAlgoSwitchOneAndTwo());
			}
			else if (isLeftOnSecondPosition && isRightOnThirdPosition) {
				addLocalMoves(rc, stepFourPath, getStepFourAlgoSwitchOneAndThree());
				
				if (RubiksCube2D.DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => apply algo step 4 switch 1 and 3 : " + getStepFourAlgoSwitchOneAndThree());
			}
			
			// TODO Finish step four algo
			
		}
		// We had a match !!
		else {
			if (RubiksCube2D.DEBUG) {
				String suffix = "";
				if (nbTopRowMove > 0)
					suffix = " by moving front top row to the left  => " + (nbTopRowMove) + " * YAW@" + rc.getSize();
				System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => we matched side-by-side front color " + frontColor + suffix);
			}
		}
		
		// FIXME : add recursive algo ?
	}
	
	/*
	 * Step Four utility methods
	 */
	private static boolean matchesStepFour(RubiksCube rc) {
		// First we turn the entire cubie upside down to match the previous steps
		for (int i = 1; i <= rc.getSize(); i++) {
			rc.move(new Defined3DMove(Move.DOUBLE_ROLL, i));
		}
		
		if (! matchesStepThreePlaceTheMiddleLayerEdges(rc))
			return false;
		
		// Then we move back the entire cubie upside down to go on with the next steps
		for (int i = 1; i <= rc.getSize(); i++) {
			rc.move(new Defined3DMove(Move.DOUBLE_ROLL, i));
		}
		
		// Now we check each corner cubie of top face
		// First we get the top color
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Then check front right corner cubie
		Cubie frontRightCornerCubie     = rc.getCubie(3, 3, 3);
		Cubie frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3); 
		if (! matchesCornerCubieOnFacelets(frontRightCornerCubie, topColor, frontFaceMiddleRightCubie.getFrontFace(), frontFaceMiddleRightCubie.getRightFace()))
			return false;
		
		// Then check back right corner cubie
		Cubie backRightCornerCubie      = rc.getCubie(3, 3, 1);
		Cubie backFaceMiddleRightCubie  = rc.getCubie(3, 2, 1); 
		if (! matchesCornerCubieOnFacelets(backRightCornerCubie, topColor, backFaceMiddleRightCubie.getBackFace(), backFaceMiddleRightCubie.getRightFace()))
			return false;
		
		// Then check back left corner cubie
		Cubie backLeftCornerCubie       = rc.getCubie(1, 3, 1);
		Cubie backFaceMiddleLeftCubie   = rc.getCubie(1, 2, 1); 
		if (! matchesCornerCubieOnFacelets(backLeftCornerCubie, topColor, backFaceMiddleLeftCubie.getBackFace(), backFaceMiddleLeftCubie.getLeftFace()))
			return false;
		
		// Then check front left corner cubie
		Cubie frontLeftCornerCubie      = rc.getCubie(1, 3, 3);
		Cubie frontFaceMiddleLeftCubie  = rc.getCubie(1, 2, 3); 
		if (! matchesCornerCubieOnFacelets(frontLeftCornerCubie, topColor, frontFaceMiddleLeftCubie.getFrontFace(), frontFaceMiddleLeftCubie.getLeftFace()))
			return false;
		
		return true;
	}	

	private static List<Defined3DMove> getStepFourAlgoSwitchOneAndTwo() {
		return Arrays.asList(new Defined3DMove(Move.PITCH, 1),
				             new Defined3DMove(Move.UNYAW, 3),
				             new Defined3DMove(Move.UNPITCH, 1),
				             new Defined3DMove(Move.ROLL, 3),
				             new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.UNROLL, 3),
				             new Defined3DMove(Move.PITCH, 1),
				             new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.UNPITCH, 1),
				             new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.YAW, 3));
	}
	
	private static List<Defined3DMove> getStepFourAlgoSwitchOneAndThree() {
		return Arrays.asList(new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.PITCH, 1),
				             new Defined3DMove(Move.UNYAW, 3),
				             new Defined3DMove(Move.UNPITCH, 1),
				             new Defined3DMove(Move.ROLL, 3),
				             new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.UNROLL, 3),
				             new Defined3DMove(Move.PITCH, 1),
				             new Defined3DMove(Move.YAW, 3),
				             new Defined3DMove(Move.UNPITCH, 1),
				             new Defined3DMove(Move.YAW, 3));
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
	
	// This method can be used either on edge cubie or on corner cubie
	private static boolean matchesCubieOnTwoFacelets(Cubie   cubie,
											  	     Facelet firstTargetColor, 
												     Facelet secondTargetColor) {
		return getCubieOnFaceletsMatchingCount(cubie, firstTargetColor, secondTargetColor, null) == 2;
	}
	
	private static boolean matchesCornerCubieOnFacelets(Cubie   cubie,
											  	 		Facelet firstTargetColor, 
														Facelet secondTargetColor,
														Facelet thirdTargetColor) {
		return getCubieOnFaceletsMatchingCount(cubie, firstTargetColor, secondTargetColor, thirdTargetColor) == 3;
	}
	
	private static int getCubieOnFaceletsMatchingCount(Cubie   cubie,
	  	     						  	 		  	   Facelet firstTargetColor, 
	  	     						  	 		  	   Facelet secondTargetColor,
	  	     						  	 		  	   Facelet thirdTargetColor) {
		int count = 0;
		
		// retrieve the 3 facelets of the cubie (we will get 3 facelets in case of corner cubie and two in case of edge cubie and the last one will be NONE and won't match)
		Facelet firstColor  = ! cubie.getFrontFace().equals(Facelet.NONE)
							? cubie.getFrontFace()
							: cubie.getBackFace();
		Facelet secondColor = ! cubie.getRightFace().equals(Facelet.NONE)
							? cubie.getRightFace()
							: cubie.getLeftFace();
		Facelet thirdColor  = ! cubie.getBottomFace().equals(Facelet.NONE)
							? cubie.getBottomFace()
							: cubie.getTopFace();
		
		if (firstColor.equals(firstTargetColor) || firstColor.equals(secondTargetColor) || firstColor.equals(thirdTargetColor))
			count++;
		
		if (secondColor.equals(firstTargetColor) || secondColor.equals(secondTargetColor) || secondColor.equals(thirdTargetColor))
			count++;
		
		if (thirdColor.equals(firstTargetColor) || thirdColor.equals(secondTargetColor) || thirdColor.equals(thirdTargetColor))
			count++;
		
		return count;
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
