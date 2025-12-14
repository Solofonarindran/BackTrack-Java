package fr.uge.data;

public record HealerRoom(int healAmount, int healCost, int fullHealCost,boolean isVisited) implements Room{
	// Points de vie restaurés
	// Coût en or
	// Coût pour soin complet
	
	public HealerRoom {
		if(healAmount < 0 || healCost < 0 || fullHealCost <0 ) {
			throw new  IllegalArgumentException();
		}
	}
	
	public HealerRoom() {
		this(10,15,50, true);
	}
	@Override 
	public HealerRoom setVisited() {
		return new HealerRoom(healAmount, healCost, fullHealCost, true);
	}
	@Override
	public boolean isAccessible() {
		return true;
	}

	@Override
  public String getDescription() {
      return "Un guérisseur bienveillant vous propose ses services. " +
             "(" + healAmount + " PV pour " + healCost + " or, soin complet: " + fullHealCost + " or)";
  }
	
	// Calcule le coût pour soigner un montant spécifique
	public int getCostForAmount(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException();
		}
		return (int) Math.ceil((double) amount / healAmount * healCost);
	}
	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return "🏥";
	}
}
