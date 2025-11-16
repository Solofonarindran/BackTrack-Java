package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Magic(String name, MagicType type, Rarity rarity, List<Coordonate> coordonates, int manaMax) implements Item, ItemTreasure{
	public Magic {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		if (manaMax < 0) {
			throw new IllegalArgumentException();
		}
	}
}
