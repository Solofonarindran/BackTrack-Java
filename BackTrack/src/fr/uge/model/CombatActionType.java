package fr.uge.model;

import java.util.Objects;

/**
* Types d'actions possibles en combat.
*/
public enum CombatActionType {
   // Actions offensives
   ATTACK("Attaque", "⚔️", "inflige des dégâts"),
   HEAVY_ATTACK("Attaque lourde", "🗡️", "inflige de lourds dégâts"),
   MULTI_ATTACK("Attaque multiple", "⚔️⚔️", "attaque plusieurs fois"),
   
   // Actions défensives
   DEFEND("Défense", "🛡️", "gagne de la protection"),
   HEAL("Soin", "💚", "récupère des PV"),
   
   // Actions de buff
   ENRAGE("Rage", "😡", "augmente ses propres dégâts"),
   FORTIFY("Fortifier", "🏰", "augmente sa défense"),
   
   
   // Actions de debuff
   POISON("Poison", "🧪", "empoisonne la cible"),
   WEAKEN("Affaiblir", "📉", "réduit les dégâts de la cible"),
   CURSE("Malédiction", "💀", "place une malédiction dans le sac"),
   
   // Prépare une attaque puissante
   CHARGE("Charge", "💨", "prépare une attaque puissante"),
   // Aucune action
   IDLE("Repos", "💤", "ne fait rien");
	
	private final String name;
  private final String icon;
  private final String description;
  
	CombatActionType(String name, String icon, String description) {
		this.name = Objects.requireNonNull(name);
		this.icon = Objects.requireNonNull(icon);
		this.description = Objects.requireNonNull(description);
	}
	
	public String getName() {
		return name;
	}
  public String getIcon() {
    return icon;
  }

	public String getDescription() {
	    return description;
	}
	
	/**
	 * @return true si l'action cible le héros
	 */
	public boolean targetsHero() {
		return switch(this) {
			case ATTACK, HEAVY_ATTACK, MULTI_ATTACK, POISON, WEAKEN, CURSE -> true;
			default -> false;
		};
	}
	
  /**
   * @return true si l'action est défensive
   */
	public boolean isDefensive() {
		return switch(this) {
			case DEFEND,HEAL,FORTIFY -> true;
			default -> false;
		};
	}
}
