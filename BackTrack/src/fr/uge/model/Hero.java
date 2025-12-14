package fr.uge.data;

import java.util.Objects;

public final class Hero implements Actor{
	// statistiques de base
	private final int healthPoints; //Points de vie actuels
	private final int maxHealthPoints; // Points de vie maximum
	private int level; //Niveau du Héros
	private	int experience; // Points d'expérience actuels
	private final int maxEnergy ;
	
	//Statistiques de combat
	//Energie disponible (3 par tour en combat), 
	//On ne peut pas le mettre final ( méthod useEnergy) 
	private int energy; 
	
  //On ne peut pas le mettre final ( méthod useMana) 
	private int manaPoints; // Point mana disponibles
	private final int protection; // POint de protection temporaire
	
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
	
	public Hero takeDamage(int damage) { // On changera le paramètre damage par un item pour garder l'encapsulation
		if(damage < 0) {
			throw new IllegalArgumentException("dégât doit être valeur positif");
		}
		var newProtection = 0;
		var restDamage = damage;	
	  // si le Hero a encore de point de protection , il l'utilise
		if(protection > 0) {
			if(protection >= damage) {
				newProtection = protection - damage;
				return new Hero(healthPoints,maxHealthPoints,level,experience, maxEnergy, energy,manaPoints,newProtection,backPack,keys);
			}else {
				restDamage = damage - protection;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		var newHealthPoints = Math.max(0, healthPoints - restDamage);
		return new Hero(newHealthPoints, maxHealthPoints, level, experience, maxEnergy, energy, manaPoints, newProtection, backPack, keys);
	}
	
	// méthode pour soigner le joueur 
	public Hero heal(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Le soin ne peut pas être négatif");
		}
		var newHealthPoints = Math.min(maxHealthPoints, healthPoints + amount);
		return new Hero(newHealthPoints, maxHealthPoints, level, experience, maxEnergy, energy, manaPoints, protection, backPack, keys);
	}
	
	// méthode pour incrémenter la protection 
	public Hero addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		var newProtection = protection + amount;
		return new Hero(healthPoints, maxHealthPoints, level, experience, maxEnergy, energy, manaPoints, newProtection, backPack, keys);
	}
	
	// réinitialisé la protection
	public Hero resetProtection() {
		return new Hero(healthPoints, maxHealthPoints, level, experience, maxEnergy, energy, manaPoints, 0, backPack, keys);
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
	public Hero resetEnergy() {
		return new Hero(healthPoints, maxHealthPoints, level, experience, maxEnergy, maxEnergy, manaPoints, protection, backPack, keys);
	}
	
	public boolean useMana(int cost) {
		if(manaPoints >= cost) {
			manaPoints-= cost;
			return true;
		}
		return false;
	}
	
	//incrémenter la valeur de mana
	public Hero increaseMana(int amount) {
		var newManaPoints = manaPoints + amount;
		return new Hero(healthPoints, maxHealthPoints, level, experience, maxEnergy, energy, newManaPoints, protection, backPack, keys);
	}
	
	//Exemple : niveau 2 = 100 XP, niveau 3 = 200 XP...
	private int getExperienceForNextLevel() {
		return level * 100; 
	}
	
	//Monter de niveau
	private void levelUp() {
		experience -= getExperienceForNextLevel();
		level ++;
		
		System.out.println("🎉 NIVEAU SUPÉRIEUR ! Vous êtes maintenant niveau " + level);
    System.out.println("💼 Votre sac à dos peut être agrandi de 3-4 cases !");
	}
	// xp point d'expérience qu'on a
	
	public void gainExperience(int xp) {
		if(xp < 0) {
			throw new IllegalArgumentException(" expérience doit toujours positive");
		}
		experience += xp;
		while(experience >= getExperienceForNextLevel()) {
			levelUp();
		}
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
	public Hero addKey() {
		return new Hero(healthPoints, maxHealthPoints, level, experience, maxEnergy, energy, manaPoints, protection, backPack, keys ++ );
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
