package fr.uge.model;

import java.util.Objects;

public class StatusEffect {
	public enum EffectType {
    // Debuffs
    POISON("Poison", "🧪", true),
    WEAKNESS("Faiblesse", "📉", true),
    STUN("Étourdi", "💫", true),
    BURN("Brûlure", "🔥", true),
    
    // Buffs
    STRENGTH("Force", "💪", false),
    REGENERATION("Régénération", "💚", false),
    SHIELD("Bouclier", "🛡️", false),
    RAGE("Rage", "😡", false);
	
    private final String name;
    private final String icon;
    private final boolean isDebuff;
    
    EffectType(String name, String icon, boolean isDebuff) {
        this.name = name;
        this.icon = icon;
        this.isDebuff = isDebuff;
    }
    
    public String getName() {
      return name;
    }
  
    public String getIcon() {
      return icon;
    }
  
    public boolean isDebuff() {
      return isDebuff;
    }
    
	}
	
	private final EffectType type;
	private int duration; //Tours restants
	private final int value; // Valeur de l'effet (dégâts/soin par tour,bonus,etc.)
	
	public StatusEffect(EffectType type, int duration, int value) {
		this.type = Objects.requireNonNull(type);
		if (duration <= 0) {
      throw new IllegalArgumentException("La durée doit être positive");
		}
		this.duration = duration;
		this.value = value;
		
	}
	public EffectType getType() {
    return type;
	}

	public int getDuration() {
    return duration;
	}

	public int getValue() {
    return value;
	}
	
	/**
	 * Décrémente la durée de l'effet
	 * @return true si l'effet est encore actif
	 */
	
	public boolean tick() {
		duration--;
		return duration > 0;
	}
	
	/**
	 * @return true si l'effet est expiré
	 */
	public boolean isExpired() {
		return duration <= 0;
	}
	
	/**
	 * Applique l'effet de début de tour
	 * @return les dégâts ou soins à appliquer (négatif = dégâts, positif = soin)
	 */
	public int applyStartOfTurn() {
		return switch(type) {
			case POISON,BURN -> -value; // Dégâts
			case REGENERATION -> value; // Soin
			default -> 0;
		};
	}
	
	/**
	 * @return le modificateur de dégâts
	 */
	public int getDamageModifier() {
		return switch(type) {
			case WEAKNESS -> -value;
			case STRENGTH, RAGE -> value;
			default -> 0;
		};
	}
	
	/**
	 * @return true si l'effet empêche d'agir
	 */
	
	public boolean preventsAction() {
		return type == EffectType.STUN;
	}
	
  @Override
  public String toString() {
      return String.format("%s %s (%d tours, valeur: %d)", 
          type.getIcon(), type.getName(), duration, value);
  }
  
  // Factory method
  // Toutes les actions 
  public static StatusEffect action(EffectType type,int duration, int amount) {
  	Objects.requireNonNull(type); 	
  	return switch(type) {
  		case POISON -> new StatusEffect(type, duration, amount);
  		case WEAKNESS -> new StatusEffect(type, duration, amount);
  		case STUN -> new StatusEffect(type, duration, 0);
  		case STRENGTH -> new StatusEffect(type, duration, amount);
  		case REGENERATION -> new StatusEffect(type, duration, amount);
  		case RAGE -> new StatusEffect(type, duration, amount);
  		case SHIELD -> new StatusEffect(type, duration, amount);
  		case BURN -> new StatusEffect(type,duration,amount);
  	};
  }
	
}
