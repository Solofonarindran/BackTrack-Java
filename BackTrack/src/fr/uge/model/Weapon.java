package fr.uge.model;

import java.util.List;
import java.util.Objects;

public record Weapon(String name, WeaponType type, WeaponClass classe, 
		int cost, int healthPoint, List<Coordonate> coordonates, Rarity rarity,List<Coordonate> references) implements Item, ItemTreasure{
	public Weapon {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(classe);
		Objects.requireNonNull(rarity);
		if( cost < 0) {
			throw new IllegalArgumentException("Cost n'accepte pas une valeur négative");
		}
		if(healthPoint < 0) {
			throw new IllegalArgumentException(" Point de vie négatif inacceptable");
		}
	}
	
	
}
