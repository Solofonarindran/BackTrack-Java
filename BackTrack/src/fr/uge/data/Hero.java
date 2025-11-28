package fr.uge.data;

import java.util.Objects;

public class Hero {
	// statistiques de base
	private final int healthPoints; //Points de vie actuels
	private final int maxHealthPoints; // Points de vie maximum
	private final int level; //Niveau du Héros
	private	final int experience; // Points d'expérience actuels
	private final int maxEnergy ;
	
	//Statistiques de combat
	private final int energy; // Energie disponible (3 par tour en combat)
	private final int manaPoints; // Point mana disponibles
	private final int protection; // POint de protection temporaire
	
	//inventaire
	private final BackPack backPack ;
	private final int keys; //
	
	public Hero(int healthPoints,int maxHealthPoints, int level, int experience,
							int maxEnergy, int energy, int manaPoints, int protection, BackPack backPack, int keys) {
		Objects.requireNonNull(backPack);
		
		this.healthPoints = healthPoints;
		this.maxHealthPoints = maxHealthPoints;
		this.maxEnergy = maxEnergy;
		this.level = level;
		this.experience = experience;
	
		this.energy = energy;
		this.manaPoints = manaPoints;
		this.protection = protection;
		this.backPack = backPack;
		this.keys = keys;
	}
	

}
