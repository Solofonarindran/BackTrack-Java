package fr.uge.controller;


import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.MerchantRoom;
import fr.uge.model.Coordonate;
import fr.uge.model.Curse;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.view.MerchantView;

public class MerchantController {
    
    private final MerchantRoom merchant;
    private final Hero hero;
    private final MerchantView view;
    private final ApplicationContext context;
    
    private boolean finished;
    
    public MerchantController(MerchantRoom merchant, Hero hero, MerchantView view, ApplicationContext context) {
        this.merchant = Objects.requireNonNull(merchant);
        this.hero = Objects.requireNonNull(hero);
        this.view = Objects.requireNonNull(view);
        this.context = Objects.requireNonNull(context);
        this.finished = false;
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
    	var shopItem = view.getShopItemAt(x, y);
      var bagItem = view.getBagItemAt(x, y);
      view.setHoveredItem(shopItem != null ? shopItem : bagItem);
      
      // Preview si item boutique sélectionné
      if (view.getSelectedShopItem() != null) {
          var bagPos = view.getBagPositionAt(x, y, hero.getBackpack());
          view.setPreviewPosition(bagPos);
      }
    }
    
    private void pointerUpHandleEvent(int x, int y) {
   // Clic sur boutique
      var shopItem = view.getShopItemAt(x, y);
      if (shopItem != null) {
          selectShopItem(shopItem);
          return;
      }
      
      // Clic sur sac
      var bagItem = view.getBagItemAt(x, y);
      if (bagItem != null && view.getSelectedShopItem() == null) {
          // Sélectionner pour vendre
          selectBagItem(bagItem);
          return;
      }
      
      // Clic sur position du sac (placer item acheté)
      if (view.getSelectedShopItem() != null) {
          var bagPos = view.getBagPositionAt(x, y, hero.getBackpack());
          if (bagPos != null) {
              tryBuyItem(view.getSelectedShopItem(), bagPos);
          }
      }
    }
    // ==================== ÉVÉNEMENTS ====================
    
    private void handlePointerEvent(PointerEvent event) {
        int x = (int) event.location().x();
        int y = (int) event.location().y();
        
        switch (event.action()) {
            case POINTER_MOVE -> {
            	pointerMoveHandleEvent(x,y);
            }
            
            case POINTER_UP -> {
            	pointerUpHandleEvent(x,y);
            }
            
            default -> {}
        }
    }
    
    private void handleKeyboardEvent(KeyboardEvent event) {
        if (event.action() != KeyboardEvent.Action.KEY_PRESSED) return;
        
        switch (event.key()) {
            case SPACE -> finished = true;
            
            case A -> {
                // Confirmer vente
                if (view.getSelectedBagItem() != null) {
                    trySellItem(view.getSelectedBagItem());
                }
            }
            
            case ESCAPE -> view.clearSelection();
            
            default -> {}
        }
    }
    
    // ==================== ACTIONS ====================
    
    private void selectShopItem(Item item) {
        int price = view.getItemPrice(item);
        
        if (hero.getGold() < price) {
            return;
        }
        
        view.setSelectedShopItem(item);
    }
    
    private void selectBagItem(Item item) {
        // Ne pas vendre les malédictions
        if (item instanceof Curse) {
            return;
        }
        
        view.setSelectedBagItem(item);
        view.getSellPrice(item);
     
    }
    
    private void tryBuyItem(Item item, Coordonate position) {
        var price = view.getItemPrice(item);
        // Vérifier l'or
        if (hero.getGold() < price) {
            view.clearSelection();
            return;
        }
        
        // Vérifier le placement
        var backpack = hero.getBackpack();
        if (!backpack.addEquipement(item, position)) {
            return;
        }
        
        // Effectuer l'achat
        hero.spendGold(price);
        merchant.removeItem(item);
        view.clearSelection();
    }
    
    private void trySellItem(Item item) {
        int sellPrice = view.getSellPrice(item);
        // Retirer du sac
        hero.getBackpack().removeEquipment(item);
        // Gagner l'or
        hero.addGold(sellPrice);
        view.clearSelection();
    }
    
    // ==================== RENDU ====================
    
    private void render() {
        context.renderFrame(graphics -> {
            view.draw(graphics, merchant, hero);
        });
    }
    
}