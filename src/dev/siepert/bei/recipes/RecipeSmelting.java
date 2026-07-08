package dev.siepert.bei.recipes;

import net.minecraft.src.ItemStack;

public class RecipeSmelting {
	public final ItemStack input;
	public final ItemStack result;
	public final int recipeTime;

	public RecipeSmelting(ItemStack input, ItemStack stack, int recipeTime) {
		this.input = input;
		result = stack;
		this.recipeTime = recipeTime;
	}
}
