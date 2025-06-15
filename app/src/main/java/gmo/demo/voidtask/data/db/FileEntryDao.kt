package gmo.demo.voidtask.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface FileEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileEntry(fileEntry: FileEntry)
} 