package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Gold(int number, Rarity rarity, String urlImage, List<Coordonate> references) implements Item, ItemTreasure{
	public Gold {
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(urlImage);
		if(number < 0) {
			throw new IllegalArgumentException();
		}
	}
}
