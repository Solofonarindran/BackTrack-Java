package fr.uge.model;

public sealed interface Ressources permits Gold, ManaStone {
	String name();
	int quantity();
}
