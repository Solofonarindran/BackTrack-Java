package fr.uge.model;

public interface Item {
	default boolean rotate() {
		return true;
	}
}
