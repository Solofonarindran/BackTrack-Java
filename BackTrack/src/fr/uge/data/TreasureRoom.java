package fr.uge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public record TreasureRoom(List<Item> loot,boolean isLooted) implements Room {
  
  public TreasureRoom {
      loot = List.copyOf(loot);
  }
  
  // Constructeur pour créer un coffre avec du loot aléatoire
  public static TreasureRoom create(int floorNumber) {
      var items = generateLoot(floorNumber);
      return new TreasureRoom(items, false);
  }
  
  // Constructeur pour coffre vide (déjà pillé)
  public static TreasureRoom createEmpty() {
      return new TreasureRoom(List.of(), true);
  }
  
  // Marquer comme pillé
  public TreasureRoom markLooted() {
      return new TreasureRoom(List.of(), true);
  }
  
  // Générer le loot aléatoire
  private static List<Item> generateLoot(int floorNumber) {
      var random = new Random();
      var items = new ArrayList<Item>();
      
      // 2 à 4 items par coffre
      int itemCount = 2 + random.nextInt(3);
      
      for (int i = 0; i < itemCount; i++) {
          items.add(generateRandomItem(random, floorNumber));
      }
      
      // Toujours un peu d'or
      int goldAmount = 20 + floorNumber * 10 + random.nextInt(30);
      items.add(new Gold(goldAmount, Rarity.COMMON, "images/gold.png", List.of(new Coordonate(0, 0))));
      
      return items;
  }
  
  private static Item generateRandomItem(Random random, int floorNumber) {
      double roll = random.nextDouble();
      
      // Déterminer la rareté (meilleure aux étages supérieurs)
      Rarity rarity;
      double rarityRoll = random.nextDouble() - (floorNumber * 0.05);
      if (rarityRoll < 0.5) {
          rarity = Rarity.COMMON;
      } else if (rarityRoll < 0.8) {
          rarity = Rarity.UNCOMMON;
      } else {
          rarity = Rarity.RARE;
      }
      
      if (roll < 0.35) {
          return generateWeapon(random, rarity, floorNumber);
      } else if (roll < 0.60) {
          return generateArmor(random, rarity, floorNumber);
      } else {
          return generateConsumable(random);
      }
  }
  
  private static Weapon generateWeapon(Random random, Rarity rarity, int floorNumber) {
      var types = WeaponType.values();
      var type = types[random.nextInt(types.length)];
      
      int baseDamage = 5 + floorNumber * 2;
      int damage = switch (rarity) {
          case COMMON -> baseDamage;
          case UNCOMMON -> baseDamage + 3;
          case RARE -> baseDamage + 6;
      };
      
      int cost = damage > 12 ? 2 : 1;
      
      List<Coordonate> refs = switch (type) {
          case SWORDS -> List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(0, 2));
          case DAGGERS -> List.of(new Coordonate(0, 0), new Coordonate(0, 1));
          case SPEARS -> List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1));
          case FRAGILE -> List.of(new Coordonate(0, 0), new Coordonate(0, 1));
      };
      
      String name = rarity.name().charAt(0) + rarity.name().substring(1).toLowerCase() + " " + type.name().toLowerCase();
      return new Weapon(name, type, WeaponClass.MELLER, cost, damage, rarity, refs);
  }
  
  private static Armor generateArmor(Random random, Rarity rarity, int floorNumber) {
      var types = ArmorType.values();
      var type = types[random.nextInt(types.length)];
      
      int baseDefense = 3 + floorNumber;
      int defense = switch (rarity) {
          case COMMON -> baseDefense;
          case UNCOMMON -> baseDefense + 2;
          case RARE -> baseDefense + 4;
      };
      
      List<Coordonate> refs = switch (type) {
          case SHIELD -> List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1), new Coordonate(1, 1));
          case HELMETS -> List.of(new Coordonate(0, 0), new Coordonate(1, 0));
          case CLOVES -> List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1), new Coordonate(1, 1), new Coordonate(0, 2), new Coordonate(1, 2));
          case CLOTHING -> List.of(new Coordonate(0, 0));
          case FOOTWEAR -> List.of(new Coordonate(0, 0), new Coordonate(0, 1));
          case STRUCTURE -> List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(0, 2));
      };
      
      String name = rarity.name().charAt(0) + rarity.name().substring(1).toLowerCase() + " " + type.name().toLowerCase();
      return new Armor(name, type, rarity, refs, 1, defense, "images/armor.png");
  }
  
  private static Consumable generateConsumable(Random random) {
      String[] names = {"Potion de vie", "Potion d'énergie", "Antidote", "Élixir"};
      String name = names[random.nextInt(names.length)];
      return new Consumable(name, List.of(new Coordonate(0, 0)));
  }
  
  @Override
  public String getDescription() {
      return isLooted ? "Coffre vide" : "Un coffre au trésor !";
  }
  
  @Override
  public boolean isAccessible() {
      return true;
  }
  
  @Override
  public Room setVisited() {
  // TODO Auto-generated method stub
  	return new TreasureRoom(loot,true);
  }
  @Override
  public boolean isVisited() {
  // TODO Auto-generated method stub
  return isLooted;
  }
  @Override
	public final String toString() {
		// TODO Auto-generated method stub
		return !isLooted ? "💰" : "📦";
	}
	
	
}
