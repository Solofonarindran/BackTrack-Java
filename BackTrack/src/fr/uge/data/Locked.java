package fr.uge.data;

public final class Locked implements Cell {

	@Override
	public boolean isUnlocked() {
		return false;
	}

	@Override
	public boolean isFree() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Locked";
	}

}
