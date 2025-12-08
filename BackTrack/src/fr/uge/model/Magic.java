package fr.uge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Magic(String name, MagicType type, Rarity rarity, int damage, int protection, RessourcesType moneyType, int cost, List<Coordonate> references) implements Item, FightItem {
	public Magic {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		//Objects.requireNonNull(urlImage);

		if(cost < 0) {
			throw new IllegalArgumentException("Coût de l'objet magique négatif inacceptable");
		}
		if(protection < 0) {
			throw new IllegalArgumentException("Point de défense négatif inacceptable");
		}
	}
	
	public static ArrayList<Magic> magicElemAvailable() {
		 ArrayList<Magic> list = new ArrayList<>();
		 //List<Coordonate> refs = List.of(new Coordonate(0, 0));
		 List<Coordonate> refs2 = List.of(new Coordonate(0, 0), new Coordonate(0, 1));
		 //var urlWand = "D:/L3/Java/images/Cleansing_Wand.png";
	     list.add(new Magic("Wand", MagicType.WAND, Rarity.COMMON, 10, 0, RessourcesType.MANASTONE, 1, refs2));
	     return list;
	}
}
