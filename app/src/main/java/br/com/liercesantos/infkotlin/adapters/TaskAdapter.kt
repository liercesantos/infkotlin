package br.com.liercesantos.infkotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.liercesantos.infkotlin.R
import br.com.liercesantos.infkotlin.models.TaskModel

class TasksAdapter(private val tasks: List<TaskModel>) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameTextView: TextView = itemView.findViewById(R.id.task_name)
    val timeTextView: TextView = itemView.findViewById(R.id.task_time)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val context = parent.context
    val inflater = LayoutInflater.from(context)
    val taskView = inflater.inflate(R.layout.item_task, parent, false)
    return ViewHolder(taskView)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val task: TaskModel = tasks[position]

    holder.nameTextView.text = task.name
    holder.timeTextView.text = task.scheduled.toString()
  }

  override fun getItemCount(): Int {
    return tasks.size
  }
}
