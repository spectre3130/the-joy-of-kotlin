package org.kotlin.example.chap05

import java.lang.IllegalStateException

sealed class List<A> {
    abstract fun isEmpty(): Boolean
    abstract fun setHead(a: A): List<A>

    private object Nil: List<Nothing>() {
        override fun isEmpty() = true
        override fun toString() = "[NIL]"
        override fun setHead(a: Nothing): List<Nothing> = throw IllegalStateException("setHead called on an empty list")
    }

    private class Cons<A>(internal val head: A, internal val tail: List<A>): List<A>() {
        override fun isEmpty() = false
        override fun toString(): String = "[${toString("", this)}NIL]"
        override fun setHead(a: A): List<A> = tail.cons(a)

        private tailrec fun toString(acc: String, list: List<A>): String =
            when (list) {
                is Nil -> acc
                is Cons -> toString("$acc${list.head}, ", list.tail)
            }
    }

    fun cons(a: A): List<A> = Cons(a, this)

    fun drop(n: Int): List<A> = drop(this, n)

//    fun drop(n: Int): List<A> {
//        tailrec fun drop(n: Int, list: List<A>): List<A> =
//                if (n <= 0) list else when (list) {
//                    is Cons -> drop(n - 1, list.tail)
//                    Nil -> list
//                }
//        return drop(n, this)
//    }

    companion object {
        tailrec fun <A> drop(list: List<A>, n: Int): List<A> = when (list) {
            Nil -> list
            is Cons -> if (n <= 0) list else drop(list.tail, n - 1)
        }

        tailrec fun <A> dropWhile(list: List<A>, p: (A) -> Boolean): List<A> = when (list) {
            Nil -> list
            is Cons -> if (p(list.head)) list else dropWhile(list.tail, p)
        }

        operator
        fun <A> invoke(vararg az: A): List<A> =
            az.foldRight(Nil as List<A>) { a: A, list: List<A> ->
                Cons(a, list)
            }
    }
}