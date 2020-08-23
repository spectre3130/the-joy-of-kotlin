package org.kotlin.example.chap05

fun main() {
    val list = List(1, 2, 3, 4, 5)
    println(list.toString())
    println(list.cons(0))
    println(list.setHead(0))
//    println(list.drop(3))
    println(List.drop(list, 3))
    println(list.drop(4).setHead(0))
    val list1 = List('a', '_', 'l', 'o', 'n', 'g')
    val list2 = List('a', '_', 'l', 'i', 's', 't')
    println(list1.concat(list2.drop(1)))
    println(list.reverse().drop(1).reverse())
    println(List.sum(list))
}