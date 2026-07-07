package dev.siepert.bei.util;

import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class StackFilters {
	public static boolean any(ItemStack ignored) {
		return true;
	}
	public static boolean none(ItemStack ignored) {
		return false;
	}

	public static Predicate<ItemStack> named(String name, boolean includeTooltip) {
		final StringTranslate translate = StringTranslate.getInstance();
		final List<String> tooltip = new ArrayList<>();
		return (stack) -> {
			if (stack == null) return false;
			if (translate.translateNamedKey(stack.getItemName()).toLowerCase().contains(name)) return true;
			if (!includeTooltip) return false;
			stack.getTooltips(tooltip, false);
			return tooltip.stream().anyMatch(s -> s.contains(name));
		};
	}
}
