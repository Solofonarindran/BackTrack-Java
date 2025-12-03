package fr.uge.data;

import fr.uge.model.Item;

public interface SynergyEffect {

	 void apply(Item item1, Item item2, Hero player);
	}

