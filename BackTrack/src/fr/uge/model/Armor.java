package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Armor(String name, ArmorType type, Rarity rarity, List<Coordonate> coordonates, 
	int cost, int defensePoint) implements Item, ItemTreasure{
	public Armor {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(rarity);
		if(cost < 0) {
			throw new IllegalArgumentException("cost valeur négatif inacceptable");
		}
		if(defensePoint < 0) {
			throw new IllegalArgumentException("Point de défense négatif inacceptable");
		}
	}
}
