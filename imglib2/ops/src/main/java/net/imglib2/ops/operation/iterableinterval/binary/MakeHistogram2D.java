/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.iterableinterval.binary;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.data.Histogram2D;
import net.imglib2.ops.operation.BinaryObjectFactory;
import net.imglib2.ops.operation.BinaryOutputOperation;
import net.imglib2.type.numeric.RealType;

/**
 * Felix Schoenenberger (University of Konstanz)
 */
public final class MakeHistogram2D<T extends RealType<T>>
		implements
		BinaryOutputOperation<IterableInterval<T>, IterableInterval<T>, Histogram2D> {

	@Override
	public final Histogram2D compute(final IterableInterval<T> op0,
			final IterableInterval<T> op1, final Histogram2D r) {
		if (!op0.iterationOrder().equals(op1.iterationOrder())) {
			throw new IllegalStateException(
					"IterationOrders are not compatible in Histogram2D");
		}
		Cursor<T> opc0 = op0.cursor();
		Cursor<T> opc1 = op1.cursor();
		r.clear();
		while (opc0.hasNext()) {
			opc0.next();
			opc1.next();
			r.incByValue(opc0.get().getRealDouble(), opc1.get().getRealDouble());
		}
		return r;
	}

	@Override
	public BinaryOutputOperation<IterableInterval<T>, IterableInterval<T>, Histogram2D> copy() {
		return new MakeHistogram2D<T>();
	}

	@Override
	public BinaryObjectFactory<IterableInterval<T>, IterableInterval<T>, Histogram2D> bufferFactory() {
		return new BinaryObjectFactory<IterableInterval<T>, IterableInterval<T>, Histogram2D>() {

			@Override
			public Histogram2D instantiate(IterableInterval<T> inputA,
					IterableInterval<T> inputB) {
				return new Histogram2D(inputA.iterator().next()
						.createVariable());
			}
		};
	}
}
