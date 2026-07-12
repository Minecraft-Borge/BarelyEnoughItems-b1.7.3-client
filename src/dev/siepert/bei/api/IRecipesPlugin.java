package dev.siepert.bei.api;

import dev.siepert.bei.api.reg.ICategoryRegistration;
import dev.siepert.bei.api.reg.IRecipeRegistration;
import dev.siepert.bei.api.reg.IScreenRegistration;

public interface IRecipesPlugin {
	/**
	 * Must be the same as the value in {@link RecipesPlugin}.
	 * @return Unique plugin ID
	 */
	String getPluginUID();

	default void registerCategories(ICategoryRegistration registration) {}
	default void registerRecipes(IRecipeRegistration registration) {}
	default void registerScreenHandlers(IScreenRegistration registration) {}
}
