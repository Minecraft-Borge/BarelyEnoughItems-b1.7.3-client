package dev.siepert.bei;

import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.api.IRecipesPlugin;
import dev.siepert.bei.api.RecipesPlugin;
import dev.siepert.bei.api.reg.ICategoryRegistration;
import dev.siepert.bei.api.reg.IRecipeRegistration;
import dev.siepert.bei.gui.GuiInventoryBEI;
import dev.siepert.bei.recipes.RecipeFurnaceFuel;
import dev.siepert.bei.recipes.RecipeSmelting;
import dev.siepert.bei.recipes.category.RecipeCategoryCraftingShaped;
import dev.siepert.bei.recipes.category.RecipeCategoryCraftingShapeless;
import dev.siepert.bei.recipes.category.RecipeCategoryFurnaceFuel;
import dev.siepert.bei.recipes.category.RecipeCategorySmelting;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraftborge.loader.*;
import net.minecraftborge.loader.event.EventBusSubscriber;
import net.minecraftborge.loader.event.EventHandler;
import net.minecraftborge.loader.event.EventPriority;
import net.minecraftborge.loader.event.IModLifecycleListener;
import net.minecraftborge.loader.event.gui.ChangeGuiEvent;
import net.minecraftborge.loader.event.lifecycle.ModInitializationEvent;
import net.minecraftborge.loader.event.lifecycle.ModPostInitializationEvent;
import net.minecraftborge.loader.event.lifecycle.ModPreInitializationEvent;
import net.minecraftborge.loader.event.misc.ItemTooltipEvent;
import net.minecraftborge.loader.event.register.ExtractSoundsEvent;
import net.minecraftborge.loader.event.world.ChangeWorldEvent;
import net.minecraftborge.loader.tag.ItemTags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RecipesPlugin(BarelyEnoughItems.MODID)
@EventBusSubscriber(BarelyEnoughItems.MODID)
public class BarelyEnoughItems implements IModLifecycleListener, IRecipesPlugin {
	public static final String MODID = "bei";

	// Utensil methods

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

	public static ItemStack unpack(int packed) {
		int itemID = packed & 0xFFFF;
		if (Item.itemsList[itemID] == null) return null;
		int meta = (packed >> 16) & 0xFFFF;
		return new ItemStack(itemID, 1, meta == 0xFFFF ? -1 : meta);
	}

	// IModLifecycleListener

	@Override
	public void modPreInit(ModPreInitializationEvent event) {
		BEIConfig.loadDefaults();

		BEIPluginManager.findPlugins(event.getASMData());
	}

	@Override
	public void modInit(ModInitializationEvent event) {
		event.registerLanguage();

		try {
			File configFolder = new File(Minecraft.getMinecraftDir(), "config");
			configFolder.mkdirs();
			BEIConfig.load(new File(configFolder, "BarelyEnoughItems.cfg"));
		} catch (IOException e) {
			System.err.println("Could not load BEI configuration: " + e);
		}
	}

	@Override
	public void modPostInit(ModPostInitializationEvent event) {
		ITEMS_CACHE.setStackOrder(BEIConfig.itemOrder());
		ITEMS_CACHE.reindex();

		BEIPluginManager.doRegistrations();
		BEIPluginManager.indexRecipes();
	}

	// Event handlers

	@EventHandler
	public static void registerSounds(ExtractSoundsEvent event) {
		event.extract("assets/sounds/bei/");
	}

	@EventHandler(EventPriority.HIGH)
	public static void injectCustomInventoryGUI(ChangeGuiEvent event) {
		if (event.getNewScreen() instanceof GuiInventory) {
			event.setNewScreen(new GuiInventoryBEI(Minecraft.getTheMinecraft().thePlayer));
		}
	}

	@EventHandler
	public static void reindexItemsCache(ChangeWorldEvent event) {
		ITEMS_CACHE.reindex();
	}

	@EventHandler(EventPriority.LOWEST)
	public static void displaySourceMod(ItemTooltipEvent event) {
		ItemStack stack = event.getStack();
		if (BEIConfig.displaySourceMod() && !ItemTags.isItemEmpty(stack)) {
			String modid = Namespace.extractModId(GameRegistries.ITEMS.getKey(stack.getItem()));
			String name = modid != null ? ModList.get().getModContainer(ModList.get().getModIndex(modid)).name : "Minecraft";
			event.getTooltip().add("§9" + name);
		}
	}

	// IRecipesPlugin

	@Override
	public String getPluginUID() {
		return MODID;
	}

	@Override
	public void registerCategories(ICategoryRegistration registration) {
		registration.registerCategory("craftingShaped", new RecipeCategoryCraftingShaped());
		registration.registerCategory("craftingShapeless", new RecipeCategoryCraftingShapeless());
		registration.registerCategory("smelting", new RecipeCategorySmelting());
		registration.registerCategory("furnaceFuel", new RecipeCategoryFurnaceFuel());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		int skip;

		CraftingManager crafts = CraftingManager.getInstance();

		IRecipeCategory<IRecipe> craftingShaped = registration.getCategoryByUID("craftingShaped");
		List<IRecipe> craftingShapedRecipes = new ArrayList<>();
		IRecipeCategory<IRecipe> craftingShapeless = registration.getCategoryByUID("craftingShapeless");
		List<IRecipe> craftingShapelessRecipes = new ArrayList<>();

		for (IRecipe recipe : crafts.getRecipeList()) {
			if (recipe instanceof ShapedRecipes || recipe instanceof RecipeShapedFix) {
				craftingShapedRecipes.add(recipe);
			}
			if (recipe instanceof ShapelessRecipes || recipe instanceof RecipeShapelessFix) {
				craftingShapelessRecipes.add(recipe);
			}
		}

		registration.addRecipes(craftingShaped, craftingShapedRecipes);
		System.out.println(craftingShapedRecipes.size() + " shaped recipes");
		registration.addRecipes(craftingShapeless, craftingShapelessRecipes);
		System.out.println(craftingShapelessRecipes.size() + " shapeless recipes");

		IRecipeCategory<RecipeSmelting> smelting = registration.getCategoryByUID("smelting");
		List<RecipeSmelting> smeltingRecipes = new ArrayList<>();
		skip = 0;
		for (Map.Entry<Integer, FurnaceRecipesFix.Result> entry : FurnaceRecipesFix.smelting().getSmeltingList().entrySet()) {
			ItemStack in = unpack(entry.getKey());
			ItemStack result = entry.getValue().item;
			if (in == null || result == null) {
				skip++;
				continue;
			}
			smeltingRecipes.add(new RecipeSmelting(in, result, entry.getValue().recipeTime));
		}

		registration.addRecipes(smelting, smeltingRecipes);
		System.out.println(smeltingRecipes.size() + " smelting recipes" + (skip > 0 ? " (" + skip + " skipped)" : ""));

		Iterator<ItemStack> items = ITEMS_CACHE.getItemsListIterator();

		IRecipeCategory<RecipeFurnaceFuel> furnaceFuel = registration.getCategoryByUID("furnaceFuel");
		List<RecipeFurnaceFuel> furnaceFuelRecipes = new ArrayList<>();
		skip = 0;
		while (items.hasNext()) {
			ItemStack next = items.next().copy();
			int ticks = TileEntityFurnace.getItemBurnTime(null, next);
			if (ticks != 0) {
				furnaceFuelRecipes.add(new RecipeFurnaceFuel(next, ticks));
			}
			skip++;
		}

		registration.addRecipes(furnaceFuel, furnaceFuelRecipes);
		System.out.println(furnaceFuelRecipes.size() + " furnace fuel recipes (" + skip + " scanned)");
	}
}
