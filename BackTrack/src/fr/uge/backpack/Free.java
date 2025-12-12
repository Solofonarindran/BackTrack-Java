package fr.uge.backpack;

public final class Free implements Cell {

	@Override
	public boolean isUnlocked() {
		return true;
	}

	@Override
	public boolean isFree() {
		return true;
	}

	@Override
	public String toString() {
		return "Free";
	}
}
