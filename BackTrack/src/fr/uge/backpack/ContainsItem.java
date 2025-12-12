package fr.uge.data;

import fr.uge.model.Item;

public final class ContainsItem implements Cell {

	private final Item item; 
	
	public ContainsItem(Item item) {
		this.item = item;
	}
	
    public Item getItem() {
        return item;
    }
	
	@Override
	public boolean isUnlocked() {
		return true;
	}

	@Override
	public boolean isFree() {
		return false;
	}
	
   @Override
    public String toString() {
        return "Contains(" + item.name() + ")";
    }

}
