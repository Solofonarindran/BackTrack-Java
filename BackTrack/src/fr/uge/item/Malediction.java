package fr.uge.item;

import java.util.List;

import fr.uge.backpack.Coordonate;

public record Malediction(String name, Rarity rarity, List<Coordonate> references) implements Item{
	
	public Malediction {
		if(references.isEmpty()) {
			throw new IllegalArgumentException();
		}
	}
	@Override
	public boolean rotate() {
		return false;
	}
	
	public static Malediction initMalediction() {
		 List<Coordonate> refs = List.of(new Coordonate(0, 1), new Coordonate(1, 1), new Coordonate(1, 0), new Coordonate(2, 0));
	     var malediction = new Malediction("Malediction", Rarity.COMMON, refs);
	     return malediction;
	}
}
