package fr.uge.data;

public sealed interface Actor permits Hero{
	int getHealthPoint();
	int getMaxHealthPoint();
}
