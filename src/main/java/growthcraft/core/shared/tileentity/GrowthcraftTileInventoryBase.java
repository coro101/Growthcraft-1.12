package growthcraft.core.shared.tileentity;

import growthcraft.core.shared.inventory.GrowthcraftInternalInventory;
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
	protected GrowthcraftInternalInventory inventory;

	@Override
	public GrowthcraftTileInventoryBase getMaster() {
		GrowthcraftTileBase master = super.getMaster();
		if( master == null )
			return null;
		if( !getClass().isAssignableFrom(master.getClass()) )
			return null;
		return (GrowthcraftTileInventoryBase)master;
	}
	
	public GrowthcraftTileInventoryBase()
	{
		super();
		this.inventory = createInventory();
	}

	public GrowthcraftInternalInventory createInventory()
	{
		// TODO: Decide how to handle slave state here.
		return new GrowthcraftInternalInventory(this, 0);
	}

	public GrowthcraftInternalInventory getInternalInventory()
	{
		// TODO: Handle slave state here.
		return inventory;
	}

	public String getDefaultInventoryName()
	{
		return "grc.inventory.name";
	}

	@Override
	public void onInventoryChanged(IInventory inv, int index)
	{
		// TODO: Handle slave state here.
		markDirty();
	}

	@Override
	public void onItemDiscarded(IInventory inv, ItemStack stack, int index, int discardedAmount)
	{
		// TODO: Decide how to handle slave state here.
		final ItemStack discarded = stack.copy();
		discarded.setCount( discardedAmount );
		ItemUtils.spawnItemStack(world, pos, discarded, world.rand);
	}

	@Override
	public String getName()
	{
		// TODO: Handle slave state here.
		return hasCustomName() ? inventoryName : getDefaultInventoryName();
	}

	@Override
	public boolean hasCustomName()
	{
		// TODO: Handle slave state here.
		return inventoryName != null && inventoryName.length() > 0;
	}

	@Override
	public void setGuiDisplayName(String string)
	{
		// TODO: Handle slave state here.
		this.inventoryName = string;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		// TODO: Handle slave state here.
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
		// TODO: Handle slave state here.
		return inventory.decrStackSize(index, par2);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		// TODO: Handle slave state here.
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack)
	{
		// TODO: Handle slave state here.
		inventory.setInventorySlotContents(index, itemstack);
	}

	@Override
	public int getInventoryStackLimit()
	{
		// TODO: Handle slave state here.
		return inventory.getInventoryStackLimit();
	}

	@Override
	public int getSizeInventory()
	{
		// TODO: Handle slave state here.
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
		// TODO: Handle slave state here.
		return inventory.isEmpty();
	}
	
	@Override
	public void clear() {
		// TODO: Handle slave state here.
		inventory.clear();
	}

	@Override
	public int getField(int id) {
		// TODO: Handle slave state here.
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		// TODO: Handle slave state here.
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		// TODO: Handle slave state here.
		return inventory.getFieldCount();
	}

	protected void readInventoryFromNBT(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		if (nbt.hasKey("items"))
		{
			inventory.readFromNBT(nbt, "items");
		}
		else if (nbt.hasKey("inventory"))
		{
			inventory.readFromNBT(nbt, "inventory");
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

	@Override
	public void readFromNBTForItem(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		super.readFromNBTForItem(nbt);
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
		inventory.writeToNBT(nbt, "inventory");
		// NAME
		if (hasCustomName())
		{
			nbt.setString("inventory_name", inventoryName);
		}
		nbt.setInteger("inventory_tile_version", 3);
	}

	@Override
	public void writeToNBTForItem(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		super.writeToNBTForItem(nbt);
		writeInventoryToNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_WRITE)
	public void writeToNBT_Inventory(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		writeInventoryToNBT(nbt);
	}
}
