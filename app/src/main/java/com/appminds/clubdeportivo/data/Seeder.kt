package com.appminds.clubdeportivo.data

import android.content.Context
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.dao.UserDao
import com.appminds.clubdeportivo.data.model.ClientEntity
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import com.appminds.clubdeportivo.data.model.UserEntity
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

object Seeder {
    fun populateIfEmpty(context: Context) {
        val userDao = UserDao(context)
        val clientDao = ClientDao(context)
        val profesorDao = ProfesorDao(context)


        if (userDao.getAll().isEmpty()) {
            val mockUser = listOf(
                UserEntity(id = 1, nombre = "Emma", "admin@mail.com", "12345", true),
                UserEntity(id = 2, nombre = "Adri", "1", "1", true)
            )
            mockUser.forEach { userDao.insert(it) }
        }

        if (!clientDao.hasRecords()) {
            val clientsList = listOf(
                ClientEntity(1, "Juan", "Pérez", "12345678", "juan.perez@email.com", "+54911234567", "Av. Corrientes 1234, CABA", "2024-01-15", true, ClientTypeEnum.SOCIO, ClientStatusEnum.ACTIVO),
                ClientEntity(2, "María", "González", "23456789", "maria.gonzalez@email.com", "+54911234568", "Calle Falsa 456, Buenos Aires", "2024-02-20", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.ACTIVO),
                ClientEntity(3, "Carlos", "Rodríguez", "34567890", "carlos.rodriguez@email.com", "+54911234569", "San Martín 789, Córdoba", "2023-12-10", false, ClientTypeEnum.SOCIO, ClientStatusEnum.INACTIVO),
                ClientEntity(4, "Ana", "Martínez", "45678901", "ana.martinez@email.com", "+54911234570", "Mitre 321, Rosario", "2024-03-05", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.ACTIVO),
                ClientEntity(5, "Luis", "Fernández", "56789012", "luis.fernandez@email.com", "+54911234571", "Belgrano 654, Mendoza", "2023-11-18", true, ClientTypeEnum.SOCIO, ClientStatusEnum.PENDIENTE),
                ClientEntity(6, "Laura", "López", "67890123", "laura.lopez@email.com", "+54911234572", "Rivadavia 987, La Plata", "2024-01-22", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.ACTIVO),
                ClientEntity(7, "Diego", "Sánchez", "78901234", "diego.sanchez@email.com", "+54911234573", "9 de Julio 147, Tucumán", "2024-02-14", false, ClientTypeEnum.SOCIO, ClientStatusEnum.ACTIVO),
                ClientEntity(8, "Sofía", "Ramírez", "89012345", "sofia.ramirez@email.com", "+54911234574", "Pellegrini 258, Santa Fe", "2023-10-30", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.INACTIVO)
            )
            clientsList.forEach { clientDao.insert(it) }
        }

        if(!profesorDao.isNotEmpty()) {
            val profesorList = listOf(
                ProfesorEntity(1, "María", "García", "12345678A", "Calle Mayor 15", "912345678", false, null),
                ProfesorEntity(2, "Juan", "Martínez", "23456789B", "Avenida Libertad 23", "923456789", false, null),
                ProfesorEntity(3, "Ana", "López", "34567890C", "Plaza España 8", "934567890", false, null),
                ProfesorEntity(4, "Carlos", "Rodríguez", "45678901D", "Calle Sol 42", "945678901", false, null),
                ProfesorEntity(5, "Laura", "Fernández", "56789012E", "Paseo Rosales 5", "956789012", false, null),
                ProfesorEntity(6, "Pedro", "Sánchez", "67890123F", "Calle Luna 18", "967890123", true, null),
                ProfesorEntity(7, "Elena", "Gómez", "78901234G", "Avenida Paz 30", "978901234", true, null),
                ProfesorEntity(8, "Miguel", "Ruiz", "89012345H", "Calle Real 7", "989012345", true, null)
            )

            profesorList.forEach { profesorDao.insert(it) }
        }
    }
}