package fr.uge.model;

import java.util.Objects;
import java.util.Random;

public enum EnemyType {
  // Ennemis faibles (Étage 1-2)
  RAT("Rat", 12, 0, 5, 15, "🐀"),
  SLIME("Slime", 15, 0, 4, 20, "🟢"),
  BAT("Chauve-souris", 10, 0, 6, 18, "🦇"),
  
  // Ennemis moyens (Étage 2-3)
  GOBLIN("Gobelin", 25, 2, 8, 35, "👺"),
  SKELETON("Squelette", 20, 3, 10, 40, "💀"),
  WOLF("Loup", 22, 1, 12, 38, "🐺"),
  ORC("Orc", 35, 4, 10, 50, "👹"),
  
  // Ennemis forts (Étage 3-4)
  TROLL("Troll", 50, 5, 15, 75, "🧌"),
  DARK_MAGE("Mage noir", 30, 2, 18, 65, "🧙"),
  VAMPIRE("Vampire", 40, 3, 14, 70, "🧛"),
  
  // Boss (Fin d'étage)
  BOSS_GOLEM("Golem de pierre", 50, 6, 3, 50, "🗿"),
  BOSS_DRAGON("Dragon", 80, 10, 5, 100, "🐉"),
  BOSS_DEMON("Démon", 90, 15, 8, 150, "😈");
	
	 private final String name;
   private final int maxHealth;
   private final int baseArmor;
   private final int baseDamage;
   private final int experienceReward;

   private static final Random RANDOM = new Random();
   
   EnemyType(String name, int maxHealth, int baseArmor, int baseDamage, 
       int experienceReward, String icon) {
  	 this.name = Objects.requireNonNull(name);
  	 this.maxHealth = maxHealth;
  	 this.baseArmor = baseArmor;
  	 this.baseDamage = baseDamage;
  	 this.experienceReward = experienceReward;
  	 
   }
   
   public String getName() {
     return name;
   }
 
	 public int getMaxHealth() {
	     return maxHealth;
	 }
	 
	 public int getBaseArmor() {
	     return baseArmor;
	 }
	 
	 public int getBaseDamage() {
	     return baseDamage;
	 }
	 
	 public int getExperienceReward() {
	     return experienceReward;
	 }
   
	 
	 // tester s'il est Boss ou pas
	 
	 public boolean isBoss() {
		 return this.equals(BOSS_DEMON) || this.equals(BOSS_DRAGON) || this.equals(BOSS_GOLEM);
	 }
	 
	 //retourne un enemi aléatoire selon le niveau de l'étage
	 public static EnemyType getRandomFloor(int floorNumber) {
		 return switch(floorNumber) {
			 case 1 -> { 
				 var weak = new EnemyType[] {RAT, SLIME, BAT};
				 yield weak[RANDOM.nextInt(weak.length)];
			 }
			 case 2 -> {
				 var medium = new EnemyType[] {GOBLIN, SKELETON, WOLF, RAT, SLIME};
				 yield medium[RANDOM.nextInt(medium.length)];
			 }
			 
			 case 3 -> {
				 var strong = new EnemyType[] {ORC, TROLL, GOBLIN, SKELETON, WOLF};
				 yield strong[RANDOM.nextInt(strong.length)];	
			 }
			 
			 case 4 -> {
				 var veryStrong = new EnemyType[] {TROLL, DARK_MAGE, VAMPIRE, ORC};
				 yield veryStrong[RANDOM.nextInt(veryStrong.length)];
			 }
			 
			 default -> {
				 var veryStrong = new EnemyType[] {TROLL, DARK_MAGE, VAMPIRE, ORC};
				 yield veryStrong[RANDOM.nextInt(veryStrong.length)];
			 }
		 };
	 }
	 
	 // retourne Boss selon le niveau étage
	 
	 public static EnemyType getBossForFloor(int floorNumber) {
     return switch (floorNumber) {
         case 1, 2 -> BOSS_GOLEM;
         case 3 -> BOSS_DRAGON;
         default -> BOSS_DEMON;
     };
	 }
	  /**
    * Retourne un ennemi aléatoire selon le niveau de l'étage
    */
   public static EnemyType getRandomForFloor(int floorNumber) {
       var random = new java.util.Random();
       
       return switch (floorNumber) {
           case 1 -> {
               var weak = new EnemyType[]{RAT, SLIME, BAT};
               yield weak[random.nextInt(weak.length)];
           }
           case 2 -> {
               var medium = new EnemyType[]{GOBLIN, SKELETON, WOLF, RAT, SLIME};
               yield medium[random.nextInt(medium.length)];
           }
           case 3 -> {
               var strong = new EnemyType[]{ORC, TROLL, GOBLIN, SKELETON, WOLF};
               yield strong[random.nextInt(strong.length)];
           }
           case 4 -> {
               var veryStrong = new EnemyType[]{TROLL, DARK_MAGE, VAMPIRE, ORC};
               yield veryStrong[random.nextInt(veryStrong.length)];
           }
           default -> {
               var all = new EnemyType[]{TROLL, DARK_MAGE, VAMPIRE, ORC};
               yield all[random.nextInt(all.length)];
           }
       };
   }
   
}
