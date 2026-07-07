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
	private float xSize_lo;
	private float ySize_lo;

	private String googleSearch = "";
	private boolean isGoogling = false;

	private String title = BarelyEnoughItems.ITEMS_CACHE.getInvName();

	public GuiInventoryBEI(EntityPlayer player) {
		super(new ContainerBEI(player.inventorySlots));
		this.field_948_f = true;
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
		this.googleSearch = "";
		this.isGoogling = false;
		InventoryDummy.INSTANCE.repopulate();
		BarelyEnoughItems.ITEMS_CACHE.filter(StackFilters::any);
		BarelyEnoughItems.ITEMS_CACHE.setPageData(9 * 5);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
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

		String google = this.googleSearch + (this.isGoogling && (((System.currentTimeMillis() / 500) & 1) == 0) ? "_" : "");
		if (!google.isEmpty()) {
			this.drawString(this.mc.fontRenderer, google, x + 176 + 3, y + this.ySize + 3, 0xFFFFFF);
		}
	}

	protected void actionPerformed(GuiButton button) {

	}

	@Override
	protected void keyTyped(char character, int code) {
		if (code == Keyboard.KEY_LEFT || code == Keyboard.KEY_PRIOR) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageDown()) this.mc.sndManager.playSoundFX("random.wood click", 1.0F, 1.0F);
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
			return;
		}
		if (code == Keyboard.KEY_RIGHT || code == Keyboard.KEY_NEXT) {
			if (BarelyEnoughItems.ITEMS_CACHE.pageUp()) this.mc.sndManager.playSoundFX("random.wood click", 1.0F, 1.0F);
			this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
			return;
		}
		if (this.isGoogling) {
			if (ChatAllowedCharacters.allowedCharacters.indexOf(character) >= 0) {
				this.googleSearch += character;
				if (BEIConfig.instantSearchResults()) {
					this.applyGoogleSearch();
					this.isGoogling = true;
				}
				return;
			}
			if (code == Keyboard.KEY_DELETE || code == Keyboard.KEY_BACK) {
				if (!this.googleSearch.isEmpty()) {
					this.googleSearch = this.googleSearch.substring(0, this.googleSearch.length() - 1);
					if (BEIConfig.instantSearchResults()) {
						this.applyGoogleSearch();
						this.isGoogling = true;
					}
				}
				return;
			}
			if (code == Keyboard.KEY_RETURN) {
				this.applyGoogleSearch();
				return;
			}
			if (code == Keyboard.KEY_ESCAPE) {
				this.googleSearch = "";
				this.applyGoogleSearch();
				return;
			}
		} else if (code == Keyboard.KEY_RETURN) {
			this.isGoogling = true;
		}
		super.keyTyped(character, code);
	}

	protected void applyGoogleSearch() {
		this.isGoogling = false;
		BarelyEnoughItems.ITEMS_CACHE.filter(this.googleSearch.isEmpty() ? StackFilters::any : StackFilters.named(this.googleSearch, false));
		BarelyEnoughItems.ITEMS_CACHE.setPageData(9 * 5);
		this.title = BarelyEnoughItems.ITEMS_CACHE.getInvName();
	}
}
