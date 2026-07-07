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
import net.minecraftborge.loader.event.lifecycle.ModPreInitializationEvent;
import net.minecraftborge.loader.event.register.ExtractSoundsEvent;

import java.io.File;
import java.io.IOException;

@EventBusSubscriber(BarelyEnoughItems.MODID)
public class BarelyEnoughItems implements IModLifecycleListener {
	public static final String MODID = "bei";
	public static String path(String path) {
		return MODID + "/" + path;
	}

	public static final ItemsCache ITEMS_CACHE = new ItemsCache();

	private static final String[] SOUND_FX = {"Back", "Click", "MoveCursor", "Scroll"};
	public static void fancyFX(Minecraft mc, int id) {
		if (!BEIConfig.fancySoundFX()) {
			if (id == 3) {
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			}
			return;
		}
		mc.sndManager.playSoundFX(MODID + SOUND_FX[id], 1.0F, 1.0F);
	}

	@Override
	public void modPreInit(ModPreInitializationEvent event) {
		BEIConfig.loadDefaults();
	}

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
		ITEMS_CACHE.setStackOrder(BEIConfig.itemOrder());
		ITEMS_CACHE.reindex();
	}

	@EventHandler
	public static void registerSounds(ExtractSoundsEvent event) {
		event.extract("assets/sounds/bei/");
	}

	@EventHandler
	public static void changeGUI(ChangeGuiEvent event) {
		if (event.getNewScreen() instanceof GuiInventory) {
			event.setNewScreen(new GuiInventoryBEI(Minecraft.getTheMinecraft().thePlayer));
		}
	}
}
