public abstract class GenericRubiksCubeFormat {

	protected RubiksCube rubiksCube;
	
	public GenericRubiksCubeFormat(RubiksCube rubiksCube) {
		super();
		this.rubiksCube = rubiksCube;
	}

	/**
	 * Méthode principale d'affichage du cube sous toutes ses faces 
	 */
	public void show() {
		showBackFace();
		showTopFace();
		showLeftFace();
		showFrontFace();
		showRightFace();
		showBottomFace();
	}

	/**
	 * Cubes attendus pour l'affichage de la face arrière
	 * 
	 * X1Y1Z1 X2Y1Z1 X3Y1Z1 
	 *                    
	 * X1Y2Z1 X2Y2Z1 X3Y2Z1 
	 *                  
	 * X1Y3Z1 X2Y3Z1 X3Y3Z1 
	 */
	protected abstract void showBackFace();

	/**
	 * Cubes attendus pour l'affichage de la face haute
	 * 
	 * X1Y3Z1 X2Y3Z1 X3Y3Z1
	 *
	 * X1Y3Z2 X2Y3Z2 X3Y3Z2
	 *
	 * X1Y3Z3 X2Y3Z3 X3Y3Z3 
	 */
	protected abstract void showTopFace();

	/**
	 * Cubes attendus pour l'affichage de la face gauche
	 *  
	 * X1Y3Z1  X1Y3Z2  X1Y3Z3 
	 *                        
	 * X1Y2Z1  X1Y2Z2  X1Y2Z3 
	 *                        
	 * X1Y1Z1  X1Y1Z2  X1Y1Z3 
	 */
	protected abstract void showLeftFace();

	/**
	 * Cubes attendus pour l'affichage de la face avant
	 *
	 *	X1Y3Z3 X2Y3Z3 X3Y3Z3
	 *	
	 *	X1Y2Z3|X2Y2Z3 X3Y2Z3
	 *	
	 *	X1Y1Z3 X2Y1Z3 X3Y1Z3
	 */
	protected abstract void showFrontFace();

	/**
	 * Cubes attendus pour l'affichage de la face droite
	 * 
	 * X3Y3Z3 X3Y3Z2 X3Y3Z1 
	 *         
	 * X3Y2Z3 X3Y2Z2 X3Y2Z1 
	 *        
	 * X3Y1Z3 X3Y1Z2 X3Y1Z1 
	 */
	protected abstract void showRightFace();

	/**
	 * Cubes attendus pour l'affichage de la face basse
	 * 
	 * X1Y1Z3 X2Y1Z3 X3Y1Z3 
	 *                  
	 * X1Y1Z2 X2Y1Z2 X3Y1Z2 
	 *                  
	 * X1Y1Z1 X2Y1Z1 X3Y1Z1 
	 */
	protected abstract void showBottomFace();

}