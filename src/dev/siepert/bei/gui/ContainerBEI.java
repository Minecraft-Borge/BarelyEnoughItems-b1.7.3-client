package dev.siepert.bei.gui;

import dev.siepert.bei.BEIConfig;
import dev.siepert.bei.BarelyEnoughItems;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;

public class ContainerBEI extends Container {
	private static boolean cheatModeWarning = false;

	private final Container inventorySlots;
	public final int slotStartIndex;

	public ContainerBEI(Container inventorySlots) {
		this.inventorySlots = inventorySlots;

		this.remoteItems.addAll(this.inventorySlots.remoteItems);
		this.slots.addAll(this.inventorySlots.slots);
		this.windowId = this.inventorySlots.windowId;

		this.slotStartIndex = this.slots.size();

		IInventory inv = BarelyEnoughItems.ITEMS_CACHE;
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 9; y++) {
				this.addSlot(new SlotBEI(inv, x + y*5, 177 + 18*x, 3 + 18*y));
			}
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.inventorySlots.isUsableByPlayer(player);
	}

	@Override
	public ItemStack clickGuiSlot(int slotId, int type, boolean shift, EntityPlayer player) {
		if (slotId < this.slotStartIndex) return super.clickGuiSlot(slotId, type, shift, player);
		else if (slotId < this.slots.size()) {
			Slot slot = this.slots.get(slotId);
			if (slot != null) {
				ItemStack stack = slot.getStack();
				if (stack != null) {
					if (shift && player instanceof EntityPlayerSP) {
						//cheat mode
						GuiIngame ingameGUI = Minecraft.getTheMinecraft().ingameGUI;
						if (BEIConfig.enableCheatMode()) {
							ItemStack copy = stack.copy();
							copy.stackSize = type == 0 ? copy.getMaxStackSize() : 1;
							ingameGUI.addChatMessage("Given " + copy.stackSize + "* [" + StringTranslate.getInstance().translateNamedKey(copy.getItemName()) + "]");
							player.inventory.addItemStackToInventory(copy);
						} else {
							if (!cheatModeWarning) {
								cheatModeWarning = true;
								ingameGUI.addChatMessage("[BarelyEnoughItems] To enable cheat mode set");
								ingameGUI.addChatMessage("'enableCheatMode' to true in the configuration");
							}
						}
					} else {
						Minecraft mc = Minecraft.getTheMinecraft();
						//recipe lookup time
						if (type == 0) {
							GuiRecipes.getRecipesFor(mc, mc.currentScreen, stack);
						}
						if (type == 1) {
							GuiRecipes.getUsesFor(mc, mc.currentScreen, stack);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlot(int slotID) {
		ItemStack ret = null;
		Slot slot = this.slots.get(slotID);
		if(slot != null && slot.hasItem()) {
			ItemStack stack = slot.getStack();
			ret = stack.copy();
			if(slotID == 0) {
				this.func_28125_a(stack, 9, 45, true);
			} else if(slotID >= 9 && slotID < 36) {
				this.func_28125_a(stack, 36, 45, false);
			} else if(slotID >= 36 && slotID < 45) {
				this.func_28125_a(stack, 9, 36, false);
			} else {
				this.func_28125_a(stack, 9, 45, false);
			}

			if(stack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if(stack.stackSize == ret.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(stack);
		}

		return ret;
	}
}
