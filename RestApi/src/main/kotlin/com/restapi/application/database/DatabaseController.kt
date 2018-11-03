package com.restapi.application.database

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class DatabaseController {
    private val databaseHelper = DatabaseHelper()

    /*
    *........................................................................
    *   DATA TABLE CONTROLLER
    * ........................................................................
    */

    @Bean
    fun initDatabase() {
        databaseHelper.loadTables()
        databaseHelper.loadImages()
    }

    @Bean
    fun deleteDatabase() = databaseHelper.dropTables()

    @Bean
    fun resetDatabase() {
        databaseHelper.dropTables()
        databaseHelper.loadTables()
        databaseHelper.loadImages()
    }

    /*
    *........................................................................
    *   PROGRESSIVE IMAGE CONTROLLER
    * ........................................................................
    */

    @Bean
    fun insertProgressive(file: File) = databaseHelper.insertProgressive(file)

    @Bean
    fun getProgressiveByName(name: String): File? = databaseHelper.getProgressiveByName(name)

    /*
    *........................................................................
    *   BASELINE IMAGE CONTROLLER
    * ........................................................................
    */

    @Bean
    fun insertBaseline(file: File) = databaseHelper.insertBaseline(file)

    @Bean
    fun getBaselineByName(name: String): File? = databaseHelper.getBaselineByName(name)
}