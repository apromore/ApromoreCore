package cs.ut.ui

import kotlin.Comparator as Comp

sealed class GridComparator

class Empty : GridComparator()

class ComparatorPair<T>(val asc: Comp<T>, val desc: Comp<T>) : GridComparator()