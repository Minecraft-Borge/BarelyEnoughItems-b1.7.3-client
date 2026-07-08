package dev.siepert.bei.apiimpl;

import dev.siepert.bei.api.IIngredients;
import dev.siepert.bei.api.IRecipeCategory;
import net.minecraft.src.ItemStack;
import net.minecraftborge.loader.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RecipeContainer<T> {
	public final IRecipeCategory<T> category;
	public final T recipe;
	public final Ingredient[] inputs;
	public final ItemStack[][] results;
	public final InputSlot[] inputSlots;
	public final ResultSlot[] resultSlots;

	private RecipeContainer(IRecipeCategory<T> category, T recipe, Ingredient[] inputs, ItemStack[][] results, InputSlot[] inputSlots, ResultSlot[] resultSlots) {
		this.category = category;
		this.recipe = recipe;
		this.inputs = inputs;
		this.results = results;
		this.inputSlots = inputSlots;
		this.resultSlots = resultSlots;
	}

	@SuppressWarnings("unchecked")
	public <C> RecipeContainer<C> cast() {
		return (RecipeContainer<C>) this;
	}
	@SuppressWarnings("unchecked")
	public <C> RecipeContainer<C> cast(Class<C> ignored) {
		return (RecipeContainer<C>) this;
	}

	public static <T> RecipeContainer<T> create(IRecipeCategory<T> category, T recipe) {
		Objects.requireNonNull(category, "category");
		Objects.requireNonNull(recipe, "recipe");

		IngredientHelper.instance.prepare();
		category.getItems(IngredientHelper.instance, recipe);
		InputSlot[] inputSlots = IngredientHelper.instance.inputs.toArray(new InputSlot[0]);
		ResultSlot[] resultSlots = IngredientHelper.instance.results.toArray(new ResultSlot[0]);
		Ingredient[] inputs = new Ingredient[inputSlots.length];
		ItemStack[][] results = new ItemStack[resultSlots.length][];

		for (int i = 0; i < inputSlots.length; i++) {
			inputs[i] = inputSlots[i].input;
		}
		for (int i = 0; i < resultSlots.length; i++) {
			results[i] = resultSlots[i].results;
		}

		return new RecipeContainer<>(category, recipe,
				inputs, results,
				inputSlots, resultSlots
		);
	}

	private static final class IngredientHelper implements IIngredients {
		private static final IngredientHelper instance = new IngredientHelper();
		private IngredientHelper() {}

		public final List<InputSlot> inputs = new ArrayList<>();
		public final List<ResultSlot> results = new ArrayList<>();

		public void prepare() {
			this.inputs.clear();
			this.results.clear();
		}

		@Override
		public void addInput(int x, int y, Ingredient input) {
			this.inputs.add(new InputSlot(x, y, input));
		}
		@Override
		public void addInput(int x, int y, Ingredient input, int count) {
			this.inputs.add(new InputSlot(x, y, input, count));
		}

		@Override
		public void addCatalyst(int x, int y, Ingredient catalyst) {
			this.addInput(x, y, catalyst);
		}
		@Override
		public void addCatalyst(int x, int y, Ingredient catalyst, int count) {
			this.addInput(x, y, catalyst, count);
		}

		@Override
		public void addResult(int x, int y, ItemStack result) {
			this.results.add(new ResultSlot(x, y, new ItemStack[]{result}));
		}
		@Override
		public void addResults(int x, int y, List<ItemStack> results) {
			this.results.add(new ResultSlot(x, y, results.toArray(new ItemStack[0])));
		}
	}
}
