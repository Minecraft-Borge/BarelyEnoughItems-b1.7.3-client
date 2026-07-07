package dev.siepert.bei;

import dev.siepert.bei.gui.GuiInventoryBEI;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiInventory;
import net.minecraftborge.loader.event.EventBusSubscriber;
import net.minecraftborge.loader.event.EventHandler;
import net.minecraftborge.loader.event.IModLifecycleListener;
import net.minecraftborge.loader.event.gui.ChangeGuiEvent;
import net.minecraftborge.loader.event.lifecycle.ModInitializationEvent;
import net.minecraftborge.loader.event.lifecycle.ModPostInitializationEvent;

import java.io.File;
import java.io.IOException;

@EventBusSubscriber(BarelyEnoughItems.MODID)
public class BarelyEnoughItems implements IModLifecycleListener {
	public static final String MODID = "bei";
	public static String path(String path) {
		return MODID + "/" + path;
	}

	public static final ItemsCache ITEMS_CACHE = new ItemsCache();

	@Override
	public void modInit(ModInitializationEvent event) {
		event.registerLanguage();

		try {
			File configFolder = new File(Minecraft.getMinecraftDir(), "config");
			configFolder.mkdirs();
			BEIConfig.load(new File(configFolder, "BarelyEnoughItems.cfg"));
		} catch (IOException e) {
			System.err.println("[BEI] Could not load configuration: " + e);
		}
	}

	@Override
	public void modPostInit(ModPostInitializationEvent event) {
		ITEMS_CACHE.reindex();
	}

	@EventHandler
	public static void changeGUI(ChangeGuiEvent event) {
		if (event.getNewScreen() instanceof GuiInventory) {
			event.setNewScreen(new GuiInventoryBEI(Minecraft.getTheMinecraft().thePlayer));
		}
	}
}
