
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
}
