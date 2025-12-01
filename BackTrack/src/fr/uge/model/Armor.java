package fr.uge.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// references sont des models de coordonnées
public record Armor(String name, int damage, int protection, ArmorType type, Rarity rarity, List<Coordonate> references, 
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
	
	public static ArrayList<Armor> armorAvailable() {
		 ArrayList<Armor> list = new ArrayList<>();
		 List<Coordonate> refs = List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(1, 0), new Coordonate(1, 1));
		 var urlShield = "D:/L3/Java/images/Rough_Buckler.png";
	     list.add(new Armor("Shield", 0, 7, ArmorType.SHIELD, Rarity.COMMON, refs, 1, 5, urlShield));
	     return list;
	}
}
