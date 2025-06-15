package gmo.demo.voidtask.ui.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gmo.demo.voidtask.data.repositories.TaskRepository

@Suppress("UNCHECKED_CAST")
class AddTaskViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddTaskViewModel(repository) as T
    }
}