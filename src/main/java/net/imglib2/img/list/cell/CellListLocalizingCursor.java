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

import net.imglib2.AbstractLocalizingCursor;
import net.imglib2.Cursor;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.list.access.container.AbstractList;

/**
 * Localizing {@link Cursor} on a {@link AbstractListCellImg}.
 *
 * @author Tobias Pietzsch
 * @author Igor Pisarev
 */
public class CellListLocalizingCursor< T, A extends AbstractList< T, A >, C extends Cell< A > >
	extends AbstractLocalizingCursor< T >
	implements AbstractCellListImg.CellListImgSampler< C >
{
	protected final Cursor< C > cursorOnCells;

	protected long[] currentCellMin;

	protected long[] currentCellMax;

	protected int lastIndexInCell;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int index;

	/**
	 * Caches cursorOnCells.hasNext().
	 */
	protected boolean isNotLastCell;

	protected CellListLocalizingCursor( final CellListLocalizingCursor< T, A, C > cursor )
	{
		super( cursor.numDimensions() );

		this.cursorOnCells = cursor.cursorOnCells.copyCursor();
		this.currentCellMin = cursor.currentCellMin;
		this.currentCellMax = cursor.currentCellMax;

		isNotLastCell = cursor.isNotLastCell;
		lastIndexInCell = cursor.lastIndexInCell;
		for ( int d = 0; d < n; ++d )
			position[ d ] = cursor.position[ d ];
		index = cursor.index;
	}

	public CellListLocalizingCursor( final AbstractCellListImg< T, A, C, ? > img )
	{
		super( img.numDimensions() );

		this.cursorOnCells = img.getCells().cursor();
//		this.currentCellMin = new long[ n ];
//		this.currentCellMax = new long[ n ];

		reset();
	}

	@Override
	public C getCell()
	{
		return cursorOnCells.get();
	}

	@Override
	public T get()
	{
		return getCell().getData().getValue( index );
	}

	public void set( final T value )
	{
		getCell().getData().setValue( index, value );
	}

	@Override
	public CellListLocalizingCursor< T, A, C > copy()
	{
		return new CellListLocalizingCursor<>( this );
	}

	@Override
	public CellListLocalizingCursor< T, A, C > copyCursor()
	{
		return copy();
	}

	@Override
	public boolean hasNext()
	{
		return isNotLastCell || ( index < lastIndexInCell );
	}

	@Override
	public void jumpFwd( final long steps )
	{
		long newIndex = index + steps;
		while ( newIndex > lastIndexInCell )
		{
			newIndex -= lastIndexInCell + 1;
			cursorOnCells.fwd();
			isNotLastCell = cursorOnCells.hasNext();
			lastIndexInCell = ( int ) ( getCell().size() - 1 );
		}

		final C cell = getCell();
		currentCellMin = cell.min;
		currentCellMax = cell.max;
//		cell.min( currentCellMin );
//		cell.max( currentCellMax );

		index = ( int ) newIndex;
		cell.indexToGlobalPosition( index, position );
	}

	@Override
	public void fwd()
	{
		if ( ++index > lastIndexInCell )
		{
			moveToNextCell();
			index = 0;
		}

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > currentCellMax[ d ] )
				position[ d ] = currentCellMin[ d ];
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
		index = -1;
	}

	/**
	 * Move cursor right before the first element of the next cell. Update type,
	 * position, and index variables.
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		isNotLastCell = cursorOnCells.hasNext();
		final C cell = getCell();

		lastIndexInCell = ( int ) ( cell.size() - 1 );
		currentCellMin = cell.min;
		currentCellMax = cell.max;
//		cell.min( currentCellMin );
//		cell.max( currentCellMax );

		position[ 0 ] = currentCellMin[ 0 ] - 1;
		for ( int d = 1; d < n; ++d )
			position[ d ] = currentCellMin[ d ];
	}
}
