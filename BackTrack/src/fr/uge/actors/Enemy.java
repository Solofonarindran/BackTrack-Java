package fr.uge.actors;

import java.util.Objects;

public final class Enemy {

	// statistiques de base
	private int healthPoints; //Points de vie actuels
	private int maxHealthPoints; // Points de vie maximum
	private int protection; // Point de protection temporaire
	private String name;
	private int nbXp; // points d'xp qu'on gagne grâce à l'ennemi
	private int nbCases; // nombre de cases qu'on gagne grâce à l'ennemi
	
	
	public Enemy(String name, int healthPoints, int maxHealthPoints, int protection, int nbXp, int nbCases) {
		Objects.requireNonNull(name);
		if (maxHealthPoints <= 0) {
            throw new IllegalArgumentException("Les PV maximum doivent être > 0");
        }
        if (protection < 0) {
            throw new IllegalArgumentException("La protection ne peut être négative");
        }
        if (nbXp < 0) {
            throw new IllegalArgumentException("Les points d'expérience ne peuvent pas être négatifs");
        }
        if (nbCases < 0) {
            throw new IllegalArgumentException("Le nombre de cases ne peut pas être négatif");
        }
        this.name = name;
		this.healthPoints = healthPoints;
		this.maxHealthPoints = maxHealthPoints;
		this.protection = protection;
		this.nbXp = nbXp;
		this.nbCases = nbCases;

	}
	
	// Inflige des dégats à l'ennemi
	
	public Enemy takeDamage(int damage) { // On changera le paramètre damage par un item pour garder l'encapsulation
		if(damage < 0) {
			throw new IllegalArgumentException("dégât doit être valeur positif");
		}
		var newProtection = 0;
		var restDamage = damage;	
	  // si le Enemy a encore de point de protection , il l'utilise
		if(protection > 0) {
			if(protection >= damage) {
				newProtection = protection - damage;
				return new Enemy(name, healthPoints, maxHealthPoints, newProtection, nbXp, nbCases);
			} else {
				restDamage = damage - protection;
				newProtection = 0;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		var newHealthPoints = Math.max(0, healthPoints - restDamage);
		return new Enemy(name, newHealthPoints, maxHealthPoints, newProtection, nbXp, nbCases);
	}

	
	// méthode pour incrémenter la protection 
	public Enemy addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		var newProtection = protection + amount;
		return new Enemy(name, healthPoints, maxHealthPoints, newProtection, nbXp, nbCases);
	}
	
	// réinitialisé la protection
	public Enemy resetProtection() {
		return new Enemy(name, healthPoints, maxHealthPoints, 0, nbXp, nbCases);
	}
	
	public int getHealthPoints() {
		return healthPoints;
	}
	
	public String getName() {
		return name;
	}
	
	public int getProtection() {
		return protection;
	}
	
	public int getNbXp() {
		return nbXp;
	}
	
	public int getNbCases() {
		return nbCases;
	}
	
	@Override
	public String toString() {
		var str = "\n========== STATISTIQUES DE L'ENNEMI ==========" +
							"    Nom        : " + name +
							"❤️  Vie        : " + healthPoints + "/" + maxHealthPoints +
							"🛡️  Protection : " + protection +
							"==========================================\n";
		return str;
	}
}
