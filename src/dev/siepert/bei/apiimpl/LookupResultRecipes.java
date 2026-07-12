package dev.siepert.bei.apiimpl;

import dev.siepert.bei.BEIPluginManager;
import dev.siepert.bei.api.IRecipeCategory;
import net.minecraft.src.ItemStack;
import net.minecraftborge.loader.tag.ItemTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LookupResultRecipes extends LookupResult {
	public static final LookupResultRecipes EMPTY = new LookupResultRecipes(Collections.emptyList(), Collections.emptyList());

	private LookupResultRecipes(List<IRecipeCategory<?>> categories, List<List<RecipeContainer<?>>> recipes) {
		super(categories, recipes);
	}

	public static LookupResultRecipes lookup(ItemStack item) {
		if (ItemTags.isItemEmpty(item)) return EMPTY;

		List<RecipeContainer<?>> indexed = BEIPluginManager.indexedRecipesWithResults;
		List<RecipeContainer<?>> matching = new ArrayList<>();
		loop:
		for (RecipeContainer<?> container : indexed) {
			for (ItemStack[] array : container.results) {
				for (ItemStack stack : array) {
					if (ItemTags.matches(item, stack)) {
						matching.add(container);
						continue loop;
					}
				}
			}
		}
		return matching.isEmpty() ? EMPTY : create(matching);
	}
	public static LookupResultRecipes create(List<RecipeContainer<?>> matching) {
		Objects.requireNonNull(matching, "matching");

		List<IRecipeCategory<?>> categoryIndex = new ArrayList<>();
		List<List<RecipeContainer<?>>> recipeIndex = new ArrayList<>();

		for (RecipeContainer<?> container : matching) {
			int index = categoryIndex.indexOf(container.category);
			if (index == -1) {
				index = categoryIndex.size();
				categoryIndex.add(container.category);
				recipeIndex.add(new ArrayList<>());
			}
			recipeIndex.get(index).add(container);
		}

		return new LookupResultRecipes(categoryIndex, recipeIndex);
	}
}
