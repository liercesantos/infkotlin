package br.com.liercesantos.infkotlin.models

import java.time.LocalDateTime

data class TaskModel(
  val id: String,
  var name: String,
  var status: String,
  var scheduled: String,
  val created: String,
  var updated: LocalDateTime? = null,
  var removed: LocalDateTime? = null
)
