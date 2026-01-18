package fr.uge.controller;

import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.model.Hero;
import fr.uge.view.HealerView;

public class HealerController {
    
    private final Hero hero;
    private final HealerView view;
    private final ApplicationContext context;
    
    // Prix des soins
    private final int fullHealCost;
    private final int halfHealCost;
    
    private boolean finished;
    
    public HealerController(Hero hero, HealerView view, ApplicationContext context, int floorNumber) {
        this.hero = Objects.requireNonNull(hero);
        this.view = Objects.requireNonNull(view);
        this.context = Objects.requireNonNull(context);
        
        // Prix basé sur l'étage
        this.fullHealCost = 15 + floorNumber * 5;
        this.halfHealCost = 8 + floorNumber * 2;
        
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
    
    // ==================== ÉVÉNEMENTS ====================
    
    private void handlePointerEvent(PointerEvent event) {
        var x = (int) event.location().x();
        var y = (int) event.location().y();
        
        switch (event.action()) {
            case POINTER_MOVE -> {
                int button = view.getButtonAt(x, y);
                view.setHoveredButton(button);
            }
            
            case POINTER_UP -> {
                int button = view.getButtonAt(x, y);
                handleButtonClick(button);
            }
            
            default -> {}
        }
    }
    
    private void handleKeyboardEvent(KeyboardEvent event) {
        if (event.action() != KeyboardEvent.Action.KEY_PRESSED) return;
        
        switch (event.key()) {
            case SPACE, ESCAPE -> finished = true;
            case A -> handleButtonClick(0);  // Soin complet
            case B -> handleButtonClick(1);  // Demi-soin
            default -> {}
        }
    }
    
    private void handleButtonClick(int button) {
        switch (button) {
            case 0 -> tryFullHeal();
            case 1 -> tryHalfHeal();
            case 2 -> finished = true;
        }
    }
    
    // ==================== ACTIONS ====================
    
    private void tryFullHeal() {
        var missingHp = hero.getMaxHealthPoint() - hero.getHealthPoint();
        
        if (missingHp <= 0) {
            System.out.println("Vous êtes déjà en pleine forme !");
            return;
        }
        
        if (hero.getGold() < fullHealCost) {
            System.out.println("Pas assez d'or ! (besoin: " + fullHealCost + ", vous avez: " + hero.getGold() + ")");
            return;
        }
        
        // Effectuer le soin
        hero.spendGold(fullHealCost);
        hero.heal(missingHp);
       
    }
    
    private void tryHalfHeal() {
        var missingHp = hero.getMaxHealthPoint() - hero.getHealthPoint();
        
        if (missingHp <= 0) {
            return;
        }
        if (hero.getGold() < halfHealCost) {
            System.out.println("Pas assez d'or ! (besoin: " + halfHealCost + ", vous avez: " + hero.getGold() + ")");
            return;
        }
        
        // Calculer le soin (50% des PV max, mais pas plus que ce qui manque)
        var healAmount = Math.min(hero.getMaxHealthPoint() / 2, missingHp);
        
        // Effectuer le soin
        hero.spendGold(halfHealCost);
        hero.heal(healAmount);
   
    }
    
    // ==================== RENDU ====================
    
    private void render() {
        context.renderFrame(graphics -> {
            view.draw(graphics, hero, fullHealCost, halfHealCost);
        });
    }
}