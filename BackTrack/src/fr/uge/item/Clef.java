package fr.uge.item;

import java.util.List;
import java.util.Objects;

import fr.uge.backpack.Coordonate;

// references sont des coordonnées qui sont reférences du matériel
public record Clef(String name, Rarity rarity, List<Coordonate> references) implements Item {
	public Clef {
		Objects.requireNonNull(name);
		Objects.requireNonNull(rarity);
		//Objects.requireNonNull(urlImage);
	}

}
