//package fr.uge.controller;
//
//import java.io.IOException;
//
//import com.github.forax.zen.ApplicationContext;
//import com.github.forax.zen.KeyboardEvent;
//import com.github.forax.zen.KeyboardEvent.Key;
//import com.github.forax.zen.PointerEvent;
//
//import fr.uge.data.Combat;
//import fr.uge.data.CombatReward;
//import fr.uge.model.Hero;
//import fr.uge.model.Item;
//import fr.uge.view.CombatView;
//
//public class CombatRun {
//	private final Combat combat;
//  private final Hero hero;
//  private final CombatView view;
//  
//  private boolean running;
//  private boolean combatEnded;
//  
//  public CombatRun(Combat combat, Hero hero, CombatView view) {
//      this.combat = combat;
//      this.hero = hero;
//      this.view = view;
//      this.running = true;
//      this.combatEnded = false;
//  }
//  
//  /**
//   * Boucle principale du combat
//   */
//  public void run(ApplicationContext context) {
//      // Démarrer le combat
//      combat.start();
//      
//      // Boucle de jeu
//      while (running) {
//          // Rendu
//          render(context);
//          
//          // Gestion des événements
//          var event = context.pollOrWaitEvent(50); // 50ms timeout
//          
//          if (event == null) continue;
//          
//          if (event instanceof PointerEvent pointer) {
//              handlePointer(pointer);
//          } else if (event instanceof KeyboardEvent keyboard) {
//              handleKeyboard(keyboard);
//          }
//      }
//      
//      // Afficher les récompenses si victoire
//      if (combat.isVictory()) {
//          var reward = CombatReward.generate(combat, hero);
//          System.out.println(reward);
//          reward.applyTo(hero);
//      }
//  }
//  
//  /**
//   * Rendu de l'écran
//   */
//  private void render(ApplicationContext context) {
//      context.renderFrame(graphics -> {
//          try {
//						view.draw(graphics, combat, hero);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//      });
//  }
//  
//  /**
//   * Gestion des événements souris
//   */
//  private void handlePointer(PointerEvent event) {
//      int x = (int) event.location().x();
//      int y = (int) event.location().y();
//      
//      var backPackView = view.getBackPackView();
//      
//      switch (event.action()) {
//          case POINTER_MOVE -> {
//              // Survol
//              if (backPackView != null && backPackView.containsPoint(x, y)) {
//                  var hoveredItem = backPackView.getItemAt(x, y);
//                  backPackView.setHoveredItem(hoveredItem);
//              } else if (backPackView != null) {
//                  backPackView.setHoveredItem(null);
//              }
//          }
//          
//          case POINTER_UP -> {
//              // Clic
//              if (combatEnded) {
//                  // Clic pour continuer après fin de combat
//                  running = false;
//                  return;
//              }
//              
//              if (combat.getState() != Combat.CombatState.HERO_TURN) {
//                  return;
//              }
//              
//              // Vérifier clic sur item du sac
//              if (backPackView != null && backPackView.containsPoint(x, y)) {
//                  var clickedItem = backPackView.getItemAt(x, y);
//                  if (clickedItem != null && backPackView.canUseItem(clickedItem)) {
//                      useItem(clickedItem);
//                  }
//              }
//          }
//          
//          default -> {}
//      }
//  }
//  
//  /**
//   * Gestion des événements clavier
//   */
//  private void handleKeyboard(KeyboardEvent event) {
//      if (event.action() != KeyboardEvent.Action.KEY_PRESSED) {
//          return;
//      }
//      
//      // Si combat terminé, n'importe quelle touche pour quitter
//      if (combatEnded) {
//          running = false;
//          return;
//      }
//      
//      switch (event.key()) {
//          case Key.SPACE -> {
//              // Fin du tour
//              if (combat.getState() == Combat.CombatState.HERO_TURN) {
//                  endTurn();
//              }
//          }
//          
//          case Key.ESCAPE	 -> {
//              // Quitter (abandon)
//              running = false;
//          }
//          
//          // A -> 1
//          //I -> 9
//          case Key.A, Key.B, Key.C,
//               Key.D, Key.E, Key.F,
//               Key.G, Key.H, Key.I -> {
//              // Raccourci pour utiliser un item
//              if (combat.getState() == Combat.CombatState.HERO_TURN) {
//                  int index = event.key().ordinal() - KeyboardEvent.Key.A.ordinal();
//                  var items = hero.getBackpack().getAllItems();
//                  if (index < items.size()) {
//                      var item = items.get(index);
//                      if (view.getBackPackView().canUseItem(item)) {
//                          useItem(item);
//                      }
//                  }
//              }
//          }
//          
//          default -> {}
//      }
//  }
//  
//  /**
//   * Utilise un item en combat
//   */
//  private void useItem(Item item) {
//      boolean success = combat.useItem(item);
//      
//      if (success) {
//          System.out.println("Item utilise: " + item);
//          
//          // Mettre à jour la vue
//          view.getBackPackView().updateEnergy(hero.getEnergy(), hero.getMaxEnergy());
//          
//          // Vérifier fin de combat
//          checkCombatEnd();
//      }
//  }
//  
//  /**
//   * Termine le tour du héros
//   */
//  private void endTurn() {
//      combat.endHeroTurn();
//      checkCombatEnd();
//  }
//  
//  /**
//   * Vérifie si le combat est terminé
//   */
//  private void checkCombatEnd() {
//      if (combat.isOver()) {
//          combatEnded = true;
//      }
//  }
//  
//  // ==================== POINT D'ENTRÉE ====================
//  
////  public static void main(String[] args) {
////      System.out.println("=== LANCEMENT DU COMBAT ===\n");
////      
////      // Créer le héros avec équipement
////      var backpack = new BackPack();
////      backpack.initializeStartingGrid();
////      
////      // Ajouter des items
////      var sword = new Weapon("Epee en fer", WeaponType.SWORDS, WeaponClass.MELLER,
////          1, 8, Rarity.COMMON,
////          List.of(new Coordonate(0, 0), new Coordonate(0, 1), new Coordonate(0, 2)));
////      backpack.addEquipement(sword, new Coordonate(2, 1));
////      
////      var shield = new Armor("Bouclier", ArmorType.SHIELD, Rarity.COMMON,
////          List.of(new Coordonate(0, 0), new Coordonate(1, 0), new Coordonate(0, 1), new Coordonate(1, 1)),
////          1, 5, "images/shield.png");
////      backpack.addEquipement(shield, new Coordonate(3, 1));
////      
////      var potion = new Consumable("Potion", List.of(new Coordonate(0, 0)));
////      backpack.addEquipement(potion, new Coordonate(4, 3));
////      
////      var hero = new Hero(25, 30, 1, 0, 3, 3, 5, 0, backpack, 0);
////      
////      // Créer un ennemi
////      var enemy = new Enemy(EnemyType.GOBLIN);
////      
////      // Créer le combat
////      var combat = new Combat(hero, enemy);
////      
////      // Lancer avec Zen
////      Application.run(Color.BLACK, context -> {
////          var view = CombatView.create();
////          var combatRun = new CombatRun(combat, hero, view);
////          combatRun.run(context);
////      });
////      
////      System.out.println("\n=== FIN DU COMBAT ===");
////      System.out.println("Resultat: " + (combat.isVictory() ? "Victoire!" : "Defaite..."));
////      System.out.println("Hero PV: " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint());
////  }
//
//}
