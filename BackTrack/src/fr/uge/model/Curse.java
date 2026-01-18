package fr.uge.model;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Une malédiction est un équipement spécial infligé par un ennemi.
 * Elle ne peut pas être tournée, seulement translatée.
 * Elle écrase les items existants quand elle est placée.
 */
public record Curse(
    String name,
    String description,
    List<Coordonate> references,
    CurseEffect effect
) implements Item {
    
    public enum CurseEffect {
        NONE("Aucun effet", "Occupe juste de la place"),
        DAMAGE_PER_TURN("Dégâts par tour", "Inflige 1 dégât par tour"),
        BLOCK_ENERGY("Bloque énergie", "Réduit l'énergie max de 1"),
        HEAVY("Lourd", "Réduit la protection de 2");
        
        private final String name;
        private final String description;
        
        CurseEffect(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    public Curse {
        Objects.requireNonNull(name);
        Objects.requireNonNull(description);
        Objects.requireNonNull(references);
        Objects.requireNonNull(effect);
        references = List.copyOf(references);
    }
    
    public static boolean isCurse(Item item) {
    	Objects.requireNonNull(item);
    	return switch(item) {
    		case Curse _ ->true;
    		default -> false;
    	};
    }
    @Override
    public List<Coordonate> references() {
        return references;
    }
    
    // Les malédictions ne peuvent PAS être tournées
    public boolean canRotate() {
        return false;
    }
    
    // Factory pour créer des malédictions aléatoires
    public static Curse createRandom(int floorNumber) {
        var random = new Random();
        
        // Formes possibles (de plus en plus grandes aux étages supérieurs)
        List<List<Coordonate>> shapes = List.of(
            // Petites (étage 1+)
            List.of(new Coordonate(0, 0)),
            List.of(new Coordonate(0, 0), new Coordonate(1, 0)),
            List.of(new Coordonate(0, 0), new Coordonate(0, 1)),
            
            // Moyennes (étage 2+)
            List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1)),
            List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(1, 1)),
            
            // Grandes (étage 3+)
            List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1), new Coordonate(1, 1)),
            List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(0, 2), new Coordonate(1, 1))
        );
        
        // Sélectionner une forme selon l'étage
        int maxShapeIndex = Math.min(2 + floorNumber, shapes.size() - 1);
        var shape = shapes.get(random.nextInt(maxShapeIndex + 1));
        
        // Sélectionner un effet
        var effects = CurseEffect.values();
        var effect = effects[random.nextInt(effects.length)];
        
        // Noms possibles
        String[] names = {
            "Malédiction de l'ombre",
            "Sceau maudit",
            "Marque du démon",
            "Corruption",
            "Fardeau noir",
            "Chaînes spectrales"
        };
        
        String name = names[random.nextInt(names.length)];
        String description = effect.getDescription();
        
        return new Curse(name, description, shape, effect);
    }
}