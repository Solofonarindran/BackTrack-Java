package fr.uge.data;

public class Enemy {

	// statistiques de base
	public int healthPoints; //Points de vie actuels
	private final int maxHealthPoints; // Points de vie maximum
	private final int protection; // Point de protection temporaire
	
	
	public Enemy(int healthPoints,int maxHealthPoints, int protection) {
		
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
				return new Enemy(healthPoints, maxHealthPoints, newProtection);
			}else {
				restDamage = damage - protection;
			}
		}
	  // si non le dégât s'implique directement au point de vie (healthPoint) de 
		var newHealthPoints = Math.max(0, healthPoints - restDamage);
		return new Enemy(newHealthPoints, maxHealthPoints, newProtection);
	}

	
	// méthode pour incrémenter la protection 
	public Enemy addProtection(int amount) { // On changera le paramètre amount par un item pour garder l'encapsulation
		var newProtection = protection + amount;
		return new Enemy(healthPoints, maxHealthPoints, newProtection);
	}
	
	// réinitialisé la protection
	public Enemy resetProtection() {
		return new Enemy(healthPoints, maxHealthPoints, 0);
	}
	
	
	@Override
	public String toString() {
		var str = "\n========== STATISTIQUES DE L'ENNEMI ==========" +
							"❤️  Vie        : " + healthPoints + "/" + maxHealthPoints +
							"🛡️  Protection : " + protection +
							"==========================================\n";
		return str;
	}
}
