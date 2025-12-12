package fr.uge.data;

import java.util.Objects;

public final class Enemy {

	// statistiques de base
	private int healthPoints; //Points de vie actuels
	private int maxHealthPoints; // Points de vie maximum
	private int protection; // Point de protection temporaire
	private String name;
	
	
	public Enemy(String name, int healthPoints, int maxHealthPoints, int protection) {
		Objects.requireNonNull(name);
		if (maxHealthPoints <= 0) {
            throw new IllegalArgumentException("Les PV maximum doivent être > 0");
        }
        if (protection < 0) {
            throw new IllegalArgumentException("La protection ne peut être négative");
        }
        this.name = name;
		this.healthPoints = healthPoints;
		this.maxHealthPoints = maxHealthPoints;
		this.protection = protection;

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
				return new Enemy(name, healthPoints, maxHealthPoints, newProtection);
			} else {
				restDamage = damage - protection;
				newProtection = 0;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		var newHealthPoints = Math.max(0, healthPoints - restDamage);
		return new Enemy(name, newHealthPoints, maxHealthPoints, newProtection);
	}

	
	// méthode pour incrémenter la protection 
	public Enemy addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		var newProtection = protection + amount;
		return new Enemy(name, healthPoints, maxHealthPoints, newProtection);
	}
	
	// réinitialisé la protection
	public Enemy resetProtection() {
		return new Enemy(name, healthPoints, maxHealthPoints, 0);
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
