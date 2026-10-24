package com.example.demo1.project

import org.springframework.stereotype.Service

@Service
class ProjectService(private val repositorio: ProjectRepository) {

    fun listar(): List<Project> =
        repositorio.findAll()

    fun obtener(id: Long): Project? =
        repositorio.findById(id).orElse(null)

    fun crear(project: Project): Project {
        project.id = null
        return repositorio.save(project)
    }

    fun reemplazar(id: Long, project: Project): Project? {
        if (!repositorio.existsById(id)) return null
        project.id = id
        return repositorio.save(project)
    }

    fun eliminar(id: Long): Boolean {
        if (!repositorio.existsById(id)) return false
        repositorio.deleteById(id)
        return true
    }
}
