package dev.siepert.bei.apiimpl;

import dev.siepert.bei.BEIPluginManager;
import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.api.IRecipeCategory;
import net.minecraft.src.ItemStack;
import net.minecraftborge.loader.Ingredient;
import net.minecraftborge.loader.tag.ItemTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LookupResultUses extends LookupResult {
	public static final LookupResultUses EMPTY = new LookupResultUses(Collections.emptyList(), Collections.emptyList());

	private LookupResultUses(List<IRecipeCategory<?>> categories, List<List<RecipeContainer<?>>> recipes) {
		super(categories, recipes);
	}

	public static LookupResultUses lookup(ItemStack item) {
		if (ItemTags.isItemEmpty(item)) return EMPTY;

		int machineLookup = BarelyEnoughItems.pack(item);
		List<RecipeContainer<?>> indexed = BEIPluginManager.indexedRecipesWithInputs;
		List<RecipeContainer<?>> matching = new ArrayList<>(BEIPluginManager.indexedRecipesWithMachine.getOrDefault(machineLookup, Collections.emptyList()));
		loop:
		for (RecipeContainer<?> container : indexed) {
			for (Ingredient in : container.inputs) {
				if (in.test(item)) {
					matching.add(container);
					continue loop;
				}
			}
		}
		return matching.isEmpty() ? EMPTY : create(matching);
	}
	public static LookupResultUses create(List<RecipeContainer<?>> matching) {
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

		return new LookupResultUses(categoryIndex, recipeIndex);
	}
}
