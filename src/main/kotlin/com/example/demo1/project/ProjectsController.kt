package com.example.demo1.project

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/projects")
class ProjectsController(private val servicio: ProjectService) {

    @GetMapping
    fun listar(): List<Project> =
        servicio.listar()

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): ResponseEntity<Project> =
        servicio.obtener(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun crear(@Valid @RequestBody project: Project): ResponseEntity<Project> {
        val guardado = servicio.crear(project)
        return ResponseEntity.created(URI("/projects/${guardado.id}")).body(guardado)
    }

    @PutMapping("/{id}")
    fun reemplazar(@PathVariable id: Long, @Valid @RequestBody project: Project): ResponseEntity<Project> =
        servicio.reemplazar(id, project)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> =
        if (servicio.eliminar(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
}
