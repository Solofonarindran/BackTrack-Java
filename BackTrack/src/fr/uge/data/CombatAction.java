package fr.uge.data;

import java.util.Objects;

import fr.uge.model.CombatActionType;



/**
 * value : Dégâts , soin, protection, etc
 * repeatCount // Nombre de répétitions (pour MULTI_ATTACK
 */
public record CombatAction(CombatActionType type, int value, int repeatCount) {
	public CombatAction {
		Objects.requireNonNull(type);
		
	   if (value < 0) {
       throw new IllegalArgumentException("La valeur ne peut pas être négative");
	   }
	   if (repeatCount < 1) {
       throw new IllegalArgumentException("Le nombre de répétitions doit être au moins 1");
	   }
	}
	
	public CombatAction(CombatActionType type, int value) {
		this(type, value, 1);
	}
	
	 /**
   * @return la description complète de l'action annoncée
   */
	public String getAnnouncement() {
		return switch(type) {
			case ATTACK -> String.format("%s Attaque -> %d dégâts",type.getIcon(),value);
			case HEAVY_ATTACK -> String.format("%s Attaque Lourde -> %d dégâts", type.getIcon(),value);
			case MULTI_ATTACK -> String.format("%s Attaque Multiple -> %d dégâts", type.getIcon(),value);
			case DEFEND -> String.format("%s Défense → +%d protection", type.getIcon(), value);
      case HEAL -> String.format("%s Soin → +%d PV", type.getIcon(), value);
      case ENRAGE -> String.format("%s Rage → +%d dégâts", type.getIcon(), value);
      case FORTIFY -> String.format("%s Fortifier → +%d armure", type.getIcon(), value);
      case POISON -> String.format("%s Poison → %d dégâts/tour", type.getIcon(), value);
      case WEAKEN -> String.format("%s Affaiblir → -%d dégâts", type.getIcon(), value);
      case CURSE -> String.format("%s Malédiction → ajoute au sac", type.getIcon());
      case CHARGE -> String.format("%s Charge → prépare attaque (%d)", type.getIcon(), value);
      case IDLE -> String.format("%s Repos → ne fait rien", type.getIcon());
		};
	}
	
	@Override
	public final String toString() {
		return switch(type) {
			case ATTACK -> String.format("%s Attaque -> %d dégâts",type.getIcon(),value);
			case HEAVY_ATTACK -> String.format("%s Attaque Lourde -> %d dégâts", type.getIcon(),value);
			case MULTI_ATTACK -> String.format("%s Attaque Multiple -> %d dégâts", type.getIcon(),value);
			case DEFEND -> String.format("%s Défense → +%d protection", type.getIcon(), value);
      case HEAL -> String.format("%s Soin → +%d PV", type.getIcon(), value);
      case ENRAGE -> String.format("%s Rage → +%d dégâts", type.getIcon(), value);
      case FORTIFY -> String.format("%s Fortifier → +%d armure", type.getIcon(), value);
      case POISON -> String.format("%s Poison → %d dégâts/tour", type.getIcon(), value);
      case WEAKEN -> String.format("%s Affaiblir → -%d dégâts", type.getIcon(), value);
      case CURSE -> String.format("%s Malédiction → ajoute au sac", type.getIcon());
      case CHARGE -> String.format("%s Charge → prépare attaque (%d)", type.getIcon(), value);
      case IDLE -> String.format("%s Repos → ne fait rien", type.getIcon());
		};
	}
	
	// au lieu de créer une fonction à chaque type de CombatAction d'enemies
	// on a créé une seule action function qui groupe toutes les types d'attaques
	public static CombatAction action(CombatActionType type, int amount) {
		Objects.requireNonNull(type);
		return switch(type) {
			case ATTACK -> new CombatAction(type, amount);
			case HEAVY_ATTACK -> new CombatAction(type, amount);
			case MULTI_ATTACK -> new CombatAction(type, amount, 3); // on a donné une valeur 3 pour count de multi-attaque
			case DEFEND -> new CombatAction(type, amount);
      case HEAL -> new CombatAction(type, amount);
      case ENRAGE -> new CombatAction(type, amount);
      case FORTIFY -> new CombatAction(type, amount);
      case POISON -> new CombatAction(type, amount);
      case WEAKEN -> new CombatAction(type, amount);
      case CURSE ->new CombatAction(type, amount);
      case CHARGE -> new CombatAction(type, amount);
      case IDLE -> new CombatAction(type, amount);
		};
	}
	
	
}
