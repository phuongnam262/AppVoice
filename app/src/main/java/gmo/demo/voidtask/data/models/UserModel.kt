package gmo.demo.voidtask.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String? = null,
    var email: String? = null,
)