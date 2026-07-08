package dev.siepert.bei.apiimpl;

import net.minecraft.src.ItemStack;

public final class ResultSlot {
	public final int x, y;
	public final ItemStack[] results;

	public ResultSlot(int x, int y, ItemStack[] results) {
		this.x = x;
		this.y = y;
		this.results = results;
	}
}
