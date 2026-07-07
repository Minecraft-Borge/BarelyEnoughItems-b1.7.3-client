package dev.siepert.bei.api;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;

public interface IRecipeEntry<T> {
	void getItems(IIngredients ingredients, T recipe);

	void drawBackdrop(Minecraft mc, Tessellator tes, int x, int y, T recipe);
	void drawExtras(Minecraft mc, RenderEngine textures, int x, int y, T recipe);
	void drawTexts(Minecraft mc, FontRenderer font, int x, int y, T recipe);
}
