package net.imglib2.img.list.access.volatiles.container;

import java.util.List;

import net.imglib2.img.list.access.container.AbstractList;
import net.imglib2.img.list.access.volatiles.VolatileListDataAccess;

/**
 * @author Igor Pisarev
 */
public abstract class AbstractVolatileList< T, A extends AbstractVolatileList< T, A > >
		extends AbstractList< T, A >
		implements VolatileListDataAccess< A >
{
	final protected boolean isValid;

	public AbstractVolatileList( final int numEntities, final boolean isValid )
	{
		super( numEntities );
		this.isValid = isValid;
	}

	public AbstractVolatileList( final List< T > data, final boolean isValid )
	{
		super( data );
		this.isValid = isValid;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	@Override
	public A createList( final int numEntities )
	{
		return createList( numEntities, true );
	}
}
