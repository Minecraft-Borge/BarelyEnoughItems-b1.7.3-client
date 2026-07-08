package dev.siepert.bei;

import dev.siepert.bei.util.StackSorters;
import net.minecraft.src.*;
import net.minecraftborge.loader.BorgeMath;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ItemsCache implements IInventory {
	private static final DecimalFormat MS_FORMAT = new DecimalFormat("#.###");

	private final ArrayList<ItemStack> list;
	private final ArrayList<ItemStack> filtered;

	public ItemsCache() {
		this.list = new ArrayList<>();
		this.filtered = new ArrayList<>();
	}

	public void reindex() {
		long start = System.nanoTime();
		this.list.clear();
		this.filtered.clear();
		for (int i = 0; i < Item.ID_SIZE; i++) {
			if (Item.itemsList[i] != null) {
				Item item = Item.itemsList[i];
				if (BEIConfig.hideBlocksWithoutStats() && item.shiftedIndex < 4096 && (!Block.blocksList[item.shiftedIndex].getEnableStats())) continue;
				if (BEIConfig.hiddenItems().contains(item.shiftedIndex)) continue;
				item.getSubItems(this.list);
			}
		}
		this.filtered.addAll(this.list);

		this.list.trimToSize();
		this.filtered.trimToSize();
		System.out.println("Indexing items took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}

	public String googleSearch = "";
	private int page = 0;
	private int pageSize = 0;
	private int maxPage = 0;
	private Comparator<ItemStack> order = StackSorters.REGISTRY_ORDER;

	public void setPageData(int pageSize) {
		this.page = 0;
		this.pageSize = pageSize;
		this.maxPage = (this.filtered.size()-1) / this.pageSize;
	}
	public boolean pageUp() {
		if (this.page < this.maxPage) {
			this.page++;
			return true;
		} else return false;
	}
	public boolean pageDown() {
		if (this.page > 0) {
			this.page--;
			return true;
		} else return false;
	}
	public void setStackOrder(Comparator<ItemStack> order) {
		this.order = order;
	}

	public int getPage() {
		return this.page;
	}
	public void setPage(int page) {
		this.page = Math.max(0, Math.min(page, this.maxPage - 1));
	}

	public void filter(Predicate<ItemStack> filter) {
		this.filtered.clear();
		this.list.stream().filter(filter).sorted(this.order).forEach(this.filtered::add);
	}

	@Override
	public int getSizeInventory() {
		return this.pageSize;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		int id = this.page * this.pageSize + slot;
		return id < this.filtered.size() ? this.filtered.get(id) : null;
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
		StringTranslate translate = StringTranslate.getInstance();
		String title = translate.translateKey("bei.itemsList");
		if (this.filtered.isEmpty()) return title + " (" + translate.translateKey("bei.noResults") + ")";
		return title + " (" + (this.page + 1) + "/" + (this.maxPage + 1) + ")";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void onInventoryChanged() {

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public Iterator<ItemStack> getItemsListIterator() {
		return this.list.iterator();
	}
}
