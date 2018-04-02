package org.aforgues.rubikscube.ai;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.aforgues.rubikscube.core.Cubie;
import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Facelet;
import org.aforgues.rubikscube.core.Move;
import org.aforgues.rubikscube.core.RubiksCube;

import static org.aforgues.rubikscube.presentation.RubiksCube2D.DEBUG;


public class RubiksCubeAI {
	private RubiksCube initialRcConfig;
	private BlockingQueue<DefinedMove> solvingPath;
	private int solvingPathSize;
	
	public RubiksCubeAI(RubiksCube rc, boolean simulate) {
		if (! simulate) {
			try {
				this.initialRcConfig = (RubiksCube) rc.clone();
			} catch (CloneNotSupportedException e) {
				System.out.println("Unable to clone " + rc + " - " + e.getMessage());
			}
		}
		// do not clone for test purpose so that we can see the effect of moves on cube directly
		else { 
			this.initialRcConfig = rc;
		}
		
		if (! isAiAvalaible())
			System.out.println("AI : only available for 3x3 Rubik's Cube !");
	}
	
	private boolean isAiAvalaible() {
		return this.initialRcConfig != null && this.initialRcConfig.getSize() == 3;
	}
	
	public void computeArtificialIntelligence() {
		if (! isAiAvalaible()) {
			System.out.println("AI : only available for 3x3 Rubik's Cube !");
		}
		
		if (this.initialRcConfig.isSolved()) {
			System.out.println("AI : RubiksCube is already solved !!!");
		}

		this.solvingPath = computeNextMoves();
		this.solvingPathSize = solvingPath.size();
	}

	public DefinedMove getNextMove() {
		try {

			if (this.solvingPath != null && ! this.solvingPath.isEmpty()) {
				if (DEBUG) {
                    System.out.println("AI : Solving path - step " + (this.solvingPathSize - this.solvingPath.size() + 1) + " on " + this.solvingPathSize);
                }
                DefinedMove nextMove = this.solvingPath.take();
                if (DEBUG) {
                    System.out.println("AI : next move => " + nextMove);
                }
				return nextMove;
			}
			else {
                if (DEBUG) {
                    System.out.println("AI : RubiksCube is already solved");
                }
            }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void reset() {
		this.initialRcConfig = null;
		if (this.solvingPath != null)
			this.solvingPath.clear();
		this.solvingPath = null;
		this.solvingPathSize = 0;
	}

	private BlockingQueue<DefinedMove> computeNextMoves() {
		List<DefinedMove> path = new ArrayList<DefinedMove>();
		
		if (DEBUG)
			System.out.println("AI : starting to compute moves");
		
		long start = System.currentTimeMillis();
		
		/**
		 *  Implementing the seven step guide from http://www.chessandpoker.com/rubiks-cube-solution.html
		 */
		
		// Pre requisite
		primeCube(path);
		
		// Step One
		placeTopRowCorner(path);
		
		// Step Two
		placeTheEdgesOfTopLayer(path);
		
		// Step Three
		alignTheCenters(path);
		placeTheMiddleLayerEdges(path, 0);
		
		// Step four
		turnTheCubeOver(path);
		arrangeTheLastLayerCorners(path);
		
		// Step five
		finishTheLastLayerCorners(path, 0);
		
		// Step six
		finishTwoEdgesAndPrepareRemainingTwo(path);
		
		// Step seven
		solveTheRubiksCube(path);
		
		if (DEBUG)
			System.out.println("AI : complete path (before optimization) is => " + path);
		
		// Finally we optimize moves in order to replace 3 PITCH with an UNPITCH for example
		// FIXME: en testant manuellement l'algo, on identifie un bug qui fait qu'on n'arrive plus au bout de la résolution du Rubik's Cube
		//optimizeMoves(path);
		
		long duration = System.currentTimeMillis() - start;

		System.out.println("AI : Rubik's Cube solved in " + path.size() + " moves in " + duration + " ms");
		//System.out.println("AI : final  path is => " + path);

		BlockingQueue queue = new LinkedBlockingQueue<>();
		queue.addAll(path);
		return queue;
	}

	/*
	 * Prerequisite algorithm
	 */
	private void primeCube(List<DefinedMove> path) {
		RubiksCube rc = this.initialRcConfig;
		
		// Pre requisite : we must prime the cube
		// Identify the upper right cubie of the front facelet and especially the color on the top face
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		Facelet topFacelet = frontFaceUpperRightCubie.getTopFace();
		
		// We move the top middle row clockwise, until the central facelet has the same color as previous topFacelet 
		Cubie topFaceCenterCubie = rc.getCubie(2, 3, 2);
		int nbMove = 0;
		List<DefinedMove> prereqPath = new ArrayList<DefinedMove>();
		while (! topFaceCenterCubie.getTopFace().equals(topFacelet) && ++nbMove <= 3) {
			addLocalMove(prereqPath, Move.ROLL, 2);
			topFaceCenterCubie = rc.getCubie(2, 3, 2);
		}

        // We had no match => remove last three unuseful moves (ROLL2)
		if (nbMove == 4) {
            rc.move(new DefinedMove(Move.ROLL, 2));
            prereqPath.remove(prereqPath.size() - 1);
            prereqPath.remove(prereqPath.size() - 1);
            prereqPath.remove(prereqPath.size() - 1);

            // We move the top center column, until the central facelet has the same color as previous topFacelet
            nbMove = 0;
			while (! topFaceCenterCubie.getTopFace().equals(topFacelet) && ++nbMove <= 3) {
				addLocalMove(prereqPath, Move.PITCH, 2);
				topFaceCenterCubie = rc.getCubie(2, 3, 2);
			}
		}
		path.addAll(prereqPath);
		
		if (DEBUG) {
			if (prereqPath.isEmpty())
				System.out.println("AI::prerequisite => already done !");
			else
				System.out.println("AI::prerequisite => " + path);
		}
	}

	/*
	 * Step One main algorithm
	 */
	private void placeTopRowCorner(List<DefinedMove> path) {
		RubiksCube rc = this.initialRcConfig;
		
		if (matchesStepOneTopCross()) {
			if (DEBUG)
				System.out.println("AI::stepOne => done !");
			return;
		}
		
		List<DefinedMove> stepOnePath = new ArrayList<DefinedMove>();

		// First we turn the top front row to the left so that our original corner cubie is at the upper left of the front face
		addLocalMove(stepOnePath, Move.YAW, rc.getSize());
		
		if (DEBUG)
			System.out.println("AI::stepOne => Moving top front row to the left => " + stepOnePath);
		
		// Let's identify upper left cubie of front face
		Cubie frontFaceUpperLeftCubie = rc.getCubie(1, 3, 3);
		Facelet topColor   = frontFaceUpperLeftCubie.getTopFace();
		Facelet frontColor = frontFaceUpperLeftCubie.getFrontFace();
		
		// First we check if our target cubie is on the top row of front face but on the wrong side on the left
		Cubie topFaceUpperLeftCubie = rc.getCubie(1, 3, 1);
		if (matchesCubieOnTwoFacelets(topFaceUpperLeftCubie, frontColor, topColor)) {
			addLocalMove(stepOnePath, Move.ROLL, 1);
			
			if (DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the left => " + new DefinedMove(Move.ROLL, 1));			
		}
		
		// Then we check if our target cubie is on the top row of front face but on the wrong side on the right
		Cubie topFaceUpperRightCubie = rc.getCubie(3, 3, 1);
		if (matchesCubieOnTwoFacelets(topFaceUpperRightCubie, frontColor, topColor)) {
			addLocalMove(stepOnePath, Move.UNPITCH, rc.getSize());
			
			if (DEBUG)
				System.out.println("AI::stepOne => target cubie is on top face but wrong side on the right => " + new DefinedMove(Move.UNPITCH, rc.getSize()));			
		}
		
		// Our target cubie could already be at its good place (upper right of front face) but not facing the right way
		Cubie frontFaceUpperRightCubie = rc.getCubie(3, 3, 3);
		if (matchesCubieOnTwoFacelets(frontFaceUpperRightCubie, frontColor, topColor)) {
			// Let's find where is the topColor on this upperRight corner cubie of the front face
			List<DefinedMove> stepOneTopRowMoves = null;
			
			// Then deduce step 1 algo when the match is front face : 4
			if (frontFaceUpperRightCubie.getFrontFace().equals(topColor)) {
				stepOneTopRowMoves = getStepOneAlgoFour();
				
				if (DEBUG)
					System.out.println("AI::stepOne => about to apply step 1 algo 4 => " + stepOneTopRowMoves);
				
			}
			// Then deduce step 1 algo when the match is right face : 5
			else if (frontFaceUpperRightCubie.getRightFace().equals(topColor)) {
				stepOneTopRowMoves = getStepOneAlgoFive();
				
				if (DEBUG)
					System.out.println("AI::stepOne => about to apply step 1 algo 5 => " + stepOneTopRowMoves);

			}
			
			// Finally apply algo
			addLocalMoves(stepOnePath, stepOneTopRowMoves);
		}
		else {
		
			// Now we turn the front bottom row until we find at the bottom right corner cubie the 2 same facelets 
			Cubie frontFaceBottomRightCubie = rc.getCubie(3, 1, 3);
			int nbBottomRowMove = 0;
			while (! matchesCubieOnTwoFacelets(frontFaceBottomRightCubie, topColor, frontColor) && ++nbBottomRowMove <= 3) {
				addLocalMove(stepOnePath, Move.YAW, 1);
				frontFaceBottomRightCubie = rc.getCubie(3, 1, 3);
			}
			
			// We had no match => remove last three unuseful moves (YAW1)
			if (nbBottomRowMove == 4) {
				rc.move(new DefinedMove(Move.YAW, 1));
				stepOnePath.remove(stepOnePath.size() - 1);
				stepOnePath.remove(stepOnePath.size() - 1);
				stepOnePath.remove(stepOnePath.size() - 1);
			}
			// We had a match !!
			else {
				if (DEBUG) {
					if (nbBottomRowMove > 0)
						System.out.println("AI::stepOne => Moving front bottom row to the left until we match the target corner cubie => " + (nbBottomRowMove) + " * YAW@1");
				}
				
				// Let's find where is the topColor on this bottomRight corner cubie of the front face
				List<DefinedMove> stepOneBottomRowMoves = null;
				
				// Then deduce step 1 algo when the match is right face : 1
				if (frontFaceBottomRightCubie.getRightFace().equals(topColor)) {
					stepOneBottomRowMoves = getStepOneAlgoOne();
					
					if (DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 1 => " + stepOneBottomRowMoves);
					
				}
				// Then deduce step 1 algo when the match is front face : 2
				else if (frontFaceBottomRightCubie.getFrontFace().equals(topColor)) {
					stepOneBottomRowMoves = getStepOneAlgoTwo();
					
					if (DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 2 => " + stepOneBottomRowMoves);
					
				}
				// Then deduce step 1 algo when the match is bottom face : 3
				else {
					stepOneBottomRowMoves = getStepOneAlgoThree();
					
					if (DEBUG)
						System.out.println("AI::stepOne => about to apply step 1 algo 3 => " + stepOneBottomRowMoves);
					
				}
				
				// Finally apply algo
				addLocalMoves(stepOnePath, stepOneBottomRowMoves);
			}
		}
		
		path.addAll(stepOnePath);
		
		// Go on with this algo until step one is finished
		placeTopRowCorner(path);
	}
	
	/*
	 * Step One utility methods
	 */
	private boolean matchesStepOneTopCross() {
		RubiksCube rc = this.initialRcConfig;
		
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
	
	private static List<DefinedMove> getStepOneAlgoOne() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.PITCH, 3));
	}
	
	private static List<DefinedMove> getStepOneAlgoTwo() {
		return Arrays.asList(new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.PITCH, 3));
	}
	
	private static List<DefinedMove> getStepOneAlgoThree() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.PITCH, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.PITCH, 3));
	}

	private static List<DefinedMove> getStepOneAlgoFour() {
		return Arrays.asList(new DefinedMove(Move.ROLL, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNROLL, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.PITCH, 3));
	}

	private static List<DefinedMove> getStepOneAlgoFive() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.PITCH, 3),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.PITCH, 3));
	}
	
	/*
	 * Step Two main algorithm
	 */
	private void placeTheEdgesOfTopLayer(List<DefinedMove> path) {
		RubiksCube rc = this.initialRcConfig;
		
		if (matchesStepTwo()) {
			if (DEBUG)
				System.out.println("AI::stepTwo => done !");
			return;
		}
		
		List<DefinedMove> stepTwoPath = new ArrayList<DefinedMove>();
		
		// First we turn the top front row to the left for recursive purpose
		addLocalMove(stepTwoPath, Move.YAW, rc.getSize());
		
		if (DEBUG)
			System.out.println("AI::stepTwo => Moving top front row to the left => " + stepTwoPath);
		
		List<DefinedMove> stepTwoMoves = null;
		
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
				
				if (DEBUG)
					System.out.println("AI::stepTwo => target edge cubie is on top row of front face but on the wrong side => about to apply step 2 algo 5 => " + stepTwoMoves);				
			}
			else {
				if (DEBUG)
					System.out.println("AI::stepTwo => target edge cubie is already on top row of front face and also on the good side");
			}
			
		}
		else {
			// Now we will turn the middle row of front face to the left until we match target edge cubie on the right of this middle row or not
			Cubie frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3);
			int nbMiddleRowMove = 0;
			while (! matchesCubieOnTwoFacelets(frontFaceMiddleRightCubie, topColor, frontColor) && ++nbMiddleRowMove <= 3) {
				addLocalMove(stepTwoPath, Move.YAW, 2);
				frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3);
			}
			
			// We had no match => remove last three unusefull moves (YAW2) and check on bottom row of front face
			if (nbMiddleRowMove == 4) {
				rc.move(new DefinedMove(Move.YAW, 2));
				stepTwoPath.remove(stepTwoPath.size() - 1);
				stepTwoPath.remove(stepTwoPath.size() - 1);
				stepTwoPath.remove(stepTwoPath.size() - 1);

				// Before checking on bottom row, we deal with specific cases : if our target edge cubie is on the top row on the right, back or left face
				// We check for right edge cubie of top layer
				if (matchesCubieOnTwoFacelets(rc.getCubie(3, 3, 2), topColor, frontColor)) {
					addLocalMove(stepTwoPath, Move.ROLL, 2);
					addLocalMove(stepTwoPath, Move.YAW, 1);
					addLocalMove(stepTwoPath, Move.UNROLL, 2);
					
					if (DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of right face => moving the middle column of right face clockwise to match the bottom row => ROLL@2, YAW@1, UNROLL@2");				
				}
				// Then we check for back edge cubie of top layer
				else if (matchesCubieOnTwoFacelets(rc.getCubie(2, 3, 1), topColor, frontColor)) {
					addLocalMove(stepTwoPath, Move.PITCH, 2);
					addLocalMove(stepTwoPath, Move.YAW, 1);
					addLocalMove(stepTwoPath, Move.UNPITCH, 2);
					
					if (DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of back face => moving the middle column of back face clockwise to match the bottom row => PITCH@2, YAW@1, UNPITCH@2");
				}
				// Finally we check for left edge cubie of top layer
				else if (matchesCubieOnTwoFacelets(rc.getCubie(1, 3, 2), topColor, frontColor)) {
					addLocalMove(stepTwoPath, Move.UNROLL, 2);
					addLocalMove(stepTwoPath, Move.YAW, 1);
					addLocalMove(stepTwoPath, Move.ROLL, 2);
					
					if (DEBUG)
						System.out.println("AI::stepTwo => target edge cubie is on top row of left face => moving the middle column of left face counter-clockwise to match the bottom row => UNROLL@2, YAW@1, ROLL@2");
				}
				
				// Now we can check on bottom row of front face
				Cubie frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
				int nbBottomRowMove = 0;
				while (! matchesCubieOnTwoFacelets(frontFaceBottomCenterCubie, topColor, frontColor) && ++nbBottomRowMove <= 3) {
					addLocalMove(stepTwoPath, Move.YAW, 1);
					frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
				}
				
				// We had no match => remove last three unusefull moves (YAW1)
				if (nbBottomRowMove == 4) {
					rc.move(new DefinedMove(Move.YAW, 1));
					stepTwoPath.remove(stepTwoPath.size() - 1);
					stepTwoPath.remove(stepTwoPath.size() - 1);
					stepTwoPath.remove(stepTwoPath.size() - 1);
					
					if (DEBUG)
						System.out.println("AI::stepTwo => No match for target edge cubie on bottom row !! That should not be the case ;-)");
				}
				// We had a match !!
				else {
					if (DEBUG) {
						String suffix = "";
						if (nbBottomRowMove > 0)
							suffix = " by moving front bottom row to the left => " + (nbBottomRowMove) + " * YAW@1";
						System.out.println("AI::stepTwo => We matched the target center edge cubie of bottom row" + suffix);
					}
					
					// Let's find where is the topColor on this bottomCenter edge cubie of the front face
					
					// Then deduce step 2 algo when the match is bottom face : 1
					if (frontFaceBottomCenterCubie.getBottomFace().equals(topColor)) {
						stepTwoMoves = getStepTwoAlgoOne();
						
						if (DEBUG)
							System.out.println("AI::stepTwo => about to apply step 2 algo 1 => " + stepTwoMoves);
						
					}
					// Then deduce step 2 algo when the match is front face : 2
					else if (frontFaceBottomCenterCubie.getFrontFace().equals(topColor)) {
						stepTwoMoves = getStepTwoAlgoTwo();
						
						if (DEBUG)
							System.out.println("AI::stepTwo => about to apply step 2 algo 2 => " + stepTwoMoves);
					}
				}
			}
			// We had a match !!
			else {
				if (DEBUG) {
					String suffix = "";
					if (nbMiddleRowMove > 0)
						suffix = " by moving front middle row to the left => " + (nbMiddleRowMove) + " * YAW@2";
					System.out.println("AI::stepTwo => We matched the target right edge cubie of middle row" + suffix);
				}
				
				// Let's find where is the topColor on this middleRight edge cubie of the front face
				
				// Then deduce step 2 algo when the match is right face : 3
				if (frontFaceMiddleRightCubie.getRightFace().equals(topColor)) {
					stepTwoMoves = getStepTwoAlgoThree();
					
					if (DEBUG)
						System.out.println("AI::stepTwo => about to apply step 2 algo 3 => " + stepTwoMoves);
					
				}
				// Then deduce step 2 algo when the match is front face : 4
				else if (frontFaceMiddleRightCubie.getFrontFace().equals(topColor)) {
					stepTwoMoves = getStepTwoAlgoFour();
					
					if (DEBUG)
						System.out.println("AI::stepTwo => about to apply step 2 algo 4 => " + stepTwoMoves);
					
				}
			}
		}
		
		// Finally apply algo
		addLocalMoves(stepTwoPath, stepTwoMoves);
		
		if (DEBUG)
			System.out.println("AI::stepTwo => " + stepTwoPath);
		
		path.addAll(stepTwoPath);
		
		// Go on with this algo until step two is finished
		placeTheEdgesOfTopLayer(path);
	}
	
	/*
	 * Step Two utility methods
	 */
	private boolean matchesStepTwo() {
		RubiksCube rc = this.initialRcConfig;
		
		if (! matchesStepOneTopCross())
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
	
	private static List<DefinedMove> getStepTwoAlgoOne() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 2),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.PITCH, 2));
	}
	
	private static List<DefinedMove> getStepTwoAlgoTwo() {
		return Arrays.asList(new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.UNPITCH, 2),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.PITCH, 2));
	}
	
	private static List<DefinedMove> getStepTwoAlgoThree() {
		return Arrays.asList(new DefinedMove(Move.UNYAW, 2),
				             new DefinedMove(Move.ROLL, 3),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.UNROLL, 3));
	}

	private static List<DefinedMove> getStepTwoAlgoFour() {
		return Arrays.asList(new DefinedMove(Move.UNYAW, 2),
				             new DefinedMove(Move.UNROLL, 3),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.ROLL, 3));
	}

	private static List<DefinedMove> getStepTwoAlgoFive() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 2),
							 new DefinedMove(Move.YAW, 1),
							 new DefinedMove(Move.YAW, 1),
							 new DefinedMove(Move.PITCH, 2),
				             new DefinedMove(Move.YAW, 1),
				             new DefinedMove(Move.UNPITCH, 2),
				             new DefinedMove(Move.UNYAW, 1),
				             new DefinedMove(Move.PITCH, 2));
	}
	
	
	/*
	 * Step Three main algorithm
	 */	
	
	// Forming the Half-T
	private void alignTheCenters(List<DefinedMove> path) {
		if (matchesStepThreeAlignTheCenters()) {
			if (DEBUG)
				System.out.println("AI::stepThree::AlignTheCenters => done !");
			return;
		}
		
		List<DefinedMove> stepThreePath = new ArrayList<DefinedMove>();
		
		// First we turn the top front row to the left for recursive purpose
		addLocalMove(stepThreePath, Move.YAW, 2);
		
		if (DEBUG)
			System.out.println("AI::stepThree::AlignTheCenters => Moving middle front row to the left => " + stepThreePath);
		
		path.addAll(stepThreePath);
		
		// Go on with this algo until step two is finished
		alignTheCenters(path);
	}
	
	// Place the remaining edges
	private void placeTheMiddleLayerEdges(List<DefinedMove> path, int nbConsecutiveFaceWithoutFullTFound) {
		if (matchesStepThreePlaceTheMiddleLayerEdges()) {
			if (DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => done !");
			return;
		}
		
		RubiksCube rc = this.initialRcConfig;
		List<DefinedMove> stepThreePath = new ArrayList<DefinedMove>();

		// First we turn the top and middle front row to the left for recursive purpose
		addLocalMove(stepThreePath, Move.YAW, rc.getSize());
		addLocalMove(stepThreePath, Move.YAW, 2);
		
		if (DEBUG)
			System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => Moving top and middle front row to the left => " + stepThreePath);
		
		// Compute usefull cubie
		Cubie frontFaceUpperLeftCornerCubie  = rc.getCubie(1, 3, 3);
		Cubie frontFaceUpperRightCornerCubie = rc.getCubie(3, 3, 3);
		Cubie frontFaceMiddleLeftEdgeCubie   = rc.getCubie(1, 2, 3);
		Cubie frontFaceMiddleRightEdgeCubie  = rc.getCubie(3, 2, 3);
		
		// No Full-T found 4 consecutive times => apply left or right algo
		if (nbConsecutiveFaceWithoutFullTFound >= 4) {
			if (DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T found " + nbConsecutiveFaceWithoutFullTFound + " consecutive times");
			
			// apply left algo only if middle left edge cubie of front face is not already correct
			if (! frontFaceMiddleLeftEdgeCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace())
			 || ! frontFaceMiddleLeftEdgeCubie.getLeftFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())) {
				addLocalMoves(stepThreePath, getStepThreeAlgoLeft());
				
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => apply left algo");
			}
			// apply right algo only if middle right edge cubie of front face is not already correct
			else if (! frontFaceMiddleRightEdgeCubie.getFrontFace().equals(frontFaceUpperRightCornerCubie.getFrontFace())
			 || ! frontFaceMiddleRightEdgeCubie.getRightFace().equals(frontFaceUpperRightCornerCubie.getRightFace())) {
				addLocalMoves(stepThreePath, getStepThreeAlgoRight());
				
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => apply right algo");
			}
			else {
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No Full-T => all left and right middle edge cubie of front face are correct");
			}
			
			
		}
		
		// First check if the correct edge on the left side is in the proper position but is turned around
		List<DefinedMove> stepThreePreLeftMoves = null;
		if (frontFaceMiddleLeftEdgeCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())
		 && frontFaceMiddleLeftEdgeCubie.getLeftFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace())) {
			stepThreePreLeftMoves = getStepThreeAlgoLeft();
			if (DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => middle front left edge cubie is in the proper position but is turned around => apply step 3 algo left to force the proper cubie to the bottom => " + stepThreePreLeftMoves);
		}
		addLocalMoves(stepThreePath, stepThreePreLeftMoves);
		
		// First check if the correct edge on the left side is in the proper position but is turned around
		List<DefinedMove> stepThreePreRightMoves = null;
		if (frontFaceMiddleRightEdgeCubie.getFrontFace().equals(frontFaceUpperRightCornerCubie.getRightFace())
		 && frontFaceMiddleRightEdgeCubie.getRightFace().equals(frontFaceUpperRightCornerCubie.getFrontFace())) {
			stepThreePreRightMoves = getStepThreeAlgoRight();
			if (DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => middle front right edge cubie is in the proper position but is turned around => apply step 3 algo right to force the proper cubie to the bottom => " + stepThreePreRightMoves);
		}
		addLocalMoves(stepThreePath, stepThreePreRightMoves);
		
		// Trying to form the Full-T by turning the front bottom row to the left
		// Then check that we match one correct edge on the middle row (left or right cubie)
		Cubie frontFaceBottomCenterCubie     = rc.getCubie(2, 1, 3);
		int nbBottomRowMove = 0;
		while (! (frontFaceBottomCenterCubie.getFrontFace().equals(frontFaceUpperLeftCornerCubie.getFrontFace()) 
			   && (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace()) || frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperRightCornerCubie.getRightFace()))) && ++nbBottomRowMove <= 3) {
			addLocalMove(stepThreePath, Move.YAW, 1);
			frontFaceBottomCenterCubie = rc.getCubie(2, 1, 3);
		}
		
		// We had no match => remove last three unuseful moves (YAW1)
		if (nbBottomRowMove == 4) {
			rc.move(new DefinedMove(Move.YAW, 1));
			stepThreePath.remove(stepThreePath.size() - 1);
			stepThreePath.remove(stepThreePath.size() - 1);
			stepThreePath.remove(stepThreePath.size() - 1);

			if (DEBUG)
				System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => no match found on bottom row for front face color : " + frontFaceUpperLeftCornerCubie.getFrontFace());
			
			nbConsecutiveFaceWithoutFullTFound++;
		}
		// We had a match !!
		else {
			nbConsecutiveFaceWithoutFullTFound = 0;
			
			if (DEBUG) {
				if (nbBottomRowMove > 0)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => Moving front bottom row to the left until we match the target center edge cubie => " + (nbBottomRowMove) + " * YAW@1");
			}
			
			// Let's find where is the bottomColor : on left or right edge cubie ?
			List<DefinedMove> stepThreeMoves = null;
			
			// Then deduce step 3 algo when the match is on the left
			if (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperLeftCornerCubie.getLeftFace())) {
				stepThreeMoves = getStepThreeAlgoLeft();
				
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => about to apply step 3 algo left => " + stepThreeMoves);
				
			}
			// Then deduce step 3 algo when the match is on the right
			else if (frontFaceBottomCenterCubie.getBottomFace().equals(frontFaceUpperRightCornerCubie.getRightFace())) {
				stepThreeMoves = getStepThreeAlgoRight();
				
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => about to apply step 3 algo right => " + stepThreeMoves);
			}
			else {
				if (DEBUG)
					System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => No match on bottom color " + frontFaceBottomCenterCubie.getBottomFace() + " on left edge cubie : " + frontFaceUpperLeftCornerCubie.getLeftFace() + " or on right edge cubie : " + frontFaceUpperRightCornerCubie.getRightFace());
			}

			// Finally apply algo
			addLocalMoves(stepThreePath, stepThreeMoves);
		}
		
		if (DEBUG)
			System.out.println("AI::stepThree::PlaceTheMiddleLayerEdges => " + stepThreePath);
		
		path.addAll(stepThreePath);
		
		// Go on with this algo until step three is finished
		placeTheMiddleLayerEdges(path, nbConsecutiveFaceWithoutFullTFound);
	}

	/*
	 * Step three utility methods
	 */
	private boolean matchesStepThreeAlignTheCenters() {
		if (! matchesStepTwo())
			return false;

		RubiksCube rc = this.initialRcConfig;
		
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
	
	private boolean matchesStepThreePlaceTheMiddleLayerEdges() {
		if (! matchesStepThreeAlignTheCenters())
			return false;
				
		RubiksCube rc = this.initialRcConfig;
		
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
	
	private static List<DefinedMove> getStepThreeAlgoLeft() {
		return Arrays.asList(new DefinedMove(Move.UNYAW, 1),
	             			 new DefinedMove(Move.UNPITCH, 1),
	             			 new DefinedMove(Move.YAW, 1),
	             			 new DefinedMove(Move.PITCH, 1),
	             			 new DefinedMove(Move.YAW, 1),
	             			 new DefinedMove(Move.UNROLL, 3),
	             			 new DefinedMove(Move.UNYAW, 1),
	             			 new DefinedMove(Move.ROLL, 3));
	}

	private static List<DefinedMove> getStepThreeAlgoRight() {
		return Arrays.asList(new DefinedMove(Move.YAW, 1),
    			 			 new DefinedMove(Move.UNPITCH, 3),
    			 			 new DefinedMove(Move.UNYAW, 1),
    			 			 new DefinedMove(Move.PITCH, 3),
    			 			 new DefinedMove(Move.UNYAW, 1),
    			 			 new DefinedMove(Move.ROLL, 3),
    			 			 new DefinedMove(Move.YAW, 1),
    			 			 new DefinedMove(Move.UNROLL, 3));
	}
	
	/*
	 * Step Four main algorithm
	 */
	
	// Here we turn the entire cube upside down to prepare next step algorithm
	private void turnTheCubeOver(List<DefinedMove> path) {
		turnTheCube(path, Move.DOUBLE_ROLL);
		
		if (DEBUG)
			System.out.println("AI::stepFour::TurnTheCubeOver => done !");
	}
	
	// Here we turn the entire cube frontside back
	private void turnTheCubeAround(List<DefinedMove> path) {
		turnTheCube(path, Move.DOUBLE_YAW);
		
		if (DEBUG)
			System.out.println("AI::stepFour::TurnTheCubeAround => done !");
	}
	
	// Arrange the corners of last layer (on top now) without good facelets on good place
	private void arrangeTheLastLayerCorners(List<DefinedMove> path) {
		if (matchesStepFour()) {
			if (DEBUG)
				System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => done !");
			return;
		}
		
		RubiksCube rc = this.initialRcConfig;
		List<DefinedMove> stepFourPath = new ArrayList<DefinedMove>();
		
		// First we get the top color
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// First check if the two corner cubie are already side-by-side
		// Check front right corner cubie
		Cubie frontRightCornerCubie     = rc.getCubie(3, 3, 3);
		Cubie frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3); 
		
		// Then check front left corner cubie
		Cubie frontLeftCornerCubie      = rc.getCubie(1, 3, 3); 
		
		int nbTopRowMove = 0;
		while (! (matchesCubieOnTwoFacelets(frontRightCornerCubie, topColor, frontFaceMiddleRightCubie.getFrontFace())
			   && matchesCubieOnTwoFacelets(frontLeftCornerCubie,  topColor, frontFaceMiddleRightCubie.getFrontFace())) && ++nbTopRowMove <= 3) {
			addLocalMove(stepFourPath, Move.YAW, rc.getSize());
			frontRightCornerCubie     = rc.getCubie(3, 3, 3);
			frontLeftCornerCubie      = rc.getCubie(1, 3, 3);
			frontFaceMiddleRightCubie = rc.getCubie(3, 2, 3);
		}
		
		Facelet rightColor = frontFaceMiddleRightCubie.getRightFace();
		Cubie frontFaceMiddleLeftCubie  = rc.getCubie(1, 2, 3);
		Facelet leftColor = frontFaceMiddleLeftCubie.getLeftFace();
		Facelet frontColor = frontFaceMiddleRightCubie.getFrontFace();
		
		// We had no match => remove last three unusefull moves (YAW3)
		if (nbTopRowMove == 4) {
			rc.move(new DefinedMove(Move.YAW, rc.getSize()));
			stepFourPath.remove(stepFourPath.size() - 1);
			stepFourPath.remove(stepFourPath.size() - 1);
			stepFourPath.remove(stepFourPath.size() - 1);

			if (DEBUG)
				System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => no match found on top row for side-by-side front face color : " + frontColor);
			
			// Then we search where are our two target corner cubie
			frontLeftCornerCubie       = rc.getCubie(1, 3, 3);
			Cubie backRightCornerCubie = rc.getCubie(3, 3, 1);
			
			// We check for the left corner cubie on second position
			boolean isLeftOnSecondPosition = matchesCornerCubieOnFacelets(frontLeftCornerCubie, topColor, frontColor, leftColor);
			
			// We check for the right corner cubie on third position
			boolean isRightOnThirdPosition  = matchesCornerCubieOnFacelets(backRightCornerCubie, topColor, frontColor, rightColor);
			
			if (isLeftOnSecondPosition && isRightOnThirdPosition) {
				addLocalMoves(stepFourPath, getStepFourAlgoSwitchOneAndThree());
				
				if (DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => apply algo step 4 switch 1 and 3 : " + getStepFourAlgoSwitchOneAndThree());
			}
			else {
				// We turn the top front row to the left for recursive purpose
				addLocalMove(stepFourPath, Move.YAW, rc.getSize());
				
				if (DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => we turn the top front row to the left to find two corner cubie to switch => YAW@" + rc.getSize());
			}
		}
		// We had a match !!
		else {
			if (DEBUG) {
				String suffix = "";
				if (nbTopRowMove > 0)
					suffix = " by moving front top row to the left  => " + (nbTopRowMove) + " * YAW@" + rc.getSize();
				System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => we matched side-by-side front color " + frontColor + suffix);
			}
			
			// Check if the two corner cubie are already in proper sides
			if (matchesCornerCubieOnFacelets(frontRightCornerCubie, topColor, frontColor, rightColor)) {
				if (DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => left and right corner cubie are already in proper sides => go on !");
			}
			else {
				addLocalMoves(stepFourPath, getStepFourAlgoSwitchOneAndTwo());
				
				if (DEBUG)
					System.out.println("AI::stepFour::ArrangeTheLastLayerCorners => left and right corner cubie are not in proper sides => apply algo step 4 switch 1 and 2 : " + getStepFourAlgoSwitchOneAndTwo());
			}

			// Turn the entire cube around so that we can deal with back corner cubies
			turnTheCubeAround(stepFourPath);
		}
		
		path.addAll(stepFourPath);
		
		// Call step 4 method recursively
		arrangeTheLastLayerCorners(path);
	}
	
	/*
	 * Step Four utility methods
	 */
	private boolean matchesStepFour() {
		RubiksCube rc = this.initialRcConfig; 
		
		// First we turn the entire cubie upside down to match the previous steps
		for (int i = 1; i <= rc.getSize(); i++) {
			rc.move(new DefinedMove(Move.DOUBLE_ROLL, i));
		}
		
		if (! matchesStepThreePlaceTheMiddleLayerEdges())
			return false;
		
		// Then we move back the entire cubie upside down to go on with the next steps
		for (int i = 1; i <= rc.getSize(); i++) {
			rc.move(new DefinedMove(Move.DOUBLE_ROLL, i));
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

	private static List<DefinedMove> getStepFourAlgoSwitchOneAndTwo() {
		return Arrays.asList(new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.ROLL, 3),
				             new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.UNROLL, 3),
				             new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.YAW, 3));
	}
	
	private static List<DefinedMove> getStepFourAlgoSwitchOneAndThree() {
		return Arrays.asList(new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.ROLL, 3),
				             new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.UNROLL, 3),
				             new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.YAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.YAW, 3));
	}
	
	/*
	 * Step Five main algorithm
	 */
	private void finishTheLastLayerCorners(List<DefinedMove> path, int nbConsecutiveFaceWithoutConfigFound) {
		if (matchesStepFive()) {
			if (DEBUG)
				System.out.println("AI::stepFive => done !");
			return;
		}
		
		RubiksCube rc = this.initialRcConfig;
		List<DefinedMove> stepFivePath = new ArrayList<DefinedMove>();
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// We want to identify one of 3 specific configurations => get three corner cubie for that
		Cubie upperLeftFrontFaceCubie  = rc.getCubie(1, 3, 3);
		Cubie upperRightFrontFaceCubie = rc.getCubie(3, 3, 3);
		Cubie upperRightRightFaceCubie = rc.getCubie(3, 3, 1);
		
		if (nbConsecutiveFaceWithoutConfigFound == 4
		 || (upperLeftFrontFaceCubie.getFrontFace().equals(topColor) && upperRightFrontFaceCubie.getTopFace().equals(topColor)
		  || upperRightFrontFaceCubie.getRightFace().equals(topColor) && upperRightRightFaceCubie.getRightFace().equals(topColor)
		  || upperRightFrontFaceCubie.getTopFace().equals(topColor) && upperRightRightFaceCubie.getRightFace().equals(topColor))) {
			addLocalMoves(stepFivePath, getStepFiveAlgo());
			
			if (DEBUG) {
				String detail = "";
				if (nbConsecutiveFaceWithoutConfigFound == 4)
					detail = "no match for one of the three configurations after full (4) top row turn";
				else
					detail = "we matched one of the three target configurations";
				System.out.println("AI::stepFive => " + detail + " => apply algo step 5 : " + getStepFiveAlgo());
			}
			
			nbConsecutiveFaceWithoutConfigFound = 0;
		}
		else {
			// We turn the top front row to the left for recursive purpose
			addLocalMove(stepFivePath, Move.YAW, rc.getSize());
			nbConsecutiveFaceWithoutConfigFound++;
			
			if (DEBUG)
				System.out.println("AI::stepFive => no match for one of the three target configurations => turn top row to the left => YAW@" + rc.getSize());
		}
		
		path.addAll(stepFivePath);
		
		// Call step 5 method recursively
		finishTheLastLayerCorners(path, nbConsecutiveFaceWithoutConfigFound);
	}
	
	/*
	 * Step Five utility methods
	 */
	private boolean matchesStepFive() {
		if (! matchesStepFour())
			return false;
		
		// Here we have to check that the top face has its top cross properly positionned => we reuse step one check :-)
		if (! matchesStepOneTopCross())
			return false;
		
		return true;
	}
	
	private static List<DefinedMove> getStepFiveAlgo() {
		return Arrays.asList(new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.PITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 1),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNYAW, 3));
	}
	
	/*
	 * Step Six main algorithm
	 * Warning : in some cases, we won't be able to completely solve two edges, in this case we will have all 4 edges correctly positionned => step seven "H" pattern will resolve this :)
	 */
	private void finishTwoEdgesAndPrepareRemainingTwo(List<DefinedMove> path) {
		if (matchesStepSix()) {
			if (DEBUG)
				System.out.println("AI::stepSix => done !");
			return;
		}
		
		RubiksCube rc = this.initialRcConfig;
		List<DefinedMove> stepSixPath = new ArrayList<DefinedMove>();
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		Cubie frontFaceTopEdgeCubie = rc.getCubie(2, 3, 3);
		int nbEntireCubeMove = 0;
		while (! (matchesCubieOnTwoFacelets(frontFaceTopEdgeCubie, topColor, rc.getCubie(1, 3, 3).getFrontFace())) && ++nbEntireCubeMove <= 3) {
			turnTheCube(stepSixPath, Move.YAW);
			frontFaceTopEdgeCubie = rc.getCubie(2, 3, 3);
		}
		
		// We had no match => remove last three unusefull moves (YAW)
		if (nbEntireCubeMove == 4) {
			for (int i = 1; i <= rc.getSize(); i++) {
				rc.move(new DefinedMove(Move.YAW, i));
				stepSixPath.remove(stepSixPath.size() - 1);
				stepSixPath.remove(stepSixPath.size() - 1);
				stepSixPath.remove(stepSixPath.size() - 1);
			}

			// apply step 6 algo
			addLocalMoves(stepSixPath, getStepSixAlgo());
			
			if (DEBUG)
				System.out.println("AI::stepSix => no match found on front top edge cubie on any face => applying step 6 algo anyway : " + getStepSixAlgo());
		}
		// We had a match !!
		else {
			if (DEBUG) {
				String suffix = "";
				if (nbEntireCubeMove > 0)
					suffix = " by moving entire cube to the left  => " + (nbEntireCubeMove) + " * YAW";
				System.out.println("AI::stepSix => we matched on a front top edge cubie of Face " + rc.getCubie(1, 3, 3).getFrontFace() + suffix);
			}
			
			// apply step 6 algo
			addLocalMoves(stepSixPath, getStepSixAlgo());
			
			if (DEBUG)
				System.out.println("AI::stepSix => applying step 6 algo : " + getStepSixAlgo());
		}
			
		path.addAll(stepSixPath);
		
		// Call step 6 method recursively
		finishTwoEdgesAndPrepareRemainingTwo(path);
	}
	
	/*
	 * Step Six utility methods
	 */
	private boolean matchesStepSix() {
		if (! matchesStepFive())
			return false;
				
		// we check if the four edge cubies are correctly positionned (may need to be flipped)
		return countCorrectlyPositionnedTopEdgeCubie() == 4;
	}
	
	private int countCorrectlyPositionnedTopEdgeCubie() {
		int count = 0;
		
		RubiksCube rc = this.initialRcConfig;
		
		// Get the four top layer edge cubies
		Cubie frontTopFaceEdgeCubie  = rc.getCubie(2, 3, 3);
		Cubie rightTopFaceEdgeCubie  = rc.getCubie(3, 3, 2);
		Cubie backTopFaceEdgeCubie   = rc.getCubie(2, 3, 1);
		Cubie leftTopFaceEdgeCubie   = rc.getCubie(1, 3, 2);
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Check front top edge
		boolean isFrontEdgeCorrect = matchesCubieOnTwoFacelets(frontTopFaceEdgeCubie, topColor, rc.getCubie(1, 3, 3).getFrontFace());
		if (isFrontEdgeCorrect) {
			count++;
		}
		
		// Check right top edge
		boolean isRightEdgeCorrect = matchesCubieOnTwoFacelets(rightTopFaceEdgeCubie, topColor, rc.getCubie(3, 3, 3).getRightFace());
		if (isRightEdgeCorrect) {
			count++;
		}
		// Check back top edge
		boolean isBackEdgeCorrect  = matchesCubieOnTwoFacelets(backTopFaceEdgeCubie, topColor, rc.getCubie(3, 3, 1).getBackFace());
		if (isBackEdgeCorrect) {
			count++;
		}
		
		// Check left top edge
		boolean isLeftEdgeCorrect  = matchesCubieOnTwoFacelets(leftTopFaceEdgeCubie, topColor, rc.getCubie(1, 3, 1).getLeftFace());
		if (isLeftEdgeCorrect) {
			count++;
		}
		
		return count;
	}
	
	private static List<DefinedMove> getStepSixAlgo() {
		return Arrays.asList(new DefinedMove(Move.PITCH, 2),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 2),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.PITCH, 2),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNPITCH, 2));
	}
	
	/*
	 * Step Seven main algorithm
	 */
	private void solveTheRubiksCube(List<DefinedMove> path) {
		RubiksCube rc = this.initialRcConfig;
		
		if (rc.isSolved()) {
			if (DEBUG)
				System.out.println("AI::stepSeven => already done !");
			return;
		}
		
		List<DefinedMove> stepSevenPath = new ArrayList<DefinedMove>();
		
		// Special case where step 6 did not completely solve two of the four edge cubies => apply H pattern algo
		if (countCompletelySolvedTopEdgeCubie() == 0) {
			// apply step 6 algo
			addLocalMoves(stepSevenPath, getStepSevenAlgoHPattern());
			
			if (DEBUG)
				System.out.println("AI::stepSeven => all four edges flipped after step six => applying step 7 H pattern algo : " + getStepSevenAlgoHPattern());
		}
		
		// We will turn the entire cube to the left until we identify one of the two target configurations
		int nbEntireCubeMove = 0;
		while (! (matchesStepSevenDedmoreHPattern() || matchesStepSevenDedmoreFishPattern()) && ++nbEntireCubeMove <= 3) {
			turnTheCube(stepSevenPath, Move.YAW);
		}
		
		// We had no match => remove last three unusefull moves (YAW)
		if (nbEntireCubeMove == 4) {
			for (int i = 1; i <= rc.getSize(); i++) {
				rc.move(new DefinedMove(Move.YAW, i));
				stepSevenPath.remove(stepSevenPath.size() - 1);
				stepSevenPath.remove(stepSevenPath.size() - 1);
				stepSevenPath.remove(stepSevenPath.size() - 1);
			}

			if (DEBUG)
				System.out.println("AI::stepSeven => no match found for H or Fish pattern => should not happened => Fail !!");
		}
		// We had a match !!
		// Now we want to identify one of the 2 specific configurations named Dedmore "H" pattern and Dedmore "Fish" pattern
		else {
			if (DEBUG) {
				String suffix = "";
				if (nbEntireCubeMove > 0)
					suffix = " by moving entire cube to the left  => " + (nbEntireCubeMove) + " * YAW";
				System.out.println("AI::stepSeven => we matched one of the two pattern (H or Fish)" + suffix);
			}
			
			if (matchesStepSevenDedmoreHPattern()) {
				// apply step 7 algo H pattern
				addLocalMoves(stepSevenPath, getStepSevenAlgoHPattern());
				
				if (DEBUG)
					System.out.println("AI::stepSeven => applying step 7 H pattern algo : " + getStepSevenAlgoHPattern());
			}
			else {
				// apply step 7 algo Fish pattern
				addLocalMoves(stepSevenPath, getStepSevenAlgoFishPattern());
				
				if (DEBUG)
					System.out.println("AI::stepSeven => applying step 7 Fish pattern algo : " + getStepSevenAlgoFishPattern());
			}
		}
		
		path.addAll(stepSevenPath);
		
		if (DEBUG) {
            System.out.println("AI::stepSeven => done !");

            if (rc.isSolved()) {
                System.out.println("AI::stepSeven => checked that it is really done !");
            }
        }
	}
	
	/*
	 * Step Seven utility methods
	 */
	private int countCompletelySolvedTopEdgeCubie() {
		int count = 0;
		
		RubiksCube rc = this.initialRcConfig;
		
		// Get the four top layer edge cubies
		Cubie frontTopFaceEdgeCubie  = rc.getCubie(2, 3, 3);
		Cubie rightTopFaceEdgeCubie  = rc.getCubie(3, 3, 2);
		Cubie backTopFaceEdgeCubie   = rc.getCubie(2, 3, 1);
		Cubie leftTopFaceEdgeCubie   = rc.getCubie(1, 3, 2);
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Check front top edge
		boolean isFrontEdgeSolved = frontTopFaceEdgeCubie.getTopFace().equals(topColor) && frontTopFaceEdgeCubie.getFrontFace().equals(rc.getCubie(1, 3, 3).getFrontFace());
		if (isFrontEdgeSolved) {
			count++;
		}
		
		// Check right top edge
		boolean isRightEdgeSolved = rightTopFaceEdgeCubie.getTopFace().equals(topColor) && rightTopFaceEdgeCubie.getRightFace().equals(rc.getCubie(3, 3, 3).getRightFace());
		if (isRightEdgeSolved) {
			count++;
		}
		// Check back top edge
		boolean isBackEdgeSolved  = backTopFaceEdgeCubie.getTopFace().equals(topColor) && backTopFaceEdgeCubie.getBackFace().equals(rc.getCubie(3, 3, 1).getBackFace());
		if (isBackEdgeSolved) {
			count++;
		}
		
		// Check left top edge
		boolean isLeftEdgeSolved  = leftTopFaceEdgeCubie.getTopFace().equals(topColor) && leftTopFaceEdgeCubie.getLeftFace().equals(rc.getCubie(1, 3, 1).getLeftFace());
		if (isLeftEdgeSolved) {
			count++;
		}
		
		return count;
	}
	
	private boolean matchesStepSevenDedmoreHPattern() {
		// Check common Dedmore pattern
		if (! matchesStepSevenCommonDedmorePattern())
			return false;
		
		RubiksCube rc = this.initialRcConfig;
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Get the two last edge cubies that should be flipped for the first and correctly positionned for the second one
		Cubie c1 = rc.getCubie(1, 3, 2); // on left face
		if (c1.getTopFace().equals(topColor))
			return false;
		
		Cubie c2 = rc.getCubie(2, 3, 3); // on front face
		if (! c2.getTopFace().equals(topColor))
			return false;
		
		return true;
	}
	
	private boolean matchesStepSevenDedmoreFishPattern() {
		// Check common Dedmore pattern
		if (! matchesStepSevenCommonDedmorePattern())
			return false;
		
		RubiksCube rc = this.initialRcConfig;
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Get the two last edge cubies that should be flipped for the first and correctly positionned for the second one
		Cubie c1 = rc.getCubie(2, 3, 3); // on front face
		if (c1.getTopFace().equals(topColor))
			return false;
		
		Cubie c2 = rc.getCubie(1, 3, 2); // on left face
		if (! c2.getTopFace().equals(topColor))
			return false;
		
		return true;
	}

	private boolean matchesStepSevenCommonDedmorePattern() {
		// First we check that all cubies are correctly positionned until step 6
		if (! matchesStepSix())
			return false;
		
		RubiksCube rc = this.initialRcConfig;
		
		// Get top color from top face center cubie
		Facelet topColor = rc.getCubie(2, 3, 2).getTopFace();
		
		// Get the two common edge cubies that should be flipped for the first and correctly positionned for the second one
		Cubie c1 = rc.getCubie(3, 3, 2); // on right face
		if (c1.getTopFace().equals(topColor))
			return false;
		
		Cubie c2 = rc.getCubie(2, 3, 1); // on back face
		if (! c2.getTopFace().equals(topColor))
			return false;
		
		return true;
	}
	
	private static List<DefinedMove> getStepSevenAlgoHPattern() {
		return Arrays.asList(new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.YAW, 2),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.PITCH, 3),
				             new DefinedMove(Move.UNYAW, 2),
				             new DefinedMove(Move.UNYAW, 2),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNPITCH, 3),
				             new DefinedMove(Move.UNYAW, 2),
				             new DefinedMove(Move.PITCH, 3),
				             new DefinedMove(Move.UNYAW, 3),
				             new DefinedMove(Move.UNYAW, 3));
	}
	
	private static List<DefinedMove> getStepSevenAlgoFishPattern() {
		List<DefinedMove> moves = new ArrayList<DefinedMove>();
		moves.addAll(Arrays.asList(new DefinedMove(Move.UNROLL, 3), new DefinedMove(Move.PITCH, 1)));
		moves.addAll(getStepSevenAlgoHPattern());
		moves.addAll(Arrays.asList(new DefinedMove(Move.UNPITCH, 1), new DefinedMove(Move.ROLL, 3)));
		return moves;
	}
	
	/*
	 * Global utility methods
	 */

	private void addLocalMove(List<DefinedMove> localPath, Move move, int faceIndex) {
		DefinedMove definedMove = new DefinedMove(move, faceIndex); 
		this.initialRcConfig.move(definedMove);
		localPath.add(definedMove);
	}

	private void addLocalMoves(List<DefinedMove> localPath, List<DefinedMove> moves) {
		if (moves != null) {
			this.initialRcConfig.move(moves);
			localPath.addAll(moves);
		}
	}
	
	// Here we turn the entire cube considering given Move param
	private void turnTheCube(List<DefinedMove> path, Move move) {
		for (int i=1; i <= this.initialRcConfig.getSize(); i++) {
			addLocalMove(path, move, i);
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
	
	private static void optimizeMoves(List<DefinedMove> path) {
		DefinedMove prevMove = null;
		int count = 1;
		for (ListIterator<DefinedMove> it = path.listIterator(); it.hasNext(); ) {
			DefinedMove move = it.next();
			
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
				
				if (DEBUG)
					System.out.println("AI : we should replace last three " + move + " with a " + Move.inverse(move.getMove()) + "@" + move.getFaceIndex());
				
				// Finally we replace with the inverse move
				move = new DefinedMove(Move.inverse(move.getMove()), move.getFaceIndex());
				it.add(move);
				
				// We reinit the previous correctly then we reset the iterator at the current position
				prevMove = it.previous();
				it.next();
				
				count = 1;
			}
			
			/*
			 * Check if some move is followed by its opposite
			 */
			if (prevMove != null && move.equals(new DefinedMove(Move.inverse(prevMove.getMove()), prevMove.getFaceIndex()))) {
				it.remove();
				it.previous();
				it.remove();
				
				if (DEBUG)
					System.out.println("AI : we should remove last " + move + " and its previous inverse " + prevMove);
			}
				
			prevMove = move;
		}
	}
}
