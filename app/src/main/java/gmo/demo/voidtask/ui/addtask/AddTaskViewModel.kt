package gmo.demo.voidtask.ui.addtask

import android.util.Log
import androidx.lifecycle.MutableLiveData
import gmo.demo.voidtask.data.models.Task
import gmo.demo.voidtask.data.repositories.TaskRepository
import gmo.demo.voidtask.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AddTaskViewModel(private val repository: TaskRepository) : BaseViewModel() {
    val title = MutableLiveData<String>()
    val detail = MutableLiveData<String>()

    fun addTask() {
        ioScope.launch {
            val titleValue = title.value ?: ""
            val detailValue = detail.value ?: ""
            val a = repository.insertTask(Task(title = titleValue, detail = detailValue))
            Log.d("add", a.toString())
            val task = repository.getAllTask()
            Log.d("task", task.toString())
        }
    }
}