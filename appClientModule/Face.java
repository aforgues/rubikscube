
public enum Face {
	TOP,
	LEFT,
	RIGHT,
	FRONT,
	BOTTOM,
	BACK;
	
	public boolean isAnHorizontalFace() {
		return Face.LEFT == this || Face.FRONT == this || Face.RIGHT == this;
	}
	
	public boolean isAVerticalFace() {
		return Face.BACK == this || Face.TOP == this || Face.FRONT == this || Face.BOTTOM == this;
	}

	public boolean isALateralFace() {
		return isAnHorizontalLateralFace() || isAVerticalLateralFace();
	}

	public boolean isAnHorizontalLateralFace() {
		return Face.LEFT == this || Face.RIGHT == this;
	}

	public boolean isAVerticalLateralFace() {
		return Face.TOP == this || Face.BOTTOM == this;
	}
}
