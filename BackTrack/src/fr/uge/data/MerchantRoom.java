package fr.uge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.model.Item;

public final class MerchantRoom implements Room{
	private static final int PRICE_RARITY_RARE = 15;
	private static final int PRICE_RARITY_COMMON = 10;
	private static final int PRICE_RARITY_UNCOMMON = 5;
	
	private final List<Item> stocks;
	private final boolean isVisited;
	private final int money;
	
	public MerchantRoom () {
		this.stocks = new ArrayList<Item>();
		this.isVisited = false;
		this.money = 50;
	}
	
	public MerchantRoom(List<Item> stocks, boolean isVisited, int money) {
		this.stocks = stocks;
		this.isVisited = isVisited;
		this.money = money;
	}
	
	@Override
	public boolean isAccessible() {
		return true;
	}
	
  @Override
  public String getDescription() {
      return "Un marchand ambulant vous propose ses services. (" + stocks.size() + " articles)";
  }
  
  @Override
  public boolean isVisited() {
  	return isVisited;
  }
	@Override
	public MerchantRoom setVisited() {
		return new MerchantRoom(stocks, true,money);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "🛒";
	}
	
	/**
   * Achète un item du marchand
   * @return l'item acheté ou null si pas en stock
   */
  public Item buyItem(Item item) {
      Objects.requireNonNull(item);
      if (stocks.remove(item)) {
          return item;
      }
      return null;
  }
  
  /**
   * Vend un item au marchand
   */
  public void sellItem(Item item) {
      Objects.requireNonNull(item);
      stocks.add(item);
  }
	
}
