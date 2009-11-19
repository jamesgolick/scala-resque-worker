package com.protose.resque
import com.twitter.json.Json

class JobFactory(performableMap: Map[String, Performable]) {
    def apply(worker: Worker, queue: String, payload: String): Job = {
        Job(worker, queue, payload, performableMap)
    }
}

case class Job(worker: Worker, queue: String,
               payload: String, performableMap: Map[String, Performable]) {
    def perform = {
        performer.perform(parsedPayload("args").asInstanceOf[List[String]])
    }

    def performer: Performable = {
        performableMap(parsedPayload("class"))
    }

    def parsedPayload: Map[String, String] =
        Json.parse(payload).asInstanceOf[Map[String, String]]
}

// vim: set ts=4 sw=4 et:
