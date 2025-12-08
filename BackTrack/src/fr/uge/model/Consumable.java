package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Consumable(String name, Rarity rarity, List<Coordonate> references) implements Item {
	public Consumable {
		Objects.requireNonNull(name);
		Objects.requireNonNull(rarity);
	}
}
