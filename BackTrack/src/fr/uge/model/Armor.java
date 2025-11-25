package fr.uge.model;


import java.util.List;
import java.util.Objects;

// references sont des models de coordonnées
public record Armor(String name, ArmorType type, Rarity rarity, List<Coordonate> references, 
	int cost, int defensePoint, String urlImage) implements Item, ItemTreasure{
	public Armor {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(urlImage);
		if(cost < 0) {
			throw new IllegalArgumentException("cost valeur négatif inacceptable");
		}
		if(defensePoint < 0) {
			throw new IllegalArgumentException("Point de défense négatif inacceptable");
		}
	}
}
