package com.protose.resque

object Performable {
    val performables = scala.collection.mutable.Map[String, Performable]()

    def apply(name: String): Performable = performables.apply(name)
}
abstract class Performable(val name: String) {
    Performable.performables += Tuple(name, this)

    def perform(args: List[String]): Unit
}

// vim: set ts=4 sw=4 et:
