package com.restapi.application.metadata

import javax.imageio.ImageIO
import java.io.File

/**
 * This class handles the metadata of a given image file.
 *
 * @author      Markus Graf
 * @see         javax.imageio.ImageIO
 * @see         java.io.File
 */
class Metadata {
    
    /**
     * Transforms the given file to a buffered image.
     *
     * @param   file   file which shall be transformed to a buffered image
     * @return  the buffered image
     * @see     javax.imageio.ImageIO#read(File input)
     */
    private fun getBufferedImage(file: File) = ImageIO.read(file)
    
    /**
     * Retrieves the image width.
     *
     * @param   file   file from which the width shall be received
     * @return  the image width (in pixel)
     */
    fun getWidth(file: File): Int = getBufferedImage(file).width
    
    /**
     * Retrieves the image height.
     *
     * @param   file   file from which the height shall be received
     * @return  the image height (in pixel)
     */
    fun getHeight(file: File): Int = getBufferedImage(file).height
}