package dev.siepert.bei.api.reg;

import net.minecraft.src.GuiScreen;

public interface IScreenRegistration {
	void addCraftingCategoryUIDs(String... categoryUIDs);
	void addScreenHandler(Class<? extends GuiScreen> clazz, int x, int y, int w, int h, String... categoryUIDs);
	void addItemsListCompatible(Class<? extends GuiScreen> clazz);
}
