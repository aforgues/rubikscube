package org.aforgues.rubikscube.core;

public enum Move {
	/**
	 *  DOWN_TO_UP
	 */
	PITCH(0),
	
	/**
	 * @see {@link PITCH}
	 */
	DOUBLE_PITCH(1),
	
	/**
	 *  UP_TO_DOWN
	 */
	UNPITCH(2),         
	
	/**
	 *  RIGHT_TO_LEFT
	 */
	YAW(3),
	
	/**
	 * @see {@link YAW}
	 */
	DOUBLE_YAW(4),
	
	/**
	 *  LEFT_TO_RIGHT     
	 */
	UNYAW(5),
	
	/**
	 *  CIRCULAR_HOUR
	 */
	ROLL(6),
	
	/**
	 * @see {@link ROLL}
	 */
	DOUBLE_ROLL(7),
	
	/**
	 *  CIRCULAR_ANTI_HOUR
	 */
	UNROLL(8);
	
	private int index;

	private Move(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public static Move inverse(Move move) {
		switch(move) {
			case ROLL:
				return UNROLL;
			case YAW:
				return UNYAW;
			case PITCH:
				return UNPITCH;
			case UNROLL:
				return ROLL;
			case UNYAW:
				return YAW;
			case UNPITCH:
				return PITCH;
		}
		return null;
	}
}