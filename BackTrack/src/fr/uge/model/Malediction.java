package fr.uge.model;

import java.util.List;

public record Malediction(String name, Rarity rarity, List<Coordonate> size,List<Coordonate> references) implements Item{
	
	public Malediction {
		if( references.isEmpty()) {
			throw new IllegalArgumentException();
		}
	}
	@Override
	public boolean rotate() {
		return false;
	}
}
