package dev.siepert.bei.recipes.category;

import dev.siepert.bei.api.IIngredients;
import dev.siepert.bei.api.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftborge.loader.Ingredient;
import net.minecraftborge.loader.RecipeShapelessFix;

import java.lang.reflect.Field;
import java.util.List;

public class RecipeCategoryCraftingShapeless implements IRecipeCategory<IRecipe> {
	private final ItemStack icon = new ItemStack(Block.workbench);
	private final ItemStack[] machines = {this.icon};

	public RecipeCategoryCraftingShapeless() {}

	@Override
	public ItemStack[] getCategoryMachines() {
		return this.machines;
	}
	@Override
	public ItemStack getCategoryIcon() {
		return this.icon;
	}

	@Override
	public String getBackdropTexture() {
		return "/gui/crafting.png";
	}

	@Override
	public int getWidth() {
		return 118;
	}

	@Override
	public int getHeight() {
		return 56;
	}

	@Override
	public String getTitle() {
		return "Shapeless Crafting";
	}

	@Override
	public void getItems(IIngredients ingredients, IRecipe recipe) {
		Ingredient[] in = getIngredients(recipe);
		loop:
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int idx = x + 3*y;
				if (in.length > idx) ingredients.addInput(2 + 18*x, 2 + 18*y, in[idx]);
				else break loop;
			}
		}
		ingredients.addResult(96, 20, recipe.getRecipeOutput());
	}

	@Override
	public void drawBackdrop(Minecraft mc, Tessellator tes, int x, int y, IRecipe recipe, float pt) {
		this.drawTexturedModalRect(tes, x, y, 28, 15, this.getWidth(), this.getHeight());
	}

	@Override
	public void drawExtras(Minecraft mc, RenderEngine textures, int x, int y, double mouseX, double mouseY, IRecipe recipe, float pt) {

	}

	@Override
	public void drawTexts(Minecraft mc, FontRenderer font, int x, int y, double mouseX, double mouseY, IRecipe recipe, float pt) {

	}

	public void drawTexturedModalRect(Tessellator tes, int x, int y, int srcX, int srcY, int w, int h) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		tes.addVertexWithUV(x, y + h, 0, (float)(srcX) * var7, (float)(srcY + h) * var8);
		tes.addVertexWithUV(x + w, y + h, 0, (float)(srcX + w) * var7, (float)(srcY + h) * var8);
		tes.addVertexWithUV(x + w, y, 0, (float)(srcX + w) * var7, (float)(srcY) * var8);
		tes.addVertexWithUV(x, y, 0, (float)(srcX) * var7, (float)(srcY) * var8);
	}

	private static final Field badInput;
	private static final Field goodInput;
	private static Ingredient[] getIngredients(IRecipe recipe) {
		try {
			if (recipe instanceof ShapelessRecipes) {
				// noinspection unchecked
				List<ItemStack> items = (List<ItemStack>) badInput.get(recipe);
				Ingredient[] in = new Ingredient[items.size()];
				for (int i = 0; i < items.size(); i++) {
					in[i] = Ingredient.of(items.get(i));
				}
				return in;
			}
			if (recipe instanceof RecipeShapelessFix) {
				// noinspection unchecked
				List<Ingredient> items = (List<Ingredient>) goodInput.get(recipe);
				Ingredient[] in = new Ingredient[items.size()];
				for (int i = 0; i < items.size(); i++) {
					in[i] = items.get(i);
				}
				return in;
			}
			throw new IllegalStateException("Invalid recipe: " + recipe.getClass().getName());
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("gng", e);
		}
	}
	static {
		try {
			badInput = ShapelessRecipes.class.getDeclaredField("b");
			badInput.setAccessible(true);

			goodInput = RecipeShapelessFix.class.getDeclaredField("inputs");
			goodInput.setAccessible(true);
		} catch (Exception e) {
			throw new IllegalStateException("Field problems", e);
		}
	}
}
