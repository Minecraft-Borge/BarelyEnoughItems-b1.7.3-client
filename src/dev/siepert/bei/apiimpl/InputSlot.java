package dev.siepert.bei.apiimpl;

import net.minecraft.src.ItemStack;
import net.minecraftborge.loader.Ingredient;

import java.util.List;

public final class InputSlot {
	public final int x, y;
	public final Ingredient input;
	public final int count;
	public final List<ItemStack> displayItems;

	public InputSlot(int x, int y, Ingredient input, int count) {
		this.x = x;
		this.y = y;
		this.input = input;
		this.count = count;
		this.displayItems = input.getDisplayItems();
	}
	public InputSlot(int x, int y, Ingredient input) {
		this(x, y, input, 1);
	}
}
