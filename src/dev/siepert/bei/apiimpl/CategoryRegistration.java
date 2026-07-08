package dev.siepert.bei.apiimpl;

import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.api.reg.ICategoryRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CategoryRegistration implements ICategoryRegistration {
	private final Map<String, IRecipeCategory<?>> registry = new HashMap<>();

	public CategoryRegistration() {}

	@Override
	public void registerCategory(String categoryUID, IRecipeCategory<?> category) {
		if (this.registry.containsKey(Objects.requireNonNull(categoryUID, "categoryUID"))) {
			throw new IllegalStateException("Duplicate category UID: " + categoryUID);
		}
		if (this.registry.containsValue(Objects.requireNonNull(category, "category"))) {
			throw new IllegalStateException("Duplicate category: " + category);
		}
		this.registry.put(categoryUID, category);
	}

	public Map<String, IRecipeCategory<?>> getRegistry() {
		return this.registry;
	}
}
