package fr.uge.model;


public sealed interface FightItem permits Armor, Weapon, Magic {
	int damage();
	int protection();
	RessourcesType moneyType();
	int cost();
	String name();
}
