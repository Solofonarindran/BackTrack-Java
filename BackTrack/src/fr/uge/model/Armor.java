package fr.uge.model;


import java.util.List;
import java.util.Objects;

// references sont des models de coordonnées
public record Armor(String name, ArmorType type, Rarity rarity, List<Coordonate> references, 
	int cost, int defensePoint, String uriImage) implements Item, ItemTreasure{
	public Armor {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(uriImage);
		if(cost < 0) {
			throw new IllegalArgumentException("cost valeur négatif inacceptable");
		}
		if(defensePoint < 0) {
			throw new IllegalArgumentException("Point de défense négatif inacceptable");
		}
	}
	
	public static boolean isArmor(Item item) {
		return switch(item) {
			case Armor _ -> true;
			default -> false;
		};
	}
	
	public static boolean isShield(Item item) {
		return switch(item) {
			case Armor a -> a.type().equals(ArmorType.SHIELD);
			default -> false;
		};
	}
	
	public static boolean isClothing(Item item) {
		return switch(item) {
			case Armor a -> a.type().equals(ArmorType.CLOTHING);
			default -> false;
		};
	}
}
