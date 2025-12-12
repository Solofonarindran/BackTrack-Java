package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record ManaStone(String name, Rarity rarity, int quantity, List<Coordonate> references) implements Item, Ressources {

    public ManaStone {
    	Objects.requireNonNull(name);
        if (quantity < 0) throw new IllegalArgumentException("Quantité négative impossible");
    }
    
	public static ManaStone initMana() {
		 List<Coordonate> refs = List.of(new Coordonate(0, 0));
		 //var urlGold = "D:/L3/Java/images/Gold.png";
	     var manaStone = new ManaStone("Mana Stone", Rarity.COMMON, 0, refs);
	     return manaStone;
	}

    public ManaStone add(int amount) {
        return new ManaStone(name, rarity, quantity + amount, references);
    }

    public ManaStone spend(int amount) {
        if (amount > quantity)
            throw new IllegalStateException("Pas assez de mana stones");
        return new ManaStone(name, rarity, quantity - amount, references);
    }
}
