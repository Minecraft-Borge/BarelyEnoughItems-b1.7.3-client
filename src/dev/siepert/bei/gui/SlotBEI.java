package dev.siepert.bei.gui;

import net.minecraft.src.*;

public class SlotBEI extends Slot {
	public SlotBEI(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public void putStack(ItemStack stack) {

	}
}
