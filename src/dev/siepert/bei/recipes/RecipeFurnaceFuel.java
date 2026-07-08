package dev.siepert.bei.recipes;

import net.minecraft.src.ItemStack;

public class RecipeFurnaceFuel {
	public final ItemStack item;
	public final int ticks;

	public RecipeFurnaceFuel(ItemStack item, int ticks) {
		this.item = item;
		this.ticks = ticks;
	}
}
