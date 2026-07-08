package dev.siepert.bei.gui;

import dev.siepert.bei.BEIConfig;
import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.apiimpl.LookupResultRecipes;
import dev.siepert.bei.apiimpl.LookupResultUses;
import dev.siepert.bei.util.InventoryDummy;
import dev.siepert.bei.util.StackFilters;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiRecipes extends GuiContainer {
	public static final String TEXTURE = "assets/gui/" + BarelyEnoughItems.path("recipes.png");

	private final GuiScreen parent;
	private final EntityPlayer player;

	private int mouseX, mouseY;

	private boolean isGoogling = false;

	private String title = BarelyEnoughItems.ITEMS_CACHE.getInvName();

	public GuiRecipes(GuiScreen parent, EntityPlayer player) {
		super(new ContainerRecipes());
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
		BarelyEnoughItems.ITEMS_CACHE.setPageData(11 * 5);
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

		this.drawString(this.mc.fontRenderer, this.title, x+176+3, y-12, 0xFFFFFF);

		String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch + (this.isGoogling && (((System.currentTimeMillis() / 500) & 1) == 0) ? "_" : "");
		if (!google.isEmpty()) {
			this.drawString(this.mc.fontRenderer, google, x + 176 + 3, y + this.ySize + 3, 0xFFFFFF);
		}
	}

	@Override
	protected void keyTyped(char character, int code) {
		if (code == Keyboard.KEY_LEFT || code == Keyboard.KEY_PRIOR) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageDown()) {
				BarelyEnoughItems.fancyFX(this.mc, 3);
			}
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
			return;
		}
		if (code == Keyboard.KEY_RIGHT || code == Keyboard.KEY_NEXT) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageUp()) {
				BarelyEnoughItems.fancyFX(this.mc, 3);
			}
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
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
		if (lookup.hasResults()) mc.displayGuiScreen(new GuiRecipes(parent, mc.thePlayer));
	}
	public static void getUsesFor(Minecraft mc, GuiScreen parent, ItemStack stack) {
		LookupResultUses lookup = LookupResultUses.lookup(stack);
		if (lookup.hasResults()) mc.displayGuiScreen(new GuiRecipes(parent, mc.thePlayer));
	}
}
