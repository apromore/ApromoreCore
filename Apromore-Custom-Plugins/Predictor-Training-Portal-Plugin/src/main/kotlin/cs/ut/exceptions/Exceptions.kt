/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package cs.ut.exceptions

/**
 * Sealed class that represents result of an action
 */
sealed class Either<out L, out R>

/**
 * Left part of the result, containing on the left side if it occurs and Nothing on the right side.
 *
 * @see Nothing
 */
data class Left<out L>(val error: L) : Either<L, Nothing>()

/**
 * Right part of the result containing value on the right side and Nothing on the left side
 *
 * @see Nothing
 */
data class Right<out R>(val result: R) : Either<Nothing, R>()

/**
 * Perform an operation that returns an Either
 *
 * @see Either
 */
inline fun <R> perform(f : () -> R) : Either<Exception, R> = try {
    Right(f())
} catch (e: Exception) {
    Left(e)
}