package dev.siepert.bei.util;

import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;

import java.util.Comparator;
import java.util.Locale;

public enum StackSorters implements Comparator<ItemStack> {
	REGISTRY_ORDER() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			int id1 = o1.itemID;
			int id2 = o2.itemID;
			if (id1 < id2) return -1;
			if (id1 > id2) return 1;
			int meta1 = o1.getItemDamage();
			int meta2 = o2.getItemDamage();
			if (meta1 == meta2) return o1.itemNBT == null ? -1 : o2.itemNBT == null ? 1 : 0;
			return Integer.compare(meta1, meta2);
		}
	},
	REGISTRY_ORDER_REVERSED() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return -REGISTRY_ORDER.compare(o1, o2);
		}
	},
	ALPHABETICAL() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			StringTranslate translate = StringTranslate.getInstance();
			String name1 = translate.translateNamedKey(o1.getItemName());
			String name2 = translate.translateNamedKey(o2.getItemName());
			return name1.compareTo(name2);
		}
	},
	ALPHABETICAL_REVERSED() {
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			return -ALPHABETICAL.compare(o1, o2);
		}
	};

	public static StackSorters get(String name) {
		if (name == null || name.isEmpty()) return REGISTRY_ORDER;
		switch (name.toLowerCase(Locale.ENGLISH)) {
			case "registry_order_reversed":
				return REGISTRY_ORDER_REVERSED;
			case "alphabetical":
			case "a-z":
				return ALPHABETICAL;
			case "alphabetical_reversed":
			case "z-a":
				return ALPHABETICAL_REVERSED;
			default:
				return REGISTRY_ORDER;
		}
	}
}
