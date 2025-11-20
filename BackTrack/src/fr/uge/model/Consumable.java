package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Consumable(String name, List<Coordonate> references) implements Item, ItemTreasure{
	public Consumable {
		Objects.requireNonNull(name);
	}
}
