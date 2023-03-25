package br.com.liercesantos.infkotlin.manager

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import br.com.liercesantos.infkotlin.models.TaskModel
import java.time.LocalDateTime
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*
import java.time.format.DateTimeFormatter
import java.util.*

class TaskManager(private val context: Context) {
  private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
  private val sharedPreferences: SharedPreferences = context.getSharedPreferences("TaskManager", Context.MODE_PRIVATE)
  private val tasksFile: File = File(context.filesDir, "tasks.json")

  @RequiresApi(Build.VERSION_CODES.O)
  fun populateTasks() {
    for (i in 1..15) {
      val task = TaskModel(
        "",
        "Task $i",
        listOf("To Do", "Doing", "Done").random(),
        LocalDateTime.now().plusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE_TIME),
        LocalDateTime.now().minusDays(i.toLong()).format(DateTimeFormatter.ISO_DATE_TIME),
        null,
        null
      )
      addTask(task)
    }
  }

  fun addTask(task: TaskModel) {
    val tasks = loadTasks()

    val newTask = task.copy(id = UUID.randomUUID().toString())
    tasks.add(newTask)

    saveTasksToFile(tasks)
    saveTasksToLocalStorage(tasks)
  }

  private fun loadTasks(): ArrayList<TaskModel> {
    val tasksJson = sharedPreferences.getString("tasks", "[]")
    return gson.fromJson(tasksJson, Array<TaskModel>::class.java).toMutableList() as ArrayList<TaskModel>
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun allTasks(): List<TaskModel> {
    // carrega a lista atual de tasks do local storage
    val tasks = loadTasks()

    // ordena as tasks pelo campo scheduled em ordem decrescente e formata a data para mostrar apenas o horário
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return tasks.sortedByDescending {
      LocalDateTime.parse(it.scheduled, DateTimeFormatter.ISO_DATE_TIME)
        .format(formatter)
    }
  }

  private fun saveTasksToFile(tasks: ArrayList<TaskModel>) {
    val tasksJson = gson.toJson(tasks)
    tasksFile.writeText(tasksJson)
  }

  private fun saveTasksToLocalStorage(tasks: ArrayList<TaskModel>) {
    val tasksJson = gson.toJson(tasks)
    sharedPreferences.edit().putString("tasks", tasksJson).apply()
  }
}