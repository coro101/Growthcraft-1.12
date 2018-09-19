package growthcraft.core.shared.tileentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import growthcraft.core.shared.block.BlockFlags;
import growthcraft.core.shared.io.nbt.IAltNBTSerializable;
import growthcraft.core.shared.io.stream.IStreamable;
import growthcraft.core.shared.tileentity.event.TileEventFunction;
import growthcraft.core.shared.tileentity.event.TileEventHandler;
import growthcraft.core.shared.tileentity.event.TileEventHandlerMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Extend this base class if you just need a Base tile with the event system.
 *
 * Event handling system is a stripped version of the one seen in AE2, I've
 * copied the code for use in YATM, but I've ported it over to Growthcraft as
 * well.
 */
public abstract class GrowthcraftTileBase extends TileEntity implements IStreamable, IAltNBTSerializable
{
	protected static TileEventHandlerMap<GrowthcraftTileBase> HANDLERS = new TileEventHandlerMap<GrowthcraftTileBase>();
	
	protected boolean hasMaster;
	protected int masterOffsX;
	protected int masterOffsY;
	protected int masterOffsZ;
	
	public boolean hasMaster() {
		return hasMaster;
	}
	
	public GrowthcraftTileBase getMaster() {
		if( !hasMaster )
			return null;

		BlockPos masterPos = new BlockPos(pos.getX()+masterOffsX, pos.getY()+masterOffsY, pos.getZ()+masterOffsZ);
		TileEntity ent = world.getTileEntity(masterPos);
		if( ent == null )
			return null;
		if( !getClass().isAssignableFrom(ent.getClass()) )
			return null;
		return (GrowthcraftTileBase)ent;
	}
	
	protected void setMaster( GrowthcraftTileBase master ) {
		if( !getClass().isAssignableFrom(master.getClass()) )
			throw new IllegalArgumentException("Incompatible class.");
		BlockPos masterPos = master.getPos();
		
		hasMaster = true;
		masterOffsX = masterPos.getX() - pos.getX();
		masterOffsY = masterPos.getY() - pos.getY();
		masterOffsZ = masterPos.getZ() - pos.getZ();
	}

	public void markForUpdate()
	{
		IBlockState curState = getWorld().getBlockState(pos);
        world.markBlockRangeForRenderUpdate(pos, pos);
        world.notifyBlockUpdate(pos, curState, curState, BlockFlags.UPDATE_AND_SYNC);
	}

	public void markDirtyAndUpdate()
	{
		markDirty();
		markForUpdate();
	}
	
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if( this.getCapability(capability, facing) != null )
        	return true;
        return super.hasCapability(capability, facing);
    }

	protected List<TileEventFunction> getHandlersFor(@Nonnull TileEventHandler.EventType event)
	{
		return HANDLERS.getEventFunctionsForClass(getClass(), event);
	}

	@Override
	public final boolean writeToStream(ByteBuf stream)
	{
		// TODO: Handle slave state here.
		
		final List<TileEventFunction> handlers = getHandlersFor(TileEventHandler.EventType.NETWORK_WRITE);
		if (handlers != null)
		{
			for (TileEventFunction func : handlers)
			{
				func.writeToStream(this, stream);
			}
		}
		return false;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		final NBTTagCompound data = new NBTTagCompound();
		final ByteBuf stream = Unpooled.buffer();

		try
		{
			writeToStream(stream);
			if (stream.readableBytes() == 0)
			{
				return null;
			}
		}
		catch (Throwable t)
		{
			System.err.println(t);
		}


		// P, for payload
		data.setByteArray("P", stream.array());

		return new SPacketUpdateTileEntity(pos, 127, data);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return writeToNBT(tag);
	}

	@Override
	public final boolean readFromStream(ByteBuf stream)
	{
		// TODO: Handle slave state here.
		
		boolean shouldUpdate = false;
		final List<TileEventFunction> handlers = getHandlersFor(TileEventHandler.EventType.NETWORK_READ);
		if (handlers != null)
		{
			for (TileEventFunction func : handlers)
			{
				if (func.readFromStream(this, stream))
				{
					shouldUpdate = true;
				}
			}
		}
		return shouldUpdate;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		if (packet.getTileEntityType() == 127)
		{
			final NBTTagCompound tag = packet.getNbtCompound();
			boolean dirty = false;
			if (tag != null)
			{
				final ByteBuf stream = Unpooled.copiedBuffer(tag.getByteArray("P"));
				if (readFromStream(stream))
				{
					dirty = true;
				}
			}
			if (dirty) markForUpdate();
		}
	}

	public void readFromNBTForItem(NBTTagCompound tag)
	{
		// TODO: Make final and handle slave state here.
	}

	public void writeToNBTForItem(NBTTagCompound tag)
	{
		// TODO: Make final and handle slave state here.
	}

	@Override
	public final void readFromNBT(NBTTagCompound nbt)
	{
		// TODO: Handle slave state here.
		
		super.readFromNBT(nbt);
		final List<TileEventFunction> handlers = getHandlersFor(TileEventHandler.EventType.NBT_READ);
		if (handlers != null)
		{
			for (TileEventFunction func : handlers)
			{
				func.readFromNBT(this, nbt);
			}
		}
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		// TODO: Handle slave state here.
		
		super.writeToNBT(nbt);
		final List<TileEventFunction> handlers = getHandlersFor(TileEventHandler.EventType.NBT_WRITE);
		if (handlers != null)
		{
			for (TileEventFunction func : handlers)
			{
				func.writeToNBT(this, nbt);
			}
		}
		
		return nbt;
	}
}
