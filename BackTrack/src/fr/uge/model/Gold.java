package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Gold(String name, int number, int damage, int protection, Rarity rarity, String urlImage, List<Coordonate> references) implements Item, ItemTreasure{
	public Gold {
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(urlImage);
		if(number < 0) {
			throw new IllegalArgumentException();
		}
	}
	
	public static Gold initGold() {
		 List<Coordonate> refs = List.of(new Coordonate(0, 0));
		 var urlGold = "D:/L3/Java/images/Gold.png";
	     var gold = new Gold("Gold", 1, 0, 0, Rarity.COMMON, urlGold, refs);
	     return gold;
	}

}
