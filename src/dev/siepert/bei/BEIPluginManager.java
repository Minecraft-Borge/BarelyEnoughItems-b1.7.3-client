package dev.siepert.bei;

import dev.siepert.bei.api.RecipesPlugin;
import dev.siepert.bei.api.IRecipesPlugin;
import net.minecraftborge.MinecraftBorge;
import net.minecraftborge.loader.asm.ASMDataTable;
import net.minecraftborge.loader.asm.ClassASMData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BEIPluginManager {
	private static final DecimalFormat MS_FORMAT = new DecimalFormat("#.###");

	public static List<IRecipesPlugin> plugins = Collections.emptyList();

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

		for (IRecipesPlugin plugin : plugins) {

		}

		System.out.println("Registering recipes took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}

	public static void indexRecipes() {
		long start = System.nanoTime();

		System.out.println("Indexing recipes took " + MS_FORMAT.format((System.nanoTime() - start) * 0.001 * 0.001) + "ms");
	}
}
