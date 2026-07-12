package dev.siepert.bei.apiimpl;

import dev.siepert.bei.BEIPluginManager;
import dev.siepert.bei.api.IRecipeCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LookupResultEverything extends LookupResult {
	public static final LookupResultEverything EMPTY = new LookupResultEverything(Collections.emptyList(), Collections.emptyList());

	private LookupResultEverything(List<IRecipeCategory<?>> categories, List<List<RecipeContainer<?>>> recipes) {
		super(categories, recipes);
	}

	public static LookupResultEverything lookup() {
		if (BEIPluginManager.indexedRecipes.isEmpty()) return EMPTY;
		return create();
	}

	public static LookupResultEverything create() {
		List<IRecipeCategory<?>> categories = new ArrayList<>(BEIPluginManager.indexedRecipesWithCategory.keySet());
		List<List<RecipeContainer<?>>> recipes = new ArrayList<>(categories.size());
		for (IRecipeCategory<?> category : categories) {
			List<RecipeContainer<?>> list = BEIPluginManager.indexedRecipesWithCategory.get(category);
			recipes.add(list);
		}
		return new LookupResultEverything(categories, recipes);
	}
}
