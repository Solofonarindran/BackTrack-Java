package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Magic(String name, MagicType type, Rarity rarity, int manaMax, String urlImage, List<Coordonate> references) implements Item, ItemTreasure{
	public Magic {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(urlImage);
		if (manaMax < 0) {
			throw new IllegalArgumentException();
		}
	}
}
