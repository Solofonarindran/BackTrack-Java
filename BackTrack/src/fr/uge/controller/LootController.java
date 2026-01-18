package fr.uge.controller;

import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.LootZone;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.view.LootZoneView;

public class LootController {
    
    private final LootZone lootZone;
    private final Hero hero;
    private final LootZoneView view;
    private final ApplicationContext context;
    
    private boolean finished;
    private Item selectedItem;
    
    public LootController(LootZone lootZone, Hero hero, LootZoneView view, ApplicationContext context) {
        this.lootZone = Objects.requireNonNull(lootZone);
        this.hero = Objects.requireNonNull(hero);
        this.view = Objects.requireNonNull(view);
        this.context = Objects.requireNonNull(context);
        this.finished = false;
        this.selectedItem = null;
    }
    
    // ==================== BOUCLE PRINCIPALE ====================
    
    public void run() {
        while (!finished) {
            render();
            
            var event = context.pollOrWaitEvent(50);
            if (event == null) continue;
            
            if (event instanceof PointerEvent pointer) {
                handlePointerEvent(pointer);
            } else if (event instanceof KeyboardEvent keyboard) {
                handleKeyboardEvent(keyboard);
            }
        }
    }
    
    
    private void pointerMoveHandleEvent(int x, int y) {
    	// Survol des items de loot
      var hoveredItem = view.getLootItemAt(x, y);
      view.setHoveredItem(hoveredItem);
      
      // Preview dans le sac
      if (selectedItem != null) {
          var bagPos = view.getBagPositionAt(x, y, hero.getBackpack());
          view.setPreviewPosition(bagPos);
      }
    }
    
    private void pointerUpHandleEvent(int x, int y) {
    	 // Clic sur un item de loot
      var clickedLootItem = view.getLootItemAt(x, y);
      if (clickedLootItem != null) {
          selectItem(clickedLootItem);
          return;
      }
      
      // Clic sur le sac (placer l'item)
      if (selectedItem != null) {
          var bagPos = view.getBagPositionAt(x, y, hero.getBackpack());
          if (bagPos != null) {
              tryPlaceItem(selectedItem, bagPos);
          }
      }
    }
    // ==================== ÉVÉNEMENTS ====================
    
    private void handlePointerEvent(PointerEvent event) {
        int x = (int) event.location().x();
        int y = (int) event.location().y();
        
        switch (event.action()) {
            case POINTER_MOVE -> {
              pointerMoveHandleEvent(x, y);
            }
            
            case POINTER_UP -> {
               pointerUpHandleEvent(x, y);
            }
            default -> {}
        }
    }
    
    private void handleKeyboardEvent(KeyboardEvent event) {
        if (event.action() != KeyboardEvent.Action.KEY_PRESSED) return;
        
        switch (event.key()) {
            case SPACE -> finish();
            case ESCAPE -> {
                if (selectedItem != null) {
                    deselectItem();
                } else {
                    finish();
                }
            }
            default -> {}
        }
    }
    
    // ==================== ACTIONS ====================
    
    private void selectItem(Item item) {
        // Or = récupération automatique
        if (item instanceof Gold gold) {
            hero.addGold(gold.number());
            lootZone.removeItem(item);
            return;
        }
        
        selectedItem = item;
        view.setSelectedItem(item);
    }
    
    private void deselectItem() {
        selectedItem = null;
        view.setSelectedItem(null);
        view.setPreviewPosition(null);
    }
    
    private void tryPlaceItem(Item item, Coordonate position) {
        var backpack = hero.getBackpack();
        
        // Utiliser la méthode addEquipement de ton BackPack
        boolean success = backpack.addEquipement(item, position);
        
        if (success) {
            lootZone.removeItem(item);
            
            selectedItem = null;
            view.setSelectedItem(null);
            view.setPreviewPosition(null);
            
        } else {
            System.out.println("Impossible de placer ici !");
        }
    }
    
    private void finish() {
        if (!lootZone.getItems().isEmpty()) {
            System.out.println("Items non récupérés seront perdus !");
        }
        finished = true;
    }
    
    // ==================== RENDU ====================
    
    private void render() {
        context.renderFrame(graphics -> {
            view.draw(graphics, lootZone, hero);
        });
    }
    

    
    // ==================== GETTERS ====================
    
    public boolean isFinished() { return finished; }
    public LootZone getLootZone() { return lootZone; }
}