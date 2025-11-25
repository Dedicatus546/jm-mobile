package com.par9uet.jm

class P(var n: Int) {
    val t = n * 2
    val m: Int get() = n * 4
    var k
        get() = n * 4
        set(value) {
            n = value / 2
        }

    init {
        println("t = $t")
    }

    override fun toString(): String {
        return "t = $t"
    }
}

fun main() {
    val p = P(2)
    println(p)
    p.n
    p.t
}