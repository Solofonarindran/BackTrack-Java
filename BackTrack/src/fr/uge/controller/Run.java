package fr.uge.controller;


import com.github.forax.zen.ApplicationContext;

import fr.uge.data.BackPack;
import fr.uge.model.Enemy;
import fr.uge.model.Hero;
import fr.uge.view.Index;

public class Run {
	public static void run(ApplicationContext context, Hero hero, Enemy enemy,BackPack bag) {
		context.renderFrame(graphics -> Index.page().body(context, graphics, hero, enemy,bag));
	}
	

}
