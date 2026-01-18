package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.uge.model.Armor;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Enemy;
import fr.uge.model.EnemyType;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Malediction;
import fr.uge.model.Weapon;

public class Combat {
	public enum CombatState {
    NOT_STARTED,
    HERO_TURN,
    ENEMY_TURN,
    VICTORY,
    DEFEAT
	}
	
	
	public record CombatLog(int turn, String message, boolean isImportant) {
    public CombatLog(int turn, String message) {
      this(turn, message, false);
    }
  }
	
	private final Hero hero;
  private final List<Enemy> enemies;
  private final List<Enemy> deadEnemies;
  private final List<CombatLog> logs;
  
  private CombatState state;
  private int currentTurn;
  private int currentEnemyIndex;
  
  // Récompenses accumulées
  private int totalExperience;
  private final List<Item> lootedItems;
  
  public Combat(Hero hero, List<Enemy> enemies) {
    this.hero = Objects.requireNonNull(hero);
    Objects.requireNonNull(enemies);
    if (enemies.isEmpty()) {
        throw new IllegalArgumentException("Au moins un ennemi requis");
    }
    this.enemies = new ArrayList<>(enemies);
    this.deadEnemies = new ArrayList<>();
    this.logs = new ArrayList<>();
    this.lootedItems = new ArrayList<>();
    this.state = CombatState.NOT_STARTED;
    this.currentTurn = 0;
    this.currentEnemyIndex = 0;
    this.totalExperience = 0;
  }
  
//Dans Combat.java, ajouter ces méthodes publiques :

	public void addLog(String message, boolean important) {
	   logs.add(new CombatLog(currentTurn, message, important));
	}
	
	public void addLog(String message) {
	   addLog(message, false);
	}
  /**
   * Constructeur pour un seul ennemi
   */
  	public Combat(Hero hero, Enemy enemy) {
      this(hero, List.of(enemy));
  	}
  
  /**
   * Démarre le combat
   */
  public void start() {
  	if (state != CombatState.NOT_STARTED) {
  		throw new IllegalStateException("Combat déjà démarré");
    }
      
    currentTurn = 1;
    state = CombatState.HERO_TURN;
    log("═══════ COMBAT COMMENCE ! ═══════", true);
    log("Tour " + currentTurn);
      
      // Les ennemis annoncent leur première action
    for (var enemy : enemies) {
    	log(enemy.getType().getName() + " prépare: " + enemy.getNextAction().getAnnouncement());
    }
      
     // Reset énergie du héros
    hero.resetEnergy();
    log("⚡ Énergie restaurée: " + hero.getEnergy() + "/" + hero.getMaxEnergy());
  }
  
  
  
	
  /**
   * Début d'un nouveau tour
   */
  private void startNewTurn() {
  	currentTurn++;
    log("──────── TOUR " + currentTurn + " ────────", true);
      
    // Reset protection du héros
    hero.resetProtection();
      
    // Reset énergie du héros
    hero.resetEnergy();
    log("⚡ Énergie restaurée: " + hero.getEnergy() + "/" + hero.getMaxEnergy());
      
    // Les ennemis ont déjà annoncé leurs actions
    for (var enemy : getAliveEnemies()) {
      log(enemy.getType().getName() + " prépare: " + enemy.getNextAction().getAnnouncement());
    }
      
    state = CombatState.HERO_TURN;
  }
  
  /**
   * Le héros utilise un item
   * @return true si l'utilisation a réussi
   */
  public boolean useItem(Item item) {
    if (state != CombatState.HERO_TURN) {
    	log("❌ Ce n'est pas votre tour !");
      return false;
    }
    Objects.requireNonNull(item);  
    // Vérifier si l'item est dans le sac
    if (!hero.getBackpack().contains(item)) {
    	log("❌ Item non trouvé dans le sac");
      return false;
    }
    // Calculer le coût en énergie
    var energyCost = getItemEnergyCost(item);
      
    // Vérifier l'énergie
    if (!hero.useEnergy(energyCost)) {
    	log("❌ Pas assez d'énergie ! (besoin: " + energyCost + ", disponible: " + hero.getEnergy() + ")");
      return false;
    }
    
     // Appliquer l'effet de l'item
     applyItemEffect(item); 
     log("⚡ Énergie restante: " + hero.getEnergy() + "/" + hero.getMaxEnergy());
     return true;
  }
  /**
   * Calcule le coût en énergie d'un item
   */
  public int getItemEnergyCost(Item item) {
    return switch (item) {
    	case Weapon w -> w.cost();
      case Armor a -> a.cost();
      case Magic _ -> 2; // Les sorts coûtent plus
      case Consumable _ -> 1;
      default -> 1;
    };
  }
  
  /**
   * Applique l'effet d'un item
   */
  private void applyItemEffect(Item item) {
    var target = getCurrentEnemy();  
      switch (item) {
      	case Weapon weapon -> {
      		var damage = weapon.healthPoint(); // healthPoint = dégâts pour les armes
          var actualDamage = target.takeDamage(damage);
          log("⚔️ " + weapon.name() + " inflige " + actualDamage + " dégâts à " + target.getType().getName());
          checkEnemyDeath(target);
        }  
        case Armor armor -> {
        	var protection = armor.defensePoint();
          	hero.addProtection(protection);
            log("🛡️ " + armor.name() + " ajoute " + protection + " protection");
          }
          
        case Magic magic -> {
          // Vérifie le mana
        	var manaCost = magic.manaMax();
          if (!hero.useMana(manaCost)) {
          	log("❌ Pas assez de mana !");
            return;
          }
         // Effet selon le type de magie
          log("✨ " + magic.name() + " lancé !");
          }
          
          case Consumable consumable -> {
          	
            // Les consommables soignent généralement
            hero.heal(10); // Valeur par défaut
            log("🧪 " + consumable.name() + " utilisé - Soin de 10 PV");
            // Retirer du sac après utilisation
            hero.getBackpack().removeEquipment(consumable);
          }
          
          default -> log("❓ Item utilisé: " + item);
      	}
      
  }
  
  /**
   * Vérifie si un ennemi est mort et gère les conséquences
   */
  private void checkEnemyDeath(Enemy enemy) {
  	if (enemy.isDead()) {
  		log("💀 " + enemy.getType().getName() + " est vaincu !", true);
          
      // Récupérer l'XP
      totalExperience += enemy.getExperienceReward();
      log("✨ +" + enemy.getExperienceReward() + " XP");
          
      // Récupérer le loot
      var loot = enemy.dropLoot();
      if (!loot.isEmpty()) {
      	lootedItems.addAll(loot);
        log("💎 Butin obtenu !");
      }
      deadEnemies.add(enemy);
      enemies.remove(enemy);
          
      // Vérifier victoire
      if (enemies.isEmpty()) {
      	state = CombatState.VICTORY;
        log("═══════ VICTOIRE ! ═══════", true);
        log("XP total gagné: " + totalExperience);
      }
    }
  }
  /**
   * Le héros termine son tour
   */
  public void endHeroTurn() {
  	if (state != CombatState.HERO_TURN) {
  		return;
    }
      
    if (state == CombatState.VICTORY || state == CombatState.DEFEAT) {
    	return;
    }
    log("── Fin du tour du héros ──");
    state = CombatState.ENEMY_TURN;
    currentEnemyIndex = 0;  
    // Exécuter les actions des ennemis
    executeEnemyTurns();
  }
  
  /**
   * Exécute les tours de tous les ennemis
   */
  private void executeEnemyTurns() {
  	for (var enemy : getAliveEnemies()) {
  		if (state == CombatState.DEFEAT) break;
          
      // Appliquer les effets de statut
      int statusDamage = enemy.applyStatusEffects();
      if (statusDamage != 0) {
      	log(enemy.getType().getName() + " subit " + Math.abs(statusDamage) + " dégâts des effets");
        checkEnemyDeath(enemy);
        if (enemy.isDead()) continue;
      }
          
      // Exécuter l'action annoncée
      executeEnemyAction(enemy);
   }
      
   // Si pas de défaite, préparer le prochain tour
    if (state == CombatState.ENEMY_TURN) {
    // Les ennemis restants annoncent leur prochaine action
    for (var enemy : getAliveEnemies()) {
    	enemy.resetArmor(); // Reset armure temporaire
    }
    	startNewTurn();
    }
  }
  
  /**
   * Exécute l'action d'un ennemi
   */
  private void executeEnemyAction(Enemy enemy) {
  	var action = enemy.executeAction();
    log(enemy.getType().getName() + " exécute: " + action.getAnnouncement());  
    switch (action.type()) {
    	case ATTACK, HEAVY_ATTACK -> {
    		var damage = action.value() + enemy.getDamageModifier();
        hero.takeDamage(damage);
        // Note: Hero est immutable, il faudrait gérer ça différemment
        // Pour l'instant on log juste
        log("💥 Vous subissez " + damage + " dégâts !");
        checkHeroDeath();
      }
          
      case MULTI_ATTACK -> {
      	var totalDamage = 0;
        for (int i = 0; i < action.repeatCount(); i++) {
        	var damage = action.value() + enemy.getDamageModifier();
          	hero.takeDamage(damage);
            totalDamage += damage;
         }
         log("💥💥 Vous subissez " + totalDamage + " dégâts (" + action.repeatCount() + " coups) !");
         checkHeroDeath();
       }  
       case DEFEND -> {
      	 enemy.addArmor(action.value());
         log("🛡️ " + enemy.getType().getName() + " gagne " + action.value() + " protection");
       }
          
       case HEAL -> {
      	 enemy.heal(action.value());
         log("💚 " + enemy.getType().getName() + " récupère " + action.value() + " PV");
       }
       case POISON -> {
      	 // Ajouter effet poison au héros
         log("🧪 Vous êtes empoisonné ! (" + action.value() + " dégâts/tour)");
         // TODO: implémenter les effets sur le héros
       }
          
       case CURSE -> {
         // Ajouter une malédiction au sac
         var curse = new Malediction(
                 	List.of(new Coordonate(0, 0), new Coordonate(1, 0)),
                  List.of(new Coordonate(0, 0), new Coordonate(1, 0))
              );
         var destroyed = hero.getBackpack().forceMalediction(curse, new Coordonate(3, 2));
         log("💀 Une MALÉDICTION apparaît dans votre sac !");
         if (!destroyed.isEmpty()) {
        	 log("😱 Items détruits: " + destroyed.size());
         }
       }
       case IDLE -> {
      	 log("💤 " + enemy.getType().getName() + " ne fait rien");
       }
       default -> {}
    }
  }
  
  /**
   * Vérifie si le héros est mort
   */
  private void checkHeroDeath() {
  	if (hero.isDead()) {
  		state = CombatState.DEFEAT;
      log("═══════ DÉFAITE ! ═══════", true);
      log("Vous avez été vaincu...");
    }
  }
  
  // ==================== UTILITAIRES ====================
  
  private void log(String message) {
  	logs.add(new CombatLog(currentTurn, message));
  }
  
  private void log(String message, boolean important) {
    logs.add(new CombatLog(currentTurn, message, important));
  }

  public List<Enemy> getAliveEnemies() {
    return new ArrayList<>(enemies);
  }
  
  public Enemy getCurrentEnemy() {
    if (enemies.isEmpty()) return null;
    return enemies.get(Math.min(currentEnemyIndex, enemies.size() - 1));
  }

	/**
	 * Change la cible actuelle
	 */
  public void setTargetEnemy(int index) {
    if (index >= 0 && index < enemies.size()) {
        currentEnemyIndex = index;
    }
  }
  
  // ==================== GETTERS ====================
  
  public Hero getHero() {
  	return hero;
  }
  
  public List<Enemy> getEnemies() {
  	return Collections.unmodifiableList(enemies);
  }
  
  public CombatState getState() {
  	return state;
  }
  
  public int getCurrentTurn() {
  	return currentTurn;
  }
  
  public List<CombatLog> getLogs() {
  	return Collections.unmodifiableList(logs);
  }
  
  public int getTotalExperience() {
	    int total = 0;
	    for (var enemy : enemies) {
	        total += enemy.getExperienceValue();
	    }
	    return total;
	}

	public List<Item> getLootedItems() {
	    // Pour l'instant, retourne une liste vide
	    // Phase 4B ajoutera le vrai loot
	    return new ArrayList<>();
	}
  public List<CombatLog> getRecentLogs(int count) {
    int start = Math.max(0, logs.size() - count);
    return logs.subList(start, logs.size());
  }


  
  public boolean isOver() {
  	return state == CombatState.VICTORY || state == CombatState.DEFEAT;
  }
  
  public boolean isVictory() {
    return state == CombatState.VICTORY;
  }
  
  public boolean isDefeat() {
    return state == CombatState.DEFEAT;
  }
  
  // ==================== FACTORY ====================
  
  /**
   * Crée un ennemi aléatoire pour un étage donné
   */
  public static Enemy createForFloor(int floorNumber) {
  	return new Enemy(EnemyType.getRandomForFloor(floorNumber));
  }
  
  /**
   * Crée un boss pour un étage donné
   */
  public static Enemy createBossForFloor(int floorNumber) {
  	return new Enemy(EnemyType.getBossForFloor(floorNumber));
  }
}
