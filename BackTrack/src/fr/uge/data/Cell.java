package fr.uge.data;

public sealed interface Cell permits ContainsItem, Locked, Free {
	boolean isUnlocked();
    boolean isFree();
}
