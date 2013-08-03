
public class ThreeDimCoordinate implements Comparable<ThreeDimCoordinate> {

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
}
