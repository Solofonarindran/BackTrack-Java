package fr.uge.model;

public record Coordonate(int x, int y, boolean active) {
	public Coordonate {
		if( x < 0 || y < 0 ) {
			throw new IllegalArgumentException();
		}
	}
}
