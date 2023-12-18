package com.example.pipegame.utils

fun conditional(condition: () -> Boolean, ifTrue: () -> Unit, ifFalse: () -> Unit) {
    if (condition()) {
        ifTrue()
    } else {
        ifFalse()
    }
}

fun <T> conditional(condition: () -> Boolean, ifTrue: () -> T, ifFalse: () -> T): T =
    if (condition()) ifTrue() else ifFalse()

fun <T> conditional(condition: Boolean, ifTrue:  T, ifFalse: T): T =
    if (condition) ifTrue else ifFalse