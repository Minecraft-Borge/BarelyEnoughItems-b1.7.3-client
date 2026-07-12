package dev.siepert.bei;

import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.api.IRecipesPlugin;
import dev.siepert.bei.api.RecipesPlugin;
import dev.siepert.bei.api.reg.ICategoryRegistration;
import dev.siepert.bei.api.reg.IRecipeRegistration;
import dev.siepert.bei.api.reg.IScreenRegistration;
import dev.siepert.bei.apiimpl.LookupResultCategories;
import dev.siepert.bei.gui.*;
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
import net.minecraftborge.loader.event.gui.GuiKeyboardEvent;
import net.minecraftborge.loader.event.gui.GuiMouseEvent;
import net.minecraftborge.loader.event.lifecycle.ModInitializationEvent;
import net.minecraftborge.loader.event.lifecycle.ModPostInitializationEvent;
import net.minecraftborge.loader.event.lifecycle.ModPreInitializationEvent;
import net.minecraftborge.loader.event.misc.ItemTooltipEvent;
import net.minecraftborge.loader.event.register.ExtractSoundsEvent;
import net.minecraftborge.loader.event.world.ChangeWorldEvent;
import net.minecraftborge.loader.tag.ItemTags;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

	public static int pack(ItemStack unpacked) {
		return FurnaceRecipesFix.pack(unpacked.itemID, unpacked.getItemDamage());
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
		BEIPluginManager.registerScreenHandlers();
		BEIPluginManager.indexRecipes();
	}

	// Event handlers

	@EventHandler
	public static void registerSounds(ExtractSoundsEvent event) {
		event.extract("assets/sounds/bei/");
	}

	private static final boolean USE_SPECIALIZED_INVENTORY_WRAPPER = true;
	private static final boolean USE_GLOBAL_INVENTORY_WRAPPER = false;
	@EventHandler(EventPriority.HIGH)
	public static void injectCustomInventoryGUI(ChangeGuiEvent event) {
		if (USE_SPECIALIZED_INVENTORY_WRAPPER && event.getNewScreen() instanceof GuiInventory) {
			event.setNewScreen(new GuiInventoryBEI(Minecraft.getTheMinecraft().thePlayer));
		} else if (USE_GLOBAL_INVENTORY_WRAPPER) {
			if (event.getNewScreen() instanceof GuiContainer) {
				GuiContainer container = (GuiContainer) event.getNewScreen();
				Class<? extends GuiContainer> clazz = container.getClass();
				if (BEIPluginManager.compatibleItemsList.contains(clazz)) {
					event.setNewScreen(new GuiWrapperList(container));
				}
			}
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

	@EventHandler(EventPriority.HIGH)
	public static void keyboardGUI(GuiKeyboardEvent event) {
		if (event.isCanceled() || !Keyboard.getEventKeyState()) return;
		GuiScreen screen = event.getScreen();
		if (screen == null) return;
		if (screen instanceof IGuiWrapper && ((IGuiWrapper)screen).isGoogling()) {
			event.setCanceled(true);
			return;
		}
		if (!(screen instanceof GuiContainer)) return;
		int key = Keyboard.getEventKey();
		if (key != Keyboard.KEY_R && key != Keyboard.KEY_U)	return;

		Minecraft mc = Minecraft.getTheMinecraft();
		int dx = Mouse.getX() * screen.width / mc.displayWidth;
		int dy = screen.height - Mouse.getY() * screen.height / mc.displayHeight - 1;
		Slot slot = GuiRecipes.getHoveredSlot((GuiContainer) screen, dx, dy);
		if (slot != null) {
			ItemStack stack = slot.getStack();
			if (ItemTags.isItemEmpty(stack)) return;
			if (key == Keyboard.KEY_R) {
				GuiRecipes.getRecipesFor(mc, screen, stack);
			}
			if (key == Keyboard.KEY_U) {
				GuiRecipes.getUsesFor(mc, screen, stack);
			}
		}
	}
	@EventHandler(EventPriority.HIGH)
	public static void mouseGUI(GuiMouseEvent event) {
		if (event.isCanceled()) return;
		GuiScreen screen = event.getScreen();
		if (!(screen instanceof GuiContainer)) return;
		GuiContainer container = (GuiContainer) screen;
		Class<? extends GuiContainer> clazz = container.getClass();

		Minecraft mc = Minecraft.getTheMinecraft();
		if (mc.thePlayer != null && mc.thePlayer.inventory.getItemStack() != null) return;

		try {
			if (Mouse.getEventButtonState()) {
				List<ScreenHandler> handlers = BEIPluginManager.screenHandlers.get(clazz);
				if (handlers != null && !handlers.isEmpty()) {
					int x = (screen.width - xSizeField.getInt(container)) / 2;
					int y = (screen.height - ySizeField.getInt(container)) / 2;
					int dx = (Mouse.getEventX() * screen.width / mc.displayWidth) - x;
					int dy = (screen.height - Mouse.getEventY() * screen.height / mc.displayHeight - 1) - y;
					for (ScreenHandler handler : handlers) {
						if (handler.x <= dx && handler.y <= dy) {
							if (handler.x + handler.w > dx && handler.y + handler.h > dy) {
								event.setCanceled(true);
								LookupResultCategories lookup = LookupResultCategories.lookup(handler.categoryUIDs);
								if (lookup.hasResults())
									mc.displayGuiScreen(new GuiRecipes(screen, mc.thePlayer, lookup));
								mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
								return;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Screen handler exception: " + e);
		}
	}

	public static final Field xSizeField, ySizeField;
	static {
		try	{
			xSizeField = GuiContainer.class.getDeclaredField("a");
			xSizeField.setAccessible(true);

			ySizeField = GuiContainer.class.getDeclaredField("i");
			ySizeField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException("Field problems", e);
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

	@Override
	public void registerScreenHandlers(IScreenRegistration registration) {
		registration.addScreenHandler(GuiFurnace.class, 79, 34, 24, 17, "smelting", "furnaceFuel");

		registration.addItemsListCompatible(GuiInventory.class);
		registration.addItemsListCompatible(GuiChest.class);
		registration.addItemsListCompatible(GuiCrafting.class);
		registration.addItemsListCompatible(GuiFurnace.class);
	}
}
