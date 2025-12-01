package fr.uge.model;
import fr.uge.data.Hero;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Stream;


public class Fight {
	
	private final Hero hero;
	private final Enemy ratLoup;
	private final Enemy petitRatLoup;
	private final ArrayList<Enemy> enemies;
	
	
	private final ArrayList<ItemTreasure> weapons;
	
	public Fight() {
		// a revoir en fonction de la classe Hero
		hero = new Hero("Héro", 40, 20);
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
	
	// attaque du hero 
	private static void heroAttack(Enemy enemy) {
		Objects.requireNonNull(enemy);
		System.out.println("Vous avez choisi d'attaquer ! Quelle arme souhaitez vous utiliser ?\n");
		List<ItemTreasure> itemInBackPack = hero.getBackPack();
		// faire la méthode string pour avoir un bel affichage 
		System.out.println(itemInBackPack);
		// On récupère la saisie de l'utilisateur
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		int index = choice - 1; // -1 car la liste commence à 1 lors de l'affichage
		// On vérifie que l'index est valide
		Objects.checkIndex(choice, itemInBackPack.size());
		ItemTreasure item = itemInBackPack.get(index);
	    System.out.println("Vous utilisez : " + item.name());
	    int damage = item.damage();
	    int protection = item.protection();
	    
	}
	
	// augmenter niv de protection -> avec interface ?
	
	// choix du hero (attaque ou aug niv protection
	private static void heroChoice(char choice, Enemy enemy) {
		Objects.requireNonNull(enemy);
		switch(choice) {
		case 'A' -> heroAttack(enemy);
		case 'P' -> improveProtection(hero);
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
	}
	// cout d'utilisation d'une arme
	// liste des armes dispo dans le sac à dos du hero
	// hero equipé d'une arme -> les points de vie en plus + points d'attaque
	// affiche en points numérotés des armes
	// récup entrée de l'user pour la sélection de l'arme 
	// équipe
	// augmente niv protection hero
	// attaque ennemi (1 à 15 pts de dégâts)
	// augmente niv protection ennemi
	// ennemi mort 
	// hero mort
	// partie gagnée
	// sauvegarde des points de vie et de protection des acteurs + durée totale du combat dans un fichier texte
		// si rencontre avec le guérisseur (boolean) -> on reprend ces points de vie
		// sinon on prendra le dernier score en date dans le fichier
	// hall of fame -> on prend les trois meilleures parties réalisées
		// en fonction du temps du combat ? 
	
	// mécanimse du tour par tour avec hero en premier
	public void fight() {
		// recup les anciens niveaux de vie, protection et équipements depuis la sauvegarde
		var hero = new Hero();
		var enemy = enemyToFight(enemies);
		while(!deadHero() | !deadEnemy()) {
			// prévision enemi
			// choix hero
			// action hero
			// choix ennemi
			// action ennemi
		}
	}
}
