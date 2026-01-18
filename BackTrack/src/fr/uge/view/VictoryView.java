package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.uge.model.Hero;

public class VictoryView {
	public static void showGameOver(Graphics2D graphics , Hero hero, int width, int height) {
		 // Fond sombre
    graphics.setColor(new Color(30, 10, 10));
    graphics.fillRect(0, 0, width, height);
    
    // Bordure rouge
    graphics.setColor(new Color(150, 30, 30));
    graphics.setStroke(new BasicStroke(8));
    graphics.drawRect(10, 10, width - 20, height - 20);
    
    // Titre
    graphics.setColor(new Color(200, 50, 50));
    graphics.setFont(new Font("Arial", Font.BOLD, 56));
    String title = "💀 GAME OVER 💀";
    int titleWidth = graphics.getFontMetrics().stringWidth(title);
    graphics.drawString(title, (width - titleWidth) / 2, 180);
    
    // Message
    graphics.setColor(new Color(180, 180, 180));
    graphics.setFont(new Font("Arial", Font.PLAIN, 24));
    String message = "Le héros a succombé...";
    int msgWidth = graphics.getFontMetrics().stringWidth(message);
    graphics.drawString(message, (width - msgWidth) / 2, 260);
    
    // Stats
    graphics.setColor(new Color(150, 150, 150));
    graphics.setFont(new Font("Arial", Font.PLAIN, 18));
    
    int statsY = 340;
    String[] stats = {
        "Niveau atteint : " + hero.getLevel(),
        "Or accumulé : " + hero.getGold()
    };
    
    for (String stat : stats) {
        int statWidth = graphics.getFontMetrics().stringWidth(stat);
        graphics.drawString(stat, (width - statWidth) / 2, statsY);
        statsY += 30;
    }
    
    // Instructions
    graphics.setColor(new Color(120, 120, 120));
    graphics.setFont(new Font("Arial", Font.PLAIN, 16));
    String instruction = "Appuyez sur une touche pour quitter...";
    int instrWidth = graphics.getFontMetrics().stringWidth(instruction);
    graphics.drawString(instruction, (width - instrWidth) / 2, height - 50);
	}
	
	public static void showVictory(Graphics2D graphics,int width,int height, int f,
			Hero hero) {
	  // Fond avec dégradé
    graphics.setColor(new Color(20, 20, 50));
    graphics.fillRect(0, 0, width, height);
    
    // Étoiles animées
    graphics.setColor(new Color(255, 255, 200));
    for (int i = 0; i < 50; i++) {
        int starX = (i * 73 + f) % width;
        int starY = (i * 47) % height;
        int starSize = 2 + (i % 3);
        graphics.fillOval(starX, starY, starSize, starSize);
    }
    
    // Bordure dorée
    graphics.setColor(new Color(255, 215, 0));
    graphics.setStroke(new BasicStroke(10));
    graphics.drawRect(15, 15, width - 30, height - 30);
    
    // Titre
    graphics.setFont(new Font("Arial", Font.BOLD, 56));
    String title = "🎉 VICTOIRE ! 🎉";
    int titleWidth = graphics.getFontMetrics().stringWidth(title);
    graphics.drawString(title, (width - titleWidth) / 2, 150);
    
    // Sous-titre
    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Arial", Font.BOLD, 28));
    String subtitle = "Vous avez conquis le donjon !";
    int subWidth = graphics.getFontMetrics().stringWidth(subtitle);
    graphics.drawString(subtitle, (width - subWidth) / 2, 210);
    
    // Stats finales
    graphics.setColor(new Color(200, 200, 255));
    graphics.setFont(new Font("Arial", Font.PLAIN, 22));
    
    int statsY = 300;
    String[] stats = {
        "Niveau atteint : " + hero.getLevel(),
        "Or accumulé : " + hero.getGold(),
        "PV finaux : " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint()
    };
    
    for (String stat : stats) {
        int statWidth = graphics.getFontMetrics().stringWidth(stat);
        graphics.drawString(stat, (width - statWidth) / 2, statsY);
        statsY += 35;
    }
    
    // Message de fin
    graphics.setColor(new Color(255, 215, 0));
    graphics.setFont(new Font("Arial", Font.ITALIC, 20));
    String endMsg = "Merci d'avoir joué !";
    int endWidth = graphics.getFontMetrics().stringWidth(endMsg);
    graphics.drawString(endMsg, (width - endWidth) / 2, height - 120);
    
    // Instructions
    graphics.setColor(new Color(180, 180, 180));
    graphics.setFont(new Font("Arial", Font.PLAIN, 16));
    String instruction = "Appuyez sur une touche pour quitter...";
    int instrWidth = graphics.getFontMetrics().stringWidth(instruction);
    graphics.drawString(instruction, (width - instrWidth) / 2, height - 50);
	}
}
