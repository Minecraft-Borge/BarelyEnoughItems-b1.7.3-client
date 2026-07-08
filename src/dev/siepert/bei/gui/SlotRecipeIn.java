package dev.siepert.bei.gui;

import dev.siepert.bei.apiimpl.InputSlot;
import dev.siepert.bei.apiimpl.RecipeContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotRecipeIn extends Slot {
	private final InputSlot slot;
	private final boolean invalid;
	public SlotRecipeIn(RecipeContainer<?> container, int id, int x, int y) {
		super(null, id, 0, 0);
		this.slot = container.inputSlots[id];
		this.invalid = this.slot.displayItems.isEmpty();
		this.xDisplayPosition = this.slot.x + x;
		this.yDisplayPosition = this.slot.y + y;
	}

	@Override
	public ItemStack getStack() {
		if (this.invalid) return null;
		return this.slot.displayItems.get((Math.toIntExact((System.currentTimeMillis() / 1000) % this.slot.displayItems.size())));
	}

	@Override
	public void onPickupFromSlot(ItemStack stack) {
		System.err.println("DO NOT PICK UP FROM SLOT.");
	}
	@Override
	public void putStack(ItemStack stack) {

	}
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
	@Override
	public void onSlotChanged() {

	}
	@Override
	public int getSlotStackLimit() {
		return 64;
	}
	@Override
	public ItemStack decrStackSize(int count) {
		return null;
	}
}
