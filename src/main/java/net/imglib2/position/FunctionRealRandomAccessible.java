/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.position;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 * A {@link RealRandomAccessible} that generates a function value for each
 * position in real coordinate space by side-effect using a {@link BiConsumer}.
 *
 * @author Stephan Saalfeld
 */
public class FunctionRealRandomAccessible< T > extends AbstractFunctionEuclideanSpace< RealLocalizable, T > implements RealRandomAccessible< T >
{
	public FunctionRealRandomAccessible(
			final int n,
			final BiConsumer< RealLocalizable, T > function,
			final Supplier< T > typeSupplier )
	{
		super( n, function, typeSupplier );
	}

	public FunctionRealRandomAccessible(
			final int n,
			final Supplier< BiConsumer< RealLocalizable, T > > function,
			final Supplier< T > typeSupplier )
	{
		super( n, function, typeSupplier );
	}

	public class RealFunctionRealRandomAccess extends RealPoint implements RealRandomAccess< T >
	{
		private final T t = typeSupplier.get();
		private final BiConsumer< RealLocalizable, T > function = functionSupplier.get();

		public RealFunctionRealRandomAccess()
		{
			super( FunctionRealRandomAccessible.this.n );
		}

		@Override
		public T get()
		{
			function.accept( this, t );
			return t;
		}

		@Override
		public RealFunctionRealRandomAccess copy()
		{
			return new RealFunctionRealRandomAccess();
		}

		@Override
		public RealFunctionRealRandomAccess copyRealRandomAccess()
		{
			return copy();
		}
	}

	@Override
	public RealFunctionRealRandomAccess realRandomAccess()
	{
		return new RealFunctionRealRandomAccess();
	}

	@Override
	public RealFunctionRealRandomAccess realRandomAccess( final RealInterval interval )
	{
		return realRandomAccess();
	}
}