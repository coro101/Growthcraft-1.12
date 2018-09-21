package growthcraft.core.shared.tileentity;

import java.io.IOException;

import javax.annotation.Nullable;

import growthcraft.core.shared.fluids.FluidTest;
import growthcraft.core.shared.handlers.FluidHandlerBlockWrapper;
import growthcraft.core.shared.fluids.DelegatedFluidTanks;
import growthcraft.core.shared.fluids.NullFluidTanks;
import growthcraft.core.shared.fluids.FluidTanks;
import growthcraft.core.shared.fluids.IFluidTanks;
import growthcraft.core.shared.tileentity.event.TileEventHandler;
import growthcraft.core.shared.tileentity.feature.IFluidTankOperable;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Extend this base class if you want a base class with an `Inventory` and `Fluid Tanks`
 */
public abstract class GrowthcraftTileDeviceBase extends GrowthcraftTileInventoryBase implements IFluidTankOperable, IFluidTanks
{
	private IFluidTanks tanks;

	public GrowthcraftTileDeviceBase()
	{
		super();
		this.tanks = new FluidTanks(createTanks());
	}
	
	@Override
	public GrowthcraftTileDeviceBase getMaster() {
		GrowthcraftTileInventoryBase master = super.getMaster();
		if( master == null )
			return null;
		if( !getClass().isAssignableFrom(master.getClass()) )
			return null;
		return (GrowthcraftTileDeviceBase)master;
	}
	
	@Override
	public void setMaster( GrowthcraftTileBase master ) {
		super.setMaster(master);
		this.tanks = new DelegatedFluidTanks(
				()->{
					GrowthcraftTileDeviceBase tdMaster = getMaster();
					if( tdMaster != null )
						return tdMaster.tanks;
					else
						return new NullFluidTanks();
				});
	}

	protected void markFluidDirty()
	{
		// TODO: Decide how to handle slave state here.
		
		markDirty();
	}

	protected FluidTank[] createTanks()
	{
		// TODO: Decide how to handle slave state here.
		
		return new FluidTank[] {};
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid)
	{
		// TODO: Make final and handle slave state here.
		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid)
	{
		// TODO: Make final and handle slave state here.
		return true;
	}

	protected FluidStack doDrain(EnumFacing dir, int amount, boolean shouldDrain)
	{
		// TODO: Make final and handle slave state here.
		return null;
	}

	protected FluidStack doDrain(EnumFacing dir, FluidStack stack, boolean shouldDrain)
	{
		// TODO: Make final and handle slave state here.
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing dir, int amount, boolean shouldDrain)
	{
		// TODO: Make final and handle slave state here.
		final FluidStack result = doDrain(dir, amount, shouldDrain);
		if (shouldDrain && FluidTest.isValid(result)) markFluidDirty();
		return result;
	}

	@Override
	public FluidStack drain(EnumFacing dir, FluidStack stack, boolean shouldDrain)
	{
		// TODO: Make final and handle slave state here.
		if (!FluidTest.isValid(stack)) return null;
		final FluidStack result = doDrain(dir, stack, shouldDrain);
		if (shouldDrain && FluidTest.isValid(result)) markFluidDirty();
		return result;
	}

	protected int doFill(EnumFacing dir, FluidStack stack, boolean shouldFill)
	{
		// TODO: Make final and handle slave state here.
		return 0;
	}

	@Override
	public int fill(EnumFacing dir, FluidStack stack, boolean shouldFill)
	{
		// TODO: Make final and handle slave state here.
		final int result = doFill(dir, stack, shouldFill);
		if (shouldFill && result != 0) markFluidDirty();
		return result;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from)
	{
		// TODO: Handle slave state here.
		return tanks.getTankInfo(from);
	}
	
	@Override
	public IFluidTankProperties[] getTankProperties(EnumFacing from) {
		// TODO: Handle slave state here.
		return tanks.getTankProperties(from);
	}

	public IFluidTanks getTanks()
	{
		// TODO: Handle slave state here.
		return tanks;
	}

	@Override
	public int getTankCount()
	{
		// TODO: Handle slave state here.
		return tanks.getTankCount();
	}

	@Override
	public FluidTank[] getFluidTanks()
	{
		// TODO: Handle slave state here.
		return tanks.getFluidTanks();
	}

	@Override
	public int getFluidAmountScaled(int scalar, int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluidAmountScaled(scalar, slot);
	}

	@Override
	public float getFluidAmountRate(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluidAmountRate(slot);
	}

	@Override
	public boolean isFluidTankFilled(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.isFluidTankFilled(slot);
	}

	@Override
	public boolean isFluidTankFull(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.isFluidTankFull(slot);
	}

	@Override
	public boolean isFluidTankEmpty(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.isFluidTankEmpty(slot);
	}

	@Override
	public int getFluidAmount(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluidAmount(slot);
	}

	@Override
	public FluidTank getFluidTank(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluidTank(slot);
	}

	@Override
	public FluidStack getFluidStack(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluidStack(slot);
	}

	@Override
	public FluidStack drainFluidTank(int slot, int amount, boolean shouldDrain)
	{
		// TODO: Handle slave state here.
		final FluidStack result = tanks.drainFluidTank(slot, amount, shouldDrain);
		if (shouldDrain && FluidTest.isValid(result)) markFluidDirty();
		return result;
	}

	@Override
	public int fillFluidTank(int slot, FluidStack fluid, boolean shouldFill)
	{
		// TODO: Handle slave state here.
		final int result = tanks.fillFluidTank(slot, fluid, shouldFill);
		if (shouldFill && result != 0) markFluidDirty();
		return result;
	}

	@Override
	public void setFluidStack(int slot, FluidStack stack)
	{
		// TODO: Handle slave state here.
		tanks.setFluidStack(slot, stack);
		markFluidDirty();
	}

	@Override
	public Fluid getFluid(int slot)
	{
		// TODO: Handle slave state here.
		return tanks.getFluid(slot);
	}

	@Override
	public void clearTank(int slot)
	{
		// TODO: Handle slave state here.
		tanks.clearTank(slot);
		markFluidDirty();
	}

    @SuppressWarnings("unchecked")
	@Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {

            if (facing == null) {
                facing = EnumFacing.UP;
            }

            return (T)new FluidHandlerBlockWrapper(this, facing);
        }
        
        return super.getCapability(capability, facing);
    }
	
	protected void readTanksFromNBT(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		FluidTanks masterFluidTanks;
		if( !(tanks instanceof FluidTanks) )
			throw new IllegalStateException("Shouldn't have been called.");
		masterFluidTanks = (FluidTanks)tanks;
		
		if (masterFluidTanks != null)
			masterFluidTanks.readFromNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_ITEM_READ)
	public void readFromNBTForItem_DeviceBase(NBTTagCompound nbt)
	{
		// NOTE: No slave call
//		super.readFromNBTForItem(nbt);
		readTanksFromNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_READ)
	public void readFromNBT_DeviceBase(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		readTanksFromNBT(nbt);
	}

	private void writeTanksToNBT(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		FluidTanks masterFluidTanks;
		if( !(tanks instanceof FluidTanks) )
			throw new IllegalStateException("Shouldn't have been called.");
		masterFluidTanks = (FluidTanks)tanks;
		
		if (masterFluidTanks != null)
			masterFluidTanks.writeToNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_ITEM_WRITE)
	public void writeToNBTForItem_DeviceBase(NBTTagCompound nbt)
	{
		// NOTE: No slave call
//		super.writeToNBTForItem(nbt);
		writeTanksToNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NBT_WRITE)
	public void writeToNBT_DeviceBase(NBTTagCompound nbt)
	{
		// NOTE: No slave call
		writeTanksToNBT(nbt);
	}

	@TileEventHandler(event=TileEventHandler.EventType.NETWORK_READ)
	public boolean readFromStream_FluidTanks(ByteBuf stream) throws IOException
	{
		// NOTE: No slave call
		FluidTanks masterFluidTanks;
		if( !(tanks instanceof FluidTanks) )
			throw new IllegalStateException("Shouldn't have been called.");
		masterFluidTanks = (FluidTanks)tanks;
		
		if (masterFluidTanks != null)
			masterFluidTanks.readFromStream(stream);
		return true;
	}

	@TileEventHandler(event=TileEventHandler.EventType.NETWORK_WRITE)
	public boolean writeToStream_FluidTanks(ByteBuf stream) throws IOException
	{
		// NOTE: No slave call
		FluidTanks masterFluidTanks;
		if( !(tanks instanceof FluidTanks) )
			throw new IllegalStateException("Shouldn't have been called.");
		masterFluidTanks = (FluidTanks)tanks;
		
		if (masterFluidTanks != null)
			masterFluidTanks.writeToStream(stream);
		return false;
	}
}
