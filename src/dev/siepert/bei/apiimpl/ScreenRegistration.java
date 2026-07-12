package dev.siepert.bei.apiimpl;

import dev.siepert.bei.api.reg.IScreenRegistration;
import dev.siepert.bei.gui.ScreenHandler;
import net.minecraft.src.GuiScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ScreenRegistration implements IScreenRegistration {
	private String[] craftingCategoryUIDs = {"craftingShaped", "craftingShapeless"};
	private final HashMap<Class<? extends GuiScreen>, List<ScreenHandler>> screenHandlers = new HashMap<>();
	private final HashSet<Class<? extends GuiScreen>> compatibleItemsList = new HashSet<>();

	public ScreenRegistration() {}

	@Override
	public void addCraftingCategoryUIDs(String... categoryUIDs) {
		if (categoryUIDs.length == 0) return;
		String[] copy = new String[this.craftingCategoryUIDs.length + categoryUIDs.length];
		System.arraycopy(this.craftingCategoryUIDs, 0, copy, 0, this.craftingCategoryUIDs.length);
		System.arraycopy(categoryUIDs, 0, copy, this.craftingCategoryUIDs.length, categoryUIDs.length);
		this.craftingCategoryUIDs = copy;
	}

	@Override
	public void addScreenHandler(Class<? extends GuiScreen> clazz, int x, int y, int w, int h, String... categoryUIDs) {
		ScreenHandler handler = new ScreenHandler(x, y, w, h, categoryUIDs);
		this.screenHandlers.computeIfAbsent(clazz, $ -> new ArrayList<>()).add(handler);
	}

	@Override
	public void addItemsListCompatible(Class<? extends GuiScreen> clazz) {
		this.compatibleItemsList.add(clazz);
	}

	public String[] getCraftingCategoryUIDs() {
		return this.craftingCategoryUIDs;
	}
	public HashMap<Class<? extends GuiScreen>, List<ScreenHandler>> getScreenHandlers() {
		return this.screenHandlers;
	}
	public HashSet<Class<? extends GuiScreen>> getCompatibleItemsList() {
		return this.compatibleItemsList;
	}
}
