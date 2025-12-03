package fr.uge.data;

import java.util.Objects;

/**
* Record représentant une synergie entre deux types d'items
*/
public record Synergy(
 String name,              // Nom de la synergie
 String description,       // Description de l'effet
 Class<?> itemType1,       // Premier type d'item (ex: Weapon.class)
 Class<?> itemType2,       // Second type d'item (ex: Armor.class)
 SynergyEffect effect      // Effet à appliquer
) {
 public Synergy {
     Objects.requireNonNull(name);
     Objects.requireNonNull(description);
     Objects.requireNonNull(itemType1);
     Objects.requireNonNull(itemType2);
     Objects.requireNonNull(effect);
 }
}
