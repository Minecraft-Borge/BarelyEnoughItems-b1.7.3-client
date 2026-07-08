package dev.siepert.bei.gui;

import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.apiimpl.InputSlot;
import dev.siepert.bei.apiimpl.LookupResult;
import dev.siepert.bei.apiimpl.RecipeContainer;
import dev.siepert.bei.apiimpl.ResultSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

import java.util.List;

public class ContainerRecipes extends Container {
	public final LookupResult lookup;
	public final int slotEndIndex;

	public int pageSize = 0;
	public int maxPage = 0;

	private final GuiScreen parent;

	public ContainerRecipes(LookupResult lookup, GuiScreen parent) {
		this.lookup = lookup;
		this.parent = parent;

		IInventory inv = BarelyEnoughItems.ITEMS_CACHE;
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 11; y++) {
				this.addSlot(new SlotBEI(inv, x + y*5, 177 + 18*x, 2 + 18*y));
			}
		}

		this.slotEndIndex = this.slots.size();

		this.setCategory();
		this.setRecipes();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	public void clearRecipeSlots() {
		while (this.slots.size() > this.slotEndIndex) {
			this.slots.remove(this.slotEndIndex);
			this.remoteItems.remove(this.slotEndIndex);
		}
	}

	public void setCategory() {
		IRecipeCategory<?> category = this.lookup.categories.get(this.lookup.categoryIndex);
		this.lookup.recipePage = 0;
		int entries = this.lookup.recipes.get(this.lookup.categoryIndex).size();
		int h = category.getHeight() + 1;
		this.pageSize = (200-6-20) / h;
		this.maxPage = (entries - 1) / this.pageSize;
	}
	public void setRecipes() {
		this.clearRecipeSlots();
		int width = this.lookup.currentCategory().getWidth();
		int height = this.lookup.currentCategory().getHeight() + 1;
		List<RecipeContainer<?>> recipes = this.lookup.currentRecipes();
		int x = 176 / 2 - width / 2;
		for (int i = 0; i < this.pageSize; i++) {
			int index = this.lookup.recipePage * this.pageSize + i;
			if (index < recipes.size()) {
				int y = 3 + 20 + i * height;
				RecipeContainer<?> recipe = recipes.get(index);
				InputSlot[] slotsIn = recipe.inputSlots;
				ResultSlot[] slotsOut = recipe.resultSlots;

				for (int id = 0; id < slotsIn.length; id++) {
					this.addSlot(new SlotRecipeIn(recipe, id, x, y));
				}
				for (int id = 0; id < slotsOut.length; id++) {
					this.addSlot(new SlotRecipeOut(recipe, id, x, y));
				}
			}
		}
	}

	@Override
	public ItemStack clickGuiSlot(int slotId, int type, boolean shift, EntityPlayer player) {
		if (slotId < 0) return null;
		if (slotId < this.slots.size()) {
			Slot slot = this.slots.get(slotId);
			if (slot != null && slot.getStack() != null) {
				Minecraft mc = Minecraft.getTheMinecraft();
				if (type == 0) {
					GuiRecipes.getRecipesFor(mc, this.parent, slot.getStack());
				}
				if (type == 1) {
					GuiRecipes.getUsesFor(mc, this.parent, slot.getStack());
				}
			}
		}
		return null;
	}
}
