/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.img.list.cell;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.img.list.access.container.AbstractList;

/**
 * Abstract superclass for {@link Img} types that divide their underlying data
 * into cells.
 *
 * @author Mark Hiner
 * @author Tobias Pietzsch
 * @author Igor Pisarev
 */
public abstract class AbstractCellListImg<
				T,
				A extends AbstractList< T, A >,
				C extends Cell< A >,
				I extends RandomAccessible< C > & IterableInterval< C > >
		extends AbstractImg< T >
{
	protected final CellGrid grid;

	protected final I cells;

	public AbstractCellListImg( final CellGrid grid, final I imgOfCells )
	{
		super( grid.getImgDimensions() );
		this.grid = grid;
		this.cells = imgOfCells;
	}

	/**
	 * This interface is implemented by all samplers on the {@link AbstractCellImg}. It
	 * allows to ask for the cell the sampler is currently in.
	 */
	public interface CellListImgSampler< C >
	{
		/**
		 * @return the cell the sampler is currently in.
		 */
		public C getCell();
	}

	@Override
	public CellListCursor< T, A, C > cursor()
	{
		return new CellListCursor<>( this );
	}

	@Override
	public CellListLocalizingCursor< T, A, C > localizingCursor()
	{
		return new CellListLocalizingCursor<>( this );
	}

	@Override
	public CellListRandomAccess< T, A, C > randomAccess()
	{
		return new CellListRandomAccess<>( this );
	}

	@Override
	public CellListIterationOrder iterationOrder()
	{
		return new CellListIterationOrder( this );
	}

	/**
	 * Get the underlying image of cells which gives access to the individual
	 * {@link Cell}s through Cursors and RandomAccesses.
	 *
	 * @return the image of cells.
	 */
	public I getCells()
	{
		return cells;
	}

	/**
	 * Get the {@link CellGrid} which describes the layout of the
	 * {@link AbstractCellImg}. The grid provides the dimensions of the image, the
	 * number of cells in each dimension, and the dimensions of individual
	 * cells.
	 *
	 * @return the cell grid layout.
	 */
	public CellGrid getCellGrid()
	{
		return grid;
	}

	protected void copyDataTo( final AbstractCellListImg< T, A, C, ? > copy )
	{
		final CellListCursor< T, A, C > source = this.cursor();
		final CellListCursor< T, A, ? > target = copy.cursor();

		while ( source.hasNext() )
		{
			target.fwd();
			target.set( source.next() );
		}
	}
}
