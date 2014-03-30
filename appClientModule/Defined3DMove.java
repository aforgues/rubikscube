
public class Defined3DMove {

	private Move move;
	private int faceIndex;
	
	public Move getMove() {return this.move;}
	public int getFaceIndex() {return this.faceIndex;}
	
	public Defined3DMove(Move move, int faceIndex) {
		this.move = move;
		this.faceIndex = faceIndex;
	}
	
	public static Move getMove(int index) {
		for (Move move : Move.values()) {
			if (move.getIndex() == index)
				return move;
		}
		return null;
	}
	
	public String toString() {
		return move.name() + "@" + faceIndex;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + faceIndex;
		result = prime * result + ((move == null) ? 0 : move.hashCode());
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
		Defined3DMove other = (Defined3DMove) obj;
		if (faceIndex != other.faceIndex)
			return false;
		if (move != other.move)
			return false;
		return true;
	}
}
