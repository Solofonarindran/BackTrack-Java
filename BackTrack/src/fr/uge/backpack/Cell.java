package fr.uge.backpack;

public sealed interface Cell permits ContainsItem, Locked, Free {
	boolean isUnlocked();
    boolean isFree();
}
