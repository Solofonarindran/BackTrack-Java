package fr.uge.data;

import java.util.Random;

public record SurpriseRoom(SurpriseEvent event, boolean revealed) implements Room{
  public enum SurpriseEvent {
    TREASURE("Vous trouvez un trésor caché !", true),
    TRAP("C'est un piège ! Vous perdez des PV.", false),
    HEALING("Une fontaine magique vous soigne.", true),
    CURSE("Une malédiction s'abat sur votre sac !", false),
    GOLD("Des pièces d'or jonchent le sol.", true),
    NOTHING("La salle est vide... décevant.", true),
    ENEMY("Un ennemi surgit de l'ombre !", false),
    BLESSING("Une bénédiction augmente vos stats.", true);
    
    private final String description;
    private final boolean positive;
    
    SurpriseEvent(String description, boolean positive) {
        this.description = description;
        this.positive = positive;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isPositive() {
        return positive;
    }
}

public SurpriseRoom() {
    this(generateRandomEvent(),false);
}

public SurpriseRoom(SurpriseEvent event) {
  this(event,false);
}

private static SurpriseEvent generateRandomEvent() {
    var events = SurpriseEvent.values();
    var random = new Random();
    return events[random.nextInt(events.length)];
}

public SurpriseEvent getEvent() {
    return event;
}

public boolean isRevealed() {
    return revealed;
}

/**
 * Révèle l'événement surprise
 * @return l'événement révélé
 */
public SurpriseRoom reveal() {
	return new SurpriseRoom(event,true);
}

@Override
public boolean isVisited() {
	return revealed;
}

@Override
public boolean isAccessible() {
	// TODO Auto-generated method stub
	return true;
}

@Override
public Room setVisited() {
	return new SurpriseRoom(event,true);
}

@Override
public String getDescription() {
    if (!revealed) {
        return "Une lueur étrange émane de cette salle... Qu'est-ce qui vous attend ?";
    }
    return event.getDescription();
}

@Override
public String toString() {
    return revealed ? (event.isPositive() ? "✨" : "💀") : "❓";
}
}
