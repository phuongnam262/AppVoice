package gmo.demo.voidtask.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_entries")
data class FileEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileContent: String
) 