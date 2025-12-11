package fr.uge.data;

import java.util.Objects;

public final class Hero {
	// statistiques de base
	private int healthPoints; //Points de vie actuels
	private int maxHealthPoints; // Points de vie maximum
	private int level; //Niveau du Héros
	private	int experience; // Points d'expérience actuels
	private int maxEnergy ;
	
	//Statistiques de combat
	//Energie disponible (3 par tour en combat), 
	//On ne peut pas le mettre final ( méthod useEnergy) 
	private int energy; 
	private int protection; // Point de protection temporaire
	//inventaire
	private BackPack backPack;
	private int keys; //
	
	public Hero(int healthPoints,int maxHealthPoints,
							int energy, int maxEnergy, int protection, BackPack backPack, int keys, int experience, int level) {
		Objects.requireNonNull(backPack);
		
		this.healthPoints = healthPoints;
		this.maxHealthPoints = maxHealthPoints;
		this.energy = energy;
		this.maxEnergy = maxEnergy;
		this.protection = protection;
		this.backPack = Objects.requireNonNull(backPack);
		this.keys = keys;
		
		this.experience = experience;
		this.level = level;
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
				return new Hero(healthPoints, maxHealthPoints, energy, maxEnergy, newProtection, backPack, keys, experience, level);
			}else {
				restDamage = damage - protection;
				newProtection = 0;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		var newHealthPoints = Math.max(0, healthPoints - restDamage);
		return new Hero(newHealthPoints, maxHealthPoints, energy, maxEnergy, newProtection, backPack, keys, experience, level);
	}
	
	// méthode pour soigner le joueur 
	public Hero heal(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Le soin ne peut pas être négatif");
		}
		var newHealthPoints = Math.min(maxHealthPoints, healthPoints + amount);
		return new Hero(newHealthPoints, maxHealthPoints, energy, maxEnergy, protection, backPack, keys, experience, level);
	}
	
	// méthode pour incrémenter la protection 
	public Hero addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		var newProtection = protection + amount;
		return new Hero(healthPoints, maxHealthPoints, energy, maxEnergy, newProtection, backPack, keys, experience, level);
	}
	
	// réinitialisé la protection
	public Hero resetProtection() {
		return new Hero(healthPoints, maxHealthPoints, energy, maxEnergy, 0, backPack, keys, experience, level);
	}
	
	
	//checker si un item peu utiliser grâce au point de l'energie
	// si oui , décrementer le point return true
	// si non , return false
	public Hero useEnergy(int amount) {
	    if(energy < amount) {
	        throw new IllegalStateException("Pas assez d'énergie !");
	    }
	    return new Hero(healthPoints, maxHealthPoints, energy - amount, maxEnergy,
	                    protection, backPack, keys, experience, level);
	}

	
	//réinitialise le point de l'energy
	// en valeur max du point de l'energy
	public Hero resetEnergy() {
		return new Hero(healthPoints, maxHealthPoints, maxEnergy, maxEnergy, protection, backPack, keys, experience, level);
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
		return new Hero(healthPoints, maxHealthPoints, energy, maxEnergy, protection, backPack, keys++, experience, level);
	}
	
	
	public int getHealthPoints() {
		return healthPoints;
	}
	
	public int getProtection() {
		return protection;
	}
	
	public BackPack getBackPack() {
		return backPack;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public int getMaxHealthPoints() {
		return maxHealthPoints;
	}
	
	@Override
	public String toString() {
		var str = "\n========== STATISTIQUES DU HÉROS ==========" +
							"❤️  Vie        : " + healthPoints + "/" + maxHealthPoints +
							"⚡ Énergie    : " + energy + "/" + maxEnergy + 
							"🛡️  Protection : " + protection +
							"⭐ Niveau     : " + level +
							"✨ Expérience : " + experience + "/" + getExperienceForNextLevel() +
						
							"🔑 Clés       : " + keys +
							"==========================================\n";
		return str;
	}
}
