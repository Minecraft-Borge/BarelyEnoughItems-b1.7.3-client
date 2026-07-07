package dev.siepert.bei.api.reg;

import dev.siepert.bei.api.IRecipeCategory;

import java.util.Collection;

public interface IRecipeRegistration {
	<T> IRecipeCategory<T> getCategoryByUID(String categoryUID);
	<T> void addRecipes(IRecipeCategory<T> category, Collection<T> recipes);
}
