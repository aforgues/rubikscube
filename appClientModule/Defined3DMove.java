
public class Defined3DMove {

	private static final int NO_INDEX_FOR_FACE_MOVE = -1;
	
	private Move move;
	private int faceIndex;
	
	public Move getMove() {return this.move;}
	public int getFaceIndex() {return this.faceIndex;}
	public boolean isFaceMove() {return this.faceIndex == NO_INDEX_FOR_FACE_MOVE;}
	
	public void setMove(Move move) {this.move = move;}
	public void setFaceIndex(int faceIndex) {this.faceIndex = faceIndex;}
	
	public Defined3DMove() {}

	public Defined3DMove(Move move, int faceIndex) {
		this.move = move;
		this.faceIndex = faceIndex;
	}
	
	public Defined3DMove(Move move) {
		this(move, NO_INDEX_FOR_FACE_MOVE);
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
