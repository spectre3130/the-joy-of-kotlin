package org.kotlin.example.chap05

fun main() {
    val list = List(1, 2, 3, 4, 5)
    println(list.toString())
    println(list.cons(0))
    println(list.setHead(0))
//    println(list.drop(3))
    println(List.drop(list, 3))
    println(list.drop(4).setHead(0))
}