package dev.siepert.bei;

import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.api.RecipesPlugin;
import dev.siepert.bei.api.IRecipesPlugin;
import dev.siepert.bei.apiimpl.CategoryRegistration;
import dev.siepert.bei.apiimpl.RecipeContainer;
import dev.siepert.bei.apiimpl.RecipeRegistration;
import net.minecraftborge.MinecraftBorge;
import net.minecraftborge.loader.asm.ASMDataTable;
import net.minecraftborge.loader.asm.ClassASMData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BEIPluginManager {
	private static final DecimalFormat MS_FORMAT = new DecimalFormat("#.###");

	public static List<IRecipesPlugin> plugins = Collections.emptyList();
	public static Map<String, IRecipeCategory<?>> categories = Collections.emptyMap();
	public static Map<IRecipeCategory<?>, List<?>> recipes = Collections.emptyMap();
	public static List<RecipeContainer<?>> indexedRecipes = Collections.emptyList();
	public static List<RecipeContainer<?>> indexedRecipesWithResults = Collections.emptyList();
	public static List<RecipeContainer<?>> indexedRecipesWithInputs = Collections.emptyList();

	public static void findPlugins(ASMDataTable asm) {
		long start = System.nanoTime();
		List<ClassASMData> classes = asm.getAnnotated(RecipesPlugin.class.getName());
		ArrayList<IRecipesPlugin> detected = new ArrayList<>(classes.size());
		for (ClassASMData data : classes) {
			String pluginID = String.valueOf(data.annotations.get(RecipesPlugin.class.getName()).get("value"));
			System.out.println("Found BEI plugin '" + pluginID + "' at " + data.className);
			try {
				Class<?> clazz = MinecraftBorge.getClassLoader().loadClass(data.className);
				if (IRecipesPlugin.class.isAssignableFrom(clazz)) {
					IRecipesPlugin instance = (IRecipesPlugin) clazz.newInstance();
					if (!pluginID.equals(instance.getPluginUID())) {
						throw new IllegalStateException("Mismatching plugin UID: " + pluginID + " / " + instance.getPluginUID());
					}
					detected.add(instance);
				} else throw new ClassCastException(clazz + " has not implemented IRecipesPlugin but is annotated with @RecipesPlugin");
			} catch (Exception e) {
				System.err.println("Exception loading plugin " + pluginID + ": " + e);
			}
		}
		plugins = detected.isEmpty() ? Collections.emptyList()
				: detected.size() == 1 ? Collections.singletonList(detected.get(0))
				  : Collections.unmodifiableList(detected);
		System.out.println("Detecting BEI plugins took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}

	public static void doRegistrations() {
		long start = System.nanoTime();

		registerCategories();
		registerRecipes();

		System.out.println("Registering recipes took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}

	private static void registerCategories() {
		CategoryRegistration registration = new CategoryRegistration();
		for (IRecipesPlugin plugin : plugins) {
			plugin.registerCategories(registration);
		}
		Map<String, IRecipeCategory<?>> registry = registration.getRegistry();
		switch (registry.size()) {
			case 0:
				categories = Collections.emptyMap();
				break;
			case 1:
				categories = Collections.singletonMap((String) registry.keySet().toArray()[0], (IRecipeCategory<?>) registry.values().toArray()[0]);
				break;
			default:
				categories = Collections.unmodifiableMap(registry);
				break;
		}
	}
	private static void registerRecipes() {
		RecipeRegistration registration = new RecipeRegistration();
		for (IRecipesPlugin plugin : plugins) {
			plugin.registerRecipes(registration);
		}
		Map<IRecipeCategory<?>, List<?>> registry = registration.getRegistry();
		switch (registry.size()) {
			case 0:
				recipes = Collections.emptyMap();
				break;
			case 1:
				recipes = Collections.singletonMap((IRecipeCategory<?>) registry.keySet().toArray()[0], (List<?>) registry.values().toArray()[0]);
				break;
			default:
				recipes = Collections.unmodifiableMap(registry);
				break;
		}
	}

	public static void indexRecipes() {
		long start = System.nanoTime();

		ArrayList<RecipeContainer<?>> containers = new ArrayList<>();
		for (IRecipeCategory<?> category : recipes.keySet()) {
			indexCategory(containers, category);
		}
		ArrayList<RecipeContainer<?>> withResults = new ArrayList<>(containers.size());
		ArrayList<RecipeContainer<?>> withInputs = new ArrayList<>(containers.size());

		for (RecipeContainer<?> container : containers) {
			if (container.results.length > 0) withResults.add(container);
			if (container.inputs.length > 0) withInputs.add(container);
		}

		containers.trimToSize();
		withResults.trimToSize();
		withInputs.trimToSize();

		indexedRecipes = Collections.unmodifiableList(containers);
		indexedRecipesWithResults = Collections.unmodifiableList(withResults);
		indexedRecipesWithInputs = Collections.unmodifiableList(withInputs);

		System.out.println(containers.size() + " recipe containers (with results: " + withResults.size() + ", with inputs: " + withInputs.size() + ")");
		System.out.println("Indexing recipes took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}

	@SuppressWarnings("unchecked")
	private static <T> void indexCategory(List<RecipeContainer<?>> containers, IRecipeCategory<T> category) {
		List<T> list = (List<T>)recipes.get(category);
		for (T recipe : list) {
			containers.add(RecipeContainer.create(category, recipe));
		}
	}
}
