package gmo.demo.voidtask.data.repositories

import gmo.demo.voidtask.data.db.AppDatabase
import gmo.demo.voidtask.data.models.Task

class TaskRepository(
    private val db: AppDatabase
) {
    suspend fun insertTask(task: Task) = db.taskDAO().insertTask(task)

    suspend fun getAllTask() = db.taskDAO().getAllTask()
}