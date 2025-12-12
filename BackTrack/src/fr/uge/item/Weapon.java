package fr.uge.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.backpack.Coordonate;

public record Weapon(String name, WeaponType type, WeaponClass classe, Rarity rarity,
		 int damage, int protection, RessourcesType moneyType, int cost, List<Coordonate> references) implements Item, FightItem {
	
	public Weapon {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		Objects.requireNonNull(classe);
		Objects.requireNonNull(rarity);
		if(cost < 0) {
			throw new IllegalArgumentException("Coût de l'arme négatif inacceptable");
		}
		if(protection < 0) {
			throw new IllegalArgumentException("Point de vie négatif inacceptable");
		}
	}
	
	// Méthode qui renvoie la liste des armes dispo (code en dur) -> temporaire pour phase 1
	public static ArrayList<Weapon> weaponsAvailable() {
		 ArrayList<Weapon> list = new ArrayList<>();
		 List<Coordonate> refs = List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(2, 0));
		 List<Coordonate> refs2 =  List.of(new Coordonate(0, 0), new Coordonate(0, 1));
	     list.add(new Weapon("Épée", WeaponType.SWORDS, WeaponClass.MELLER, Rarity.COMMON, 7, 2, RessourcesType.ENERGY, 8, refs));
	     list.add(new Weapon("Fléchettes", WeaponType.FRAGILE, WeaponClass.DISTANCE, Rarity.COMMON, 2, 1,  RessourcesType.GOLD, 8, refs2));
	     return list;
	}
	
	
}
