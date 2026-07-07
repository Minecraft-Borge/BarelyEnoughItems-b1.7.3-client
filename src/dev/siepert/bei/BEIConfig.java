package dev.siepert.bei;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraftborge.loader.GameRegistries;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BEIConfig {
	private static boolean instantSearchResults = false;
	private static boolean hideBlocksWithoutStats = true;
	private static List<Integer> hiddenItems = Collections.emptyList();
	private static boolean enableCheatMode = false;
	private static boolean fancySoundFX = true;

	public static boolean instantSearchResults() {
		return instantSearchResults;
	}
	public static boolean hideBlocksWithoutStats() {
		return hideBlocksWithoutStats;
	}
	public static List<Integer> hiddenItems() {
		return hiddenItems;
	}
	public static boolean enableCheatMode() {
		return enableCheatMode;
	}
	public static boolean fancySoundFX() {
		return fancySoundFX;
	}

	public static void loadDefaults() {
		instantSearchResults = false;
		hideBlocksWithoutStats = true;
		List<Integer> list = new ArrayList<>(2);
		list.add(Block.pistonMoving.blockID);
		list.add(Block.lockedChest.blockID);
		hiddenItems = Collections.unmodifiableList(list);
		enableCheatMode = false;
		fancySoundFX = true;
	}
	public static void load(File config) throws IOException {
		if (config != null && config.isFile()) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(config.toPath())))) {
				reader.lines().forEach(line -> {
					String[] kv = line.split(":", 2);
					if (kv.length != 2) return;
					switch (kv[0]) {
						case "instantSearchResults":
							instantSearchResults = "true".equalsIgnoreCase(kv[1]);
							break;
						case "hideBlocksWithoutStats":
							hideBlocksWithoutStats = "true".equalsIgnoreCase(kv[1]);
							break;
						case "hiddenItems":
							String[] itemIDs = kv[1].split(";");
							if (itemIDs.length == 0) {
								hiddenItems = Collections.emptyList();
							} else {
								List<Integer> list = new ArrayList<>(itemIDs.length);
								for (String itemID : itemIDs) {
									Item item = GameRegistries.ITEMS.getValue(itemID);
									if (item != null) {
										list.add(item.shiftedIndex);
									} else System.err.println("[BEI Config] Unknown item ID: " + itemID);
								}
								hiddenItems = Collections.unmodifiableList(list);
							}
							break;
						case "enableCheatMode":
							enableCheatMode = "true".equalsIgnoreCase(kv[1]);
							break;
						case "fancySoundFX":
							fancySoundFX = "true".equalsIgnoreCase(kv[1]);
							break;
					}
				});
			}
		}
		save(config);
	}
	private static void save(File config) throws IOException {
		if (config == null) return;
		if (config.exists() && !config.delete()) throw new IOException("Could not delete old configuration file!");
		if (!config.createNewFile()) throw new IOException("Could not create configuration file!");
		StringBuilder builder = new StringBuilder();
		builder.append("instantSearchResults:").append(instantSearchResults).append("\n");
		builder.append("hideBlocksWithoutStats:").append(hideBlocksWithoutStats).append("\n");
		builder.append("hiddenItems:");
		for (int itemID : hiddenItems) {
			Item item = Item.itemsList[itemID];
			if (item != null) builder.append(GameRegistries.ITEMS.getKey(item)).append(";");
		}
		builder.append("\n");
		builder.append("enableCheatMode:").append(enableCheatMode).append("\n");
		builder.append("fancySoundFX:").append(fancySoundFX).append("\n");
		try (FileWriter writer = new FileWriter(config)) {
			writer.write(builder.toString());
		}
	}
}
