package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import fr.uge.model.Coordonate;
import fr.uge.model.Direction;

import fr.uge.model.Enemy;


/**
 * Représente un étage du donjon.
 * Gère la grille 5×11, les connexions entre salles et la navigation.
 */
public class Floor {
    
    public static final int WIDTH = 11;
    public static final int HEIGHT = 5;
    
    private final int floorNumber;
    private final Room[][] grid;
    private final Map<Coordonate, Set<Coordonate>> connections; // Connexions entre salles
    private Coordonate playerPosition;
    private Coordonate startPosition;
    
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.grid = new Room[HEIGHT][WIDTH];
        this.connections = new HashMap<>();
        generate();
    }
    
    /**
     * Génère l'étage de manière procédurale
     */
    private void generate() {
        var random = new Random(); 
        // 1. Initialiser toutes les cases à null (pas de salle)
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        
        // 2. Générer un chemin principal
        generateMainPath(random);
        
        // 3. Ajouter des branches secondaires
        generateBranches(random);
        
        // 4. Placer les salles spéciales
        placeSpecialRooms(random);
        
        // 5. S'assurer que l'étage est connexe
        ensureConnectivity();
    }
    
    private void generateMainPath(Random random) {
        // Point de départ au milieu à gauche
        startPosition = new Coordonate(0, HEIGHT / 2);
        playerPosition = startPosition;
        
        var currentPos = startPosition;
        grid[currentPos.y()][currentPos.x()] = new EmptyRoom();
        initializeConnections(currentPos);
        
        // Générer un chemin vers la droite
        while (currentPos.x() < WIDTH - 1) {
            var possibleMoves = new ArrayList<Direction>();
            possibleMoves.add(Direction.EAST); // Toujours possible d'aller à droite
            
            if (currentPos.y() > 0) possibleMoves.add(Direction.NORTH);
            if (currentPos.y() < HEIGHT - 1) possibleMoves.add(Direction.SOUTH);
            
            // Favoriser l'avancement vers la droite
            if (random.nextDouble() < 0.6) {
                possibleMoves.clear();
                possibleMoves.add(Direction.EAST);
            }
            
            var direction = possibleMoves.get(random.nextInt(possibleMoves.size()));
            var newPos = currentPos.move(direction);
            
            if (newPos.isInBounds(WIDTH, HEIGHT) && grid[newPos.y()][newPos.x()] == null) {
                grid[newPos.y()][newPos.x()] = new EmptyRoom();
                initializeConnections(newPos);
                addConnection(currentPos, newPos);
                currentPos = newPos;
            } else if (direction == Direction.EAST) {
                // Si on ne peut pas aller à l'est, forcer
                newPos = currentPos.move(Direction.EAST);
                if (newPos.isInBounds(WIDTH, HEIGHT)) {
                    if (grid[newPos.y()][newPos.x()] == null) {
                        grid[newPos.y()][newPos.x()] = new EmptyRoom();
                        initializeConnections(newPos);
                    }
                    addConnection(currentPos, newPos);
                    currentPos = newPos;
                }
            }
        }
        
        // Placer la sortie à la fin du chemin principal
        grid[currentPos.y()][currentPos.x()] = new ExitRoom(floorNumber + 1);
    }
    
    private void generateBranches(Random random) {
        var existingRooms = getAllRoomPositions();
        
        for (var pos : existingRooms) {
            // 30% de chance de créer une branche
            if (random.nextDouble() < 0.3) {
                for (var dir : Direction.values()) {
                    var newPos = pos.move(dir);
                    if (newPos.isInBounds(WIDTH, HEIGHT) && 
                        grid[newPos.y()][newPos.x()] == null &&
                        random.nextDouble() < 0.5) {
                        
                        grid[newPos.y()][newPos.x()] = new EmptyRoom();
                        initializeConnections(newPos);
                        addConnection(pos, newPos);
                        
                        // Possibilité d'étendre la branche
                        if (random.nextDouble() < 0.3) {
                            var extendPos = newPos.move(dir);
                            if (extendPos.isInBounds(WIDTH, HEIGHT) && 
                                grid[extendPos.y()][extendPos.x()] == null) {
                                grid[extendPos.y()][extendPos.x()] = new EmptyRoom();
                                initializeConnections(extendPos);
                                addConnection(newPos, extendPos);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void placeSpecialRooms(Random random) {
        var emptyRooms = getAllRoomPositions().stream()
            .filter(p -> grid[p.y()][p.x()] instanceof EmptyRoom)
            .filter(p -> !p.equals(startPosition))
            .toList();
        
        var availableRooms = new ArrayList<>(emptyRooms);
        Collections.shuffle(availableRooms, random);
        
        int index = 0;
        
        // Placer des salles d'ennemis (30-40% des salles)
        int enemyCount = Math.max(1, availableRooms.size() * 35 / 100);
        for (int i = 0; i < enemyCount && index < availableRooms.size(); i++) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = new EnemyRoom(new ArrayList<Enemy>(),false);
        }
        
        // Placer des trésors (15-20% des salles)
        int treasureCount = Math.max(1, availableRooms.size() * 17 / 100);
        for (int i = 0; i < treasureCount && index < availableRooms.size(); i++) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = TreasureRoom.create();
        }
        
        // Placer un marchand
        if (index < availableRooms.size()) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = new MerchantRoom();
        }
        
        // Placer un guérisseur
        if (index < availableRooms.size()) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = new HealerRoom();
        }
        
        // Placer des surprises (10% des salles restantes)
        int surpriseCount = Math.max(0, (availableRooms.size() - index) * 10 / 100);
        for (int i = 0; i < surpriseCount && index < availableRooms.size(); i++) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = new SurpriseRoom();
        }
        
        // Placer une grille avec trésor rare (optionnel)
        if (index < availableRooms.size() && random.nextDouble() < 0.5) {
            var pos = availableRooms.get(index++);
            grid[pos.y()][pos.x()] = new GateRoom(TreasureRoom.create());
        }
    }
    
    private void ensureConnectivity() {
        var allRooms = getAllRoomPositions();
        if (allRooms.isEmpty()) return;
        
        // BFS pour vérifier la connectivité
        var visited = new HashSet<Coordonate>();
        var queue = new LinkedList<Coordonate>();
        queue.add(startPosition);
        visited.add(startPosition);
        
        while (!queue.isEmpty()) {
            var current = queue.poll();
            for (var neighbor : getConnectedPositions(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        // Connecter les salles non visitées
        for (var pos : allRooms) {
            if (!visited.contains(pos)) {
                // Trouver la salle visitée la plus proche
                Coordonate nearest = null;
                int minDist = Integer.MAX_VALUE;
                
                for (var visitedPos : visited) {
                    int dist = pos.manhattanDistance(visitedPos);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = visitedPos;
                    }
                }
                
                if (nearest != null && pos.isAdjacentTo(nearest)) {
                    addConnection(pos, nearest);
                    visited.add(pos);
                }
            }
        }
    }
    
    private void initializeConnections(Coordonate pos) {
        connections.putIfAbsent(pos, new HashSet<>());
    }
    
    private void addConnection(Coordonate from, Coordonate to) {
        initializeConnections(from);
        initializeConnections(to);
        connections.get(from).add(to);
        connections.get(to).add(from);
    }
    
    // ================== MÉTHODES PUBLIQUES ==================
    
    /**
     * Récupère la salle à une position donnée
     */
    public Room getRoom(Coordonate pos) {
        Objects.requireNonNull(pos);
        if (!pos.isInBounds(WIDTH, HEIGHT)) {
            return null;
        }
        return grid[pos.y()][pos.x()];
    }
    
    /**
     * Récupère la salle à des coordonnées données
     */
    public Room getRoom(int x, int y) {
        return getRoom(new Coordonate(x, y));
    }
    
    /**
     * Récupère toutes les positions de salles existantes
     */
    public List<Coordonate> getAllRoomPositions() {
        var positions = new ArrayList<Coordonate>();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != null) {
                    positions.add(new Coordonate(x, y));
                }
            }
        }
        return positions;
    }
    
    /**
     * Récupère les positions connectées à une position
     */
    public Set<Coordonate> getConnectedPositions(Coordonate pos) {
        Objects.requireNonNull(pos);
        return connections.getOrDefault(pos, Set.of());
    }
    
    /**
     * Vérifie si deux positions sont connectées
     */
    public boolean areConnected(Coordonate from, Coordonate to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        return connections.getOrDefault(from, Set.of()).contains(to);
    }
    
    /**
     * Vérifie si le joueur peut se déplacer vers une position
     */
    public boolean canMoveTo(Coordonate to) {
        Objects.requireNonNull(to);
        
        // Vérifier si connecté à la position actuelle
        if (!areConnected(playerPosition, to)) {
            return false;
        }
        
        // Vérifier si la salle existe et est accessible
        var room = getRoom(to);
        return room != null && room.isAccessible();
    }
    
    
    /**
     * Résultat d'un déplacement
     */
    public enum MoveResult {
        SUCCESS,           // Déplacement réussi, pas d'interaction spéciale
        COMBAT_REQUIRED,   // Entré dans une salle avec ennemis non vaincus
        TREASURE_FOUND,    // Trésor à récupérer
        MERCHANT_FOUND,    // Marchand disponible
        HEALER_FOUND,      // Guérisseur disponible
        SURPRISE_FOUND,    // Événement surprise
        EXIT_FOUND,        // Sortie d'étage trouvée
        GATE_LOCKED,       // Grille fermée (besoin d'une clé)
        BLOCKED,           // Déplacement impossible
        INVALID            // Position non connectée ou hors limites
    }
    
    /**
     * Déplace le joueur vers une nouvelle position
     * @return le résultat du déplacement avec le type de salle
     */
    public MoveResult movePlayerWithResult(Coordonate to) {
      Objects.requireNonNull(to);
      
      // Vérifier si connecté
      if (!areConnected(playerPosition, to)) {
          return MoveResult.INVALID;
      }
      
      var room = getRoom(to);
      if (room == null) {
          return MoveResult.INVALID;
      }
      
      // Cas spécial : GateRoom fermée
      if (room instanceof GateRoom gate && !gate.isUnlocked()) {
          return MoveResult.GATE_LOCKED;
      }
      
      // Vérifier si accessible
      if (!room.isAccessible()) {
          return MoveResult.BLOCKED;
      }
      
      // Déplacer le joueur
      playerPosition = to;
      room.setVisited();
      
      // Retourner le résultat selon le type de salle
      return switch (room) {
          case EnemyRoom e -> e.isCleared() ? MoveResult.SUCCESS : MoveResult.COMBAT_REQUIRED;
          case TreasureRoom t -> t.isLooted() ? MoveResult.SUCCESS : MoveResult.TREASURE_FOUND;
          case MerchantRoom _ -> MoveResult.MERCHANT_FOUND;
          case HealerRoom _ -> MoveResult.HEALER_FOUND;
          case SurpriseRoom s -> s.isRevealed() ? MoveResult.SUCCESS : MoveResult.SURPRISE_FOUND;
          case ExitRoom _ -> MoveResult.EXIT_FOUND;
          case GateRoom _ -> MoveResult.SUCCESS; // Déjà vérifié qu'elle est ouverte
          default -> MoveResult.SUCCESS;
      };
  }
    
    /**
     * Déplace le joueur vers une nouvelle position
     * @return true si le déplacement a réussi
     */
    public boolean movePlayer(Coordonate to) {
    	 var result = movePlayerWithResult(to);
    	 return result != MoveResult.INVALID && result != MoveResult.BLOCKED && result != MoveResult.GATE_LOCKED;
    }
    
    
    /**
     * Prévisualise ce qui attend le joueur dans une direction
     * @return description de la salle sans y entrer
     */
    public String peekRoom(Direction direction) {
        Objects.requireNonNull(direction);
        var targetPos = playerPosition.move(direction);
        
        if (!areConnected(playerPosition, targetPos)) {
            return "Pas de passage dans cette direction.";
        }
        
        var room = getRoom(targetPos);
        if (room == null) {
            return "Mur solide.";
        }
        
        // Afficher un aperçu selon le type
        var sb = new StringBuilder();
        sb.append(direction).append(" → ").append(room).append(" ");
        
        return switch (room) {
            case EnemyRoom e -> {
                if (e.isCleared()) {
                    yield sb.append("Salle sécurisée (ennemis vaincus)").toString();
                }
                yield sb.append("⚠️ DANGER: ").append(e.enemies().size()).append(" ennemi(s)").toString();
            }
            case TreasureRoom t -> {
                if (t.isLooted()) {
                    yield sb.append("Coffre vide").toString();
                }
                yield sb.append("✨ Trésor !").toString();
            }
            case MerchantRoom _ -> sb.append("Marchand").toString();
            case HealerRoom h -> sb.append("Guérisseur (").append(h.healCost()).append(" or)").toString();
            case SurpriseRoom s -> {
                if (s.isRevealed()) {
                    yield sb.append(s.getEvent().getDescription()).toString();
                }
                yield sb.append("??? Surprise ???").toString();
            }
            case ExitRoom _ -> sb.append("🚪 SORTIE (irréversible!)").toString();
            case GateRoom g -> {
                if (g.isUnlocked()) {
                    yield sb.append("Grille ouverte → ").append(g.getHiddenRoom()).toString();
                }
                yield sb.append("🔒 Grille fermée (clé requise)").toString();
            }
            default -> sb.append("Couloir").toString();
        };
    }
    
    /**
     * Affiche toutes les options de déplacement avec aperçu
     */
    public String getMovementOptions() {
        var sb = new StringBuilder();
        sb.append("=== OPTIONS DE DÉPLACEMENT ===\n");
        
        for (var dir : Direction.values()) {
            var targetPos = playerPosition.move(dir);
            if (targetPos.isInBounds(WIDTH, HEIGHT) && areConnected(playerPosition, targetPos)) {
                sb.append("  ").append(peekRoom(dir)).append("\n");
            }
        }
        
        if (sb.toString().equals("=== OPTIONS DE DÉPLACEMENT ===\n")) {
            sb.append("  Aucun passage disponible !\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Déplace le joueur dans une direction avec résultat détaillé
     * @return le résultat du déplacement
     */
    public MoveResult movePlayerWithResult(Direction direction) {
        Objects.requireNonNull(direction);
        var newPos = playerPosition.move(direction);
        return movePlayerWithResult(newPos);
    }
    
    
    /**
     * Déplace le joueur dans une direction
     * @return true si le déplacement a réussi
     */
    public boolean movePlayer(Direction direction) {
        Objects.requireNonNull(direction);
        var newPos = playerPosition.move(direction);
        return movePlayer(newPos);
    }
    
    /**
     * @return les directions possibles depuis la position actuelle
     */
    public List<Direction> getAvailableDirections() {
        var directions = new ArrayList<Direction>();
        for (var dir : Direction.values()) {
            var newPos = playerPosition.move(dir);
            if (canMoveTo(newPos)) {
                directions.add(dir);
            }
        }
        return directions;
    }
    
    /**
     * @return les salles voisines accessibles
     */
    public Map<Direction, Room> getNeighborRooms() {
        var neighbors = new HashMap<Direction, Room>();
        for (var dir : Direction.values()) {
            var newPos = playerPosition.move(dir);
            if (newPos.isInBounds(WIDTH, HEIGHT) && areConnected(playerPosition, newPos)) {
                var room = getRoom(newPos);
                if (room != null) {
                    neighbors.put(dir, room);
                }
            }
        }
        return neighbors;
    }
    
    // ================== GETTERS ==================
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public Coordonate getPlayerPosition() {
        return playerPosition;
    }
    
    public Coordonate getStartPosition() {
        return startPosition;
    }
    
    public Room getCurrentRoom() {
        return getRoom(playerPosition);
    }
    
    // ================== AFFICHAGE ==================
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("══════════ ÉTAGE ").append(floorNumber).append(" ══════════\n");
        
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                var pos = new Coordonate(x, y);
                var room = grid[y][x];
                
                if (pos.equals(playerPosition)) {
                    sb.append("🧑");
                } else if (room == null) {
                    sb.append("██");
                } else {
                    sb.append(room);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        
        sb.append("Position: ").append(playerPosition);
        sb.append(" | Salle: ").append(getCurrentRoom());
        
        return sb.toString();
    }
    
    /**
     * Affiche la carte avec les connexions
     */
    public String toDetailedString() {
        var sb = new StringBuilder();
        sb.append(this.toString()).append("\n\n");
        sb.append("Connexions depuis ").append(playerPosition).append(":\n");
        
        for (var dir : Direction.values()) {
            var newPos = playerPosition.move(dir);
            if (areConnected(playerPosition, newPos)) {
                var room = getRoom(newPos);
                sb.append("  ").append(dir).append(" → ");
                if (room != null) {
                    sb.append(room);
                    sb.append(room.isAccessible() ? " (accessible)" : " (bloqué)");
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}