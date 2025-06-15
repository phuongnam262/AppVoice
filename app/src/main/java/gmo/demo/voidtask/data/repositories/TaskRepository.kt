package gmo.demo.voidtask.data.repositories

import gmo.demo.voidtask.data.db.AppDatabase
import gmo.demo.voidtask.data.models.Task
import gmo.demo.voidtask.data.network.SafeApiRequest

class TaskRepository(
    private val db: AppDatabase
) : SafeApiRequest()  {
    suspend fun insertTask(task: Task) = db.taskDAO().insertTask(task)

    suspend fun getAllTask() = db.taskDAO().getAllTask()
}