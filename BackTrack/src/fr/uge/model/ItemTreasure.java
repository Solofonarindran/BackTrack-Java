package fr.uge.model;

public sealed interface ItemTreasure permits Armor, Weapon, Magic, Clef, Consumable, Gold{
	String name();
	int damage();
	int protection();
}
