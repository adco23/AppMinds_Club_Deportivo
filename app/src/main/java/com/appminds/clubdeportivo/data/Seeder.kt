package com.appminds.clubdeportivo.data

import android.content.Context
import com.appminds.clubdeportivo.data.dao.UserDao
import com.appminds.clubdeportivo.data.model.UserEntity

object Seeder {
    fun populateIfEmpty(context: Context) {
        val userDao = UserDao(context)
        if (userDao.getAll().isEmpty()) {
            val mockUser = listOf(
                UserEntity(id = 1, nombre = "Emma", "admin@mail.com", "12345", true),
                UserEntity(id = 2, nombre = "Adri", "1", "1", true)
            )
            mockUser.forEach { userDao.insert(it) }
        }
    }
}