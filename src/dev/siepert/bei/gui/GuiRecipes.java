package dev.siepert.bei.gui;

import dev.siepert.bei.BEIConfig;
import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.api.IRecipeCategory;
import dev.siepert.bei.apiimpl.LookupResult;
import dev.siepert.bei.apiimpl.LookupResultRecipes;
import dev.siepert.bei.apiimpl.LookupResultUses;
import dev.siepert.bei.apiimpl.RecipeContainer;
import dev.siepert.bei.util.InventoryDummy;
import dev.siepert.bei.util.StackFilters;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiRecipes extends GuiContainer {
	public static final String TEXTURE = "assets/gui/" + BarelyEnoughItems.path("recipes.png");

	private final GuiScreen parent;
	private final EntityPlayer player;

	private int mouseX, mouseY;

	private boolean isGoogling = false;

	private String title = BarelyEnoughItems.ITEMS_CACHE.getInvName();

	public GuiRecipes(GuiScreen parent, EntityPlayer player, LookupResult lookup) {
		super(new ContainerRecipes(lookup));
		this.parent = parent;
		this.field_948_f = true;
		this.player = player;
		player.craftingInventory = this.container();

		this.xSize = 256;
		this.ySize = 200;
	}

	public ContainerRecipes container() {
		return (ContainerRecipes) this.inventorySlots;
	}

	@Override
	public void initGui() {
		this.controlList.clear();
		this.isGoogling = false;
		InventoryDummy.INSTANCE.repopulate();
		//BarelyEnoughItems.ITEMS_CACHE.filter(StackFilters::any);
		int page = BarelyEnoughItems.ITEMS_CACHE.getPage();
		BarelyEnoughItems.ITEMS_CACHE.setPageData(11 * 5);
		BarelyEnoughItems.ITEMS_CACHE.setPage(page);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
		this.player.craftingInventory = this.container();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		int textureID = this.mc.renderEngine.getTexture(TEXTURE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(textureID);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		Tessellator tes = Tessellator.instance;
		IRecipeCategory<?> category = this.container().lookup.currentCategory();
		List<RecipeContainer<?>> recipes = this.container().lookup.currentRecipes();
		int width = category.getWidth();
		int height = category.getHeight();
		int renderX = x+88-width/2;
		int backdropID = this.mc.renderEngine.getTexture(category.getBackdropTexture());
		this.mc.renderEngine.bindTexture(backdropID);
		tes.startDrawingQuads();
		for (int i = 0; i < this.container().pageSize; i++) {
			int index = this.container().pageSize * this.container().lookup.recipePage + i;
			if (index < recipes.size()) {
				int renderY = y + 3 + 20 + i * height;
				RecipeContainer<?> recipe = recipes.get(index);

				recipe.drawBackdrop(this.mc, tes, renderX, renderY, partialTick);
			}
		}
		tes.draw();

		for (int i = 0; i < this.container().pageSize; i++) {
			int index = this.container().pageSize * this.container().lookup.recipePage + i;
			if (index < recipes.size()) {
				int renderY = y + 3 + 20 + i * height;
				RecipeContainer<?> recipe = recipes.get(index);

				recipe.drawExtras(this.mc, this.mc.renderEngine, renderX, renderY, this.mouseX, this.mouseY, partialTick);
			}
		}

		String categoryTitle = this.container().lookup.currentCategory().getTitle();
		this.fontRenderer.drawString(categoryTitle, x+88-this.fontRenderer.getStringWidth(categoryTitle)/2, y+2, 0x000000);
		String pageIndicator = (this.container().lookup.recipePage+1) + " / " + (this.container().maxPage+1);
		this.fontRenderer.drawString(pageIndicator, x+88-this.fontRenderer.getStringWidth(pageIndicator)/2, y+11, 0x000000);

		for (int i = 0; i < this.container().pageSize; i++) {
			int index = this.container().pageSize * this.container().lookup.recipePage + i;
			if (index < recipes.size()) {
				int renderY = y + 3 + 20 + i * height;
				RecipeContainer<?> recipe = recipes.get(index);

				recipe.drawTexts(this.mc, this.fontRenderer, renderX, renderY, this.mouseX, this.mouseY, partialTick);
			}
		}

		this.drawString(this.mc.fontRenderer, this.title, x+176+3, y-12, 0xFFFFFF);

		String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch + (this.isGoogling && (((System.currentTimeMillis() / 500) & 1) == 0) ? "_" : "");
		if (!google.isEmpty()) {
			this.drawString(this.mc.fontRenderer, google, x + 176 + 3, y + this.ySize + 3, 0xFFFFFF);
		}
	}

	@Override
	protected void keyTyped(char character, int code) {
		if (code == Keyboard.KEY_UP) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageDown()) {
				BarelyEnoughItems.fancyFX(this.mc, 3);
			}
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
			return;
		}
		if (code == Keyboard.KEY_DOWN) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageUp()) {
				BarelyEnoughItems.fancyFX(this.mc, 3);
			}
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
			return;
		}
		LookupResult lookup = this.container().lookup;
		if (code == Keyboard.KEY_PRIOR || code == Keyboard.KEY_LBRACKET) {
			if (lookup.categoryIndex > 0) {
				lookup.categoryIndex--;
			} else {
				lookup.categoryIndex = lookup.categories.size() - 1;
			}
			this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			this.container().setCategory();
			this.container().setRecipes();
			return;
		}
		if (code == Keyboard.KEY_NEXT || code == Keyboard.KEY_RBRACKET) {
			if (lookup.categoryIndex < lookup.categories.size()-1) {
				lookup.categoryIndex++;
			} else {
				lookup.categoryIndex = 0;
			}
			this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			this.container().setCategory();
			this.container().setRecipes();
			return;
		}
		if (code == Keyboard.KEY_LEFT) {
			if (lookup.recipePage > 0) {
				lookup.recipePage--;
				this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				this.container().setRecipes();
			}
			return;
		}
		if (code == Keyboard.KEY_RIGHT) {
			if (lookup.recipePage < this.container().maxPage) {
				lookup.recipePage++;
				this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				this.container().setRecipes();
			}
			return;
		}
		if (this.isGoogling) {
			if (ChatAllowedCharacters.allowedCharacters.indexOf(character) >= 0) {
				BarelyEnoughItems.ITEMS_CACHE.googleSearch += character;
				BarelyEnoughItems.fancyFX(this.mc, 2);
				if (BEIConfig.instantSearchResults()) {
					this.applyGoogleSearch();
					this.isGoogling = true;
				}
				return;
			}
			if (code == Keyboard.KEY_DELETE || code == Keyboard.KEY_BACK) {
				String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch;
				if (!google.isEmpty()) {
					BarelyEnoughItems.ITEMS_CACHE.googleSearch = google.substring(0, google.length() - 1);
					BarelyEnoughItems.fancyFX(this.mc, 2);
					if (BEIConfig.instantSearchResults()) {
						this.applyGoogleSearch();
						this.isGoogling = true;
					}
				}
				return;
			}
			if (code == Keyboard.KEY_RETURN) {
				BarelyEnoughItems.fancyFX(this.mc, 1);
				this.applyGoogleSearch();
				return;
			}
			if (code == Keyboard.KEY_ESCAPE) {
				BarelyEnoughItems.fancyFX(this.mc, 0);
				BarelyEnoughItems.ITEMS_CACHE.googleSearch = "";
				this.applyGoogleSearch();
				return;
			}
		} else if (code == Keyboard.KEY_RETURN) {
			BarelyEnoughItems.fancyFX(this.mc, 1);
			this.isGoogling = true;
			return;
		}

		if (code == Keyboard.KEY_R) {
			Slot hovered = this.getHoveredSlot();
			if (hovered != null && hovered.getStack() != null) {
				GuiRecipes.getRecipesFor(this.mc, this.parent, hovered.getStack());
				return;
			}
		}
		if (code == Keyboard.KEY_U) {
			Slot hovered = this.getHoveredSlot();
			if (hovered != null && hovered.getStack() != null) {
				GuiRecipes.getUsesFor(this.mc, this.parent, hovered.getStack());
				return;
			}
		}

		if(code == 1 || code == this.mc.gameSettings.keyBindInventory.keyCode) {
			this.mc.displayGuiScreen(this.parent);
		}
	}

	protected void applyGoogleSearch() {
		this.isGoogling = false;
		String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch;
		BarelyEnoughItems.ITEMS_CACHE.filter(google.isEmpty() ? StackFilters::any : StackFilters.named(google.toLowerCase(), false));
		BarelyEnoughItems.ITEMS_CACHE.setPageData(11 * 5);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
	}

	protected Slot getHoveredSlot() {
		for (Slot slot : this.inventorySlots.slots) {
			if (this.getIsMouseOverSlot(slot, this.mouseX, this.mouseY)) return slot;
		}
		return null;
	}

	private boolean getIsMouseOverSlot(Slot slot, int mouseX, int mouseY) {
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		mouseX -= x;
		mouseY -= y;
		return mouseX >= slot.xDisplayPosition - 1 && mouseX < slot.xDisplayPosition + 16 + 1 && mouseY >= slot.yDisplayPosition - 1 && mouseY < slot.yDisplayPosition + 16 + 1;
	}

	public static void getRecipesFor(Minecraft mc, GuiScreen parent, ItemStack stack) {
		LookupResultRecipes lookup = LookupResultRecipes.lookup(stack);
		if (lookup.hasResults()) mc.displayGuiScreen(new GuiRecipes(parent, mc.thePlayer, lookup));
	}
	public static void getUsesFor(Minecraft mc, GuiScreen parent, ItemStack stack) {
		LookupResultUses lookup = LookupResultUses.lookup(stack);
		if (lookup.hasResults()) mc.displayGuiScreen(new GuiRecipes(parent, mc.thePlayer, lookup));
	}
}
