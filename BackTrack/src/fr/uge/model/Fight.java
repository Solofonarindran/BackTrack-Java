package fr.uge.model;
import fr.uge.data.BackPack;
import fr.uge.data.Hero;
import jdk.internal.org.jline.terminal.TerminalBuilder.SystemOutput;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Stream;


public class Fight {
	
	private final static Hero hero;
	private final static Enemy ratLoup;
	private final Enemy petitRatLoup;
	private final ArrayList<Enemy> enemies;
	private final static BackPack back;
	
	
	private final List<ItemTreasure> weapons;
	
	public Fight() {
		// pour le moment à modifier plus tard 
		back = new BackPack();
		hero = new Hero(40, 40, 1, 0, 0, 3, 0, 0, back, 0);
		ratLoup = new Enemy("Rat-Loup", 50, 10);
        petitRatLoup = new Enemy("Petit Rat-Loup", 30, 5);
        enemies = new ArrayList<>();
		enemies.add(ratLoup);
		enemies.add(petitRatLoup);
		weapons = ItemFactory.itemsAvailable();
	}
	
	
	// tirage de l'ennemi aléatoirement 
	private static Enemy enemyToFight(ArrayList<Enemy> enemies) {
		Objects.requireNonNull(enemies);
		Random random = new Random();
        int randomIndex = random.nextInt(enemies.size());
        Enemy newChallenger = enemies.get(randomIndex);
        return newChallenger;
        
	}
	
	// choix de l'arme
	private static Weapon weaponChoice(int choice, ArrayList<Weapon> weapons) {
		Objects.requireNonNull(weapons);
		return weapons.stream()
                .skip(choice)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Index invalide"));
	}
	
	// cout d'utilisation d'une arme
	private static void costWeapon(ItemTreasure item) {
		Objects.requireNonNull(item);
		// il faudrait un costEnergy, costMana et costGold ? dans les paramètres des items
		hero.energy -= item.costEnergy();
		hero.manaPoints -= item.costMana();
	    ItemTreasure gold = ItemFactory.getItemByName("Gold");
	    int costGold = item.costGold();
	    // maj du nombre d'or 
	    gold = gold.spend(costGold);
	    // màj dans le sac à dos
	    back.replaceItemInBackpack(gold);
	}
	
	// attaque du hero 
	private static void heroAttack(Enemy enemy) {
		Objects.requireNonNull(enemy);
		System.out.println("Vous avez choisi d'attaquer ! Quelle arme souhaitez vous utiliser ?\n");
		List<ItemTreasure> itemInBackPack = hero.getBackPack();
		// faire la méthode string pour avoir un bel affichage + les dégâts et la protection 
		System.out.println(itemInBackPack);
		// On récupère la saisie de l'utilisateur
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		int index = choice - 1; // -1 car la liste commence à 1 lors de l'affichage
		// On vérifie que l'index est valide
		Objects.checkIndex(choice, itemInBackPack.size());
		ItemTreasure item = itemInBackPack.get(index);
	    System.out.println("Vous utilisez : " + item.name());
	    // On récupère les effets de l'arme
	    int damage = item.damage();
	    enemy.takeDamage(damage);
	}
	
	// augmentation de la protection du héro
	private static void heroProtection() {
		System.out.println("Vous avez choisi de vous protéger ! Quelle protection souhaitez vous utiliser ?\n");
		List<ItemTreasure> itemInBackPack = hero.getBackPack();
		System.out.println(itemInBackPack);
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		int index = choice - 1;
		Objects.checkIndex(choice, itemInBackPack.size());
		ItemTreasure item = itemInBackPack.get(index);
	    System.out.println("Vous utilisez : " + item.name());
	    int protection = item.protection();
	    hero.addProtection(protection);
	}
	
	// choix du hero (attaque ou aug niv protection)
	private static void heroChoice(char choice, Enemy enemy) {
		Objects.requireNonNull(enemy);
		switch(choice) {
		case 'A' -> heroAttack(enemy);
		case 'P' -> heroProtection();
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
		
	}
	
	private static void heroAttack(Enemy enemy) {
		Objects.requireNonNull(enemy);
		System.out.println("Vous avez choisi d'attaquer ! Quelle arme souhaitez vous utiliser ?\n");
		List<ItemTreasure> itemInBackPack = hero.getBackPack();
		// faire la méthode string pour avoir un bel affichage + les dégâts et la protection 
		System.out.println(itemInBackPack);
		// On récupère la saisie de l'utilisateur
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		int index = choice - 1; // -1 car la liste commence à 1 lors de l'affichage
		// On vérifie que l'index est valide
		Objects.checkIndex(choice, itemInBackPack.size());
		ItemTreasure item = itemInBackPack.get(index);
	    System.out.println("Vous utilisez : " + item.name());
	    // On récupère les effets de l'arme
	    int damage = item.damage();
	    enemy.takeDamage(damage);
	}
	
	
	// choix de l'ennemi (attaque ou aug niv protection)
	private static EnemyAction enemyChoice() {
		var choices = List.of('A', 'P');
		Random random = new Random();
		var choice = choices.get(random.nextInt(choices.size()));
		int min=1, max=15;
		var ptsDegats = random.nextInt(max - min + 1) + min;
		var protection = random.nextInt(max - min + 1) + min;
		switch(choice) {
		case 'A' -> System.out.println("L'ennemi a prévu d'attaquer de " + ptsDegats + " de dégâts !");
		case 'P' -> System.out.println("L'ennemi a prévu de se protéger de " + protection + " !");
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
		return new EnemyAction(choice, ptsDegats, protection);
	}
	
	// liste des armes dispo dans le sac à dos du hero -> avec getbackpack
	
	// équipé -> boolean dans hero ?

	// ennemi mort -> partie gagnée
	public boolean winGame() {
		if(enemy.healthPoints() <= 0) {
			System.out.println("Partie gagnée !\n");
			return true;
		}
		return false;
	}
	// hero mort
	public boolean lostGame() {
		if(hero.healthPoints <= 0) {
			System.out.println("Partie perdue !\n");
			return true;
		}
		return false;
	}

	// sauvegarde des points de vie et de protection des acteurs + durée totale du combat dans un fichier texte
		// si rencontre avec le guérisseur (boolean) -> on reprend ces points de vie
		// sinon on prendra le dernier score en date dans le fichier
	// hall of fame -> on prend les trois meilleures parties réalisées
		// en fonction du temps du combat ? 
	
	// mécanimse du tour par tour avec hero en premier
	public void fight() {
		// à faire -> recup les anciens niveaux de vie, protection et équipements depuis la sauvegarde
		Enemy enemy = enemyToFight(enemies);
		Scanner scanner = new Scanner(System.in);
		while(!winGame() && !lostGame()) {
			// prévision enemi
			var action = enemyChoice();
			// choix hero
			System.out.println("Héro, c'est à votre tour de jouer ! Quelle action voulez vous effectuer : attaquer(A) ou augmenter votre niveau de protection(P) ?\n");
			char playerChoice = scanner.next().toUpperCase().charAt(0);
			// action hero
			heroChoice(playerChoice, enemy);
			// action ennemi
			switch(action.choice()) {
			case 'A' -> { System.out.println("L'ennemi vous a infligé " + action.ptsDegats() + " points de dégâts !");
			hero.takeDamage(action.ptsDegats());
			}
			case 'P' -> { System.out.println("L'ennemi a augmenté son niveau de protection de " + action.protection() + " points !"); 
			enemy.addProtection(action.protection());
			}
			};
		}
	}
}
