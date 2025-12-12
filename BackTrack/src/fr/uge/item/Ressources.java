package fr.uge.item;

public sealed interface Ressources permits Gold, ManaStone {
	String name();
	int quantity();
}
