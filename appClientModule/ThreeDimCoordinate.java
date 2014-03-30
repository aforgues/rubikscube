
public class ThreeDimCoordinate implements Comparable<ThreeDimCoordinate>, Cloneable {

	private int x;
	private int y;
	private int z;
	
	public ThreeDimCoordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	@Override
	public int compareTo(ThreeDimCoordinate coord) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
		
		if (coord == null)
			return AFTER;
		
		if (this.getX() == coord.getX() && this.getY() == coord.getY() && this.getZ() == coord.getZ())
			return EQUAL;
		
		if (this.getZ() == coord.getZ()) {
			if (this.getY() == coord.getY()) {
				return Integer.valueOf(this.getX()).compareTo(Integer.valueOf(coord.getX()));
			}
			else if (this.getY() < coord.getY()) {
				return BEFORE;
			}
			return AFTER;
		}
		else if (this.getZ() < coord.getZ()) {
			return BEFORE;
		}
		return AFTER;
	}
	
	public String simpleFormat() {
		return "X" + getX()
		     + "Y" + getY()
		     + "Z" + getZ();
	}
	
	@Override
	public String toString() {
		return "ThreeDimCoordinate [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		ThreeDimCoordinate other = (ThreeDimCoordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ThreeDimCoordinate(this.x, this.y, this.z);
	}
}
