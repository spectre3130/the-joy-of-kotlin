package org.kotlin.example.chap06

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

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }

    fun <B> flatMap(f: (A)-> Option<B>): Option<B> = map(f).getOrElse{ None }

    companion object {
        operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
            null -> None
            else -> Some(a)
        }

        fun max(list: List<Int>): Option<Int> = Option(list.max())

        fun getDefault(): Int = throw RuntimeException()
    }
}