package com.restapi.application.database

import org.springframework.data.repository.CrudRepository

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called 'progressiveRepository'
 * CRUD refers Create, Read, Update, Delete
 */

interface ProgressiveRepository : CrudRepository<Progressive, Int>