package com.restapi.application.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import java.io.BufferedInputStream
import com.drew.imaging.ImageMetadataReader
import java.net.URL
import java.io.IOException
import com.drew.imaging.ImageProcessingException
import com.restapi.application.image.Metadata
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.attribute.BasicFileAttributes
import javax.sql.rowset.serial.SerialBlob

class DatabaseHelper {
    private val logger: Logger = LoggerFactory.getLogger(DatabaseHelper::class.java)

    object Progressive : Table() {
        val id = integer("id").autoIncrement().primaryKey()
        var name = varchar("name", length = 50)
        val path = varchar("path", length = 50)
        val size = varchar("size", length = 50)
        val width = integer("width")
        val height = integer("height")
        val comments = varchar("comments", length = 50)
        val image = blob("image")
    }

    object Baseline : Table() {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", length = 50)
        val path = varchar("path", length = 50)
        val size = varchar("size", length = 50)
        val width = integer("width")
        val height = integer("height")
        val comments = varchar("comments", length = 50)
        val image = blob("image")
    }

    fun loadTables() {
        try {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
            logger.info("Database loaded")

            transaction {
                create(Baseline, Progressive)
            }
            logger.info("Tables created")
        } catch (e: Exception) {
            logger.error("Error during loading databases!\n{}", e.message)
        }
    }

    fun loadImages() {
        for (path in listOf( "/images/baseline", "/images/progressive")) {
            try {
                File(path).walkTopDown().forEach {
                    if (it.isFile) {
                        getMetadata(path)
                        when(path) {
                            "/images/baseline" -> insertBaseline(file = it, stream = FileInputStream(it))
                            "/images/progressive" -> insertProgressive(file = it, stream = FileInputStream(it))
                        }
                    }
                }
            } catch (e: IOException) {
                logger.error("Error during loading image in path '$path'\n{}", e.message)
            }
        }
    }

    fun dropTables() {
        try {
            transaction {
                drop(Baseline, Progressive)
            }
            logger.info("Databases dropped")
        } catch (e: Exception) {
            logger.error("Error during dropping datatables!\n}", e.message)
        }
    }

    private fun getMetadata(path: String) {
        try {
            val stream = BufferedInputStream(URL(path).openStream())
            val metadata = ImageMetadataReader.readMetadata(stream, -1)
            val attributes = Files.readAttributes(
                    File(path).toPath(),
                    BasicFileAttributes::class.java,
                    LinkOption.NOFOLLOW_LINKS
            )
            Metadata.size = attributes.size().toString()

            for (directory in metadata.directories) {
                println("Loading directories ...")
                println(directory.name)

                for (tag in directory.tags) {
                    println("Loading tags ...")
                    println(tag.description)
                    println(tag.tagName)
                    println(tag.directoryName)
                    println(tag.tagTypeHex)

                    when (tag.tagName.toString()) {
                        "ImageWidth" -> Metadata.width = tag.description.toInt()
                        "ImageHeight" -> Metadata.height = tag.description.toInt()
                        "ImageDescription" -> Metadata.comments = tag.description
                        else -> logger.info("Unused tag ${tag.tagName} found.")
                    }
                }
            }
        } catch (e: ImageProcessingException) {
            logger.error("Metadata cannot be loaded! {}", e.message)
        } catch (e: IOException) {
            logger.error("Metadata cannot be loaded! {}", e.message)
        }
    }

    @Throws(IOException::class)
    private fun insertProgressive(file: File, stream: FileInputStream) {
        transaction {
            Progressive.insert  {
                it[name] = file.name
                it[size] = Metadata.size
                it[path] = file.path
                it[comments] = Metadata.comments
                it[width] = Metadata.width
                it[height] = Metadata.height
                it[image] = SerialBlob(stream.readBytes())
            }
        }
        logger.info("Progressive image '${file.name}' added to database")
    }

    @Throws(IOException::class)
    private fun insertBaseline(file: File, stream: FileInputStream) {
        transaction {
            Baseline.insert  {
                it[name] = file.name
                it[size] = Metadata.size
                it[path] = file.path
                it[comments] = Metadata.comments
                it[width] = Metadata.width
                it[height] = Metadata.height
                it[image] = SerialBlob(stream.readBytes())
            }
        }
        logger.info("Baseline image '${file.name}' added to database")
    }

    fun getProgressiveByName(name: String) {
        transaction {
            val query: Query = Progressive.select { Progressive.name eq name }
        }
    }

    fun getBaselineByName(name: String) {
        transaction {
            val query: Query = Baseline.select { Progressive.name eq name }
        }
    }
}
