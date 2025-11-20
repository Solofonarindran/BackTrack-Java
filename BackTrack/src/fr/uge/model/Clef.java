package fr.uge.model;

import java.util.List;
import java.util.Objects;

// references sont des coordonnées qui sont reférences du matériel
public record Clef(Rarity rarity, String urlImage,List<Coordonate> references) implements Item, ItemTreasure{
	public Clef {
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(urlImage);
	}
}
