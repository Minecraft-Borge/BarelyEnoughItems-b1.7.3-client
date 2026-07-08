package dev.siepert.bei.recipes.category;

import dev.siepert.bei.api.IIngredients;
import dev.siepert.bei.api.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftborge.loader.Ingredient;
import net.minecraftborge.loader.RecipeShapedFix;

import java.lang.reflect.Field;

public class RecipeCategoryCraftingShaped implements IRecipeCategory<IRecipe> {
	private final ItemStack icon = new ItemStack(Block.workbench);
	private final ItemStack[] machines = {this.icon};

	public RecipeCategoryCraftingShaped() {

	}

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
		return "Shaped Crafting";
	}

	@Override
	public void getItems(IIngredients ingredients, IRecipe recipe) {
		Ingredient[][] in = getIngredients(recipe);
		for (int x = 0; x < in.length; x++) {
			for (int y = 0; y < in[0].length; y++) {
				if (in[x][y] != null) ingredients.addInput(30 + 18*x, 17 + 18*y, in[x][y]);
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

	private static final Field badWidth, badHeight, badInput;
	private static final Field goodWidth, goodHeight, goodInput;
	private static Ingredient[][] getIngredients(IRecipe recipe) {
		try {
			if (recipe instanceof ShapedRecipes) {
				int w = (int) badWidth.get(recipe);
				int h = (int) badHeight.get(recipe);
				ItemStack[] items = (ItemStack[]) badInput.get(recipe);
				Ingredient[][] in = new Ingredient[w][h];
				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						if (items[x+y*w] != null) in[x][y] = Ingredient.of(items[x+y*w]);
					}
				}
				return in;
			}
			if (recipe instanceof RecipeShapedFix) {
				int w = (int) goodWidth.get(recipe);
				int h = (int) goodHeight.get(recipe);
				Ingredient[] items = (Ingredient[]) goodInput.get(recipe);
				Ingredient[][] in = new Ingredient[w][h];
				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						in[x][y] = items[x+y*w];
					}
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
			badWidth = ShapedRecipes.class.getDeclaredField("b");
			badWidth.setAccessible(true);
			badHeight = ShapedRecipes.class.getDeclaredField("c");
			badHeight.setAccessible(true);
			badInput = ShapedRecipes.class.getDeclaredField("d");
			badInput.setAccessible(true);

			goodWidth = RecipeShapedFix.class.getDeclaredField("width");
			goodWidth.setAccessible(true);
			goodHeight = RecipeShapedFix.class.getDeclaredField("height");
			goodHeight.setAccessible(true);
			goodInput = RecipeShapedFix.class.getDeclaredField("inputs");
			goodInput.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException("Field problems", e);
		}
	}
}
