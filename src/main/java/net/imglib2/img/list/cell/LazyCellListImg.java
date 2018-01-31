package net.imglib2.img.list.cell;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.img.list.AbstractLongListImg;
import net.imglib2.img.list.access.container.AbstractList;
import net.imglib2.img.list.cell.LazyCellListImg.LazyListCells;

/**
 * A {@link AbstractCellListImg} that obtains its Cells lazily when they are
 * accessed. Cells are obtained by a {@link Get} method that is provided by the
 * user. Typically this is some kind of cache.
 *
 * @param <T>
 *            the pixel type
 * @param <A>
 *            the underlying access type
 *
 * @author Tobias Pietzsch
 * @author Igor Pisarev
 */
public class LazyCellListImg< T, A extends AbstractList< T, A > >
		extends AbstractCellListImg< T, A, Cell< A >, LazyListCells< Cell< A > > >
{
	@FunctionalInterface
	public interface Get< T >
	{
		T get( long index );
	}

	public LazyCellListImg( final CellGrid grid, final Get< Cell< A > > get )
	{
		super( grid, new LazyListCells<>( grid.getGridDimensions(), get ) );
	}

	@Override
	public ImgFactory< T > factory()
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	@Override
	public Img< T > copy()
	{
		throw new UnsupportedOperationException( "not implemented yet" );
	}

	public static final class LazyListCells< T > extends AbstractLongListImg< T >
	{
		private final Get< T > get;

		public LazyListCells( final long[] dimensions, final Get< T > get )
		{
			super( dimensions );
			this.get = get;
		}

		@Override
		protected T get( final long index )
		{
			return get.get( index );
		}

		@Override
		protected void set( final long index, final T value )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public ImgFactory< T > factory()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Img< T > copy()
		{
			throw new UnsupportedOperationException();
		}
	}
}
