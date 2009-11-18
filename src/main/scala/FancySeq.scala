package com.protose.resque

object FancySeq {
    implicit def set2FancySeq[A](set: Seq[A]) = new FancySeq(set)
}

class FancySeq[A](set: Seq[A]) {
    def join: String = {
        set.foldLeft("") { (joined, str) => joined + str }
    }

    def join(delimiter: String): String = {
        set.slice(1, set.length).foldLeft(set.first.toString) { (joined, str) => 
            joined + delimiter + str
        }
    }
}


// vim: set ts=4 sw=4 et:
