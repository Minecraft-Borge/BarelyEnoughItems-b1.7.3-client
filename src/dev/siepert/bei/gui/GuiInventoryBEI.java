package dev.siepert.bei.gui;

import dev.siepert.bei.BEIConfig;
import dev.siepert.bei.BarelyEnoughItems;
import dev.siepert.bei.util.InventoryDummy;
import dev.siepert.bei.util.StackFilters;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiInventoryBEI extends GuiContainer {
	private final EntityPlayer player;

	private float xSize_lo;
	private float ySize_lo;

	private boolean isGoogling = false;

	private String title = BarelyEnoughItems.ITEMS_CACHE.getInvName();

	public GuiInventoryBEI(EntityPlayer player) {
		super(new ContainerBEI(player.inventorySlots));
		this.field_948_f = true;
		this.player = player;
		player.triggerAchievement(AchievementList.openInventory);
		player.craftingInventory = this.container();

		this.xSize = 256;
	}

	private ContainerBEI container() {
		return (ContainerBEI) this.inventorySlots;
	}

	@Override
	public void initGui() {
		this.controlList.clear();
		this.isGoogling = false;
		InventoryDummy.INSTANCE.repopulate();
		//BarelyEnoughItems.ITEMS_CACHE.filter(StackFilters::any);
		BarelyEnoughItems.ITEMS_CACHE.setPageData(9 * 5);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
		this.player.craftingInventory = this.container();
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString("Crafting", 86, 16, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		super.drawScreen(mouseX, mouseY, partialTick);
		this.xSize_lo = mouseX;
		this.ySize_lo = mouseY;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		int textureID = this.mc.renderEngine.getTexture("/gui/inventory.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(textureID);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(x + 51), (float)(y + 75), 50.0F);
		float scalar = 30.0F;
		GL11.glScalef(-scalar, scalar, scalar);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float playerYaw = this.mc.thePlayer.renderYawOffset;
		float yaw = this.mc.thePlayer.rotationYaw;
		float pitch = this.mc.thePlayer.rotationPitch;
		float lookX = (float)(x + 51) - this.xSize_lo;
		float lookY = (float)(y + 75 - 50) - this.ySize_lo;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan(lookY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		this.mc.thePlayer.renderYawOffset = (float)Math.atan(lookX / 40.0F) * 20.0F;
		this.mc.thePlayer.rotationYaw = (float)Math.atan(lookX / 40.0F) * 40.0F;
		this.mc.thePlayer.rotationPitch = -((float)Math.atan(lookY / 40.0F)) * 20.0F;
		this.mc.thePlayer.entityBrightness = 1.0F;
		GL11.glTranslatef(0.0F, this.mc.thePlayer.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(this.mc.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		this.mc.thePlayer.entityBrightness = 0.0F;
		this.mc.thePlayer.renderYawOffset = playerYaw;
		this.mc.thePlayer.rotationYaw = yaw;
		this.mc.thePlayer.rotationPitch = pitch;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		this.drawString(this.mc.fontRenderer, this.title, x+176+3, y-12, 0xFFFFFF);

		String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch + (this.isGoogling && (((System.currentTimeMillis() / 500) & 1) == 0) ? "_" : "");
		if (!google.isEmpty()) {
			this.drawString(this.mc.fontRenderer, google, x + 176 + 3, y + this.ySize + 3, 0xFFFFFF);
		}
	}

	protected void actionPerformed(GuiButton button) {

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
				GuiRecipes.getRecipesFor(this.mc, this, hovered.getStack());
				return;
			}
		}
		if (code == Keyboard.KEY_U) {
			Slot hovered = this.getHoveredSlot();
			if (hovered != null && hovered.getStack() != null) {
				GuiRecipes.getUsesFor(this.mc, this, hovered.getStack());
				return;
			}
		}

		super.keyTyped(character, code);
	}

	protected void applyGoogleSearch() {
		this.isGoogling = false;
		String google = BarelyEnoughItems.ITEMS_CACHE.googleSearch;
		BarelyEnoughItems.ITEMS_CACHE.filter(google.isEmpty() ? StackFilters::any : StackFilters.named(google.toLowerCase(), false));
		BarelyEnoughItems.ITEMS_CACHE.setPageData(9 * 5);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
	}

	protected Slot getHoveredSlot() {
		for (Slot slot : this.inventorySlots.slots) {
			if (this.getIsMouseOverSlot(slot, (int) this.xSize_lo, (int) this.ySize_lo)) return slot;
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
}
