package dev.siepert.bei.gui;

import dev.siepert.bei.BarelyEnoughItems;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;

public class ContainerRecipes extends Container {
	public final int slotEndIndex;

	public ContainerRecipes() {
		IInventory inv = BarelyEnoughItems.ITEMS_CACHE;
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 11; y++) {
				this.addSlot(new SlotBEI(inv, x + y*5, 177 + 18*x, 2 + 18*y));
			}
		}

		this.slotEndIndex = this.slots.size();
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
}
