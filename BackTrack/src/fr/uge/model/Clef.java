package fr.uge.model;

import java.util.Objects;

public record Clef(Rarity rarity) implements Item{
	public Clef {
		Objects.requireNonNull(rarity);
	}
}
