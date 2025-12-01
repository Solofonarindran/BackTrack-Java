package fr.uge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Magic(String name, int damage, int protection,  MagicType type, Rarity rarity, int manaMax, String urlImage, List<Coordonate> references) implements Item, ItemTreasure{
	public Magic {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(urlImage);
		if (manaMax < 0) {
			throw new IllegalArgumentException();
		}
	}
	
	public static ArrayList<Magic> magicElemAvailable() {
		 ArrayList<Magic> list = new ArrayList<>();
		 List<Coordonate> refs = List.of(new Coordonate(0, 0));
		 List<Coordonate> refs2 = List.of(new Coordonate(0, 0), new Coordonate(0, 1));
		 var urlManaStone = "D:/L3/Java/images/Manastones.png";
		 var urlWand = "D:/L3/Java/images/Cleansing_Wand.png";
	     list.add(new Magic("Mana Stone", 0, 0,  MagicType.MANASTONE, Rarity.COMMON, 1, urlManaStone ,refs));
	     list.add(new Magic("Wand", 10, 0, MagicType.WAND, Rarity.COMMON, 1, urlWand, refs2));
	     return list;
	}
}
