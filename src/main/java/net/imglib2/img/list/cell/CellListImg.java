package net.imglib2.img.list.cell;

import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.access.container.AbstractList;

public class CellListImg< T, A extends AbstractList< T, A > > extends AbstractCellListImg< T, A, Cell< A >, ListImg< Cell< A > > >
{
	private final CellListImgFactory< T > factory;

	public CellListImg( final CellListImgFactory< T > factory, final CellGrid grid, final ListImg< Cell< A > > imgOfCells )
	{
		super( grid, imgOfCells );
		this.factory = factory;
	}

	@Override
	public ImgFactory< T > factory()
	{
		return factory;
	}

	@Override
	public CellListImg< T, A > copy()
	{
		@SuppressWarnings( "unchecked" )
		final CellListImg< T, A > copy = ( CellListImg< T, A > ) factory().create( dimension, firstElement() );
		copyDataTo( copy );
		return copy;
	}
}
