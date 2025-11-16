package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Gold(int number,List<Coordonate> coordonnates, Rarity rarity) implements Item, ItemTreasure{
	public Gold {
		Objects.requireNonNull(rarity);
		if(number < 0) {
			throw new IllegalArgumentException();
		}
	}
}
