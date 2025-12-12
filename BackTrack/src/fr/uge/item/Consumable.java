package fr.uge.item;

import java.util.List;
import java.util.Objects;

import fr.uge.backpack.Coordonate;

public record Consumable(String name, Rarity rarity, List<Coordonate> references) implements Item {
	public Consumable {
		Objects.requireNonNull(name);
		Objects.requireNonNull(rarity);
	}
}
