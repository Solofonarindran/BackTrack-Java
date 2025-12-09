package fr.uge.data;

public sealed interface Actor permits Hero, Enemy{
	int getHealthPoint();
	int getMaxHealthPoint();
}
