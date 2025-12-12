package fr.uge.item;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.backpack.Coordonate;


// references sont des models de coordonnées
public record Armor(String name, ArmorType type, Rarity rarity, int damage, int protection, RessourcesType moneyType, int cost, List<Coordonate> references) implements Item, FightItem {
	public Armor {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(rarity);
		//Objects.requireNonNull(urlImage);
		if(cost < 0) {
			throw new IllegalArgumentException("Coût de l'armure négatif inacceptable");
		}
		if(protection < 0) {
			throw new IllegalArgumentException("Point de défense négatif inacceptable");
		}
	}
	
	public static ArrayList<Armor> armorAvailable() {
		 ArrayList<Armor> list = new ArrayList<>();
		 List<Coordonate> refs = List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(1, 0), new Coordonate(1, 1));
		 //var urlShield = "D:/L3/Java/images/Rough_Buckler.png";
	     list.add(new Armor("Shield", ArmorType.SHIELD, Rarity.COMMON, 0, 7, RessourcesType.GOLD, 4, refs));
	     return list;
	}
}
