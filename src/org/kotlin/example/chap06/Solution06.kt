package org.kotlin.example.chap06

import org.kotlin.example.chap06.Option.Companion.getDefault
import org.kotlin.example.chap06.Option.Companion.max

fun main() {
    val max1 = max(listOf(3, 5, 7, 2, 1)).getOrElse(::getDefault)
    println(max1)
    val max2 = max(listOf()).getOrElse(::getDefault)
    println(max2)
}