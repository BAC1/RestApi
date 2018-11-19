package com.restapi.application.database

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class Progressive {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private val id: Int? = null

    private var name: String? = null
    fun getName(): String? = name
    fun setName(name: String) {
        this.name = name
    }

    private var path: String? = null
    fun getPath(): String? = path
    fun setPath(path: String) {
        this.path = path
    }

    private var size: Int? = null
    fun getSize(): Int? = size
    fun setSize(size: Int) {
        this.size = size
    }

    private var width: Int? = null
    fun getWidth(): Int? = width
    fun setWidth(width: Int) {
        this.width = width
    }

    private var height: Int? = null
    fun getHeight(): Int? = height
    fun setHeight(height: Int) {
        this.height = height
    }

    private var extension: String? = null
    fun getExtension(): String? = extension
    fun setExtension(extension: String) {
        this.extension = extension
    }
}