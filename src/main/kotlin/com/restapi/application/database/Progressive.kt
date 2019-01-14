package com.restapi.application.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * This entity creates a table for progressive images that holds the image file and all related metadata
 *
 * @author  	Markus Graf
 * @see         javax.persistence.Entity
 * @see         javax.persistence.GeneratedValue
 * @see         javax.persistence.GenerationType
 * @see         javax.persistence.Id
 * @version   	1.0
 */
@Entity
class Progressive {
    
    /**
     * Automatic generated entity Id in the table
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private val id: Int? = null
    
    /**
     * Filename of the JPEG file
     *
     * @return  filename
     */
    private var name: String? = null
    fun getName(): String? = name
    fun setName(name: String) {
        this.name = name
    }
    
    /**
     * Absolute path of the JPEG file
     *
     * @return  absolute path
     */
    private var path: String? = null
    fun getPath(): String? = path
    fun setPath(path: String) {
        this.path = path
    }
    
    /**
     * File size of the JPEG file in Bytes
     *
     * @return  file size
     */
    private var size: Int? = null
    fun getSize(): Int? = size
    fun setSize(size: Int) {
        this.size = size
    }
    
    /**
     * Width of the JPEG file in pixel
     *
     * @return  width
     */
    private var width: Int? = null
    fun getWidth(): Int? = width
    fun setWidth(width: Int) {
        this.width = width
    }
    
    /**
     * Height of the JPEG file in pixel
     *
     * @return  height
     */
    private var height: Int? = null
    fun getHeight(): Int? = height
    fun setHeight(height: Int) {
        this.height = height
    }
    
    /**
     * Extension of the JPEG file (<code>jpg</code> or <code>jpeg</code>)
     *
     * @return  extension
     */
    private var extension: String? = null
    fun getExtension(): String? = extension
    fun setExtension(extension: String) {
        this.extension = extension
    }
}