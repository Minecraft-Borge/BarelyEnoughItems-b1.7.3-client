package dev.siepert.bei.api.reg;

import dev.siepert.bei.api.IRecipeCategory;

public interface ICategoryRegistration {
	void registerCategory(String categoryUID, IRecipeCategory<?> category);
}
