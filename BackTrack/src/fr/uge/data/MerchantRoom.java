package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Item;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public final class MerchantRoom implements Room {
    
    private final List<Item> itemsForSale;
    
    public MerchantRoom() {
        this.itemsForSale = new ArrayList<>();
    }
    
    public MerchantRoom(List<Item> items) {
        this.itemsForSale = new ArrayList<>(items);
    }
    
    // Factory pour créer un marchand avec stock aléatoire
    public static MerchantRoom create(int floorNumber) {
    	 if(floorNumber <0 ) {
    		 throw new IllegalArgumentException("Valeur " + floorNumber + " inacceptable" );
    	 }
        var items = generateStock(floorNumber);
        return new MerchantRoom(items);
    }
    
    private static List<Item> generateStock(int floorNumber) {
        var random = new Random();
        var items = new ArrayList<Item>();
        
        // 4 à 6 items en vente
        int itemCount = 4 + random.nextInt(3);
        
        for (int i = 0; i < itemCount; i++) {
            items.add(generateShopItem(random, floorNumber));
        }
        
        // Toujours des potions
        items.add(new Consumable("Potion de vie", List.of(new Coordonate(0, 0))));
        items.add(new Consumable("Potion de vie", List.of(new Coordonate(0, 0))));
        
        return items;
    }
    
    private static Item generateShopItem(Random random, int floorNumber) {
    	Objects.requireNonNull(random);
    	if(floorNumber < 0) {
    		throw new IllegalArgumentException();
    	}
        double roll = random.nextDouble();
        
        Rarity rarity;
        double rarityRoll = random.nextDouble() - (floorNumber * 0.03);
        if (rarityRoll < 0.6) {
            rarity = Rarity.COMMON;
        } else if (rarityRoll < 0.85) {
            rarity = Rarity.UNCOMMON;
        } else {
            rarity = Rarity.RARE;
        }
        
        if (roll < 0.4) {
            return generateWeapon(random, rarity, floorNumber);
        } else if (roll < 0.7) {
            return generateArmor(random, rarity, floorNumber);
        } else {
            return new Consumable("Potion de vie", List.of(new Coordonate(0, 0)));
        }
    }
    
    private static Weapon generateWeapon(Random random, Rarity rarity, int floorNumber) {
        var types = WeaponType.values();
        var type = types[random.nextInt(types.length)];
        
        int baseDamage = 6 + floorNumber * 2;
        int damage = switch (rarity) {
            case COMMON -> baseDamage;
            case UNCOMMON -> baseDamage + 4;
            case RARE -> baseDamage + 8;
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
        
        int baseDefense = 4 + floorNumber;
        int defense = switch (rarity) {
            case COMMON -> baseDefense;
            case UNCOMMON -> baseDefense + 3;
            case RARE -> baseDefense + 6;
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
    
    public List<Item> getItemsForSale() {
        return Collections.unmodifiableList(itemsForSale);
    }
    
    public void removeItem(Item item) {
        itemsForSale.remove(item);
    }
    
    @Override
    public String toString() {
    // TODO Auto-generated method stub
    	return "💳";
    }
    @Override
    public boolean isVisited() {
    // TODO Auto-generated method stub
    	return false;
    }
    @Override
    public String getDescription() {
        return "Un marchand ambulant";
    }
    
    @Override
    public boolean isAccessible() {
        return true;
    }
    

    @Override
    public Room setVisited() {
    	// TODO Auto-generated method stub
    	return null;
    }
}