package com.appminds.clubdeportivo.models


import java.io.Serializable
class AddActividadDto (
    val name: String,
    val days: String,     // ej: "Lunes, Martes y Viernes"
    val time: String,     // ej: "15:00"
    val price: String,    // ej: "$ 20"
    val capacity: String  // ej: "50 personas"
    ) : Serializable