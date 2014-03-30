
public class Cubie implements Comparable<Cubie>, Cloneable {
	
	private final int rubiksCubeSize;
	private ThreeDimCoordinate coord;
	
	private Facelet frontFace;
   	private Facelet leftFace;
    private Facelet rightFace;
    private Facelet bottomFace;
    private Facelet topFace;
    private Facelet backFace;
    
    public ThreeDimCoordinate getCoordinates() {return coord;}
    
    public Facelet getFrontFace()  {return frontFace;}
	public Facelet getLeftFace()   {return leftFace;}
	public Facelet getRightFace()  {return rightFace;}
	public Facelet getBottomFace() {return bottomFace;}
	public Facelet getTopFace()    {return topFace;}
	public Facelet getBackFace()   {return backFace;}
    
	public Facelet getFace(Face face) {
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
		return Facelet.NONE;
	}
	
    public Cubie(int   rubiksCubeSize,
    			ThreeDimCoordinate coord,
    		    Facelet frontFace, 
    		    Facelet leftFace, 
    		    Facelet rightFace,
    		    Facelet bottomFace,
    		    Facelet topFace,
    		    Facelet backFace) {
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
		Facelet oldFrontFace = this.frontFace;

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
		Facelet oldFrontFace = this.frontFace;

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
		Facelet oldTopFace = this.topFace;

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
	public int compareTo(Cubie cube) {
		return this.getCoordinates().compareTo(cube.getCoordinates());
	}
	
	@Override
	public String toString() {
		return "Cube [coord=" + coord + ", frontFace=" + frontFace + ", leftFace=" + leftFace
				+ ", rightFace=" + rightFace + ", bottomFace=" + bottomFace
				+ ", topFace=" + topFace + ", backFace=" + backFace + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((backFace == null) ? 0 : backFace.hashCode());
		result = prime * result
				+ ((bottomFace == null) ? 0 : bottomFace.hashCode());
		result = prime * result + ((coord == null) ? 0 : coord.hashCode());
		result = prime * result
				+ ((frontFace == null) ? 0 : frontFace.hashCode());
		result = prime * result
				+ ((leftFace == null) ? 0 : leftFace.hashCode());
		result = prime * result
				+ ((rightFace == null) ? 0 : rightFace.hashCode());
		result = prime * result + rubiksCubeSize;
		result = prime * result + ((topFace == null) ? 0 : topFace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cubie other = (Cubie) obj;
		if (backFace != other.backFace)
			return false;
		if (bottomFace != other.bottomFace)
			return false;
		if (coord == null) {
			if (other.coord != null)
				return false;
		} else if (!coord.equals(other.coord))
			return false;
		if (frontFace != other.frontFace)
			return false;
		if (leftFace != other.leftFace)
			return false;
		if (rightFace != other.rightFace)
			return false;
		if (rubiksCubeSize != other.rubiksCubeSize)
			return false;
		if (topFace != other.topFace)
			return false;
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Cubie(this.rubiksCubeSize, 
						 (ThreeDimCoordinate) this.coord.clone(), 
						 this.frontFace, 
						 this.leftFace, 
						 this.rightFace, 
						 this.bottomFace, 
						 this.topFace, 
						 this.backFace);
	}
}
