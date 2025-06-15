package gmo.demo.voidtask.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileEntry(fileEntry: FileEntry)

    @Query("SELECT * FROM file_entries")
    suspend fun getAllFileEntries(): List<FileEntry>
} 