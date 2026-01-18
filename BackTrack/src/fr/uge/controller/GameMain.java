package fr.uge.controller;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.BackPack;
import fr.uge.data.BossRoom;
import fr.uge.data.Combat;
import fr.uge.data.CombatReward;
import fr.uge.data.Dungeon;
import fr.uge.data.EnemyRoom;
import fr.uge.data.ExitRoom;
import fr.uge.data.Floor;
import fr.uge.data.GateRoom;
import fr.uge.data.LootZone;
import fr.uge.data.MerchantRoom;
import fr.uge.data.TreasureRoom;
import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Enemy;
import fr.uge.model.Gold;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;
import fr.uge.view.BossView;
import fr.uge.view.CombatView;
import fr.uge.view.DungeonView;
import fr.uge.view.FloorTransitionView;
import fr.uge.view.GatePromptView;
import fr.uge.view.HealerView;
import fr.uge.view.LootZoneView;
import fr.uge.view.MerchantView;
import fr.uge.view.VictoryView;

public class GameMain {
    
    private static final int TOTAL_FLOORS = 3;
    
    public static void main(String[] args) {
        System.out.println("=== BACKPACK HERO ===\n");
        
        Application.run(Color.BLACK, context -> {
            try {
                runGame(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        System.out.println("\n=== FIN DU JEU ===");
    }
    
    private static boolean combatEventRunGame(Controller dungeonController, Floor floor, Hero hero, CombatView combatView, 
    																				ApplicationContext context) throws IOException {
    	var roomPos = dungeonController.getEventRoom();
      var room = (EnemyRoom) floor.getRoom(roomPos);
      
      boolean victory = runCombat(hero, room.enemies(), combatView, context);
      
      if (victory) {
          // Marquer la salle comme visitée
      		floor.markEnemyRoomCleared(roomPos);
        
          hero.resetEnergy();
      } else {
      		showGameOver(hero, context);
          return false;
      }
      return true;
    }
    
    private static boolean boossEventRunGame(Controller dungeonController, Floor floor, Hero hero, CombatView combatView, 
    				ApplicationContext context) throws IOException{
    	
      var roomPos = dungeonController.getEventRoom();
      var bossRoom =(BossRoom) floor.getRoom(roomPos);
      
      if (!bossRoom.isDefeated()) {
        
          showBossIntro(bossRoom.boss(), floor.getFloorNumber(), context);
          
          // Combat contre le boss (1 seul ennemi mais très fort)
          var enemies = List.of(bossRoom.boss());
          boolean victory = runCombat(hero, enemies, combatView, context);
          
          if (victory) {
              
              floor.markBossDefeated(roomPos);
              
              // Récompenses bonus du boss
              int bonusGold = 50 + floor.getFloorNumber() * 25;
              hero.addGold(bonusGold);
                                      
              // Vérifier si victoire finale
              if (floor.getFloorNumber() >= 3) {  // 3 étages = fin du jeu
                  return false;
              } else {
                  System.out.println(" La sortie vers l'étage suivant est débloquée !");
              }
          } else {
              showGameOver(hero, context);
              return false;
          }
      }
      return true;
    }
    
    private static void treasureEventRunGame(Controller dungeonController, Floor floor, Hero hero, ApplicationContext context) {
      var roomPos = dungeonController.getEventRoom();
      var treasureRoom = (TreasureRoom) floor.getRoom(roomPos);
      
      if (!treasureRoom.isLooted()) {
          System.out.println("Coffre au trésor trouvé !");
          
          // Afficher le loot
          showLootZone(hero, treasureRoom.loot(), LootZone.LootType.TREASURE_CHEST, context);
          
          // Marquer comme pillé
          floor.markRoomLooted(roomPos);
      } else {
          System.out.println("Ce coffre a déjà été pillé");
      }
    }
    
    private static void merchantEventRunGame(Controller dungeonController, Floor floor, Hero hero, ApplicationContext context) {
    	 var roomPos = dungeonController.getEventRoom();
       var room = floor.getRoom(roomPos);
       
       if (room instanceof MerchantRoom merchant) {
           System.out.println("🛒 Bienvenue chez le marchand !");
           
           showMerchant(hero, merchant, context);
       }
    }
    
    private static void gateLockedRunGame(Controller dungeonController, Floor floor, Hero hero, ApplicationContext context) {
    	 var roomPos = dungeonController.getEventRoom();
       var gateRoom = (GateRoom) floor.getRoom(roomPos);
       
       if (!gateRoom.isUnlocked()) {
           

           if (hero.hasKey()) {
               // Proposer d'utiliser la clé
               boolean useKey = showGatePrompt(hero, gateRoom, context);
               
               if (useKey) {
                   hero.useKey();
                   gateRoom.unlock();

                   // Entrer dans la salle cachée
                   floor.movePlayer(roomPos);
                   
                   // Déclencher l'événement de la salle cachée
                   var treasure =(TreasureRoom) gateRoom.getHiddenRoom();
                   if (!treasure.isLooted()) {
                       System.out.println("💎 Trésor caché trouvé !");
                       showLootZone(hero, treasure.loot(), LootZone.LootType.TREASURE_CHEST, context);
                   }
               }
           } else {
               showNoKeyMessage(context);
           }
       }
    }
    
    private static void runGame(ApplicationContext context) throws IOException {
        // Créer le héros
        var hero = createStartingHero();  // Créer le donjon
        var dungeon = new Dungeon(TOTAL_FLOORS);

        // Créer les vues
        var screenInfo = context.getScreenInfo();   var screenWidth = (int) screenInfo.width(); var screenHeight = (int) screenInfo.height();

        var dungeonView = new DungeonView(screenWidth, screenHeight);
        var combatView = new CombatView(screenWidth, screenHeight);
        
        // Boucle principale du jeu
        boolean gameRunning = true;
        
        while (gameRunning && hero.isAlive()) {
            var floor = dungeon.getCurrentFloor();
            
            // Phase Donjon
            var dungeonController = new DungeonController(floor, hero, dungeonView, context);  
            var roomEvent = dungeonController.run();
            // Traiter l'événement
            switch (roomEvent) {
                case COMBAT -> {
                  gameRunning =  combatEventRunGame(dungeonController,floor,hero,combatView, context);
                }
                case BOSS -> {
                	gameRunning = boossEventRunGame(dungeonController,floor,hero,combatView,context); 			
                }
                case TREASURE -> {
                	treasureEventRunGame(dungeonController, floor, hero,context);
                }
                
                case MERCHANT -> {
                		merchantEventRunGame(dungeonController,floor, hero,context);
                }
                
                case HEALER -> {
                    showHealer(hero, dungeon.getCurrentFloorNumber(), context);
                 
                }
                
                case EXIT -> {
                	 var roomPos = dungeonController.getEventRoom();
                   var room = floor.getRoom(roomPos);
                   
                   if (room instanceof ExitRoom exitRoom) {
                       var currentFloorNum = floor.getFloorNumber();
                       var nextFloorNum = exitRoom.getNextFloor();
                       
                       // Vérifier si c'est le dernier étage
                       if (currentFloorNum >= 3) {
                           showVictoryScreen(hero, context);
                           gameRunning = false;
                       } else {
                           // Afficher la transition
                           showFloorTransition(currentFloorNum, nextFloorNum, hero, context);
                           
                           // Créer le nouvel étage
                           floor = new Floor(nextFloorNum);
                           
                           // Recréer le contrôleur et la vue
                           dungeonView = new DungeonView(screenWidth, screenHeight);
                           dungeonController = new DungeonController(floor, hero, dungeonView, context);
                           
                           System.out.println("🗺️ Bienvenue à l'étage " + nextFloorNum + " !");
                       }
                   }
                }
                
                case GAME_OVER -> {
                    gameRunning = false;
                }
                
                case GATE_LOCKED -> {
                	 gateLockedRunGame(dungeonController,floor,hero, context);
                }
                case NONE -> {}
            }
        }
        
        // Écran de fin
        if (hero.isDead()) {
            System.out.println("💀 GAME OVER - Le héros est mort");
        }
    }
    
    private static void showHealer(Hero hero, int floorNumber, ApplicationContext context) {
      var screenInfo = context.getScreenInfo();
      var width = (int) screenInfo.width();
      var height = (int) screenInfo.height();
      
      var healerView = new HealerView(width, height);
      var healerController = new HealerController(hero, healerView, context, floorNumber);
      healerController.run();
      
    }
    
    private static void showMerchant(Hero hero, MerchantRoom merchant, ApplicationContext context) {
      var screenInfo = context.getScreenInfo();
      var width = (int) screenInfo.width();
      var height = (int) screenInfo.height();
      
      var merchantView = new MerchantView(width, height);
      var merchantController = new MerchantController(merchant, hero, merchantView, context);
      merchantController.run();

    }
    
 // ════════════════════════════════════════════
 // PRÉSENTATION DU BOSS
 // ════════════════════════════════════════════

    
// private static boolean isKeyBoard(Event event) {
//	 return switch(event) {
//		 case KeyboardEvent _ -> true;
//		 default -> false;
//	 };
// }
 private static void showBossIntro(Enemy boss, int floorNumber, ApplicationContext context) {
     var screenInfo = context.getScreenInfo();
     var width = (int) screenInfo.width();
     var height = (int) screenInfo.height();
     
     var bossView = new BossView(width, height);

     // Animation d'intro (quelques secondes)
     long startTime = System.currentTimeMillis();
     boolean waiting = true;
     
     while (waiting) {
         context.renderFrame(graphics -> {
             bossView.draw(graphics, boss, floorNumber);
         });
         
         var event = context.pollOrWaitEvent(30);
         
         // Attendre au moins 1 seconde avant de permettre de skip
         if (System.currentTimeMillis() - startTime > 1000) {
             if (event instanceof KeyboardEvent keyboard) {
                 if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED &&
                		 keyboard.key() == KeyboardEvent.Key.SPACE) {
                     waiting = false;
                 }
             }
             if (event instanceof PointerEvent pointer) {
                 if (pointer.action() == PointerEvent.Action.POINTER_UP) {
                     waiting = false;
                 }
             }
         }
     }
 }

 private static void showFloorTransition(int currentFloor, int nextFloor, Hero hero, ApplicationContext context) {
   var screenInfo = context.getScreenInfo();
   var width = (int) screenInfo.width();
   var height = (int) screenInfo.height();
   
   var transitionView = new FloorTransitionView(width, height);
   
   // Attendre au moins 500ms avant de permettre de skip
   var startTime = System.currentTimeMillis();
   var waiting = true;
   
   while (waiting) {
       context.renderFrame(graphics -> {
           transitionView.draw(graphics, currentFloor, nextFloor, hero);
       });
       
       var event = context.pollOrWaitEvent(30);
       
       // Attendre un peu avant de permettre de continuer
       if (System.currentTimeMillis() - startTime > 500) {
           if (event instanceof KeyboardEvent keyboard) {
               if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED &&
                   keyboard.key() == KeyboardEvent.Key.SPACE) {
                   waiting = false;
               }
           }
           if (event instanceof PointerEvent pointer) {
               if (pointer.action() == PointerEvent.Action.POINTER_UP) {
                   waiting = false;
               }
           }
       }
   }
}
 // ════════════════════════════════════════════
 // VICTOIRE CONTRE LE BOSS
 // ════════════════════════════════════════════
 private static boolean showGatePrompt(Hero hero, GateRoom gate, ApplicationContext context) {
   var screenInfo = context.getScreenInfo();
   int width = (int) screenInfo.width();
   int height = (int) screenInfo.height();
   
   boolean[] result = {false};
   boolean waiting = true;
   
   while (waiting) {
       context.renderFrame(graphics -> {
           // Fond semi-transparent
          GatePromptView.draw(graphics, width, height, hero);
       });
       
       var event = context.pollOrWaitEvent(50);
       
       
       if (event instanceof KeyboardEvent keyboard) {
           if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED) {
               switch (keyboard.key()) {
                   case O, SPACE -> {
                       result[0] = true;
                       waiting = false;
                   }
                   case N, ESCAPE -> {
                       result[0] = false;
                       waiting = false;
                   }
                   default -> {}
               }
           }
       }
   }
   
   return result[0];
}
 
 private static void showNoKeyMessage(ApplicationContext context) {
   var screenInfo = context.getScreenInfo();
   int width = (int) screenInfo.width();
   int height = (int) screenInfo.height();
   
   long startTime = System.currentTimeMillis();
   
   while (System.currentTimeMillis() - startTime < 2000) {  // Afficher 2 secondes
       context.renderFrame(graphics -> {
          GatePromptView.showNoKeyMessage(graphics, width, height);
       });
       
       // Permettre de skip avec une touche
       var event = context.pollOrWaitEvent(50);
       if (event instanceof KeyboardEvent keyboard) {
           if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED) {
               break;
           }
       }
   }
}


 // ════════════════════════════════════════════
 // ÉCRAN VICTOIRE FINALE
 // ════════════════════════════════════════════

 public static void showVictoryScreen(Hero hero, ApplicationContext context) {
     var screenInfo = context.getScreenInfo();
     int width = (int) screenInfo.width();
     int height = (int) screenInfo.height();
     
     
     boolean waiting = true;
     int frame = 0;
     
     while (waiting) {
         final int f = frame++;
         
         context.renderFrame(graphics -> {
            VictoryView.showVictory(graphics, width, height, f, hero);
         });
         
         var event = context.pollOrWaitEvent(30);
         
         if (event instanceof KeyboardEvent keyboard) {
             if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED) {
                 waiting = false;
             }
         }
     }
 }

 // ════════════════════════════════════════════
 // ÉCRAN GAME OVER
 // ════════════════════════════════════════════

 private static void showGameOver(Hero hero, ApplicationContext context) {
     var screenInfo = context.getScreenInfo();
     var width = (int) screenInfo.width();
     var height = (int) screenInfo.height();
     
     
     boolean waiting = true;
     
     while (waiting) {
         context.renderFrame(graphics -> {
           VictoryView.showGameOver(graphics, hero, width, height);
         });
         
         var event = context.pollOrWaitEvent(50);
         
         if (event instanceof KeyboardEvent keyboard) {
             if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED) {
            	 
            	 
                 waiting = false;
             }
         }
     }
 }
    
    // ==================== COMBAT ====================
    
    private static boolean runCombat(Hero hero, java.util.List<Enemy> enemies, 
        CombatView view, ApplicationContext context) throws IOException {

    	var combat = new Combat(hero, enemies);
    	var controller = new CombatController(combat, context);
    	controller.setView(view);
    	controller.start();

    	// Boucle de combat
    	while (!controller.isCombatOver()) {
    		// Rendu
    		context.renderFrame(graphics -> {
    		try {
    			view.draw(graphics, combat, hero);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	});

    	// Événements
    		var event = context.pollOrWaitEvent(50);
    		if (event == null) continue;

    		if (event instanceof PointerEvent pointer) {
    			controller.handlePointerEvent(pointer);
    		} else if (event instanceof KeyboardEvent keyboard) {
    			controller.handleKeyboardEvent(keyboard);
    		}
    	}
	
	// ═══════════════════════════════════════════
	// RÉCOMPENSES (si victoire)
	// ═══════════════════════════════════════════

   if (controller.isVictory()) {
  	 // Calculer les récompenses
	   var reward = CombatReward.generate(combat, hero);
	
	   // Afficher dans le journal
	   combat.addLog("", false);
	   combat.addLog("══ RÉCOMPENSES ══", true);
	   combat.addLog("+ " + reward.getExperience() + " XP", false);
	   combat.addLog("+ " + reward.getGold() + " Or", false);
	
	   if (!reward.getItems().isEmpty()) {
	  	 combat.addLog("Items trouvés :" + reward.getItems().size(), false);
	  	 for (var item : reward.getItems()) {
	  		 var itemName = getItemName(item);
	  		 combat.addLog("  • " + itemName, false);
	  	 }
	   }

	   // Vérifier level up
	   // Sauvegarder niveau actuel AVANT d'appliquer XP
	   
	   var oldLevel = hero.getLevel();
	   int oldSlots = hero.getBackpack().getUnlockedSlotCount();
	
	   // Appliquer les récompenses (XP et Or)
	    hero.gainExperience(reward.getExperience());
	    hero.addGold(reward.getGold());
	    
	
	    // Verifier level Up
	   if (hero.getLevel() > oldLevel) {
	  	 int newSlots = hero.getBackpack().getUnlockedSlotCount();
       int slotsGained = newSlots - oldSlots;
       
	  	 combat.addLog("", false);
	  	 combat.addLog("★ NIVEAU SUPÉRIEUR ! ★", true);
	  	 combat.addLog("Vous êtes maintenant niveau " + hero.getLevel(), true);
	  	 combat.addLog("PV max : " + hero.getMaxHealthPoint(), false);
       combat.addLog("Énergie max : " + hero.getMaxEnergy(), false);
       if (slotsGained > 0) {
         combat.addLog("+" + slotsGained + " cases de sac !", true);
  	   }
	   }
	  
	   combat.addLog("", false);
	   combat.addLog("XP : " + hero.getExperience() + "/" + hero.getExperienceToNextLevel(), false);
	   
	   combat.addLog("Appuyez sur ESPACE pour continuer...", true);
	   
	   // Afficher écran de fin de combat
     context.renderFrame(graphics -> {
         try {
             view.draw(graphics, combat, hero);
         } catch (IOException e) {
             e.printStackTrace();
         }
     });
     
     waitForKey(context);
     
     // ═══════════════════════════════════════════
     // AFFICHER LOOT ZONE (si items disponibles)
     // ═══════════════════════════════════════════
     
     if (!reward.getItems().isEmpty()) {
         showLootZone(hero, reward.getItems(), LootZone.LootType.COMBAT_REWARD, context);
     }
     
   } else {
  	 // Défaite
  	 combat.addLog("", false);
  	 combat.addLog("Appuyez sur ESPACE...", true);
  	 context.renderFrame(graphics -> {
	       try {
	           view.draw(graphics, combat, hero);
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	   });
	   
	   waitForKey(context);
   }

   

   return controller.isVictory();
  }
    
    private static void showLootZone(Hero hero, List<Item> items, 
        LootZone.LootType type, ApplicationContext context) {
			
			var screenInfo = context.getScreenInfo();
			int width = (int) screenInfo.width();
			int height = (int) screenInfo.height();
			
			// Créer la LootZone avec les items
			var lootZone = new LootZone(type, items);
			
			// Créer la vue
			var lootView = new LootZoneView(width, height);
			
			// Créer le contrôleur et lancer
			var lootController = new LootController(lootZone, hero, lootView, context);
			lootController.run();
			
			System.out.println("Loot terminé - Items récupérés !");
		}
//Méthode helper pour obtenir le nom d'un item
    private static String getItemName(fr.uge.model.Item item) {
    	return switch (item) {
    		case Weapon w -> w.name();
    		case Armor a -> a.name();
    		case Consumable c -> c.name();
    		case Magic m -> m.name();
    		case Gold g -> g.number() + " Or";
    		default -> "Item";
    	};
    }
    
    private static void waitForKey(ApplicationContext context) {
      while (true) {
        var event = context.pollOrWaitEvent(100);
        
        if (event instanceof KeyboardEvent keyboard) {
        	if (keyboard.action() == KeyboardEvent.Action.KEY_PRESSED &&
        		keyboard.key() == KeyboardEvent.Key.SPACE) {
            	break;
          }
        }
        if (event instanceof PointerEvent pointer) {
        	if (pointer.action() == PointerEvent.Action.POINTER_UP) {
        		break;
          }
        }
      }
    }
    
    
    private static Hero createStartingHero() {
        var backpack = new BackPack();
        backpack.init();
        
        // Ajouter une arme de départ
        var sword = new Weapon("Épée en fer", WeaponType.SWORDS, WeaponClass.MELLER,
            1, 8, Rarity.COMMON,
            List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(0, 2)));
        backpack.addEquipement(sword, new Coordonate(2, 1));
        
        // Ajouter un bouclier
        var shield = new Armor("Bouclier", ArmorType.SHIELD, Rarity.COMMON,
            List.of(new Coordonate(0, 0), new Coordonate(1, 0), 
                    new Coordonate(0, 1), new Coordonate(1, 1)),
            1, 5, "images/shield.png");
        backpack.addEquipement(shield, new Coordonate(3, 1));
        
        // Ajouter une potion
        var potion = new Consumable("Potion", List.of(new Coordonate(0, 0)));
        backpack.addEquipement(potion, new Coordonate(4, 3));
        
        return new Hero(30, 30, 1, 0, 3, 3, 10, 0, backpack, 1);
    }
}
