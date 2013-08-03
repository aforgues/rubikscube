
public class Cube implements Comparable<Cube> {
	
	private final int rubiksCubeSize;
	private ThreeDimCoordinate coord;
	
	private Color frontFace;
   	private Color leftFace;
    private Color rightFace;
    private Color bottomFace;
    private Color topFace;
    private Color backFace;
    
    public ThreeDimCoordinate getCoordinates() {return coord;}
    
    public Color getFrontFace()  {return frontFace;}
	public Color getLeftFace()   {return leftFace;}
	public Color getRightFace()  {return rightFace;}
	public Color getBottomFace() {return bottomFace;}
	public Color getTopFace()    {return topFace;}
	public Color getBackFace()   {return backFace;}
    
	public Color getFace(Face face) {
		switch(face) {
			case FRONT:
				return getFrontFace();
			case LEFT:
				return getLeftFace();
			case RIGHT:
				return getRightFace();
			case BOTTOM:
				return getBottomFace();
			case TOP:
				return getTopFace();
			case BACK:
				return getBackFace();
		}
		return Color.NONE;
	}
	
    public Cube(int   rubiksCubeSize,
    			ThreeDimCoordinate coord,
    		    Color frontFace, 
    		    Color leftFace, 
    		    Color rightFace,
    		    Color bottomFace,
    		    Color topFace,
    		    Color backFace) {
    	this.rubiksCubeSize = rubiksCubeSize;
    	this.coord      = coord;
        this.frontFace  = frontFace;
        this.leftFace   = leftFace;
        this.rightFace  = rightFace;
        this.bottomFace = bottomFace;
        this.topFace    = topFace;
        this.backFace   = backFace;
    }
    
	public void pitch() {
		//System.out.println("Starting cube pitching (around X axis) : " + this.toString());
		
		// Changing coordinates
		int oldYCoord = this.getCoordinates().getY();
		this.coord.setY(this.coord.getZ());
		this.coord.setZ(this.rubiksCubeSize - oldYCoord + 1);
		
		// Circular changes of colors
		Color oldFrontFace = this.frontFace;

		// Bottom face becomes front face
		this.frontFace = this.bottomFace;
		
		// Back face becomes bottom face
		this.bottomFace = this.backFace;
		
		// Top face becomes back face
		this.backFace = this.topFace;
				
		// Front face becomes top face
		this.topFace = oldFrontFace;
		
		// Left and right faces doesn't change
		
		//System.out.println("Ending cube pitching (around X axis) : " + this.toString());
		
	}

	public void yaw() {
		//System.out.println("Starting cube yawing (around Y axis) : " + this.toString());
		
		// Changing coordinates
		int oldZCoord = this.getCoordinates().getZ();
		this.coord.setZ(this.coord.getX());
		this.coord.setX(this.rubiksCubeSize - oldZCoord + 1);
		
		// Circular changes of colors
		Color oldFrontFace = this.frontFace;

		// Right face becomes front face
		this.frontFace = this.rightFace;
		
		// Back face becomes right face
		this.rightFace = this.backFace;
		
		// Left face becomes back face
		this.backFace = this.leftFace;
				
		// Front face becomes left face
		this.leftFace = oldFrontFace;
		
		// Top and bottom faces doesn't change
		
		//System.out.println("Ending cube yawing (around Y axis) : " + this.toString());
	}
	
	public void roll() {
		//System.out.println("Starting cube rolling (around Z axis) : " + this.toString());
		
		// Changing coordinates
		int oldXCoord = this.getCoordinates().getX();
		this.coord.setX(this.coord.getY());
		this.coord.setY(this.rubiksCubeSize - oldXCoord + 1);
		
		// Circular changes of colors
		Color oldTopFace = this.topFace;

		// Left face becomes top face
		this.topFace = this.leftFace;
		
		// Bottom face becomes left face
		this.leftFace = this.bottomFace;
		
		// Right face becomes bottom face
		this.bottomFace = this.rightFace;
				
		// Top face becomes right face
		this.rightFace = oldTopFace;
		
		// Front and back faces doesn't change
		
		//System.out.println("Ending cube rolling (around Z axis) : " + this.toString());
	}
	
	@Override
	public int compareTo(Cube cube) {
		return this.getCoordinates().compareTo(cube.getCoordinates());
	}
	
	@Override
	public String toString() {
		return "Cube [coord=" + coord + ", frontFace=" + frontFace + ", leftFace=" + leftFace
				+ ", rightFace=" + rightFace + ", bottomFace=" + bottomFace
				+ ", topFace=" + topFace + ", backFace=" + backFace + "]";
	}
}
