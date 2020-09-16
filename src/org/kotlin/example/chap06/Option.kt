package org.kotlin.example.chap06

import org.kotlin.example.chap05.List
import kotlin.math.pow

sealed class Option<out A> {
    abstract fun isEmpty(): Boolean
//    abstract fun <B> map(f: (A) -> B): Option<B>

    internal object None: Option<Nothing>() {
        override fun isEmpty() = true
        override fun toString(): String = "None"
        override fun equals(other: Any?): Boolean = other === None
        override fun hashCode(): Int = 0
//        override fun <B> map(f: (Nothing) -> B): Option<B> = None
    }

    internal data class Some<out A>(internal val value: A) : Option<A>() {
        override fun isEmpty(): Boolean = false
//        override fun <B> map(f: (A) -> B): Option<B> = Some(f(value))
    }

    fun getOrElse(default: @UnsafeVariance A): A = when (this) {
        is None -> default
        is Some -> value
    }

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is None -> default()
        is Some -> value
    }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> =
        map { _ -> this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> =
        flatMap { if (p(it)) this else None }

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse{ None }

    companion object {
        operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
            null -> None
            else -> Some(a)
        }

        fun max(list: kotlin.collections.List<Int>): Option<Int> = Option(list.max())

        fun getDefault(): Int = throw RuntimeException()

        fun <A, B, C> map2(
                oa: Option<A>,
                ob: Option<B>,
                f: (A) -> (B) -> C): Option<C> =
                oa.flatMap { a ->
                    ob.map { b ->
                        f(a)(b)
                    }
                }

        fun <A, B, C, D> map3(
                oa: Option<A>,
                ob: Option<B>,
                oc: Option<C>,
                f: (A) -> (B) -> (C) -> D): Option<D> =
                oa.flatMap { a ->
                    ob.flatMap { b ->
                        oc.map { c ->
                            f(a)(b)(c)
                        }
                    }
                }

//        fun <A> sequence(list: List<Option<A>>): Option<List<A>> =
//                list.foldRight(Option(List())) { x ->
//                    { y: Option<List<A>> -> map2(x, y) { a ->
//                        { b: List<A> -> b.cons(a) } }
//                    }
//                }

        fun <A, B> traverse(list: List<A>, f: (A) -> Option<B>): Option<List<B>> =
                list.foldRight(Option(List())) { x ->
                    { y: Option<List<B>> ->
                        map2(f(x), y) { a ->
                            { b: List<B> ->
                               b.cons(a)
                            }
                        }
                    }
                }

        fun <A> sequence(list: List<Option<A>>): Option<List<A>> =
                traverse(list) { x -> x }
    }
}

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = {
    try {
        it.map(f)
    } catch (e: Exception) {
        Option()
    }
}

fun <A, B> hLift(f: (A) -> B):(A) -> Option<B> = {
    try {
        Option(it).map(f)
    } catch (e: Exception) {
        Option()
    }
}

val mean: (kotlin.collections.List<Double>) -> Option<Double> = { list ->
    when {
        list.isEmpty() -> Option()
        else -> Option(list.sum() / list.size)
    }
}

val variance: (kotlin.collections.List<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { m ->
        mean(list.map { x ->
            (x - m).pow(2.0)
        })
    }
}