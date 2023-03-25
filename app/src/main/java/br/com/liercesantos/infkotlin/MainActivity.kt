package br.com.liercesantos.infkotlin

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.liercesantos.infkotlin.adapters.TasksAdapter
import br.com.liercesantos.infkotlin.manager.TaskManager

class MainActivity : AppCompatActivity() {
  lateinit var tasksRecyclerView: RecyclerView

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()

    tasksRecyclerView = findViewById(R.id.tasks_recyclerview)
    tasksRecyclerView.layoutManager = LinearLayoutManager(this)

    val manager = TaskManager(applicationContext)
    manager.populateTasks()
    val tasks = manager.allTasks()
    val adapter = TasksAdapter(tasks)
    tasksRecyclerView.adapter = adapter
  }
}