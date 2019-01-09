package com.restapi.application.metadata

import javax.imageio.ImageIO
import java.io.File

class Metadata(private val file: File) {
    fun getWidth(): Int {
        val bufferedImage = ImageIO.read(file)
        return bufferedImage.width
    }

    fun getHeight(): Int {
        val bufferedImage = ImageIO.read(file)
        return bufferedImage.height
    }
}