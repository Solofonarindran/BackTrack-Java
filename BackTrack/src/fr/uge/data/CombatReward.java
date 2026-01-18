package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public class CombatReward {
	private final int experience;
  private final int gold;
  private final List<Item> items;
  private final boolean levelUp;
  private final int newSlotsUnlocked;
 
  
  public CombatReward(int experience, int gold, List<Item> items, boolean levelUp, int newSlotsUnlocked) {
		this.experience = experience;
		this.gold = gold;
		this.items = new ArrayList<>(items);
		this.levelUp = levelUp;
		this.newSlotsUnlocked = newSlotsUnlocked;
	
	}
  
  public int getExperience() {
    return experience;
}

public int getGold() {
    return gold;
}

public List<Item> getItems() {
    return Collections.unmodifiableList(items);
}

public boolean isLevelUp() {
    return levelUp;
}

public void applyTo(Hero hero) {
  // Ajouter l'or
  hero.addGold(gold);
  
  // Ajouter l'XP (peut déclencher level up automatiquement)
  hero.gainExperience(experience);
}
public int getNewSlotsUnlocked() {
    return newSlotsUnlocked;
}

// ==================== FACTORY ====================

/**
 * Génère les récompenses pour un combat gagné
 */
public static CombatReward generate(Combat combat, Hero hero) {
    var random = new Random();
    
    // XP total des ennemis vaincus
    int totalXp = combat.getTotalExperience();
    
    // Or basé sur l'XP + bonus aléatoire
    int goldReward = totalXp / 2 + random.nextInt(20);
    
    // Items lootés + génération aléatoire
    var items = new ArrayList<>(combat.getLootedItems());
    
    // Chance de drop supplémentaire
    for (int i = 0; i < combat.getEnemies().size(); i++) {
      if (random.nextDouble() < 0.5) {
          items.add(generateRandomItem(random, hero.getLevel()));
      }
    }
    
 // Chance de drop une clé (10%)
    if (random.nextDouble() < 0.10) {
        // Donc on l'ajoute directement au héros après le combat
    	hero.addKey();
    }
    
    // Garantir au moins 1 item si victoire
    if (items.isEmpty() && random.nextDouble() < 0.7) {
        items.add(generateRandomItem(random, hero.getLevel()));
    }
    
    // Vérifier level up
    int oldLevel = hero.getLevel();
    int newXp = hero.getExperience() + totalXp;
    int requiredXp = oldLevel * 100;
    boolean levelUp = newXp >= requiredXp;
    
    // Slots débloqués si level up (3-4 cases)
    int newSlots = levelUp ? 3 + random.nextInt(2) : 0;
    
    return new CombatReward(totalXp, goldReward, items, levelUp, newSlots);
}

/**
 * Génère un item aléatoire basé sur le niveau
 */
	private static Item generateRandomItem(Random random, int level) {
    var roll = random.nextDouble();
    
    // Déterminer la rareté
    Rarity rarity;
    if (roll < 0.5) {
        rarity = Rarity.COMMON;
    } else if (roll < 0.8) {
        rarity = Rarity.UNCOMMON;
    } else {
        rarity = Rarity.RARE;
    } 
    
    // Type d'item
    var itemRoll = random.nextDouble();
    
    if (itemRoll < 0.35) {
        // Arme
        return generateWeapon(random, rarity, level);
    } else if (itemRoll < 0.6) {
        // Armure
        return generateArmor(random, rarity, level);
    } else if (itemRoll < 0.8) {
        // Consommable
        return generateConsumable(random);
    } else {
        // Or
        return generateGold(random, rarity);
    }
	}
	
	private static List<Coordonate> refsGenerateWeapon(WeaponType type) {
		return switch (type) {
      case SWORDS -> List.of(new Coordonate(0, 0),new Coordonate(0, 1),new Coordonate(0, 2));
      case DAGGERS -> List.of( new Coordonate(0, 0), new Coordonate(0, 1));
      case SPEARS -> List.of( new Coordonate(0, 0),new Coordonate(1, 0),new Coordonate(0, 1));
      case FRAGILE -> List.of(new Coordonate(0, 0),new Coordonate(0, 1));
        
		};
	}
	
	
  private static Weapon generateWeapon(Random random, Rarity rarity, int level) {
    var types = WeaponType.values();
    var type = types[random.nextInt(types.length)];
    
    var baseDamage = 5 + level * 2;
    var damage = switch (rarity) {
        case COMMON -> baseDamage;
        case UNCOMMON -> baseDamage + 3;
        case RARE -> baseDamage + 6;
    };
    var cost = 1;
    if (damage > 12) cost = 2;
    
    // Forme de l'arme (références)
    var refs = refsGenerateWeapon(type);
    
    String name = rarity.name().charAt(0) + rarity.name().substring(1).toLowerCase() +  " " + type.name().toLowerCase();       
    return new Weapon(name, type, WeaponClass.MELLER, cost, damage, rarity, refs);
  }
  
  private static List<Coordonate> refsGenerateArmor(ArmorType type) {
  	 return switch (type) {
       case SHIELD -> List.of(new Coordonate(0, 0),new Coordonate(1, 0),new Coordonate(0, 1),new Coordonate(1, 1) );
  
       case HELMETS -> List.of(new Coordonate(0, 0),new Coordonate(1, 0));
           
       case CLOVES -> List.of(new Coordonate(0, 0), new Coordonate(1, 0),new Coordonate(0, 1),new Coordonate(1, 1),new Coordonate(0, 2),
       		 new Coordonate(1, 2));
       
       case CLOTHING ->List.of(new Coordonate(0,0));
          
       case FOOTWEAR -> List.of(new Coordonate(0,0), new Coordonate(0,1));
           
       case STRUCTURE -> List.of(new Coordonate(0,0), new Coordonate(0,1), new Coordonate(0,2));
   };
  }
  
  private static Armor generateArmor(Random random, Rarity rarity, int level) {
    var types = ArmorType.values();
    var type = types[random.nextInt(types.length)];
    
    var baseDefense = 3 + level;
    var defense = switch (rarity) {
        case COMMON -> baseDefense;
        case UNCOMMON -> baseDefense + 2;
        case RARE -> baseDefense + 4;
    };
    var cost = 1;
    // Forme de l'armure
    var refs = refsGenerateArmor(type);
  
    var name = rarity.name().charAt(0) + rarity.name().substring(1).toLowerCase() + " " + type.name().toLowerCase();
    return new Armor(name, type, rarity, refs, cost, defense, "images/armor.png");
	}
  
  private static Consumable generateConsumable(Random random) {
    var names = new String[]{"Potion de vie", "Potion d'énergie", "Antidote", "Élixir"};
    var name = names[random.nextInt(names.length)];
    
    return new Consumable(name, List.of(new Coordonate(0, 0)));
  }

  private static Gold generateGold(Random random, Rarity rarity) {
    int amount = switch (rarity) {
        case COMMON -> 10 + random.nextInt(20);
        case UNCOMMON -> 25 + random.nextInt(25);
        case RARE -> 50 + random.nextInt(50);

    };
    return new Gold(amount, rarity, "images/gold.png", List.of(new Coordonate(0, 0)));
  
  }
  
  
}
