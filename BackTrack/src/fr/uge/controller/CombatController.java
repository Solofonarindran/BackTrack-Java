package fr.uge.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.Combat;
import fr.uge.data.EnemyRoom;
import fr.uge.model.Armor;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Curse;
import fr.uge.model.Enemy;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Weapon;
import fr.uge.view.CombatView;

public class CombatController {
    
    private final Combat combat;

    private CombatView view;
    
    // File d'attente des ennemis
    private final List<Enemy> enemyQueue;
    private Enemy currentEnemy;
    private int enemyIndex;
    
    // Accumulation pendant le tour du héros
    private int accumulatedDamage;
    private int accumulatedProtection;
    private int accumulatedHeal;
    private final List<Item> selectedItems;
    
 // Malédiction
    private boolean waitingForCurseDecision = false;
    private Curse pendingCurse = null;
    private int floorNumber = 1;
    
    // État
    private boolean combatOver;
    private boolean victory;
    
    private Item hoveredItem = null;
    
    public CombatController(Combat combat, ApplicationContext context) {
        this.combat = Objects.requireNonNull(combat);
        
        
        this.enemyQueue = new ArrayList<>(combat.getEnemies());
        this.enemyIndex = 0;
        this.currentEnemy = enemyQueue.isEmpty() ? null : enemyQueue.get(0);
        
        this.accumulatedDamage = 0;
        this.accumulatedProtection = 0;
        this.accumulatedHeal = 0;
        this.selectedItems = new ArrayList<>();
        
        this.combatOver = false;
        this.victory = false;
    }
    
    // ==================== LOGGING ====================
    
    private void log(String message) {
        combat.addLog(message, false);
    }
    
    private void logImportant(String message) {
        combat.addLog(message, true);
    }
    
    // ==================== INITIALISATION ====================
    
    public void setView(CombatView view) {
        this.view = view;
    }
    
    public void start() {
      resetAccumulation();
      combat.getHero().resetCurseRefusalCount();
      
      logImportant("══════════════════════════════");
      logImportant("COMBAT - " + enemyQueue.size() + " ENNEMI" + (enemyQueue.size() > 1 ? "S" : ""));
      logImportant("══════════════════════════════");
      
      // Afficher la file d'attente complète
      log("");
      log("File d'attente :");
      for (int i = 0; i < enemyQueue.size(); i++) {
          var enemy = enemyQueue.get(i);
          String marker = (i == 0) ? "► " : "  ";
          log(marker + (i + 1) + ". " + enemy.getType().getName() + 
              " (PV: " + enemy.getHealthPoint() + "/" + enemy.getMaxHealthPoint() + ")");
      }
      log("");
      
      // Commencer avec le premier ennemi
      startFightWithCurrentEnemy();
    }
    

    public void setFloorNumber(int floorNumber) {
      this.floorNumber = floorNumber;
    }
    
    private void startFightWithCurrentEnemy() {
      if (currentEnemy == null) {
          victory = true;
          combatOver = true;
          return;
      }
      
      logImportant("ADVERSAIRE " + (enemyIndex + 1) + "/" + enemyQueue.size() + 
                  " : " + currentEnemy.getType().getName());
      log("PV : " + currentEnemy.getHealthPoint() + "/" + currentEnemy.getMaxHealthPoint());
      log("Armure : " + currentEnemy.getArmor());
      log("");
      
      showEnemyIntention();
      log("Sélectionnez vos items puis ESPACE");
  }
    
    private void showEnemyIntention() {
        if (currentEnemy == null) return;
        
        var action = currentEnemy.getNextAction();
        logImportant(currentEnemy.getType().getName() + " prépare : " + action.getAnnouncement());
    }
    
    private void nextEnemy() {
      enemyIndex++;
      
      if (enemyIndex >= enemyQueue.size()) {
          // Tous les ennemis sont vaincus
          victory = true;
          combatOver = true;
          
          log("");
          logImportant("══════════════════════════════");
          logImportant("VICTOIRE TOTALE !");
          logImportant(enemyQueue.size() + " ennemi" + (enemyQueue.size() > 1 ? "s" : "") + " vaincu" + (enemyQueue.size() > 1 ? "s" : "") + " !");
          logImportant("══════════════════════════════");
          return;
      }
      
      currentEnemy = enemyQueue.get(enemyIndex);
      
      log("");
      logImportant("══════════════════════════════");
      logImportant("PROCHAIN ADVERSAIRE !");
      log("Progression : " + enemyIndex + "/" + enemyQueue.size());
      logImportant("══════════════════════════════");
      log("");
      
      // Afficher la file mise à jour
      log("File d'attente :");
      for (int i = 0; i < enemyQueue.size(); i++) {
          var enemy = enemyQueue.get(i);
          String marker;
          String status;
          
          if (i < enemyIndex) {
              marker = "✗ ";
              status = " [VAINCU]";
          } else if (i == enemyIndex) {
              marker = "► ";
              status = " ← ACTUEL";
          } else {
              marker = "  ";
              status = "";
          }
          
          log(marker + (i + 1) + ". " + enemy.getType().getName() + status);
      }
      log("");
      
      startFightWithCurrentEnemy();
  }
    
    private void resetAccumulation() {
        accumulatedDamage = 0;
        accumulatedProtection = 0;
        accumulatedHeal = 0;
        selectedItems.clear();
    }
    
    // ==================== GESTION DES ÉVÉNEMENTS ====================
    
    public void handlePointerEvent(PointerEvent event) {
        if (combatOver) return;
        if (waitingForCurseDecision) return;
        
        int x = (int) event.location().x();
        int y = (int) event.location().y();
        
        switch (event.action()) {
            case POINTER_MOVE -> {
                if (view != null && view.getBackPackView() != null) {
                    var hoveredItem = view.getBackPackView().getItemAt(x, y);
                    view.getBackPackView().setHoveredItem(hoveredItem);
                }
            }
            
            case POINTER_UP -> {
                if (view != null && view.getBackPackView() != null) {
                    var clickedItem = view.getBackPackView().getItemAt(x, y);
                    if (clickedItem != null) {
                        handleItemClick(clickedItem);
                    }
                }
            }
            
            
            default -> {}
        }
    }
    
    public void handleKeyboardEvent(KeyboardEvent event) {
      if (event.action() != KeyboardEvent.Action.KEY_PRESSED) return;
      
      if (combatOver) return;
      
      // Gérer le choix de malédiction
      if (waitingForCurseDecision) {
          switch (event.key()) {
              case A -> acceptCurse();
              case R -> refuseCurse();
              default -> {}
          }
          return;
      }
      // Actions normales du tour
      switch (event.key()) {
          case SPACE -> executeHeroTurn();
          case ESCAPE -> cancelSelection();
          case T -> {
            if (hoveredItem != null && !(hoveredItem instanceof Curse)) {
                rotateHoveredItem();
            }
        }

          case A, B, C, D, E, F, G, H, I -> {
              int index = event.key().ordinal() - KeyboardEvent.Key.A.ordinal();
              selectItemByIndex(index);
          }
          default -> {}
      }
  }
   
    

    public void setHoveredItem(Item item) {
        this.hoveredItem = item;
    }
    private void rotateHoveredItem() {
      if (hoveredItem == null) {
          log("Survolez un item pour le tourner");
          return;
      }
      
      if (hoveredItem instanceof Curse) {
          log("❌ Les malédictions ne peuvent pas être tournées !");
          return;
      }
      
      var backpack = combat.getHero().getBackpack();
      boolean success = backpack.rotateEquipment(hoveredItem);
      
      if (success) {
          log("🔄 " + getItemName(hoveredItem) +" tourné !");
      } else {
          log("❌ Impossible de tourner (pas de place) !");
      }
  }
    
    private String getItemName(Item item) {
      return switch (item) {
          case Weapon w -> w.name();
          case Armor a -> a.name();
          case Consumable c -> c.name();
          case Magic m -> m.name();
          default -> "Item";
      };
    }
    
 // ==================== MALÉDICTION ====================

    private void triggerCurse() {
        pendingCurse = Curse.createRandom(floorNumber);
        waitingForCurseDecision = true;
        int refusalCount = combat.getHero().getCurseRefusalCount();
        int nextDamage = refusalCount + 1;
        log("");logImportant("⚠️ ══ MALÉDICTION ! ══ ⚠️");
        log(currentEnemy.getType().getName() + " vous lance :");
        logImportant("\"" + pendingCurse.name() + "\"");
        log("Effet : " + pendingCurse.description());
        log("Taille : " + pendingCurse.references().size() + " case(s)");
        log("");
        logImportant("CHOIX :");
        log("  [A] Accepter → Placer dans le sac");
        log("  [R] Refuser → -" + nextDamage + " PV");
        log("");
    }

    private void acceptCurse() {
        if (pendingCurse == null) return;  
        var hero = combat.getHero();
        var position = findValidCursePosition(pendingCurse);
        if (position != null) {
            var itemsLost = countItemsToRemove(pendingCurse, position);
            hero.acceptCurse(pendingCurse, position);
            log("");
            logImportant("Malédiction ACCEPTÉE !");
            log("\"" + pendingCurse.name() + "\" placée dans le sac");  
            if (itemsLost > 0) {
                log(itemsLost + " item(s) écrasé(s) et perdu(s) !");
            }
        } else {
            log("");
            log("Pas de place dans le sac !");
            log("Refus forcé...");
            refuseCurse();
            return;
        }
        finishCurseDecision();
    }

    private void refuseCurse() {
        if (pendingCurse == null) return;
        var hero = combat.getHero();
        var damage = hero.refuseCurse();
        log("");logImportant("Malédiction REFUSÉE !");
        log("Vous subissez " + damage + " dégât" + (damage > 1 ? "s" : "") + " !");
        log("PV : " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint());
        if (hero.isDead()) {
            combatOver = true;
            victory = false;
            log("");
            logImportant("══ DÉFAITE ══");
            log("Vous succombez à la malédiction...");
            return;
        }
        finishCurseDecision();
    }

    private void finishCurseDecision() {
        waitingForCurseDecision = false;
        pendingCurse = null;
        currentEnemy.decideNextAction();
        combat.getHero().resetProtection();
        log("");
        logImportant("── VOTRE TOUR ──");
        showEnemyIntention();
        log("Sélectionnez vos items puis ESPACE");
    }

    private Coordonate findValidCursePosition(Curse curse) {
        var backpack = combat.getHero().getBackpack();
        for (var y = 0; y < backpack.getMaxHeight(); y++) {
            for (var x = 0; x < backpack.getMaxWidth(); x++) {
                var pos = new Coordonate(x, y);
                if (backpack.canPlaceCurse(curse, pos)) {
                    return pos;
                }
            }
        }
        
        return null;
    }

    private int countItemsToRemove(Curse curse, Coordonate position) {
        var backpack = combat.getHero().getBackpack();
        var itemsToRemove = new java.util.HashSet<Item>();      
        for (var ref : curse.references()) {
            var x = position.x() + ref.x();
            var y = position.y() + ref.y();
            var item = backpack.getItemAt(new Coordonate(x, y));
            if (item != null && !(item instanceof Curse)) {
                itemsToRemove.add(item);
            }
        }
        
        return itemsToRemove.size();
    }
    // ==================== SÉLECTION D'ITEMS ====================
    
    private void handleItemClick(Item item) {
	    	 if (Curse.isCurse(item)) {
	         log("Les malédictions ne peuvent pas être utilisées !");
	         return;
	    	 }
        var cost = getEnergyCost(item);
        var hero = combat.getHero();
        if (hero.getEnergy() < cost) {
            log("Pas assez d'énergie ! (besoin: " + cost + ")");
            return;
        }
        hero.useEnergy(cost);
        selectedItems.add(item);
        switch (item) {
            case Weapon w -> {
                accumulatedDamage += w.healthPoint();
                log("⚔ " + w.name() + " → +" + w.healthPoint() + " dégâts");
            }
            case Armor a -> {
                accumulatedProtection += a.defensePoint();
                log("🛡 " + a.name() + " → +" + a.defensePoint() + " protection");
            }
            case Consumable c -> {
                accumulatedHeal += 10;
                log("+ " + c.name() + " → +10 soin");
            }
            case Magic m -> {
                accumulatedDamage += 15;
                log("* " + m.name() + " → +15 dégâts magiques");
            }
            default -> log("Item sélectionné");
        }
        
        log("Préparation: " + accumulatedDamage + " dég | " + 
            accumulatedProtection + " prot | " + accumulatedHeal + " soin");
        log("Énergie: " + hero.getEnergy() + "/" + hero.getMaxEnergy());
    }
    
    private void selectItemByIndex(int index) {
        var items = combat.getHero().getBackpack().getAllItems();
        if (index >= 0 && index < items.size()) {
            handleItemClick(items.get(index));
        }
    }
    
    private void cancelSelection() {
        if (selectedItems.isEmpty()) {
            log("Rien à annuler");
            return;
        }
        combat.getHero().resetEnergy();
        resetAccumulation();   
        log("Sélection annulée, énergie restaurée");
    }
    
    private int getEnergyCost(Item item) {
        return switch (item) {
            case Weapon w -> w.cost();
            case Armor a -> a.cost();
            case Magic _ -> 2;
            case Consumable _ -> 1;
            default -> 1;
        };
    }
    
    // ==================== EXÉCUTION DES TOURS ====================
    
    private void executeHeroTurn() {
        if (currentEnemy == null || combatOver) return;
        logImportant("── VOTRE TOUR ──");  
        var hero = combat.getHero();
        // Appliquer la protection
        if (accumulatedProtection > 0) {
            hero.addProtection(accumulatedProtection);
            log("Vous gagnez " + accumulatedProtection + " protection");
        }  
        // Appliquer le soin
        if (accumulatedHeal > 0) {
            var oldHp = hero.getHealthPoint();
            hero.heal(accumulatedHeal);
            log("Vous récupérez " + (hero.getHealthPoint() - oldHp) + " PV");
        }
        // Attaquer l'ennemi actuel
        if (accumulatedDamage > 0) {
            int actualDamage = currentEnemy.takeDamage(accumulatedDamage);
            log("Vous infligez " + actualDamage + " dégâts à " + currentEnemy.getType().getName());
            log(currentEnemy.getType().getName() + " : " + 
                currentEnemy.getHealthPoint() + "/" + currentEnemy.getMaxHealthPoint() + " PV");
        }
        // Consommer les consommables
        for (var item : selectedItems) {
            if (item instanceof Consumable) {
                hero.getBackpack().removeEquipment(item);
            }
        }
        // Vérifier si l'ennemi est mort
        if (currentEnemy.isDead()) {
          log("");
          logImportant("══════════════════════════════");
          logImportant(currentEnemy.getType().getName() + " VAINCU !");
          int remaining = enemyQueue.size() - enemyIndex - 1;
          if (remaining > 0) {
              log("Reste " + remaining + " ennemi" + (remaining > 1 ? "s" : "") + " à vaincre");
          }
          logImportant("══════════════════════════════");
          resetAccumulation();
          hero.resetEnergy();
          hero.resetProtection();
          nextEnemy();
          return;
      } 
        resetAccumulation();
        hero.resetEnergy();
        executeEnemyTurn();
    }
    
    private void executeEnemyTurn() {
        if (currentEnemy == null || combatOver) return;
        logImportant("── TOUR DE " + currentEnemy.getType().getName().toUpperCase() + " ──"); 
        var hero = combat.getHero();
        var action = currentEnemy.getNextAction();
        log("Exécute : " + action.getAnnouncement());
        switch (action.type()) {
            case ATTACK, HEAVY_ATTACK -> {
                int damage = action.value();
                int actualDamage = hero.takeDamage(damage);
                log(currentEnemy.getType().getName() + " vous inflige " + actualDamage + " dégâts !");
            }
            
            case MULTI_ATTACK -> {
                int totalDamage = 0;
                for (int i = 0; i < action.repeatCount(); i++) {
                    totalDamage += hero.takeDamage(action.value());
                }
                log(currentEnemy.getType().getName() + " vous inflige " + totalDamage + " dégâts !");
            }
            
            case DEFEND, FORTIFY -> {
                currentEnemy.addArmor(action.value());
                log(currentEnemy.getType().getName() + " gagne " + action.value() + " protection");
            }
            
            case HEAL -> {
                currentEnemy.heal(action.value());
                log(currentEnemy.getType().getName() + " récupère " + action.value() + " PV");
            }
            case CURSE -> {
              triggerCurse();
              return;  // Attendre la décision du joueur
            }
            default -> log(currentEnemy.getType().getName() + " ne fait rien");
        }
        
        log("Vos PV : " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint() + 
            " | Protection : " + hero.getProtection());
        
        // Vérifier défaite
        if (hero.isDead()) {
            combatOver = true;
            victory = false;
            logImportant("══ DÉFAITE ══");
            log("Vous avez été vaincu...");
            return;
        }
        currentEnemy.decideNextAction();
        hero.resetProtection();
        logImportant("── VOTRE TOUR ──");
        showEnemyIntention();
        log("Sélectionnez vos items puis ESPACE");
    }
    
    // ==================== GETTERS ====================
    
    public Combat getCombat() { return combat; }
    public boolean isCombatOver() { return combatOver; }
    public boolean isVictory() { return victory; }
    public Enemy getCurrentEnemy() { return currentEnemy; }
    public int getEnemyIndex() { return enemyIndex; }
    public int getTotalEnemies() { return enemyQueue.size(); }
    public int getAccumulatedDamage() { return accumulatedDamage; }
    public int getAccumulatedProtection() { return accumulatedProtection; }
    public boolean isWaitingForCurseDecision() { return waitingForCurseDecision; }
    public Curse getPendingCurse() { return pendingCurse; }
    // ==================== FACTORY ====================
    
    public static CombatController createFromRoom(EnemyRoom room, Hero hero, ApplicationContext context) {
        var combat = new Combat(hero, room.enemies());
        return new CombatController(combat, context);
    }
}