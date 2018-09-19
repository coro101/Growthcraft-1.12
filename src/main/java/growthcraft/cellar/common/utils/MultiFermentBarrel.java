package growthcraft.cellar.common.utils;

import growthcraft.cellar.common.block.BlockFermentBarrel;
import growthcraft.cellar.shared.init.GrowthcraftCellarBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class MultiFermentBarrel {
/*	public static boolean canMakeBigBarrelStructure(IBlockAccess world, BlockPos pos) {
		if( !isValidBigBarrelStructure(world, pos) )
			return false;

		if( (pos.getX() & 0x1) != 0 ) {
			if( isValidBigBarrelStructure(world, pos.east(2)) )
				return false;
			if( isValidBigBarrelStructure(world, pos.west(2)) )
				return false;
		}
		if( (pos.getY() & 0x1) != 0 ) {
			if( isValidBigBarrelStructure(world, pos.up(2)) )
				return false;
			if( isValidBigBarrelStructure(world, pos.down(2)) )
				return false;
		}
		if( (pos.getZ() & 0x1) != 0 ) {
			if( isValidBigBarrelStructure(world, pos.south(2)) )
				return false;
			if( isValidBigBarrelStructure(world, pos.north(2)) )
				return false;
		}
		
		return true;
	} */
	
	public static boolean isValidBigBarrelStructure(IBlockAccess world, BlockPos pos) {
		// Check if center is occupied
		{
			IBlockState state = world.getBlockState(pos);
			if( state.getBlock() != Blocks.AIR )
				return false;
		}
		
		// Determine direction
		EnumFacing direction = null;
		{
			IBlockState stateNorth = world.getBlockState(pos.north());
			direction = getDirectionFrom(stateNorth);
			if( direction == null )
				return false;
			
			IBlockState stateSouth = world.getBlockState(pos.south());
			IBlockState stateEast = world.getBlockState(pos.east());
			IBlockState stateWest = world.getBlockState(pos.west());
			IBlockState stateUp = world.getBlockState(pos.up());
			IBlockState stateDown = world.getBlockState(pos.down());
			IBlockState states[] = new IBlockState[] {stateSouth, stateEast, stateWest, stateUp, stateDown};
			if( !isAllSameDirection(states, direction) )
				return false;
		}
		
		// Check if all remaining blocks are correctly oriented
		{
			BlockPos east = pos.east();
			BlockPos west = pos.west();
			if( !isOrientedCorrectly8(world, east, direction) )
				return false;
			if( !isOrientedCorrectly4(world, pos, direction) )
				return false;
			if( !isOrientedCorrectly8(world, west, direction) )
				return false;
		}
		
		return true;
	}
	
	private static EnumFacing getDirectionFrom(IBlockState state) {
		// TODO: Fluid check! Barrels should be either empty or have same fluid
		if( state.getBlock() != GrowthcraftCellarBlocks.fermentBarrel.getBlock() )
			return null;
		return state.getValue(BlockFermentBarrel.TYPE_BARREL_ROTATION).toFacing();
	}
	
	private static boolean isAllSameDirection(IBlockState[] states, EnumFacing direction) {
		for( IBlockState s : states ) {
			EnumFacing blockDirection = getDirectionFrom(s);
			if( blockDirection == null )
				return false;
			if( !direction.equals(blockDirection) )
				return false;
		}
		return true;
	}
	
	private static boolean isOrientedCorrectly4(IBlockAccess world, BlockPos pos, EnumFacing direction) {
		BlockPos up = pos.up();
		BlockPos down = pos.down();
		IBlockState stateUpNorth = world.getBlockState(up.north());
		IBlockState stateUpSouth = world.getBlockState(up.south());
		IBlockState stateDownNorth = world.getBlockState(down.north());
		IBlockState stateDownSouth = world.getBlockState(down.south());
		IBlockState states[] = new IBlockState[] {stateUpNorth, stateUpSouth, stateDownNorth, stateDownSouth};
		
		return isAllSameDirection(states, direction);
	}
	
	private static boolean isOrientedCorrectly8(IBlockAccess world, BlockPos pos, EnumFacing direction) {
		if( !isOrientedCorrectly4(world, pos, direction) )
			return false;
		IBlockState stateUp = world.getBlockState(pos.up());
		IBlockState stateDown = world.getBlockState(pos.down());
		IBlockState stateNorth = world.getBlockState(pos.north());
		IBlockState stateSouth = world.getBlockState(pos.south());
		IBlockState states[] = new IBlockState[] {stateUp, stateDown, stateNorth, stateSouth};
		
		return isAllSameDirection(states, direction);
	}
	
	public static class BlockStateRegion {
		private final BlockPos pMin;
		private final BlockPos pMax;
		private final IBlockState[] states;
		
		public BlockStateRegion(BlockPos pA, BlockPos pB) {
			// TODO: Refactor me: Move to utils for bounding boxes or find an existing equivalent. 
			int minX = Math.min(pA.getX(), pB.getX());
			int minY = Math.min(pA.getY(), pB.getY());
			int minZ = Math.min(pA.getZ(), pB.getZ());
			pMin = new BlockPos(minX, minY, minZ);

			int maxX = Math.max(pA.getX(), pB.getX());
			int maxY = Math.max(pA.getY(), pB.getY());
			int maxZ = Math.max(pA.getZ(), pB.getZ());
			pMax = new BlockPos(maxX, maxY, maxZ);
			
			states = new IBlockState[getSizeX()*getSizeY()*getSizeZ()];
		}
		
		public BlockStateRegion fillBy(IBlockAccess world) {
			int idx = 0; 
			for( int iY = pMin.getY(); iY <= pMax.getY(); iY ++ ) {
				for( int iZ = pMin.getZ(); iZ <= pMax.getZ(); iZ ++ ) {
					for( int iX = pMin.getX(); iX <= pMax.getX(); iX ++ ) {
						states[idx ++] = world.getBlockState(new BlockPos(iX, iY, iZ)); 
					}
				}
			}
			
			return this;
		}
		
		public int getSizeX() {
			return pMax.getX()-pMin.getX()+1;
		}

		public int getSizeY() {
			return pMax.getY()-pMin.getY()+1;
		}
		
		public int getSizeZ() {
			return pMax.getZ()-pMin.getZ()+1;
		}
		
		public boolean isInBounds(BlockPos pos) {
			if( pos.getX() < pMin.getX() || pos.getX() > pMax.getX() ||
				pos.getY() < pMin.getY() || pos.getY() > pMax.getY() ||
				pos.getZ() < pMin.getZ() || pos.getZ() > pMax.getZ() )
				return false;
			return true;
		}

		public BlockStateRegion setState(BlockPos pos, IBlockState state) {
			if( !isInBounds(pos) )
				throw new IllegalArgumentException("Point out of bounds of the region.");
			states[getLocalIndexFor(pos)] = state;
			return this;
		}
		
		public IBlockState getState(BlockPos pos) {
			if( !isInBounds(pos) )
				throw new IllegalArgumentException("Point out of bounds of the region.");
			return states[getLocalIndexFor(pos)];
		}
		
		private int getLocalIndexFor(BlockPos pos) {
			int szX = getSizeX();
			int szZ = getSizeZ();
			return (pos.getY() - pMin.getY())*szX*szZ + (pos.getZ() - pMin.getZ())*szX + (pos.getX() - pMin.getX());
		}
	}
}
