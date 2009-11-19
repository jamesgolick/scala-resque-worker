package com.protose.resque

abstract class Performable(val name: String) {
    def perform(args: List[String]): Unit
}

// vim: set ts=4 sw=4 et:
