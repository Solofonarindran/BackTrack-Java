package fr.uge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Weapon(String name, WeaponType type, WeaponClass classe, 
		int cost, int healthPoint, int damage, int protection, Rarity rarity, List<Coordonate> references) implements Item, ItemTreasure {
	
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
	
	// Méthode qui renvoie la liste des armes dispo (code en dur) -> temporaire pour phase 1
	public static ArrayList<Weapon> weaponsAvailable() {
		 ArrayList<Weapon> list = new ArrayList<>();
		 List<Coordonate> refs = List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(2, 0));
		 List<Coordonate> refs2 = List.of(new Coordonate(0, 0), new Coordonate(0, 1));
	     list.add(new Weapon("Épée", WeaponType.SWORDS, WeaponClass.MELLER, 5, 2, 8, 0, Rarity.COMMON, refs));
	     list.add(new Weapon("Fléchettes", WeaponType.FRAGILE, WeaponClass.DISTANCE, 2, 1, 8, 0, Rarity.COMMON, refs2));
	     return list;
	}
	
	
}
