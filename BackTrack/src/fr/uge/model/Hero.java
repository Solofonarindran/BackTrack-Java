package fr.uge.model;

import java.util.Objects;

import fr.uge.data.BackPack;

public final class Hero implements Actor{
	// statistiques de base
	private int healthPoints; //Points de vie actuels
	private int maxHealthPoints; // Points de vie maximum
	private int level; //Niveau du Héros
	private	int experience; // Points d'expérience actuels
	private int maxEnergy ;

//Champ
	private int gold = 0;
	
	//Statistiques de combat
	//Energie disponible (3 par tour en combat), 
	//On ne peut pas le mettre final ( méthod useEnergy) 
	private int energy; 
	
  //On ne peut pas le mettre final ( méthod useMana) 
	private int manaPoints; // Point mana disponibles
	private int protection; // POint de protection temporaire
	
	//inventaire
	private final BackPack backPack ;
	private int keys; //
	
	public Hero(int healthPoints,int maxHealthPoints, int level, int experience,
							int maxEnergy, int energy, int manaPoints, int protection, BackPack backPack, int keys) {
		Objects.requireNonNull(backPack);
		
		this.healthPoints = healthPoints;
		this.maxHealthPoints = maxHealthPoints;
		this.maxEnergy = maxEnergy;
		this.level = level;
		this.experience = experience;
	
		this.energy = energy;
		this.manaPoints = manaPoints;
		this.protection = protection;
		this.backPack = backPack;
		this.keys = keys;
	}
	
	// Inflige des dégats au joueur
	
	public int takeDamage(int damage) { // On changera le paramètre damage par un item pour garder l'encapsulation
		if(damage < 0) {
			throw new IllegalArgumentException("dégât doit être valeur positif");
		}
		
		var restDamage = damage;	
	  // si le Hero a encore de point de protection , il l'utilise
		if(protection > 0) {
			if(protection >= damage) {
				protection -= damage;
				return 0;
			}else {
				restDamage = damage - protection;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		healthPoints = Math.max(0, healthPoints - restDamage);
		return restDamage;
}
	
	// méthode pour soigner le joueur 
	public int heal(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Le soin ne peut pas être négatif");
		}
		var oldHealthPoints = healthPoints;
		healthPoints = Math.min(maxHealthPoints, healthPoints + amount);
		return healthPoints - oldHealthPoints;
	}
	
	// méthode pour incrémenter la protection 
	public void addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		if (amount > 0 ) {
			protection += amount;
		}
		
	}
	
	// réinitialisé la protection
	public void resetProtection() {
		protection = 0;
	}
	
	
	//checker si un item peu utiliser grâce au point de l'energie
	// si oui , décrementer le point return true
	// si non , return false
	public boolean useEnergy(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		if(energy >= amount) {
			energy -= amount;
			return true;
		}
		return false;
	}
	
	//réinitialise le point de l'energy
	// en valeur max du point de l'energy
	public void resetEnergy() {
		energy = maxEnergy;
	}
	

	public int getExperienceToNextLevel() {
	    return level * 100;  // Niveau 1 = 100 XP, Niveau 2 = 200 XP, etc.
	}

	public boolean useMana(int cost) {
		if(manaPoints >= cost) {
			manaPoints-= cost;
			return true;
		}
		return false;
	}
	
	//incrémenter la valeur de mana
	public void increaseMana(int amount) {
		if (amount >0) {
			manaPoints += amount;
		}
	}
	
	//Exemple : niveau 2 = 100 XP, niveau 3 = 200 XP...
	private int getExperienceForNextLevel() {
		return level * 100; 
	}
	
	
	// xp point d'expérience qu'on a
	
	public void gainExperience(int xp) {
		if(xp < 0) {
			throw new IllegalArgumentException(" expérience doit toujours positive");
		}
		
		experience += xp;
		while(experience >= getExperienceForNextLevel()) {
			experience -= getExperienceToNextLevel();
			levelUp();
		}
	}
	
	
	//Méthodes
	public int getGold() {
	   return gold;
	}
	
	public void addGold(int amount) {
	   if (amount > 0) {
	       gold += amount;
	   }
	}
	
	public boolean spendGold(int amount) {
	   if (amount > 0 && gold >= amount) {
	       gold -= amount;
	       return true;
	   }
	   return false;
	}
	
		public void levelUp() {
	    level++;
	    
	    // Augmenter les stats
	    maxHealthPoints += 5;
	    maxEnergy++;
	    
	    // Soigner complètement
	    healthPoints = maxHealthPoints;
	    energy = maxEnergy;
	    
	    // Déverrouiller des cases du sac
	    int newSlots = getNewSlotsForLevel(level);
	    int unlocked = backPack.unlockNewSlots(newSlots);
	    
	    System.out.println("★ NIVEAU " + level + " !");
	    System.out.println("  +5 PV max (" + maxHealthPoints + ")");
	    System.out.println("  +1 Énergie max (" + maxEnergy + ")");
	    System.out.println("  +" + unlocked + " cases de sac déverrouillées");
	}

	private int getNewSlotsForLevel(int level) {
	  return switch (level) {
	      case 2, 3 -> 2;
	      case 4, 5 -> 3;
	      default -> level > 5 ? 3 : 0;
	  };
	}
	//==================== MALÉDICTION ====================

//Champ (ajouter avec les autres champs)
private int curseRefusalCount = 0;

//Refuser une malédiction → prend K dégâts (K = nombre de refus)
public int refuseCurse() {
   curseRefusalCount++;
   int damage = curseRefusalCount;
   takeDamage(damage);
   return damage;
}

	//Accepter une malédiction (la placer dans le sac)
	public boolean acceptCurse(Curse curse, Coordonate position) {
	   // Vérifier si la position est valide
	   if (!backPack.canPlaceCurse(curse, position)) {
	       return false;
	   }
	   
	   // Supprimer les items en dessous
	   backPack.removeItemsAt(curse.references(), position);
	   
	   // Placer la malédiction
	   backPack.placeCurse(curse, position);
	   
	   return true;
	}
	
	//Reset le compteur (nouveau combat ou nouvel étage)
	public void resetCurseRefusalCount() {
	   curseRefusalCount = 0;
	}
	
	//Getter
	public int getCurseRefusalCount() {
	   return curseRefusalCount;
	}
  // Getters
	
	@Override
  public int getHealthPoint() { return healthPoints; }
	@Override
  public int getMaxHealthPoint() { return maxHealthPoints; }
  public int getEnergy() { return energy; }
  public int getMaxEnergy() { return maxEnergy; }

  public int getProtection() { return protection; }
  public int getKeys() { return keys; }
  public int getLevel() { return level; }
  public int getExperience() { return experience; }
  public BackPack getBackpack() { return backPack; }
  
	// on va l'utiliser pour déverouiller l'un des salles
	public boolean useKey() {
		if(keys > 0) {
			keys --;
			return true;
		}
		return false;
	}
	
	//ajout du clef
	public void  addKey() {
		keys ++;
	}
	
	public void addKeys(int count) {
		if(count < 0) {
			throw new IllegalArgumentException();
		}
		keys += count; 
	}
	
	public boolean hasKey() {
		return keys > 0;
	}
	 public boolean isAlive() {
     return healthPoints > 0;
	 }
 
	 public boolean isDead() {
	     return healthPoints <= 0;
	 }
	@Override
	public String toString() {
		var str = "\n========== STATISTIQUES DU HÉROS ==========" +
							"❤️  Vie        : " + healthPoints + "/" + maxHealthPoints +
							"⚡ Énergie    : " + energy + "/" + maxEnergy + 
							"💙 Mana       : " + manaPoints + "/" + manaPoints +
							"🛡️  Protection : " + protection +
							"⭐ Niveau     : " + level +
							"✨ Expérience : " + experience + "/" + getExperienceForNextLevel() +
						
							"🔑 Clés       : " + keys +
							"==========================================\n";
		return str;
	}
}
