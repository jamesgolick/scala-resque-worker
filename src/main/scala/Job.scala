package com.protose.resque
import com.twitter.json.Json

case class Job(worker: Worker, queue: String,
               payload: String, performerFinder: { 
                   def apply(s: String): Performable }) {
    def perform = {
        performer.perform(parsedPayload("args").asInstanceOf[List[String]])
    }

    def performer: Performable = performerFinder(parsedPayload("class"))
    def parsedPayload: Map[String, String] =
        Json.parse(payload).asInstanceOf[Map[String, String]]
}

// vim: set ts=4 sw=4 et:
