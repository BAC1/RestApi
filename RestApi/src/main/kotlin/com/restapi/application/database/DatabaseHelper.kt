package com.restapi.application.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import java.io.BufferedInputStream
import com.drew.imaging.ImageMetadataReader
import java.net.URL
import java.lang.Exception
import java.io.IOException
import com.drew.imaging.ImageProcessingException
import com.restapi.application.image.Metadata
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.attribute.BasicFileAttributes
import javax.imageio.ImageIO
import javax.sql.rowset.serial.SerialBlob

class DatabaseHelper {
    private val logger: Logger = LoggerFactory.getLogger(DatabaseHelper::class.java)

    private object Progressive : Table() {
        val id = integer("id").autoIncrement().primaryKey()
        var name = varchar("name", length = 50)
        val path = varchar("path", length = 50)
        val size = varchar("size", length = 50)
        val width = integer("width")
        val height = integer("height")
        val comments = varchar("comments", length = 50)
        val image = blob("image")
        val extension = varchar("extension", length = 50)
    }

    private object Baseline : Table() {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", length = 50)
        val path = varchar("path", length = 50)
        val size = varchar("size", length = 50)
        val width = integer("width")
        val height = integer("height")
        val comments = varchar("comments", length = 50)
        val image = blob("image")
        val extension = varchar("extension", length = 50)
    }

    /*
    *........................................................................
    *   IMAGE HANDLER
    * ........................................................................
    */

    fun loadImages() {
        val filesBaseline = File(javaClass.getResource("/images/baseline").toURI()).listFiles()
        val filesProgressive = File(javaClass.getResource("/images/progressive").toURI()).listFiles()

        for (file in filesBaseline) loadImagesFromResource(file, isBaseline = true)
        for (file in filesProgressive) loadImagesFromResource(file)
    }

    private fun loadImagesFromResource(file: File, isBaseline: Boolean = false) {
        try {
            if (file.isFile && !file.isHidden && (file.extension == "jpg" || file.extension == "png")) {
                if (isBaseline) insertBaseline(file) else insertProgressive(file)
            }
        } catch (e: IOException) {
            logger.error("Error during loading image from path '${file.path}'", e.message)
        }
    }

    /*
    *........................................................................
    *   TABLE HANDLER
    * ........................................................................
    */

    fun loadTables() {
        try {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
            logger.info("Database connection established")
            transaction { create(Baseline, Progressive) }
            logger.info("Tables 'Baseline' and 'Progressive' created")
        } catch (e: Exception) {
            logger.error("Error during loading databases!\n{}", e.message)
        }
    }

    fun dropTables() {
        try {
            transaction { drop(Baseline, Progressive) }
            logger.info("Tables dropped")
        } catch (e: Exception) {
            logger.error("Error during dropping tables!\n}", e.message)
        }
    }

    /*
    *........................................................................
    *   METADATA
    * ........................................................................
    */

    private fun getMetadata(path: String): Metadata {
        val metaDataObject = Metadata()

        try {
            val stream = BufferedInputStream(URL(path).openStream())
            val metaData = ImageMetadataReader.readMetadata(stream, -1)
            val attributes = Files.readAttributes(
                    File(path).toPath(),
                    BasicFileAttributes::class.java,
                    LinkOption.NOFOLLOW_LINKS
            )

            metaDataObject.size = attributes.size().toString()

            for (directory in metaData.directories) {
                logger.info("Loading directory ${directory.name}")

                for (tag in directory.tags) {
                    logger.info("Loading tags:")
                    logger.info("\tDescription: ${tag.description}\n" +
                            "\tTag Name: ${tag.tagName}\n" +
                            "\tDirectory Name: ${tag.directoryName}\n" +
                            "\tTag Typ Hex: ${tag.tagTypeHex}")

                    when (tag.tagName.toString()) {
                        "ImageWidth" -> metaDataObject.width = tag.description.toInt()
                        "ImageHeight" -> metaDataObject.height = tag.description.toInt()
                        "ImageDescription" -> metaDataObject.comments = tag.description
                        else -> logger.info("Unused tag '${tag.tagName}' found")
                    }
                }
            }
        } catch (e: ImageProcessingException) {
            logger.error("Metadata cannot be loaded! {}", e.message)
        } catch (e: IOException) {
            logger.error("Metadata cannot be loaded! {}", e.message)
        }
        return metaDataObject
    }

    /*
    *........................................................................
    *   INSERT IMAGES
    * ........................................................................
    */

    @Throws(IOException::class)
    fun insertProgressive(file: File) {
        val metadata = getMetadata(file.path)

        transaction {
            Progressive.insert  {
                it[name] = file.name
                it[size] = metadata.size
                it[path] = file.path
                it[comments] = metadata.comments
                it[width] = metadata.width
                it[height] = metadata.height
                it[image] = SerialBlob(file.readBytes())
                it[extension] = file.extension
            }
        }
        logger.info("Progressive image '${file.name}' added to database")
    }

    @Throws(IOException::class)
    fun insertBaseline(file: File) {
        val metadata = getMetadata(file.path)

        transaction {
            Baseline.insert  {
                it[name] = file.name
                it[size] = metadata.size
                it[path] = file.path
                it[comments] = metadata.comments
                it[width] = metadata.width
                it[height] = metadata.height
                it[image] = SerialBlob(file.readBytes())
                it[extension] = file.extension
            }
        }
        logger.info("Baseline image '${file.name}' added to database")
    }

    /*
    *........................................................................
    *   LOAD IMAGES
    * ........................................................................
    */

    fun getProgressiveByName(name: String): File?  = loadImage(name)

    fun getBaselineByName(name: String): File? = loadImage(name, isBaseline = true)

    private fun loadImage(name: String, isBaseline: Boolean = false): File? {
        var image: BufferedImage? = null
        var extension: String? = null

        try {
            transaction {
                val query: Query = Progressive.select {
                    if (isBaseline) Baseline.name eq name else Progressive.name eq name
                }

                val inputStream = query.first()[
                        if (isBaseline) Baseline.image else Progressive.image
                ].binaryStream

                image = ImageIO.read(inputStream)
                extension = query.first()[
                        if (isBaseline) Baseline.extension else Progressive.extension
                ]
            }
        } catch (e: Exception) {
            logger.error("Cannot load ${if (isBaseline) "baseline" else "progressive"} image '$name' from datatable!",
                    e.message)
            return null
        }

        if (image == null || extension == null) {
            logger.error("Cannot load ${if (isBaseline) "baseline" else "progressive"} image '$name' from datatable. " +
                    "It is null (image: $image | extension: $extension)!")
            return null
        }

        val file = File("$name.$extension")
        ImageIO.write(image, extension, file)

        return file
    }
}
