package fr.uge.rooms;

import fr.uge.item.FightItem;
import fr.uge.item.Gold;
import fr.uge.item.Item;
import fr.uge.item.ItemFactory;
import fr.uge.item.Malediction;
import fr.uge.item.ManaStone;
import fr.uge.actors.Enemy;
import fr.uge.actors.EnemyAction;
import fr.uge.actors.Hero;
import fr.uge.backpack.Coordonate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Objects;


public class Fight {
	
	private Hero hero;
    private final List<Enemy> enemies;
    private int nbRefus = 0;

    
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
		case 'A' -> IO.println("Vous avez choisi d'attaquer ! Quelle arme souhaitez vous utiliser ?\n");
		case 'P' -> IO.println("Vous avez choisi de vous protéger ! Quelle protection souhaitez vous utiliser ?\n");
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
		
		List<Item> itemInBackPack = hero.getBackPack().getItem();
		IO.println(hero.getBackPack());
		
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
		
		IO.println("Vous avez payé : " + fightItem.cost());
				
	    IO.println("Vous utilisez : " + fightItem.name());
	    
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
		case 'A' -> IO.println("L'ennemi a prévu d'attaquer de " + ptsDegats + " de dégâts !");
		case 'P' -> IO.println("L'ennemi a prévu de se protéger de " + protection + " !");
		default -> throw new IllegalArgumentException("Erreur : le choix n'est pas valide");
		};
		return new EnemyAction(choice, ptsDegats, protection);
	}
	
	// On récupère le trésor gagné 
	public Item price() {
		List<Item> itemsAvailable = ItemFactory.getCatalog();
		Random random = new Random();
		var price = itemsAvailable.get(random.nextInt(itemsAvailable.size()));
		IO.println("Félicitations ! Vous avez gagné : " + price + " !");
		return price;
	}
	
	public int malediction(Scanner scanner) {
		Objects.requireNonNull(scanner);
		if(nbRefus < 0) {
			throw new IllegalArgumentException("Le nombre de refus ne peut pas être négatif !");
		}
		Malediction mal = Malediction.initMalediction();
		IO.println("L'ennemi vous a infligé une malédiction... L'acceptez vous ? (y/n)");
		var choice = scanner.next().toLowerCase();
		switch(choice) {
		case "y" -> { IO.println("Vous devez placer la malédiction dans votre BackPack.");
						/*
						 * A INIT AVEC LES CLICKS
						 */
					  Coordonate clickedCoord;
					  hero.getBackPack().addEquipement(mal, clickedCoord); 
		}
		case "n" -> { IO.println("Attention, un kème refus vous vaudra k points de dégâts !");
					  nbRefus++;
					  hero.takeDamage(nbRefus);
		}
		default -> throw new IllegalArgumentException("L'entrée saisie n'est pas valide.");
		}
		return nbRefus;
	}
	
	// On veut se débarasser d'un item
	public void getRidOf(Item item, Scanner scanner) {
		IO.println("Voulez vous vraiment vous débarraser de " + item + " ? (y/n)");
		var choice = scanner.next().toLowerCase();
		switch(choice) {
		case "y" : hero.getBackPack().removeEquipment(item);
		case "n" : IO.println("Action annulée.");
		default : throw new IllegalArgumentException("L'entrée saisie n'est pas valide.");
		}
	}

	// ennemi mort -> partie gagnée
	public boolean winGame(Enemy enemy, Scanner scanner) {
		if(enemy.getHealthPoints() <= 0) {
			IO.println("Partie gagnée !\n");
			Item price = price();
			/*
			 * A INIT AVEC LES CLICKS
			 */
			Coordonate clickedCoord;
			if(hero.getBackPack().addEquipement(price, clickedCoord)){
				IO.println("Vous avez placé : "  + price);
			} else {
				IO.println(price.name() + " n'a pas pu être placé... Désirez vous vous débarasser d'un autre objet ? (y/n)");
				var decision = scanner.next().toLowerCase();
				switch(decision) {
				case "y" : getRidOf(price, scanner);
				case "n" : IO.println("Le nouvel item est alors laissé...");
				}
			}
			return true;
		}
		return false;
	}
	
	// hero mort
	public boolean lostGame(Enemy enemy, Scanner scanner) {
		if(hero.getHealthPoints() <= 0) {
			IO.println("Partie perdue !\n");
			nbRefus = malediction(scanner);
			hero.gainExperience(enemy.getNbXp());
			/*
			 * A INIT AVEC LES CLICKS
			 */
			List<Coordonate> clickedCoord;
			hero.getBackPack().extendBackPack(enemy.getNbCases(), clickedCoord);
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
		IO.println("Vous allez combattre : " + enemy.getName());
		Scanner scanner = new Scanner(System.in);
		while(!winGame(enemy, scanner) && !lostGame(enemy, scanner)) {
			// prévision enemi
			var action = enemyChoice();
			// choix hero
			IO.println("Héro, c'est à votre tour de jouer ! Quelle action voulez vous effectuer : attaquer(A) ou augmenter votre niveau de protection(P) ?\n");
			char playerChoice = scanner.next().toUpperCase().charAt(0);
			// action hero
			enemy = heroChoice(playerChoice, enemy, scanner);
			// action ennemi
			switch(action.choice()) {
			case 'A' -> { IO.println("L'ennemi vous a infligé " + action.ptsDegats() + " points de dégâts !");
			hero = hero.takeDamage(action.ptsDegats());
			}
			case 'P' -> { IO.println("L'ennemi a augmenté son niveau de protection de " + action.protection() + " points !"); 
			enemy = enemy.addProtection(action.protection());
			}
			};
			// Pour tests 
			IO.println("Points de vie du hero : " + hero.getHealthPoints());
			IO.println("Points de vie de l'ennemi : " + enemy.getHealthPoints());
			IO.println("Points de protection du hero : " + hero.getProtection());
			IO.println("Points de protection de l'ennemi : " + enemy.getProtection());
			IO.println("Points d'énergie du hero : " + hero.getEnergy());
		}
		scanner.close();
	}
}
