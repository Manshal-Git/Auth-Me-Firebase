package com.manshal_khatri.authmefirebase.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.manshal_khatri.authmefirebase.model.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User)

    @Query("delete from userTable where emailId = :email")
    fun deleteUser(email : String)

    @Query("Select * from userTable where emailId = :email")
    fun getUser(email : String) : LiveData<User>

    @Update(onConflict = REPLACE)
    fun updateUser(user: User)
}