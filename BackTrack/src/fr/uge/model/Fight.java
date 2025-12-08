package fr.uge.model;
//import fr.uge.data.BackPack;

import fr.uge.data.Hero;
import fr.uge.data.Enemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Objects;
//import java.util.stream.Stream;


public class Fight {
	
	private Hero hero;
    private final List<Enemy> enemies;
    
	public Fight(Hero hero, List<Enemy> enemies) {
		this.hero = Objects.requireNonNull(hero);
		this.enemies = new ArrayList<>(Objects.requireNonNull(enemies));
	}
	
	
	// tirage de l'ennemi aléatoirement 
	private static Enemy enemyToFight(List<Enemy> enemies) {
		Objects.requireNonNull(enemies);
		Random random = new Random();
        int randomIndex = random.nextInt(enemies.size());
        Enemy newChallenger = enemies.get(randomIndex);
        return newChallenger;
        
	}
	
	// cout d'utilisation d'une arme
	private void costWeapon(FightItem item) {
		Objects.requireNonNull(item);
		switch(item.moneyType()) {
		case GOLD -> { Gold gold = hero.getBackPack().getGold();
				        if (gold == null) {
				            throw new IllegalStateException("Pas d'or dans le sac !");
				        }
				        Gold newGold = gold.spend(item.cost());
				        hero.getBackPack().replaceItem(newGold); }
		case MANASTONE -> { ManaStone mana = hero.getBackPack().getManaStone();
					        if (mana == null) {
					            throw new IllegalStateException("Pas de pierres de mana dans le sac !");
					        }
					        ManaStone newMana = mana.spend(item.cost());
					        hero.getBackPack().replaceItem(newMana); }
		case ENERGY -> { hero = hero.useEnergy(item.cost()); }
        default -> throw new IllegalStateException("Type de ressource inconnue !");
		}
	}
	
	// choix du hero (attaque ou aug niv protection)
	private Enemy heroChoice(char action, Enemy enemy, Scanner scanner) {
		Objects.requireNonNull(enemy);
		switch(action) {
		case 'A' -> System.out.println("Vous avez choisi d'attaquer ! Quelle arme souhaitez vous utiliser ?\n");
		case 'P' -> System.out.println("Vous avez choisi de vous protéger ! Quelle protection souhaitez vous utiliser ?\n");
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
		
		List<Item> itemInBackPack = hero.getBackPack().getItem();
		System.out.println(hero.getBackPack());
		
		int choice = scanner.nextInt();
		int index = choice - 1;
		
		// On vérifie que l'index est valide parmi la liste des objets dans le sac
		Objects.checkIndex(choice, itemInBackPack.size() + 1);
		
		Item selectedItem = itemInBackPack.get(index);

	    // Vérifie que c'est un FightItem
	    if (!(selectedItem instanceof FightItem fightItem)) {
	        throw new IllegalStateException("Cet objet n’est pas un objet de combat !");
	    }
		
		// On paie l'objet de combat
		costWeapon(fightItem);
		
		System.out.println("Vous avez payé : " + fightItem.cost());
				
	    System.out.println("Vous utilisez : " + fightItem.name());
	    
	    // Appliquer l'effet selon le choix
	    switch(action) {
	        case 'A' -> { enemy = enemy.takeDamage(fightItem.damage()); hero = hero.addProtection(fightItem.protection()); }
	        case 'P' -> hero = hero.addProtection(fightItem.protection());
	    }
	    return enemy;
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

	// ennemi mort -> partie gagnée
	public boolean winGame(Enemy enemy) {
		if(enemy.getHealthPoints() <= 0) {
			System.out.println("Partie gagnée !\n");
			return true;
		}
		return false;
	}
	
	// hero mort
	public boolean lostGame() {
		if(hero.getHealthPoints() <= 0) {
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
		System.out.println("Vous allez combattre : " + enemy.getName());
		Scanner scanner = new Scanner(System.in);
		while(!winGame(enemy) && !lostGame()) {
			// prévision enemi
			var action = enemyChoice();
			// choix hero
			System.out.println("Héro, c'est à votre tour de jouer ! Quelle action voulez vous effectuer : attaquer(A) ou augmenter votre niveau de protection(P) ?\n");
			char playerChoice = scanner.next().toUpperCase().charAt(0);
			// action hero
			enemy = heroChoice(playerChoice, enemy, scanner);
			// action ennemi
			switch(action.choice()) {
			case 'A' -> { System.out.println("L'ennemi vous a infligé " + action.ptsDegats() + " points de dégâts !");
			hero = hero.takeDamage(action.ptsDegats());
			}
			case 'P' -> { System.out.println("L'ennemi a augmenté son niveau de protection de " + action.protection() + " points !"); 
			enemy = enemy.addProtection(action.protection());
			}
			};
			// Pour tests 
			System.out.println("Points de vie du hero : " + hero.getHealthPoints());
			System.out.println("Points de vie de l'ennemi : " + enemy.getHealthPoints());
			System.out.println("Points de protection du hero : " + hero.getProtection());
			System.out.println("Points de protection de l'ennemi : " + enemy.getProtection());
			System.out.println("Points d'énergie du hero : " + hero.getEnergy());
		}
		scanner.close();
	}
}
