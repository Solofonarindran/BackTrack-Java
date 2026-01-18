package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.uge.model.Hero;

public class GatePromptView {
	public static void draw(Graphics2D graphics, int width, int height, Hero hero) {
		 graphics.setColor(new Color(0, 0, 0, 200));
     graphics.fillRect(0, 0, width, height);
     
     // Panel central
     int panelW = 400;
     int panelH = 250;
     int panelX = (width - panelW) / 2;
     int panelY = (height - panelH) / 2;
     
     graphics.setColor(new Color(50, 40, 30));
     graphics.fillRoundRect(panelX, panelY, panelW, panelH, 15, 15);
     
     graphics.setColor(new Color(150, 100, 50));
     graphics.setStroke(new BasicStroke(3));
     graphics.drawRoundRect(panelX, panelY, panelW, panelH, 15, 15);
     
     // Icône
     graphics.setFont(new Font("Arial", Font.PLAIN, 60));
     graphics.setColor(new Color(255, 215, 0));
     String icon = "🔒";
     int iconWidth = graphics.getFontMetrics().stringWidth(icon);
     graphics.drawString(icon, (width - iconWidth) / 2, panelY + 70);
     
     // Titre
     graphics.setColor(new Color(255, 215, 0));
     graphics.setFont(new Font("Arial", Font.BOLD, 24));
     String title = "GRILLE VERROUILLÉE";
     int titleWidth = graphics.getFontMetrics().stringWidth(title);
     graphics.drawString(title, (width - titleWidth) / 2, panelY + 110);
     
     // Message
     graphics.setColor(Color.WHITE);
     graphics.setFont(new Font("Arial", Font.PLAIN, 16));
     String msg = "Utiliser une clé pour ouvrir ?";
     int msgWidth = graphics.getFontMetrics().stringWidth(msg);
     graphics.drawString(msg, (width - msgWidth) / 2, panelY + 145);
     
     // Clés disponibles
     graphics.setColor(new Color(200, 200, 200));
     String keysText = "🔑 Clés : " + hero.getKeys();
     int keysWidth = graphics.getFontMetrics().stringWidth(keysText);
     graphics.drawString(keysText, (width - keysWidth) / 2, panelY + 175);
     
     // Boutons
     graphics.setFont(new Font("Arial", Font.BOLD, 16));
     
     // Bouton OUI
     int btnW = 120;
     int btnH = 40;
     int btnY = panelY + panelH - 60;
     
     graphics.setColor(new Color(50, 120, 50));
     graphics.fillRoundRect(panelX + 40, btnY, btnW, btnH, 8, 8);
     graphics.setColor(new Color(100, 200, 100));
     graphics.drawRoundRect(panelX + 40, btnY, btnW, btnH, 8, 8);
     graphics.setColor(Color.WHITE);
     graphics.drawString("[O] Ouvrir", panelX + 55, btnY + 26);
     
     // Bouton NON
     graphics.setColor(new Color(120, 50, 50));
     graphics.fillRoundRect(panelX + panelW - 160, btnY, btnW, btnH, 8, 8);
     graphics.setColor(new Color(200, 100, 100));
     graphics.drawRoundRect(panelX + panelW - 160, btnY, btnW, btnH, 8, 8);
     graphics.setColor(Color.WHITE);
     graphics.drawString("[N] Annuler", panelX + panelW - 150, btnY + 26);
	}
	
	public static void showNoKeyMessage(Graphics2D graphics, int width, int height) {
	  // Message en bas de l'écran
    var msgH = 60;
    var msgY = height - msgH - 20;
    
    graphics.setColor(new Color(80, 30, 30, 230));
    graphics.fillRoundRect(50, msgY, width - 100, msgH, 10, 10);
    
    graphics.setColor(new Color(200, 80, 80));
    graphics.setStroke(new BasicStroke(2));
    graphics.drawRoundRect(50, msgY, width - 100, msgH, 10, 10);
    
    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Arial", Font.BOLD, 20));
    var msg = "🔒Vous n'avez pas de clé !";
    var msgWidth = graphics.getFontMetrics().stringWidth(msg);
    graphics.drawString(msg, (width - msgWidth) / 2, msgY + 38);
	}
}
