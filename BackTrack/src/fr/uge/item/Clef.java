package fr.uge.model;

import java.util.List;
import java.util.Objects;

// references sont des coordonnées qui sont reférences du matériel
public record Clef(String name, Rarity rarity, List<Coordonate> references) implements Item {
	public Clef {
		Objects.requireNonNull(name);
		Objects.requireNonNull(rarity);
		//Objects.requireNonNull(urlImage);
	}

}
