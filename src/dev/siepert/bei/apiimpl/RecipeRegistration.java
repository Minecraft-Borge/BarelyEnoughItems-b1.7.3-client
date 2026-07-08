package dev.siepert.bei.apiimpl;

import dev.siepert.bei.BEIPluginManager;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.api.reg.IRecipeRegistration;

import java.util.*;

@SuppressWarnings("unchecked")
public class RecipeRegistration implements IRecipeRegistration {
	private final Map<IRecipeCategory<?>, List<?>> registry = new HashMap<>();

	public RecipeRegistration() {}

	@Override
	public <T> IRecipeCategory<T> getCategoryByUID(String categoryUID) {
		Objects.requireNonNull(categoryUID, "categoryUID");
		return (IRecipeCategory<T>) BEIPluginManager.categories.get(categoryUID);
	}

	@Override
	public <T> void addRecipes(IRecipeCategory<T> category, Collection<T> recipes) {
		Objects.requireNonNull(category, "category");
		Objects.requireNonNull(recipes, "recipes");
		((List<T>)this.registry.computeIfAbsent(category, $ -> new ArrayList<>())).addAll(recipes);
	}

	public Map<IRecipeCategory<?>, List<?>> getRegistry() {
		return this.registry;
	}
}
