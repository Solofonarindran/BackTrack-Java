package fr.uge.item;

import java.util.List;
import java.util.Objects;

import fr.uge.backpack.Coordonate;
//String urlImage
public record Gold(String name, Rarity rarity, int quantity,  List<Coordonate> references) implements Item, Ressources {
	
	public Gold {
		Objects.requireNonNull(rarity);
		//Objects.requireNonNull(urlImage);
		if(quantity < 0) {
			throw new IllegalArgumentException();
		}
	}
	
	public static Gold initGold() {
		 List<Coordonate> refs = List.of(new Coordonate(0, 0));
		 //var urlGold = "D:/L3/Java/images/Gold.png";
	     var gold = new Gold("Gold", Rarity.COMMON, 0, refs);
	     return gold;
	}
	
	 
	// créé un nouveau Gold si par exemple on a gagné un "tas"
	public static Gold of(int quantity) {
	    return new Gold("Gold", Rarity.COMMON, quantity, List.of(new Coordonate(0,0)));
	}

	
	public Gold spend(int amount) {
	    if (amount < 0) {
	        throw new IllegalArgumentException("Montant négatif impossible");
	    }
	    if (quantity < amount) {
	        throw new IllegalStateException("Pas assez d'or !");
	    }

	    return new Gold(name, rarity, quantity- amount, references);
	}
	
	public Gold add(int amount) {
	    if (amount < 0) {
	        throw new IllegalArgumentException("Montant négatif impossible");
	    }

	    return new Gold(name, rarity, quantity + amount, references);
	}
}
