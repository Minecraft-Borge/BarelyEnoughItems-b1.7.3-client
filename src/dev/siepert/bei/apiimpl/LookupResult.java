package dev.siepert.bei.apiimpl;

import dev.siepert.bei.api.IRecipeCategory;

import java.util.List;

public abstract class LookupResult {
	public final List<IRecipeCategory<?>> categories;
	public final List<List<RecipeContainer<?>>> recipes;

	public int categoryIndex = 0;
	public int recipePage = 0;

	public LookupResult(List<IRecipeCategory<?>> categories, List<List<RecipeContainer<?>>> recipes) {
		this.categories = categories;
		this.recipes = recipes;
	}

	public boolean hasResults() {
		return !this.categories.isEmpty();
	}

	public IRecipeCategory<?> currentCategory() {
		return this.categories.get(this.categoryIndex);
	}
	public List<RecipeContainer<?>> currentRecipes() {
		return this.recipes.get(this.categoryIndex);
	}
}
