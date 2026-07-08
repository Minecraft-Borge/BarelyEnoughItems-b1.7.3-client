package dev.siepert.bei.recipes.category;

import dev.siepert.bei.api.IIngredients;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.recipes.RecipeFurnaceFuel;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftborge.loader.Ingredient;

public class RecipeCategoryFurnaceFuel implements IRecipeCategory<RecipeFurnaceFuel> {
	private final ItemStack icon = new ItemStack(Block.stoneOvenActive);
	private final ItemStack[] machines = {new ItemStack(Block.stoneOvenIdle)};

	public RecipeCategoryFurnaceFuel() {}

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
		return 56;
	}
	@Override
	public int getHeight() {
		return 20;
	}
	@Override
	public String getTitle() {
		return "Furnace Fuel";
	}

	@Override
	public void getItems(IIngredients ingredients, RecipeFurnaceFuel recipe) {
		ingredients.addInput(2, 2, Ingredient.of(recipe.item));
	}

	@Override
	public void drawBackdrop(Minecraft mc, Tessellator tes, int x, int y, RecipeFurnaceFuel recipe, float pt) {
		this.drawTexturedModalRect(tes, x, y, 54, 51, this.getWidth(), this.getHeight());
	}

	@Override
	public void drawExtras(Minecraft mc, RenderEngine textures, int x, int y, double mouseX, double mouseY, RecipeFurnaceFuel recipe, float pt) {

	}

	@Override
	public void drawTexts(Minecraft mc, FontRenderer font, int x, int y, double mouseX, double mouseY, RecipeFurnaceFuel recipe, float pt) {
		int ticks = recipe.ticks;
		String time = ticks + "t / " + (ticks / 20) + "s";
		String items = (ticks / 200.0) + " items";
		font.drawString(time, x+20, y+2, 0x000000);
		font.drawString(items, x+20, y+11, 0x000000);
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
