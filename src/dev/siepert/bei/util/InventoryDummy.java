package dev.siepert.bei.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class InventoryDummy implements IInventory {
	private static final ItemStack[] LIST = new ItemStack[256];
	public static final InventoryDummy INSTANCE = new InventoryDummy();

	private InventoryDummy() {}

	public void repopulate() {
		for (int i = 0; i < 256; i++) {
			Item item = Item.itemsList[4096 + i];
			if (item != null) {
				LIST[i] = new ItemStack(item);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return LIST.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return LIST[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

	}

	@Override
	public String getInvName() {
		return "Dummy";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
