package org.kotlin.example.chap05

import java.lang.IllegalStateException

sealed class List<out A> {
    abstract fun isEmpty(): Boolean
    abstract fun setHead(a: @UnsafeVariance A): List<A>

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

    fun cons(a: @UnsafeVariance A): List<A> = Cons(a, this)

    fun drop(n: Int): List<A> = drop(this, n)

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B = foldLeft(identity, this, f)

    fun concat(list: List<@UnsafeVariance A>): List<A> = concat(this, list)

    fun reverse(): List<A> = foldLeft(invoke(), { acc -> { acc.cons(it) } })

    fun <B> foldRight(identity: B, f: (A) -> (B) -> (B)): B = this.reverse().foldLeft(identity) { acc -> { y -> f(y)(acc) } }

    fun <B> coFoldRight(identity: B, f: (A) -> (B) -> (B)): B = coFoldRight(identity, this.reverse(), identity, f)

    fun length(): Int = foldLeft(0) { acc -> { acc + 1 } }

    fun <A> concatViaFoldLeft(list1: List<A>, list2: List<A>): List<A> = foldRight(list1, list2) { x -> { acc -> Cons(x, acc) } }

    fun <B> map(f: (A) -> B): List<B> = foldLeft(Nil) { acc: List<B> -> { h: A -> Cons(f(h), acc) } }.reverse()

//    fun filter(p: (A) -> Boolean): List<A> = coFoldRight(Nil) { h -> { acc: List<A> -> if (p(h)) Cons(h, acc) else acc } }

    fun <B> flatMap(f: (A) -> List<B>): List<B> = flatten(map(f))

    fun filter(p: (A) -> Boolean): List<A> = flatMap { a -> if (p(a)) List(a) else Nil }

    companion object {
        tailrec fun <A> drop(list: List<A>, n: Int): List<A> = when (list) {
            Nil -> list
            is Cons -> if (n <= 0) list else drop(list.tail, n - 1)
        }

        tailrec fun <A> dropWhile(list: List<A>, p: (A) -> Boolean): List<A> = when (list) {
            Nil -> list
            is Cons -> if (p(list.head)) list else dropWhile(list.tail, p)
        }

        tailrec fun <A, B> foldLeft(acc: B, list: List<A>, f: (B) -> (A) -> B): B = when (list) {
            Nil -> acc
            is Cons -> foldLeft(f(acc)(list.head), list.tail, f)
        }

        tailrec fun <A> reverse(acc: List<A>, list: List<A>): List<A> = when (list) {
            Nil -> acc
            is Cons -> reverse(acc.cons(list.head), list.tail)
        }

        private tailrec fun <A, B> coFoldRight(acc: B, list: List<A>, identity: B, f: (A) -> (B) -> B): B = when (list) {
            Nil -> acc
            is Cons -> coFoldRight(f(list.head)(acc), list.tail, identity, f)
        }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = when (list1) {
            Nil -> list2
            is Cons -> Cons(list1.head, concat(list1.tail, list2))
        }

        fun <A, B> foldRight(list: List<A>, identity: B, f: (A) -> (B) -> B): B = when (list) {
            Nil -> identity
            is Cons -> f(list.head)(foldRight(list.tail, identity, f))
        }

        fun <A> flatten(list: List<List<A>>): List<A> = list.coFoldRight(Nil) { x -> x::concat }

        fun sum(list: List<Int>): Int = list.foldLeft(0, { acc -> { y -> acc + y } })

        fun product(list: List<Double>): Double = list.foldLeft(1.0, { acc -> { y -> acc * y } })

        fun triple(list: List<Int>): List<Int> = foldRight(list, invoke()) { h -> { acc: List<Int> -> acc.cons(h * 3) } }

        fun doubleToString(list: List<Double>): List<String> = foldRight(list, invoke()) { h -> { acc: List<String> -> acc.cons(h.toString()) } }

        operator
        fun <A> invoke(vararg az: A): List<A> =
            az.foldRight(Nil as List<A>) { a: A, list: List<A> ->
                Cons(a, list)
            }
    }
}