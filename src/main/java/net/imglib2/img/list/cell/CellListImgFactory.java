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

import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListLocalizingCursor;
import net.imglib2.img.list.access.container.AbstractList;
import net.imglib2.img.list.access.container.PlainList;
import net.imglib2.util.Intervals;

/**
 * Factory for creating {@link AbstractCellListImg CellListImgs}. The cell dimensions
 * for a standard cell can be supplied in the constructor of the factory. If no
 * cell dimensions are given, the factory creates cells of size <em>10 x 10 x
 * ... x 10</em>.
 *
 * @author Tobias Pietzsch
 * @author Igor Pisarev
 */
public class CellListImgFactory< T > extends ImgFactory< T >
{
	private final int[] defaultCellDimensions;

	public CellListImgFactory()
	{
		this( 10 );
	}

	public CellListImgFactory( final int... cellDimensions )
	{
		defaultCellDimensions = cellDimensions.clone();
		verifyDimensions( defaultCellDimensions );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that no
	 * dimension is less than 1. Throw {@link IllegalArgumentException}
	 * otherwise.
	 *
	 * @param dimensions
	 * @throws IllegalArgumentException
	 */
	public static void verifyDimensions( final int[] dimensions ) throws IllegalArgumentException
	{
		if ( dimensions == null )
			throw new IllegalArgumentException( "dimensions == null" );

		if ( dimensions.length == 0 )
			throw new IllegalArgumentException( "dimensions.length == 0" );

		for ( int d = 0; d < dimensions.length; d++ )
			if ( dimensions[ d ] <= 0 )
				throw new IllegalArgumentException( "dimensions[ " + d + " ] <= 0" );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that no
	 * dimension is less than 1. Throw {@link IllegalArgumentException}
	 * otherwise.
	 *
	 * @param dimensions
	 * @throws IllegalArgumentException
	 */
	public static void verifyDimensions( final long dimensions[] ) throws IllegalArgumentException
	{
		if ( dimensions == null )
			throw new IllegalArgumentException( "dimensions == null" );

		if ( dimensions.length == 0 )
			throw new IllegalArgumentException( "dimensions.length == 0" );

		for ( int d = 0; d < dimensions.length; d++ )
			if ( dimensions[ d ] <= 0 )
				throw new IllegalArgumentException( "dimensions[ " + d + " ] <= 0" );
	}

	/**
	 * Computes cell size array by truncating or expanding
	 * {@code defaultCellDimensions} to length {@code n}. Then verifies that a
	 * cell does not contain more than {@code Integer.MAX_VALUE} entities.
	 *
	 * @param defaultCellDimensions
	 * @param n
	 * @param entitiesPerPixel
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static int[] getCellDimensions( final int[] defaultCellDimensions, final int n  ) throws IllegalArgumentException
	{
		final int[] cellDimensions = new int[ n ];
		final int max = defaultCellDimensions.length - 1;
		for ( int i = 0; i < n; i++ )
			cellDimensions[ i ] = defaultCellDimensions[ ( i < max ) ? i : max ];

		final long numEntities = Intervals.numElements( cellDimensions );
		if ( numEntities > Integer.MAX_VALUE )
			throw new IllegalArgumentException( "Number of entities in cell too large. Use smaller cell size." );

		return cellDimensions;
	}

	@Override
	public CellListImg< T, ? > create( final long[] dim, final T type )
	{
		return createInstance( new PlainList<>( 0 ), dim );
	}

	@Override
	public < S > ImgFactory< S > imgFactory( final S type )
	{
		return new CellListImgFactory<>( defaultCellDimensions );
	}

	private < A extends AbstractList< T, A > >
			CellListImg< T, A >
			createInstance( final A creator, final long[] dimensions )
	{
		verifyDimensions( dimensions );

		final int n = dimensions.length;
		final int[] cellDimensions = getCellDimensions( defaultCellDimensions, n );

		final CellGrid grid = new CellGrid( dimensions, cellDimensions );
		final long[] gridDimensions = new long[ grid.numDimensions() ];
		grid.gridDimensions( gridDimensions );

		final Cell< A > type = new Cell<>( new int[] { 1 }, new long[] { 1 }, null );
		final ListImg< Cell< A > > cells = new ListImg<>( gridDimensions, type );

		final long[] cellGridPosition = new long[ n ];
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		final ListLocalizingCursor< Cell< A > > cellCursor = cells.localizingCursor();
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			grid.getCellDimensions( cellGridPosition, cellMin, cellDims );
			final A data = creator.createList( ( int ) Intervals.numElements( cellDims ) );
			cellCursor.set( new Cell<>( cellDims, cellMin, data ) );
		}

		return new CellListImg<>( this, grid, cells );
	}
}
