package dev.siepert.bei.gui;

import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.apiimpl.LookupResult;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;

public class ContainerRecipes extends Container {
	public final LookupResult lookup;
	public final int slotEndIndex;

	public int maxPage = 0;

	public ContainerRecipes(LookupResult lookup) {
		this.lookup = lookup;

		IInventory inv = BarelyEnoughItems.ITEMS_CACHE;
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 11; y++) {
				this.addSlot(new SlotBEI(inv, x + y*5, 177 + 18*x, 2 + 18*y));
			}
		}

		this.slotEndIndex = this.slots.size();

		this.setCategory();
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
		int h = category.getHeight();
		int pages = (200-6-20) / h;
		this.maxPage = (entries - 1) / pages;
	}
}
