package org.kotlin.example.chap06

import org.kotlin.example.chap05.List
import org.kotlin.example.chap06.Option.Companion.getDefault
import org.kotlin.example.chap06.Option.Companion.max

fun main() {
    val max1 = max(listOf(3, 5, 7, 2, 1)).getOrElse(::getDefault)
    println(max1)
//    val max2 = max(listOf()).getOrElse(::getDefault)
//    println(max2)

    val parseWithRadix: (Int) -> (String) -> Int = { radix ->
        { string ->
            Integer.parseInt(string ,radix)
        }
    }

    val parse16 = hLift(parseWithRadix(16))
    val list = List("4", "5", "6", "7", "8", "9", "A", "B")
    val result = Option.sequence(list.map(parse16))
    println(result)
}