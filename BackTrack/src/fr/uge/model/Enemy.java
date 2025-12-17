package fr.uge.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.uge.data.CombatAction;

public final class Enemy implements Actor{
	
	private static final Random RANDOM = new Random();
	
	private final EnemyType type;
	private int healthPoints; 
	private final int maxHealthPoints; 
	
	private int armor ; // Protection temporaire 	
	private	final int baseArmor; // Armure de base
	private int baseDamage ; // Dégâts de base
	
	private CombatAction nextAction; // Action annoncée pour le prochain tour
	private final List<StatusEffect> statusEffects;
	private final List<Item> loot; // Items offerts à l'Héro si Enemy meurt
	
	private boolean isCharging; // Pour les attaques chargées
	private int chargeBonus;

	
  
  /**
   * Constructeur avec HP personnalisé (pour boss ou variantes)
   */
  public Enemy(EnemyType type, int customHealth) {
  	if (customHealth < 0) {
  		throw new IllegalArgumentException();
    }
  	
    this.type = Objects.requireNonNull(type);
    this.maxHealthPoints = type.getMaxHealth();

    this.baseArmor = type.getBaseArmor();
    this.armor = 0;
    this.baseDamage = type.getBaseDamage();
    this.statusEffects = new ArrayList<StatusEffect>();
    this.loot = new ArrayList<>();
    this.isCharging = false;
    this.chargeBonus = 0;
    
    if ( customHealth == 0) {
    	this.healthPoints = maxHealthPoints;
    }else {
    	this.healthPoints = customHealth;
    }
    
    // Première action annoncée
    decideNextAction();
  	  
  }
  
  public Enemy(EnemyType type) {
    this(type,0);
	}
  // ==================== COMBAT ====================
  
  /**
   * L'ennemi décide de sa prochaine action (IA)
   */
  public void decideNextAction() {
      // Si étourdi, ne fait rien
    if (isStunned()) {
      nextAction = CombatAction.action(CombatActionType.IDLE,0);
      return;
    }
    // Si en charge, libère l'attaque chargée
    if (isCharging) {
        isCharging = false;
        nextAction = CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + chargeBonus);
        chargeBonus = 0;
        return;
    }
    
 // IA basée sur le type d'ennemi et la situation
    nextAction = switch (type) {
        case RAT, BAT -> decideWeakEnemyAction();
        case SLIME -> decideSlimeAction();
        case GOBLIN, ORC -> decideAggressiveAction();
        case SKELETON -> decideSkeletonAction();
        case WOLF -> decideWolfAction();
        case TROLL -> decideTrollAction();
        case DARK_MAGE -> decideMageAction();
        case VAMPIRE -> decideVampireAction();
        case BOSS_GOLEM, BOSS_DRAGON, BOSS_DEMON -> decideBossAction();
    }; 
    
  }
  
  private CombatAction decideWeakEnemyAction() {
    // Ennemis faibles : attaque simple la plupart du temps
    if (RANDOM.nextDouble() < 0.8) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    }
    return CombatAction.action(CombatActionType.DEFEND,3);
  }
  
  private CombatAction decideSlimeAction() {
    var roll = RANDOM.nextDouble();
    if (roll < 0.6) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.8) {
        return CombatAction.action(CombatActionType.DEFEND,4);
    }
    return CombatAction.action(CombatActionType.HEAL,5);
  }
  
  private CombatAction decideAggressiveAction() {
    var roll = RANDOM.nextDouble();
    if (healthPoints < maxHealthPoints * 0.3) {
        // Faible en vie : devient plus agressif
        if (roll < 0.7) {
            return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 5);
        }
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    }
    
    if (roll < 0.5) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.75) {
        return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 3);
    }
    return CombatAction.action(CombatActionType.DEFEND,5);
  }
  
  private CombatAction decideSkeletonAction() {
    var roll = RANDOM.nextDouble();
    if (roll < 0.4) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.7) {
        return CombatAction.action(CombatActionType.MULTI_ATTACK,baseDamage / 2);
    }
    return CombatAction.action(CombatActionType.DEFEND,4);
  
  }
  
  private CombatAction decideWolfAction() {
    var roll = RANDOM.nextDouble();
    if (roll < 0.6) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.85) {
        return CombatAction.action(CombatActionType.MULTI_ATTACK,baseDamage / 2);
    }
    // Charge
    isCharging = true;
    chargeBonus = 8;
    return new CombatAction(CombatActionType.CHARGE, baseDamage + chargeBonus);
  }
  
  private CombatAction decideTrollAction() {
    var roll = RANDOM.nextDouble();
    if (healthPoints < maxHealthPoints * 0.5 && roll < 0.4) {
        return CombatAction.action(CombatActionType.HEAL,10);
    }
    if (roll < 0.5) {
        return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 5);
    } else if (roll < 0.8) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    }
    return CombatAction.action(CombatActionType.DEFEND,8);
  }

  private CombatAction decideMageAction() {
    var roll = RANDOM.nextDouble();
    if (roll < 0.35) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.55) {
        return CombatAction.action(CombatActionType.POISON,4);
    } else if (roll < 0.75) {
        return CombatAction.action(CombatActionType.CURSE,0);
    }
    return CombatAction.action(CombatActionType.DEFEND,6);
  }
  
  private CombatAction decideVampireAction() {
    var roll = RANDOM.nextDouble();
    if (healthPoints < maxHealthPoints * 0.6) {
        if (roll < 0.6) {
            return CombatAction.action(CombatActionType.ATTACK,baseDamage);
        }
    }
    if (roll < 0.5) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage);
    } else if (roll < 0.8) {
        return CombatAction.action(CombatActionType.ATTACK,baseDamage - 2);
    }
    return CombatAction.action(CombatActionType.DEFEND,5);
  }

	 private CombatAction decideBossAction() {
     var roll = RANDOM.nextDouble();
     var phase = (double) healthPoints / maxHealthPoints;
     
     // Phase 1 (> 66% HP) : Attaques normales
     if (phase > 0.66) {
         if (roll < 0.5) {
             return CombatAction.action(CombatActionType.ATTACK,baseDamage);
         } else if (roll < 0.8) {
             return CombatAction.action(CombatActionType.DEFEND,10);
         }
         return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 5);
     }
     
     // Phase 2 (33-66% HP) : Plus agressif
     if (phase > 0.33) {
         if (roll < 0.4) {
             return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 8);
         } else if (roll < 0.7) {
             return CombatAction.action(CombatActionType.MULTI_ATTACK,baseDamage / 2);
         } else if (roll < 0.85) {
             return CombatAction.action(CombatActionType.CURSE,0);
         }
         return CombatAction.action(CombatActionType.DEFEND,12);
     }
     
     // Phase 3 (< 33% HP) : Mode rage
     if (roll < 0.5) {
         return CombatAction.action(CombatActionType.HEAVY_ATTACK,baseDamage + 12);
     } else if (roll < 0.8) {
         return CombatAction.action(CombatActionType.MULTI_ATTACK,baseDamage / 2);
     }
     return CombatAction.action(CombatActionType.ATTACK,baseDamage);
	 }
	
   /**
    * Exécute l'action annoncée
    * @return l'action exécutée
    */
   public CombatAction executeAction() {
       var action = nextAction;
       
       // Appliquer les effets de l'action sur l'ennemi lui-même
       switch (action.type()) {
           case DEFEND -> armor += action.value();
           case HEAL -> healthPoints = Math.min(maxHealthPoints, healthPoints + action.value());
           case ENRAGE -> baseDamage += action.value();
           case FORTIFY -> armor += action.value();
           default -> {}
       }
       
       // Décider la prochaine action
       decideNextAction();
       
       return action;
   }

   /**
    * Inflige des dégâts à l'ennemi
    * @return les dégâts réellement infligés
    */
   public int takeDamage(int damage) {
       if (damage <= 0) return 0;
       
       var actualDamage = damage;
       
       // Réduction par l'armure temporaire
       if (armor > 0) {
           if (armor >= damage) {
               armor -= damage;
               return 0;
           } else {
               actualDamage = damage - armor;
               armor = 0;
           }
       }
       
       // Réduction par l'armure de base
       actualDamage = Math.max(1, actualDamage - baseArmor);
       
       healthPoints = Math.max(0, healthPoints - actualDamage);
       return actualDamage;
   }
   
   /**
    * Ajoute de la protection temporaire
    */
   public void addArmor(int amount) {
       if (amount > 0) {
           armor += amount;
       }
   }
   
   /**
    * Réinitialise la protection temporaire (début de tour)
    */
   public void resetArmor() {
       armor = 0;
   }
   
   public void addStatusEffect(StatusEffect effect) {
     Objects.requireNonNull(effect);
     statusEffects.add(effect);
   }
 
   public void removeStatusEffect(StatusEffect effect) {
     statusEffects.remove(effect);
   }
 
	 /**
	  * Applique les effets de début de tour
	  * @return le total des dégâts/soins subis
	  */
	 public int applyStatusEffects() {
	     var total = 0;
	     var expiredEffects = new ArrayList<StatusEffect>();
	     
	     for (var effect : statusEffects) {
	         total += effect.applyStartOfTurn();
	         if (!effect.tick()) {
	             expiredEffects.add(effect);
	         }
	     }
	     
	     statusEffects.removeAll(expiredEffects);
	     
	     // Appliquer les dégâts/soins
	     if (total < 0) {
	         healthPoints = Math.max(0, healthPoints + total);
	     } else if (total > 0) {
	         healthPoints = Math.min(maxHealthPoints, healthPoints + total);
	     }
	     
	     return total;
	 }
	 
	 public boolean isStunned() {
	   return statusEffects.stream().anyMatch(StatusEffect::preventsAction);
	 }
	
		public List<StatusEffect> getStatusEffects() {
		   return Collections.unmodifiableList(statusEffects);
		}
	
	/**
	* @return le modificateur total de dégâts des effets
	*/
	public int getDamageModifier() {
	   return statusEffects.stream()
	       .mapToInt(StatusEffect::getDamageModifier)
	       .sum();
	}
	
  // ==================== LOOT ====================
  
  public void addLoot(Item item) {
      Objects.requireNonNull(item);
      loot.add(item);
  }
  
  public List<Item> getLoot() {
      return Collections.unmodifiableList(loot);
  }
  
  public List<Item> dropLoot() {
      var dropped = new ArrayList<>(loot);
      loot.clear();
      return dropped;
  }
  
  public EnemyType getType() {
    return type;
  }

	@Override
	public int getHealthPoint() {
	    return healthPoints;
	}
	
	@Override
	public int getMaxHealthPoint() {
	    return maxHealthPoints;
	}
	
	public int getArmor() {
	    return armor;
	}
	
	public int getBaseArmor() {
	    return baseArmor;
	}
	
	public int getBaseDamage() {
	    return baseDamage;
	}
	
	public int getEffectiveDamage() {
	    return Math.max(0, baseDamage + getDamageModifier());
	}
	
	public CombatAction getNextAction() {
	    return nextAction;
	}
	
	public int getExperienceReward() {
	    return type.getExperienceReward();
	}
	
	public boolean isDead() {
	    return healthPoints <= 0;
	}
	
	public boolean isBoss() {
	    return type.isBoss();
	}
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
