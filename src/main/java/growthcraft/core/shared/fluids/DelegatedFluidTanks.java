package growthcraft.core.shared.fluids;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DelegatedFluidTanks implements IFluidTanks {
	
	private WeakReference<IFluidTanks> cachedFluidTanksRef;
	private final Supplier<IFluidTanks> delegatedSupplier;

	public DelegatedFluidTanks(Supplier<IFluidTanks> delegatedSupplier) {
		this.delegatedSupplier = delegatedSupplier;
	}
	
	public IFluidTanks getDelegated() {
		IFluidTanks tanks = null;
		if( cachedFluidTanksRef != null ) {
			tanks = cachedFluidTanksRef.get();
			if( tanks == null )
				cachedFluidTanksRef = null;
		}
		
		if( cachedFluidTanksRef == null ) {
			tanks = delegatedSupplier.get();
			if( tanks == null )
				throw new IllegalStateException("FluidTanks should be never null.");
			cachedFluidTanksRef = new WeakReference<IFluidTanks>(tanks);
		}
		return tanks;	// Is never null
	}
	
	@Override
	public FluidTank[] getFluidTanks() {
		return getDelegated().getFluidTanks();
	}

	@Override
	public FluidTank getFluidTank(int slot) {
		return getDelegated().getFluidTank(slot);
	}

	@Override
	public FluidStack getFluidStack(int slot) {
		return getDelegated().getFluidStack(slot);
	}

	@Override
	public int getFluidAmountScaled(int scalar, int slot) {
		return getDelegated().getFluidAmountScaled(scalar, slot);
	}

	@Override
	public float getFluidAmountRate(int slot) {
		return getDelegated().getFluidAmountRate(slot);
	}

	@Override
	public boolean isFluidTankFilled(int slot) {
		return getDelegated().isFluidTankFilled(slot);
	}

	@Override
	public boolean isFluidTankFull(int slot) {
		return getDelegated().isFluidTankFull(slot);
	}

	@Override
	public boolean isFluidTankEmpty(int slot) {
		return getDelegated().isFluidTankEmpty(slot);
	}

	@Override
	public int getFluidAmount(int slot) {
		return getDelegated().getFluidAmount(slot);
	}

	@Override
	public FluidStack drainFluidTank(int slot, int amount, boolean doDrain) {
		return getDelegated().drainFluidTank(slot, amount, doDrain);
	}

	@Override
	public int fillFluidTank(int slot, FluidStack fluid, boolean doFill) {
		return getDelegated().fillFluidTank(slot, fluid, doFill);
	}

	@Override
	public void setFluidStack(int slot, FluidStack stack) {
		getDelegated().setFluidStack(slot, stack);
	}

	@Override
	public Fluid getFluid(int slot) {
		return getDelegated().getFluid(slot);
	}

	@Override
	public void clearTank(int slot) {
		getDelegated().clearTank(slot);
	}

	@Override
	public int getTankCount() {
		return getDelegated().getTankCount();
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return getDelegated().getTankInfo(from);
	}

	@Override
	public IFluidTankProperties[] getTankProperties(EnumFacing from) {
		return getDelegated().getTankProperties(from);
	}

}
