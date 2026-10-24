package com.example.demo1.project

import org.springframework.data.repository.Repository
import java.util.Optional

// Extiende la interfaz base 'Repository' (vacía, solo marcador), NO 'JpaRepository'.
// Así cada función se declara aquí con 'fun' normal (sin override) y Spring Data
// la implementa sola al reconocer su nombre.
@org.springframework.stereotype.Repository
interface ProjectRepository : Repository<Project, Long> {

    // Devolver todos
    fun findAll(): List<Project>

    // Devolver uno por id
    fun findById(id: Long): Optional<Project>

    // Crear o actualizar
    fun save(project: Project): Project

    // Eliminar por id
    fun deleteById(id: Long)

    // Comprobar si existe
    fun existsById(id: Long): Boolean
}
