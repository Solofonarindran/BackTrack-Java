package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Pair(Item item, List<Coordonate>	coords) {
	public Pair {
		Objects.requireNonNull(item);
	}
}
