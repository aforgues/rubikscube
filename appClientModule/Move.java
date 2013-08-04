
public enum Move {
	PITCH(0),
	DOUBLE_PITCH(1),
	UNPITCH(2),
	YAW(3),
	DOUBLE_YAW(4),
	UNYAW(5),
	ROLL(6),
	DOUBLE_ROLL(7),
	UNROLL(8);
	
	private int index;

	private Move(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}