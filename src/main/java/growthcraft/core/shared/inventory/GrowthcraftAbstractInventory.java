package growthcraft.core.shared.inventory;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class GrowthcraftAbstractInventory implements IInventory, IInventoryWatcher {
	protected final Object parent;
	private final LinkedList<WeakReference<IInventoryWatcher>> observers = new LinkedList<>();
	
	GrowthcraftAbstractInventory(Object parent) {
		this.parent = parent;
	}
	
	@Override
	public void onInventoryChanged(IInventory inv, int index)
	{
		// First notify parent
		if (parent instanceof IInventoryWatcher)
		{
			((IInventoryWatcher)parent).onInventoryChanged(inv, index);
		}
		else if (parent instanceof IInventory)
		{
			((IInventory)parent).markDirty();
		}
		
		// Notify observers
/*		boolean requiresCleanup = false;
		for( WeakReference<IInventoryWatcher> ref : observers ) {
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null ) {
				requiresCleanup = true;
				continue;
			}
			invEvts.onInventoryChanged(inv, index);
		}
		if( requiresCleanup )
			clearDereferencedListeners();	// TODO: Find a better pattern to avoid this code duplication. */
		forEachObserver(listener->listener.onInventoryChanged(inv, index));
	}
	
	@Override
	public void onItemDiscarded(IInventory inv, ItemStack stack, int index, int discardedAmount) {
		// First notify parent
		if (parent instanceof IInventoryWatcher)
		{
			((IInventoryWatcher)parent).onItemDiscarded(this, stack, index, discardedAmount);
		}
		
		// Notify observers
		forEachObserver(listener->listener.onItemDiscarded(inv, stack, index, discardedAmount));
/*		for( WeakReference<IInventoryWatcher> ref : observers ) {
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null )
				continue;
			invEvts.onItemDiscarded(inv, stack, index, discardedAmount);
		}*/
		
	}

	private void forEachObserver(Consumer<IInventoryWatcher> consum) {
/*		boolean requiresCleanup = false;
		for( WeakReference<IInventoryWatcher> ref : observers ) {
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null ) {
				requiresCleanup = true;
				continue;
			}
			// invEvts.onInventoryChanged(inv, index);
			consum.accept(invEvts);
		}
		if( requiresCleanup )
			clearDereferencedListeners();	// TODO: Find a better pattern to avoid this code duplication. */
		
		Iterator<WeakReference<IInventoryWatcher>> iter = observers.iterator();
		while(iter.hasNext()) {
			WeakReference<IInventoryWatcher> ref = iter.next();
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null ) {
				// Clear dereferenced slot
				iter.remove();
				continue;
			}
			consum.accept(invEvts);
		}
	}
	
	private IInventoryWatcher findFirstObserver(Function<IInventoryWatcher, Boolean> testFct) {
		Iterator<WeakReference<IInventoryWatcher>> iter = observers.iterator();
		while(iter.hasNext()) {
			WeakReference<IInventoryWatcher> ref = iter.next();
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null ) {
				// Clear dereferenced slot
				iter.remove();
				continue;
			}
			if( testFct.apply(invEvts) )
				return invEvts;
		}
		return null;
	}
	
/*	public void clearDereferencedListeners() {
		Iterator<WeakReference<IInventoryWatcher>> iter = observers.iterator();
		while(iter.hasNext()) {
			WeakReference<IInventoryWatcher> ref = iter.next();
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null )
				iter.remove();
		}
	}*/
	
	public boolean isExistingListener(IInventoryWatcher listener) {
/*		for( WeakReference<IInventoryWatcher> ref : observers ) {
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null )
				continue;
			if( invEvts.equals(listener) )
				return true;
		}*/
		
/*		Iterator<WeakReference<IInventoryWatcher>> iter = observers.iterator();
		while(iter.hasNext()) {
			WeakReference<IInventoryWatcher> ref = iter.next();
			IInventoryWatcher invEvts = ref.get();
			if( invEvts == null ) {
				// Clear dereferenced slot
				iter.remove();
				continue;
			}
			if( invEvts.equals(listener) )
				return true;
		}
		return false;*/
		return findFirstObserver(obs->obs.equals(listener)) != null;
	}
	
	public void subscribeListener(IInventoryWatcher listener) {
		// NOTE: No cyclic reference check!
//		clearDereferencedListeners();
		if( isExistingListener(listener) )
			return;
		
		// Add new listener
		WeakReference<IInventoryWatcher> ref = new WeakReference<IInventoryWatcher>(listener);
		observers.add(ref);
	}
}
