package fr.uge.data;

import java.util.List;

//import fr.uge.model.Armor;
//import fr.uge.model.ArmorType;
import fr.uge.model.Coordonate;
import fr.uge.model.Fight;
import fr.uge.model.Gold;
import fr.uge.model.Rarity;
import fr.uge.model.RessourcesType;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public class Main {

	public static void main(String[] args) {
		BackPack backPack = new BackPack();
		backPack.initializeStartingGrid();
		
		List<Coordonate> refs = List.of(new Coordonate(0, 0), new Coordonate(1, 0));
		var epee = new Weapon("Épée", WeaponType.SWORDS, WeaponClass.MELLER, Rarity.COMMON, 7, 2, RessourcesType.GOLD, 8, refs);
		//List<Coordonate> refs2 = List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(1, 0), new Coordonate(1, 1));
	    //var shield = new Armor("Shield", ArmorType.SHIELD, Rarity.COMMON, 0, 7, RessourcesType.GOLD, 4, refs2);
		var epee2 = new Weapon("Épée", WeaponType.SWORDS, WeaponClass.MELLER, Rarity.COMMON, 7, 2, RessourcesType.ENERGY, 2, refs);
		var gold = Gold.of(10);
		
	    var epeeAdded = backPack.addEquipement(epee, new Coordonate(2,1));
		//var shieldAdded = backPack.addEquipement(shield, new Coordonate(3,2));
		var epeeAdded2 = backPack.addEquipement(epee2, new Coordonate(2,2));
		var goldAdded = backPack.addEquipement(gold, new Coordonate(4,1));
		
		backPack.rotateEquipment(epee2);
		System.out.println("Épée ajoutée ? " + epeeAdded);
        //System.out.println("Shield ajouté ? " + shieldAdded);
		System.out.println("Épée ajoutée ? " + epeeAdded2);
		System.out.println("Gold ajouté ? " + goldAdded);
		System.out.println("MoneyType de l'arme : " + epee.moneyType());
		System.out.println("MoneyType de l'arme : " + epee2.moneyType());



		Hero hero = new Hero(40, 40, 3, 3, 0, backPack, 0, 0, 1);

		List<Enemy> enemies = List.of(
		    new Enemy("Rat-Loup", 30, 30, 0),
		    new Enemy("Petit Rat-Loup", 30, 30, 0)
		);

		Fight fight = new Fight(hero, enemies);
		fight.fight();
	}

}
