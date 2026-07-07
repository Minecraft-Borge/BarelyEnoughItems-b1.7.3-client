package dev.siepert.bei.api;

import net.minecraft.src.ItemStack;
import net.minecraftborge.loader.Ingredient;

import java.util.List;

public interface IIngredients {
	void addInput(int x, int y, Ingredient input);
	void addInput(int x, int y, Ingredient input, int count);
	void addCatalyst(int x, int y, Ingredient catalyst);
	void addCatalyst(int x, int y, Ingredient catalyst, int count);
	void addResult(int x, int y, ItemStack result);
	void addResults(int x, int y, List<ItemStack> results);
}
