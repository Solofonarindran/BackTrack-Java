package fr.uge.model;

public sealed interface Actor permits Hero, Enemy{
	int getHealthPoint();
	int getMaxHealthPoint();
}
