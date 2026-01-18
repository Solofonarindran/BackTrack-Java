package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.uge.model.Hero;

public class FloorTransitionView {
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Couleurs
    private static final Color BACKGROUND = new Color(20, 20, 35);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    private static final Color PANEL_BG = new Color(40, 40, 55);
    
    private int animationFrame = 0;
    
    public FloorTransitionView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    public void draw(Graphics2D g, int currentFloor, int nextFloor, Hero hero) {
        animationFrame++;
        
        // Fond
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Étoiles animées
        drawStars(g);
        
        // Panel central
        drawCentralPanel(g, currentFloor, nextFloor, hero);
        
        // Instructions
        drawInstructions(g);
    }
    
    private void drawStars(Graphics2D g) {
        g.setColor(new Color(255, 255, 200, 150));
        for (var i = 0; i < 30; i++) {
            var starX = (i * 73 + animationFrame / 2) % screenWidth;
            var starY = (i * 47) % screenHeight;
            var starSize = 1 + (i % 3);
            g.fillOval(starX, starY, starSize, starSize);
        }
    }
    
    private void drawCentralPanel(Graphics2D g, int currentFloor, int nextFloor, Hero hero) {
        var panelW = 500;
        var panelH = 400;
        var panelX = (screenWidth - panelW) / 2;
        var panelY = (screenHeight - panelH) / 2;
        
        // Fond du panel
        g.setColor(PANEL_BG);
        g.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        
        // Bordure dorée
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        
        var centerX = screenWidth / 2;
        var textY = panelY + 60;
        
        // Titre "ÉTAGE TERMINÉ"
        g.setColor(GOLD);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "ÉTAGE " + currentFloor + " TERMINÉ !";
        var titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, centerX - titleWidth / 2, textY);
        
        // Flèche animée
        textY += 50;
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        var arrowOffset = (int) (5 * Math.sin(animationFrame * 0.1));
        g.drawString("⬇", centerX - 15, textY + arrowOffset);
        
        // Prochain étage
        textY += 60;
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        var nextText = "ÉTAGE " + nextFloor;
        var nextWidth = g.getFontMetrics().stringWidth(nextText);
        g.drawString(nextText, centerX - nextWidth / 2, textY);
        
        // Description de l'étage
        textY += 35;
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        var desc = getFloorDescription(nextFloor);
        var descWidth = g.getFontMetrics().stringWidth(desc);
        g.drawString(desc, centerX - descWidth / 2, textY);
        
        // Ligne de séparation
        textY += 30;
        g.setColor(new Color(100, 100, 120));
        g.setStroke(new BasicStroke(1));
        g.drawLine(panelX + 50, textY, panelX + panelW - 50, textY);
        
        // Stats du héros
        textY += 40;
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("VOTRE HÉROS", centerX - 50, textY);
        
        textY += 30;
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(TEXT_GRAY);
        
        // Niveau
        g.drawString("Niveau : " + hero.getLevel(), panelX + 80, textY);
        
        // PV
        textY += 25;
        g.setColor(new Color(255, 100, 100));
        g.drawString("❤️ PV : " + hero.getHealthPoint() + " / " + hero.getMaxHealthPoint(), panelX + 80, textY);
        
        // Or
        textY += 25;
        g.setColor(GOLD);
        g.drawString("💰 Or : " + hero.getGold(), panelX + 80, textY);
        
        // Énergie
        textY += 25;
        g.setColor(new Color(100, 200, 255));
        g.drawString("⚡ Énergie : " + hero.getMaxEnergy(), panelX + 80, textY);
        
        // Avertissement
        textY += 45;
        g.setColor(new Color(255, 200, 100));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        var warning = "⚠️ Les ennemis seront plus forts !";
        var warnWidth = g.getFontMetrics().stringWidth(warning);
        g.drawString(warning, centerX - warnWidth / 2, textY);
    }
    
    private String getFloorDescription(int floor) {
        return switch (floor) {
            case 1 -> "Les cavernes sombres";
            case 2 -> "Les donjons hantés";
            case 3 -> "Le repaire du démon";
            default -> "Les profondeurs inconnues";
        };
    }
    
    private void drawInstructions(Graphics2D g) {
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        
        // Effet de pulsation
        var alpha = (int) (180 + 75 * Math.sin(animationFrame * 0.08));
        g.setColor(new Color(200, 200, 200, Math.min(255, alpha)));
        
        var instruction = "Appuyez sur ESPACE pour continuer...";
        var instrWidth = g.getFontMetrics().stringWidth(instruction);
        g.drawString(instruction, (screenWidth - instrWidth) / 2, screenHeight - 60);
    }
}