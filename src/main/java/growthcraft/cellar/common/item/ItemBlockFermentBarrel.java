package growthcraft.cellar.common.item;

import growthcraft.cellar.common.block.BlockFermentBarrel;
import growthcraft.cellar.common.utils.MultiFermentBarrel;
import growthcraft.cellar.common.utils.MultiFermentBarrel.BlockStateRegion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockFermentBarrel extends ItemBlock {
	
	private final Block bigBarrelPartBlock; 

	public ItemBlockFermentBarrel(BlockFermentBarrel smallBarrelBlock, Block bigBarrelPartBlock) {
		super(smallBarrelBlock);
		this.bigBarrelPartBlock = bigBarrelPartBlock;
	}

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	// NOTE: Not changed now
    	
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                iblockstate1 = worldIn.getBlockState(pos);
                SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
    
    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
    	// Get a snapshot of world
    	BlockStateRegion region = new BlockStateRegion(pos.west(2).north(2).down(2), pos.east(2).south(2).up(2))
    									.fillBy(world)
    									.setBlockState(pos, newState);
    	
    	// Decide whether it is a huge barrel or not
		BlockPos bigBarrelPos = null; 
    	{
    		for( int iY = pos.getY()-1; iY <= pos.getY()+1; iY ++ ) {
    			for( int iZ = pos.getZ()-1; iZ <= pos.getZ()+1; iZ ++ ) {
    				for( int iX = pos.getX()-1; iX <= pos.getX()+1; iX ++ ) {
    					if( iX == 0 || iY == 0 || iZ == 0 )
    						continue; // there is a gap in the center
    					BlockPos iPos = new BlockPos(iX, iY, iZ);
    					if( MultiFermentBarrel.isValidBigBarrelStructure(region, iPos ) ) {
    						if( bigBarrelPos != null )
    							return false;
    						bigBarrelPos = iPos;
    					}
    				}
    			}
    		}
    	}
    	
    	// Place barrel
    	if( bigBarrelPos == null ) {
    		// It is a small barrel
            if (!world.setBlockState(pos, newState, 11)) return false;

            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == this.block)
            {
                setTileEntityNBT(world, player, pos, stack);
                this.block.onBlockPlacedBy(world, pos, state, player, stack);

                if (player instanceof EntityPlayerMP)
                    CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
        }
    	else {
    		// Gather Tile entities from region and aggregate them to a single master entity
    		// ....
    		
    		// 
    	}

        return true;
    }
}


