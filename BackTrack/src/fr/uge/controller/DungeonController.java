package fr.uge.controller;

import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.data.BossRoom;
import fr.uge.data.EnemyRoom;
import fr.uge.data.ExitRoom;
import fr.uge.data.Floor;
import fr.uge.data.GateRoom;
import fr.uge.data.HealerRoom;
import fr.uge.data.MerchantRoom;
import fr.uge.data.TreasureRoom;
import fr.uge.model.Coordonate;
import fr.uge.model.Hero;
import fr.uge.view.DungeonView;

public class DungeonController implements Controller{
	public enum RoomEvent {
    NONE,
    COMBAT,
    TREASURE,
    MERCHANT,
    HEALER,
    EXIT,
    GAME_OVER,
    GATE_LOCKED,
    BOSS
	}

	private final Floor floor;
  private final Hero hero;
  private final DungeonView view;
  private final ApplicationContext context;
  
  private RoomEvent pendingEvent;
  private Coordonate eventRoom;
  
  public DungeonController(Floor floor, Hero hero, DungeonView view, ApplicationContext context) {
    this.floor = Objects.requireNonNull(floor);
    this.hero = Objects.requireNonNull(hero);
    this.view = Objects.requireNonNull(view);
    this.context = Objects.requireNonNull(context);
    this.pendingEvent = RoomEvent.NONE;
  }
  
  public RoomEvent run() {
    pendingEvent = RoomEvent.NONE;
    while (pendingEvent == RoomEvent.NONE) {
        render();
        
        var event = context.pollOrWaitEvent(50);
        if (event == null) continue;
        
        if (event instanceof PointerEvent pointer) {
            handlePointer(pointer);
        } else if (event instanceof KeyboardEvent keyboard) {
            handleKeyboard(keyboard);
        }
        
        // Vérifier mort du héros
        if (hero.isDead()) {
            pendingEvent = RoomEvent.GAME_OVER;
        }
    }
    
    return pendingEvent;
  }
  
	 
  // ==================== ÉVÉNEMENTS ====================
  
  private void handlePointer(PointerEvent event) {
      int x = (int) event.location().x();
      int y = (int) event.location().y();
      
      switch (event.action()) {
          case POINTER_MOVE -> {
              var roomPos = view.getRoomAt(x, y);
              view.setHoveredRoom(roomPos);
          }
          
          case POINTER_UP -> {
              var roomPos = view.getRoomAt(x, y);
              if (roomPos != null) {
                  handleRoomClick(roomPos);
              }
          }
          
          default -> {}
      }
  }
  
  private void handleKeyboard(KeyboardEvent event) {
    if (event.action() != KeyboardEvent.Action.KEY_PRESSED) {
        return;
    }
    
    // Déplacement avec les flèches
    Coordonate targetPos = null;
    var playerPos = floor.getPlayerPosition();
    
    switch (event.key()) {
        case UP -> targetPos = new Coordonate(playerPos.x(), playerPos.y() - 1);
        case DOWN -> targetPos = new Coordonate(playerPos.x(), playerPos.y() + 1);
        case LEFT -> targetPos = new Coordonate(playerPos.x() - 1, playerPos.y());
        case RIGHT -> targetPos = new Coordonate(playerPos.x() + 1, playerPos.y());
        case ESCAPE -> pendingEvent = RoomEvent.GAME_OVER;
        default -> {}
    }
    
    if (targetPos != null && floor.areConnected(playerPos, targetPos)) {
        handleRoomClick(targetPos);
    }
  }
  
  private void handleRoomClick(Coordonate pos) {
    // Vérifier si adjacent
    if (!floor.areConnected(floor.getPlayerPosition(), pos)) {
        return;
    }
    
    // Vérifier si c'est la position actuelle
    if (pos.equals(floor.getPlayerPosition())) {
        return;
    }
    
    var room = floor.getRoom(pos);
    if (room == null) {
        return;
    }
    
 // ════════════════════════════════════════════
    // CAS SPÉCIAL : GateRoom verrouillée
    // ════════════════════════════════════════════
    if (room instanceof GateRoom gate && !gate.isUnlocked()) {
        eventRoom = pos;
        pendingEvent = RoomEvent.GATE_LOCKED;
        return;  // Ne pas déplacer, juste signaler
    }
    
    if(!room.isAccessible()) {
    	return ;
    }
    // Déplacer le joueur
    floor.movePlayer(pos);
    eventRoom = pos;
    
    // Déclencher l'événement selon le type de salle
    switch (room) {
        case EnemyRoom e -> {
            if (!e.isCleared()) {
                pendingEvent = RoomEvent.COMBAT;
            }
        }
        
        case BossRoom b -> {
            if (!b.defeated()) {
                pendingEvent = RoomEvent.BOSS;
            }
        }
        
        case TreasureRoom t -> {
            if (!t.isLooted()) {
                pendingEvent = RoomEvent.TREASURE;
            }
        }
        
        case MerchantRoom _ -> pendingEvent = RoomEvent.MERCHANT;
        
        case HealerRoom _ -> pendingEvent = RoomEvent.HEALER;
        
        case ExitRoom _ -> pendingEvent = RoomEvent.EXIT;
        
        case GateRoom g -> {
        	if (g.isUnlocked()) {
            var hiddenRoom = g.getHiddenRoom();
            if (hiddenRoom instanceof TreasureRoom t && !t.isLooted()) {
                pendingEvent = RoomEvent.TREASURE;
            }
        	}
        }
         
        default -> {} // EmptyRoom, etc.
    }
  }
  // ==================== RENDU ====================
  
  private void render() {
      context.renderFrame(graphics -> {
          view.draw(graphics, floor, hero);
      });
  }
  
  // ==================== GETTERS ====================
  
  public Floor getFloor() { return floor; }
  public Hero getHero() { return hero; }
  @Override
  public Coordonate getEventRoom() { return eventRoom; }
  
  public RoomEvent getPendingEvent() { return pendingEvent; }

}
