package org.kotlin.example.chap03

fun main(args: Array<String>) {
    val solution: Solution = Solution()
    fun square(n: Int) = n * n
    fun triple(n: Int) = n * 3
    val squareOfTriple = solution.compose(::square, ::triple)
    println(squareOfTriple(2))

    val squareOfTriple2 = solution.compose2<Int, Int, Int>(::square, ::triple)
    println(squareOfTriple(2))

    println(solution.add(2)(3))
    println(solution.multiply(2)(3))

    val square2: IntUnaryOp = { it * it }
    val triple2: IntUnaryOp = { it * 3 }

    val squareOfTriple3 = solution.compose4(square2)(triple2)
    println(squareOfTriple3(2))
    val squareOfTriple4 = solution.higherCompose<Int, Int, Int>()(square2)(triple2)
    println(squareOfTriple4(2))
    val squareOfTriple5 = solution.higherAndThen<Int, Int, Int>()(triple2)(square2)
    println(squareOfTriple5(2))

    println(solution.curried<String, String, String, String>()("A")("B")("C")("D"))

    val add9percentTax = solution.addTax(9.0)
    val priceIncludingTax = add9percentTax(1000.0)
    println(priceIncludingTax)

    val addTaxSwap = solution.swatArgs(solution.addTax2)
    val add10percentTax = addTaxSwap(10.0)
    val priceIncludingTax2 = add10percentTax(1000.0)
    println(priceIncludingTax2)
}

class Solution {
    fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { x -> f(g(x)) }

    fun <T, U, V> compose2(f: (U) -> V, g: (T) -> U): (T) -> V = { f(g(it)) }

    val add: IntBinOp = { a -> { b -> a + b } }
    val multiply: IntBinOp = { a -> { b -> a * b } }

    val compose3: ((Int) -> Int) -> ((Int) -> Int) -> (Int) -> Int = { x -> { y -> { z -> x(y(z)) } } }
    val compose4: (IntUnaryOp) -> (IntUnaryOp) -> IntUnaryOp = { x -> { y -> { z -> x(y(z)) } } }

    fun <T, U, V> higherCompose(): ((U) -> V) -> ((T) -> U) -> (T) -> V = { f ->
        { g ->
            { x -> f(g(x)) }
        }
    }

    fun <T, U, V> higherCompose2() = { f: (U) -> V ->
        { g: (T) -> U ->
            { x: T ->
                f(g(x))
            }
        }
    }

    fun <T, U, V> higherAndThen() =
        { f: (T) -> U ->
            { g: (U) -> V ->
                { x: T ->
                    g(f(x))
                }
            }
        }

    fun <A, B, C> partialA(a: A, f: (A) -> (B) -> C): (B) -> C = f(a)
    fun <A, B, C> partialB(b: B, f: (A) -> (B) -> C): (A) -> C =
        { a: A ->
            f(a)(b)
        }

    fun <A, B, C, D> func(a: A, b: B, c: C, d: D): String = "$a, $b, $c, $d"

    fun <A, B, C, D> curried() =
        { a: A ->
            { b: B ->
                { c: C ->
                    { d: D ->
                        "$a, $b, $c, $d"
                    }
                }
            }
        }

    fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
        { a: A ->
            { b: B ->
                f(a, b)
            }
        }

    val addTax: (Double) -> (Double) -> Double =
        { x ->
            { y ->
               y + y / 100 * x
            }
        }

    val addTax2: (Double) -> (Double) -> Double =
        { x ->
            { y ->
                x + x / 100 * y
            }
        }

    fun <T, U, V> swatArgs(f: (T) -> (U) -> V): (U) -> (T) -> V =
        { u -> { t -> f(t)(u) } }
}
typealias IntBinOp = (Int) -> (Int) -> Int
typealias IntUnaryOp = (Int) -> Int
