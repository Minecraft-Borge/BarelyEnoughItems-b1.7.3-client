package dev.siepert.bei.gui;

import dev.siepert.bei.apiimpl.RecipeContainer;
import dev.siepert.bei.apiimpl.ResultSlot;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotRecipeOut extends Slot {
	private final ResultSlot slot;
	private final boolean invalid;
	public SlotRecipeOut(RecipeContainer<?> container, int id, int x, int y) {
		super(null, id, 0, 0);
		this.slot = container.resultSlots[id];
		this.invalid = this.slot.results.length == 0;
		this.xDisplayPosition = this.slot.x + x;
		this.yDisplayPosition = this.slot.y + y;
	}

	@Override
	public ItemStack getStack() {
		if (this.invalid) return null;
		return this.slot.results[Math.toIntExact((System.currentTimeMillis() / 1000) % this.slot.results.length)];
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
