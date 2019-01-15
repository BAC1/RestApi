package com.restapi.application.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * This entity creates a database table for progressive images that holds the image file and all related metadata.
 *
 * @author  	Markus Graf
 * @see         javax.persistence.Entity
 * @see         javax.persistence.GeneratedValue
 * @see         javax.persistence.GenerationType
 * @see         javax.persistence.Id
 */
@Entity
class Progressive {
    
    /**
     * Automatic generated entity Id in the database table.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private val id: Int? = null
    
    /**
     * Filename of the JPEG file.
     *
     * @return  filename
     */
    private var name: String? = null
    fun getName(): String? = name
    fun setName(name: String) {
        this.name = name
    }
    
    /**
     * Absolute path of the JPEG file.
     *
     * @return  absolute path
     */
    private var path: String? = null
    fun getPath(): String? = path
    fun setPath(path: String) {
        this.path = path
    }
    
    /**
     * File size of the JPEG file.
     *
     * @return  file size (in bytes)
     */
    private var size: Int? = null
    fun getSize(): Int? = size
    fun setSize(size: Int) {
        this.size = size
    }
    
    /**
     * Width of the JPEG file.
     *
     * @return  width (in pixel)
     */
    private var width: Int? = null
    fun getWidth(): Int? = width
    fun setWidth(width: Int) {
        this.width = width
    }
    
    /**
     * Height of the JPEG file.
     *
     * @return  height (in pixel)
     */
    private var height: Int? = null
    fun getHeight(): Int? = height
    fun setHeight(height: Int) {
        this.height = height
    }
    
    /**
     * Extension of the JPEG file.
     *
     * @return  extension (<code>jpg</code> or <code>jpeg</code>)
     */
    private var extension: String? = null
    fun getExtension(): String? = extension
    fun setExtension(extension: String) {
        this.extension = extension
    }
}