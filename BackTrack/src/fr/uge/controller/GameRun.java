package fr.uge.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.BackPack;
import fr.uge.model.Coordonate;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.view.BackPackView;


/**
 * MAIN - PHASE 2 COMPLÈTE
 */
public class GameRun {
    
    private Item selectedItem = null;
    
    private boolean gameLoop(ApplicationContext context, BackPack backpack, Hero hero, BackPackView view) {
        var event = context.pollOrWaitEvent(10);
        
        switch (event) {
            case null -> { return true; }
            
            case KeyboardEvent ke -> {
                // Q : Quitter
                if (ke.key() == KeyboardEvent.Key.Q) {
                    return false;
                }
                
                // Échap : Désélectionner
                if (ke.key() == KeyboardEvent.Key.ESCAPE) {
                    selectedItem = null;
                    BackPackView.draw(context, backpack, hero, selectedItem, view);
                }
                
                // R : Rotation
                if (ke.key() == KeyboardEvent.Key.R && selectedItem != null) {
                    if (backpack.rotateEquipment(selectedItem)) {
                        System.out.println("✅ Item tourné");
                        // Trouver le nouvel item tourné
                        var firstCoord = backpack.getItemCoordinates(selectedItem).get(0);
                        selectedItem = backpack.getItemAt(firstCoord);
                    } else {
                        System.out.println("❌ Rotation impossible");
                    }
                    BackPackView.draw(context, backpack, hero, selectedItem, view);
                }
                
         
            }
            
            case PointerEvent pe -> {
                if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
                    var loc = pe.location();
                    int x = view.cellXFromMouse(loc.x());
                    int y = view.cellYFromMouse(loc.y());
                    
                    // Vérifier limites
                    if (x < 0 || x >= backpack.getMaxWidth() || 
                        y < 0 || y >= backpack.getMaxHeight()) {
                        return true;
                    }
                    
                    var coord = new Coordonate(x, y);
                    
                    // Case verrouillée
                    if (!backpack.isUnlocked(coord)) {
                        System.out.println("⚠️  Case verrouillée");
                        return true;
                    }
                    
                    Item itemAt = backpack.getItemAt(coord);
                    
                    // Logique de sélection/déplacement
                    if (selectedItem == null) {
                        // Rien de sélectionné
                        if (itemAt != null) {
                            selectedItem = itemAt;
                            System.out.println("✅ Item sélectionné");
                        }
                    } else {
                        // Un item est sélectionné
                        if (itemAt == selectedItem) {
                            // Clic sur le même item : désélectionner
                            selectedItem = null;
                            System.out.println("➖ Désélectionné");
                        } else if (itemAt != null) {
                            // Clic sur un autre item : sélectionner le nouveau
                            selectedItem = itemAt;
                            System.out.println("✅ Autre item sélectionné");
                        } else {
                            // Case vide : déplacer
                            if (backpack.moveEquipment(selectedItem, coord)) {
                                System.out.println("✅ Item déplacé");
                            } else {
                                System.out.println("❌ Déplacement impossible");
                            }
                        }
                    }
                    
                    BackPackView.draw(context, backpack, hero, selectedItem, view);
                }
            }
        }
        
        return true;
    }
    
    private void run(ApplicationContext context) throws IOException {
        // Initialisation
    	  var HEALTH_POINT = 10;
    	  var MAX_HEALTH_POINT = 30;
    	  var level = 1;
    	  var experience = 100;
    	  var maxEnergy = 3;
    	  var energy = 3;
    	  var manaPoint = 5;
    	  var protection = 10;
    	  var key = 1;
    	  
    	  var backpack = new BackPack();
        var hero = new Hero(HEALTH_POINT,MAX_HEALTH_POINT, level, experience,maxEnergy, energy,manaPoint,protection, backpack,key);
     

        BufferedImage background = ImageIO.read(new File("images/murLampe.jpg"));
        var view = BackPackView.create(50, background);
        
        // Items de test
        backpack.init();
        
        System.out.println("✅ BACKPACK HERO - PHASE 2 COMPLÈTE");
        System.out.println("📦 Items placés : " + backpack.getItemCount());
        System.out.println("🎮 Contrôles prêts !\n");
        
        // Premier rendu
        BackPackView.draw(context, backpack, hero, selectedItem, view);
        
        // Boucle principale
        while (true) {
            if (!gameLoop(context, backpack, hero, view)) {
                System.out.println("\n👋 Au revoir !");
                context.dispose();
                return;
            }
        }
    }
    
 
    
//    public static void main(String[] args) {
//        Application.run(Color.BLACK, context -> {
//					try {
//						new GameRun().run(context);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				});
//    }
}