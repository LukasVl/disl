/*
 * Copyright 2015 Karel H�bl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta

import groovy.lang.Closure;

import org.junit.Test;

class SqlExpression extends SqlOperators {
	Object expression

	@Override
	public String toString() {
		getValue().toString()
	}
	
	protected Object getValue() {
		if (expression instanceof Closure) {
			return expression.call()
		}
		return expression
	}
	


}