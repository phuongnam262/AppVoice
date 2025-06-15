package gmo.demo.voidtask.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import gmo.demo.voidtask.data.models.UserModel

@Dao
interface UserDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel) : Long

    @Update
    suspend fun updateUser(user: UserModel) : Int

    @Delete
    suspend fun deleteUser(user: UserModel)

    @Query("DELETE FROM UserModel")
    fun deleteAllUser() : Int

    @Query("SELECT * FROM UserModel WHERE email = :email")
    suspend fun checkUserEmail(email: String) : UserModel?

}