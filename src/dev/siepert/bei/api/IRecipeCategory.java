package dev.siepert.bei.api;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;

public interface IRecipeCategory<T> {
	ItemStack[] getCategoryMachines();
	ItemStack getCategoryIcon();

	String getBackdropTexture();
	int getWidth();
	int getHeight();

	String getTitle();

	void getItems(IIngredients ingredients, T recipe);

	void drawBackdrop(Minecraft mc, Tessellator tes, int x, int y, T recipe, float pt); // batched !!!
	void drawExtras(Minecraft mc, RenderEngine textures, int x, int y, double mouseX, double mouseY, T recipe, float pt);
	void drawTexts(Minecraft mc, FontRenderer font, int x, int y, double mouseX, double mouseY, T recipe, float pt);
}
