package dev.siepert.bei.recipes.category;

import dev.siepert.bei.api.IIngredients;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.recipes.RecipeSmelting;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftborge.loader.Ingredient;

public class RecipeCategorySmelting implements IRecipeCategory<RecipeSmelting> {
	private final ItemStack icon = new ItemStack(Block.stoneOvenIdle);
	private final ItemStack[] machines = {this.icon};

	public RecipeCategorySmelting() {

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
		return "/gui/furnace.png";
	}
	@Override
	public int getWidth() {
		return 84;
	}
	@Override
	public int getHeight() {
		return 56;
	}
	@Override
	public String getTitle() {
		return "Smelting";
	}

	@Override
	public void getItems(IIngredients ingredients, RecipeSmelting recipe) {
		ingredients.addInput(2, 2, Ingredient.of(recipe.input));
		ingredients.addResult(62, 20, recipe.result);
	}

	@Override
	public void drawBackdrop(Minecraft mc, Tessellator tes, int x, int y, RecipeSmelting recipe, float pt) {
		this.drawTexturedModalRect(tes, x, y, 54, 15, this.getWidth(), this.getHeight());
		this.drawTexturedModalRect(tes, x+2, y+21, 176, 0, 14, 14);
		int progress = Minecraft.getTicksRan() % recipe.recipeTime;
		this.drawTexturedModalRect(tes, x+25, y+19, 176, 14, progress * 24 / recipe.recipeTime, 17);
	}

	@Override
	public void drawExtras(Minecraft mc, RenderEngine textures, int x, int y, double mouseX, double mouseY, RecipeSmelting recipe, float pt) {

	}

	@Override
	public void drawTexts(Minecraft mc, FontRenderer font, int x, int y, double mouseX, double mouseY, RecipeSmelting recipe, float pt) {
		String seconds = (recipe.recipeTime * 0.05F) + "s";
		font.drawString(seconds, x + this.getWidth() - font.getStringWidth(seconds) - 2, y + this.getHeight() - 10, 0x000000);
	}

	public void drawTexturedModalRect(Tessellator tes, int x, int y, int srcX, int srcY, int w, int h) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		tes.addVertexWithUV(x, y + h, 0, (float)(srcX) * var7, (float)(srcY + h) * var8);
		tes.addVertexWithUV(x + w, y + h, 0, (float)(srcX + w) * var7, (float)(srcY + h) * var8);
		tes.addVertexWithUV(x + w, y, 0, (float)(srcX + w) * var7, (float)(srcY) * var8);
		tes.addVertexWithUV(x, y, 0, (float)(srcX) * var7, (float)(srcY) * var8);
	}
}
