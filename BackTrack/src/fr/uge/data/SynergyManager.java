package fr.uge.data;

//============================================
//PHASE 2 - SYNERGY MANAGER
//Partie 3 : Gestion des synergies entre items
//============================================
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Weapon;


public class SynergyManager {
 
 // Liste de toutes les synergies disponibles
 private final List<Synergy> synergies;
 
 public SynergyManager() {
     this.synergies = new ArrayList<>();
     initializeDefaultSynergies();
 }

 private void initializeDefaultSynergies() {
     
     // ========== SYNERGIE 1 : Arme + Gemme de cœur ==========
     // Exemple du document : "toute utilisation d'une arme occupant une case 
     // adjacente à la gemme de cœur rapporte 1 point de vie au héros"
     
     // Note : On va créer un type spécial "HeartGem" qui sera un Magic
     // Pour l'instant, simplifions : toute arme à côté d'un objet magique
     
     synergies.add(new Synergy(
         "Arme Enchantée",
         "Une arme adjacente à un objet magique gagne +1 PV par utilisation",
         Weapon.class,
         Magic.class,
         (item1, item2, player) -> {
             // Cet effet sera appliqué pendant le combat (Phase 3)
             // Pour l'instant, on le note juste
         }
     ));
     
     // ========== SYNERGIE 2 : Bouclier + Armure ==========
     synergies.add(new Synergy(
         "Défense Renforcée",
         "Un bouclier adjacent à une armure donne +5 défense",
         Armor.class, // Bouclier
         Armor.class, // Armure
         (item1, item2, player) -> {
             // Vérifier que l'un est un bouclier et l'autre une armure
             if (item1 instanceof Armor a1 && item2 instanceof Armor a2) {
                 if ((a1.type() == ArmorType.SHIELD && a2.type() == ArmorType.CLOTHING) ||
                     (a2.type() == ArmorType.SHIELD && a1.type() == ArmorType.CLOTHING)) {
                     player.addProtection(5);
                 }
             }
         }
     ));
     
     // ========== SYNERGIE 3 : Épée + Pierre à aiguiser ==========
     // (On pourrait créer un type Consumable spécial "Whetstone")
     synergies.add(new Synergy(
         "Lame Affûtée",
         "Une épée adjacente à une pierre à aiguiser gagne +10% dégâts",
         Weapon.class,
         Consumable.class,
         (item1, item2, player) -> {
             // Effet appliqué en combat (Phase 3)
         }
     ));
     
     // ========== SYNERGIE 4 : Potion + Potion ==========
     synergies.add(new Synergy(
         "Double Dose",
         "Deux potions adjacentes voient leur effet décuplé",
         Consumable.class,
         Consumable.class,
         (item1, item2, player) -> {
             // Effet spécial lors de l'utilisation (Phase 3)
         }
     ));
     
     // ========== SYNERGIE 5 : Pierre de Mana + Baguette ==========
     synergies.add(new Synergy(
         "Mana Amplifié",
         "Une baguette magique adjacente à une pierre de mana coûte -1 mana",
         Magic.class, // Baguette
         Magic.class, // Pierre de mana
         (item1, item2, player) -> {
             // Réduction de coût en combat (Phase 3)
         }
     ));
 }
 
 /**
  * Ajoute une synergie personnalisée
  */
 public void addSynergy(Synergy synergy) {
     Objects.requireNonNull(synergy);
     synergies.add(synergy);
 }
 
 /**
  * Vérifie si deux items sont adjacents dans le sac
  * @param backpack Le sac à dos
  * @param item1 Premier item
  * @param item2 Second item
  * @return true s'ils sont adjacents
  */
 public boolean areAdjacent(BackPack backpack, Item item1, Item item2) {
     Objects.requireNonNull(backpack);
     Objects.requireNonNull(item1);
     Objects.requireNonNull(item2);
     
     if (!backpack.contains(item1) || !backpack.contains(item2)) {
         return false;
     }
     
     var coords1 = backpack.getItemCoordinates(item1);
     var coords2 = backpack.getItemCoordinates(item2);
     
     // Deux items sont adjacents si au moins une case de l'un touche une case de l'autre
     // (horizontalement ou verticalement, pas en diagonale)
     for (var c1 : coords1) {
         for (var c2 : coords2) {
             // Vérifier adjacence horizontale ou verticale
             int dx = Math.abs(c1.x() - c2.x());
             int dy = Math.abs(c1.y() - c2.y());
             
             // Adjacent si différence de 1 en X et 0 en Y, ou 0 en X et 1 en Y
             if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                 return true;
             }
         }
     }
     
     return false;
 }
 
 /**
  * Trouve toutes les synergies actives pour un item donné
  * @param backpack Le sac à dos
  * @param item L'item à vérifier
  * @return Liste des synergies actives impliquant cet item
  */
 public List<Synergy> getActiveSynergiesFor(BackPack backpack, Item item) {
     Objects.requireNonNull(backpack);
     Objects.requireNonNull(item);
     
     if (!backpack.contains(item)) {
         return new ArrayList<>();
     }
     
     List<Synergy> activeSynergies = new ArrayList<>();
     
     // Parcourir tous les autres items du sac
     for (var otherItem : backpack.getAllItems()) {
         if (otherItem.equals(item)) continue;
         
         // Vérifier si les items sont adjacents
         if (!areAdjacent(backpack, item, otherItem)) continue;
         
         // Chercher une synergie compatible
         for (var synergy : synergies) {
             boolean match = false;
             
             // Vérifier correspondance type1 <-> type2
             if (synergy.itemType1().isInstance(item) && 
                 synergy.itemType2().isInstance(otherItem)) {
                 match = true;
             }
             
             // Vérifier correspondance type2 <-> type1 (symétrique)
             if (synergy.itemType2().isInstance(item) && 
                 synergy.itemType1().isInstance(otherItem)) {
                 match = true;
             }
             
             if (match && !activeSynergies.contains(synergy)) {
                 activeSynergies.add(synergy);
             }
         }
     }
     
     return activeSynergies;
 }
 
 /**
  * Trouve toutes les synergies actives dans le sac
  * @param backpack Le sac à dos
  * @return Map associant chaque item à ses synergies actives
  */
 public Map<Item, List<Synergy>> getAllActiveSynergies(BackPack backpack) {
     Objects.requireNonNull(backpack);
     
     Map<Item, List<Synergy>> result = new HashMap<>();
     
     for (var item : backpack.getAllItems()) {
         var activeSynergies = getActiveSynergiesFor(backpack, item);
         if (!activeSynergies.isEmpty()) {
             result.put(item, activeSynergies);
         }
     }
     
     return result;
 }
 
 /**
  * Applique toutes les synergies actives (appelé au début d'un combat par exemple)
  * @param backpack Le sac à dos
  * @param hero Le joueur
  */
 public void applyAllSynergies(BackPack backpack, Hero hero) {
     Objects.requireNonNull(backpack);
     Objects.requireNonNull(hero);
     
     var allSynergies = getAllActiveSynergies(backpack);
     
     // Éviter les doublons : chaque paire d'items ne doit être traitée qu'une fois
     Set<String> processed = new HashSet<>();
     
     for (var entry : allSynergies.entrySet()) {
         var item1 = entry.getKey();
         
         for (var synergy : entry.getValue()) {
             // Trouver le second item impliqué
             for (var item2 : backpack.getAllItems()) {
                 if (item1.equals(item2)) continue;
                 
                 if (areAdjacent(backpack, item1, item2)) {
                     // Créer une clé unique pour cette paire
                     String key = createPairKey(item1, item2);
                     
                     if (!processed.contains(key)) {
                         // Appliquer l'effet
                         synergy.effect().apply(item1, item2, hero);
                         processed.add(key);
                     }
                 }
             }
         }
     }
 }
 
 /**
  * Crée une clé unique pour une paire d'items (ordre indépendant)
  */
 private String createPairKey(Item item1, Item item2) {
     int hash1 = System.identityHashCode(item1);
     int hash2 = System.identityHashCode(item2);
     
     // S'assurer que la clé est la même quel que soit l'ordre
     if (hash1 < hash2) {
         return hash1 + "-" + hash2;
     } else {
         return hash2 + "-" + hash1;
     }
 }
 
 /**
  * Trouve tous les items adjacents à un item donné
  * @param backpack Le sac à dos
  * @param item L'item de référence
  * @return Liste des items adjacents
  */
 public List<Item> getAdjacentItems(BackPack backpack, Item item) {
     Objects.requireNonNull(backpack);
     Objects.requireNonNull(item);
     
     if (!backpack.contains(item)) {
         return new ArrayList<>();
     }
     
     return backpack.getAllItems().stream()
         .filter(otherItem -> !otherItem.equals(item))
         .filter(otherItem -> areAdjacent(backpack, item, otherItem))
         .collect(Collectors.toList());
 }
 
 /**
  * Retourne la liste de toutes les synergies disponibles
  */
 public List<Synergy> getAllSynergies() {
     return new ArrayList<>(synergies);
 }
 
 /**
  * Affiche toutes les synergies actives dans le sac (debug)
  */
 public void displayActiveSynergies(BackPack backpack) {
     Objects.requireNonNull(backpack);
     
     var activeSynergies = getAllActiveSynergies(backpack);
     
     System.out.println("\n========== SYNERGIES ACTIVES ==========");
     
     if (activeSynergies.isEmpty()) {
         System.out.println("Aucune synergie active.");
     } else {
         activeSynergies.forEach((item, synergyList) -> {
             String itemName = switch (item) {
                 case Armor a -> a.name();
                 case Weapon w -> w.name();
                 case Magic m -> m.name();
                 case Consumable c -> c.name();
                 default -> "Item";
             };
             
             System.out.println("\n" + itemName + " :");
             synergyList.forEach(synergy -> 
                 System.out.println("  ✨ " + synergy.name() + " : " + synergy.description())
             );
         });
     }
     
     System.out.println("\n=====================================\n");
 }
}