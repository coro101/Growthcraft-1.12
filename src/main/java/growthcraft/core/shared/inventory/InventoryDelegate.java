package growthcraft.core.shared.inventory;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InventoryDelegate extends GrowthcraftAbstractInventory implements IInventory {

	private WeakReference<GrowthcraftAbstractInventory> cachedInventoryRef;
	private final Supplier<GrowthcraftAbstractInventory> delegatedSupplier;
	
	public InventoryDelegate(Object parent, Supplier<GrowthcraftAbstractInventory> delegatedSupplier) {
		super(parent);
		this.delegatedSupplier = delegatedSupplier;
		this.cachedInventoryRef = null;
	}
	
	public IInventory getDelegated() {
		GrowthcraftAbstractInventory inv = null;
		if( cachedInventoryRef != null ) {
			inv = cachedInventoryRef.get();
			if( inv == null )
				cachedInventoryRef = null;
		}
		
		if( cachedInventoryRef == null ) {
			inv = delegatedSupplier.get();
			if( inv == null )
				throw new IllegalStateException("Inventory should be never null.");
			inv.subscribeListener(this);
			cachedInventoryRef = new WeakReference<GrowthcraftAbstractInventory>(inv);
		}
		return inv;	// Is never null
	}
	
	@Override
	public final String getName() {
		return getDelegated().getName();
	}

	@Override
	public final boolean hasCustomName() {
		return getDelegated().hasCustomName();
	}

	@Override
	public final ITextComponent getDisplayName() {
		return getDelegated().getDisplayName();
	}

	@Override
	public final int getSizeInventory() {
		return getDelegated().getSizeInventory();
	}

	@Override
	public final boolean isEmpty() {
		return getDelegated().isEmpty();
	}

	@Override
	public final ItemStack getStackInSlot(int index) {
		return getDelegated().getStackInSlot(index);
	}

	@Override
	public final ItemStack decrStackSize(int index, int count) {
		return getDelegated().decrStackSize(index, count);
	}

	@Override
	public final ItemStack removeStackFromSlot(int index) {
		return getDelegated().removeStackFromSlot(index);
	}

	@Override
	public final void setInventorySlotContents(int index, ItemStack stack) {
		getDelegated().setInventorySlotContents(index, stack);
	}

	@Override
	public final int getInventoryStackLimit() {
		return getDelegated().getInventoryStackLimit();
	}

	@Override
	public final void markDirty() {
		getDelegated().markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return getDelegated().isUsableByPlayer(player);
	}

	@Override
	public final void openInventory(EntityPlayer player) {
		getDelegated().openInventory(player);
	}

	@Override
	public final void closeInventory(EntityPlayer player) {
		getDelegated().closeInventory(player);
	}

	@Override
	public final boolean isItemValidForSlot(int index, ItemStack stack) {
		return getDelegated().isItemValidForSlot(index, stack);
	}

	@Override
	public final int getField(int id) {
		return getDelegated().getField(id);
	}

	@Override
	public final void setField(int id, int value) {
		getDelegated().setField(id, value);
	}

	@Override
	public final int getFieldCount() {
		return getDelegated().getFieldCount();
	}

	@Override
	public final void clear() {
		getDelegated().clear();
	}
}
