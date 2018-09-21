package growthcraft.core.shared.tileentity;

import growthcraft.core.shared.inventory.GrowthcraftAbstractInventory;
import growthcraft.core.shared.inventory.GrowthcraftDelegatedInventory;
import growthcraft.core.shared.inventory.GrowthcraftInternalInventory;
import growthcraft.core.shared.inventory.GrowthcraftNullInventory;
import growthcraft.core.shared.inventory.IInventoryWatcher;
import growthcraft.core.shared.inventory.InventoryProcessor;
import growthcraft.core.shared.item.ItemUtils;
import growthcraft.core.shared.tileentity.event.TileEventHandler;
import growthcraft.core.shared.tileentity.feature.ICustomDisplayName;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * Extend this base class if you want a Tile with an `Inventory`
 */
public abstract class GrowthcraftTileInventoryBase extends GrowthcraftTileBase implements ISidedInventory, ICustomDisplayName, IInventoryWatcher
{
	protected static final int[] NO_SLOTS = new int[]{};

	protected String inventoryName;
	protected GrowthcraftAbstractInventory inventory;

	public GrowthcraftTileInventoryBase()
	{
		super();
		this.inventory = createInventory();
	}
	
	@Override
	public GrowthcraftTileInventoryBase getMaster() {
		GrowthcraftTileBase master = super.getMaster();
		if( master == null )
			return null;
		if( !getClass().isAssignableFrom(master.getClass()) )
			return null;
		return (GrowthcraftTileInventoryBase)master;
	}

	@Override
	protected void setMaster( GrowthcraftTileBase master ) {
		super.setMaster(master);
		this.inventory = new GrowthcraftDelegatedInventory(this,
				()->{
					GrowthcraftTileInventoryBase tiMaster = getMaster();
					if( tiMaster != null )
						return tiMaster.inventory;
					else
						return new GrowthcraftNullInventory(this);
				});
	}

	public GrowthcraftInternalInventory createInventory()
	{
		return new GrowthcraftNullInventory(this);
	}

	public IInventory getInternalInventory()
	{
		return inventory;
	}

	public String getDefaultInventoryName()
	{
		return "grc.inventory.name";
	}

	@Override
	public void onInventoryChanged(IInventory inv, int index)
	{
		markDirty();
	}

	@Override
	public void onItemDiscarded(IInventory inv, ItemStack stack, int index, int discardedAmount)
	{
		// NOTE: Decide how to handle slave state here.
		if( hasMaster() )
			return;	// TODO: Test if it will look cool and items won't be duplicated.
		
		final ItemStack discarded = stack.copy();
		discarded.setCount( discardedAmount );
		ItemUtils.spawnItemStack(world, pos, discarded, world.rand);
	}

	@Override
	public String getName()
	{
		// NOTE: Decide how to handle slave state here.
		return hasCustomName() ? inventoryName : getDefaultInventoryName();
	}

	@Override
	public boolean hasCustomName()
	{
		// NOTE: Decide how to handle slave state here.
		return inventoryName != null && inventoryName.length() > 0;
	}

	@Override
	public void setGuiDisplayName(String string)
	{
		// NOTE: Decide how to handle slave state here.
		this.inventoryName = string;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return inventory.getStackInSlot(index);
	}

	public ItemStack tryMergeItemIntoSlot(ItemStack itemstack, int index)
	{
		// TODO: Decide how to handle slave state here.
		final ItemStack result = ItemUtils.mergeStacksBang(getStackInSlot(index), itemstack);
		if (!ItemUtils.isEmpty(result))
		{
			inventory.setInventorySlotContents(index, result);
		}
		return result;
	}

	// Attempts to merge the given itemstack into the main slot
	public ItemStack tryMergeItemIntoMainSlot(ItemStack itemstack)
	{
		// TODO: Decide how to handle slave state here.
		return tryMergeItemIntoSlot(itemstack, 0);
	}

	@Override
	public ItemStack decrStackSize(int index, int par2)
	{
		return inventory.decrStackSize(index, par2);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack)
	{
		inventory.setInventorySlotContents(index, itemstack);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.getSizeInventory();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		if (world.getTileEntity(pos) != this)
		{
			return false;
		}
		return player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer _player)
	{
		// TODO: Make final and handle slave state here.
	}

	@Override
	public void closeInventory(EntityPlayer _player)
	{
		// TODO: Make final and handle slave state here.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		// TODO: Decide how to handle slave state here.
		return inventory.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side)
	{
		// TODO: Make final and handle slave state here.
		return InventoryProcessor.instance().canInsertItem(this, stack, slot);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side)
	{
		// TODO: Make final and handle slave state here.
		return InventoryProcessor.instance().canExtractItem(this, stack, slot);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return NO_SLOTS;
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
	
	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	protected void readInventoryFromNBT(NBTTagCompound nbt)
	{
		GrowthcraftInternalInventory internalInv;
		if( !(inventory instanceof GrowthcraftInternalInventory) )
			throw new IllegalStateException("Shouldn't have been called.");
		internalInv = (GrowthcraftInternalInventory)inventory;
		
		// NOTE: No slave call
		if (nbt.hasKey("items"))
		{
			internalInv.readFromNBT(nbt, "items");
		}
		else if (nbt.hasKey("inventory"))
		{
			internalInv.readFromNBT(nbt, "inventory");
		}
	}

	private void readInventoryNameFromNBT(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		if (nbt.hasKey("name"))
		{
			this.inventoryName = nbt.getString("name");
		}
		else if (nbt.hasKey("inventory_name"))
		{
			this.inventoryName = nbt.getString("inventory_name");
		}
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_ITEM_READ)
	public void readFromNBTForItem_Inventory(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		readInventoryFromNBT(nbt);
		// Do not reload the inventory name from NBT, allow the ItemStack to do that
		//readInventoryNameFromNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_READ)
	public void readFromNBT_Inventory(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		readInventoryFromNBT(nbt);
		readInventoryNameFromNBT(nbt);
	}

	private void writeInventoryToNBT(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		GrowthcraftInternalInventory internalInv;
		if( !(inventory instanceof GrowthcraftInternalInventory) )
			throw new IllegalStateException("Shouldn't have been called.");
		internalInv = (GrowthcraftInternalInventory)inventory;
				
		internalInv.writeToNBT(nbt, "inventory");
		
		// NAME
		if (hasCustomName())
		{
			nbt.setString("inventory_name", inventoryName);
		}
		nbt.setInteger("inventory_tile_version", 3);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_ITEM_WRITE)
	public void writeToNBTForItem_Inevntory(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		writeInventoryToNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_WRITE)
	public void writeToNBT_Inventory(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		writeInventoryToNBT(nbt);
	}
}
