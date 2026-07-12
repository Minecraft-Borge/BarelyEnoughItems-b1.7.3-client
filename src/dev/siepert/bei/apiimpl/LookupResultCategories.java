package dev.siepert.bei.apiimpl;

import dev.siepert.bei.BEIPluginManager;
import dev.siepert.bei.api.IRecipeCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LookupResultCategories extends LookupResult {
	public static final LookupResultCategories EMPTY = new LookupResultCategories(Collections.emptyList(), Collections.emptyList());

	private LookupResultCategories(List<IRecipeCategory<?>> categories, List<List<RecipeContainer<?>>> recipes) {
		super(categories, recipes);
	}

	public static LookupResultCategories lookup(String[] categoryUIDs) {
		if (categoryUIDs == null || categoryUIDs.length == 0) return EMPTY;
		List<IRecipeCategory<?>> categories = new ArrayList<>();
		for (String uid : categoryUIDs) {
			IRecipeCategory<?> category = BEIPluginManager.categories.get(uid);
			if (category != null && !BEIPluginManager.indexedRecipesWithCategory.getOrDefault(category, Collections.emptyList()).isEmpty()) {
				categories.add(category);
			}
		}
		if (categories.isEmpty()) return EMPTY;
		else return create(categories);
	}

	public static LookupResultCategories create(List<IRecipeCategory<?>> categories) {
		List<List<RecipeContainer<?>>> recipes = new ArrayList<>(categories.size());
		for (IRecipeCategory<?> category : categories) {
			List<RecipeContainer<?>> list = BEIPluginManager.indexedRecipesWithCategory.get(category);
			recipes.add(list);
		}
		return new LookupResultCategories(categories, recipes);
	}
}
