package com.restapi.application.database

import org.springframework.data.repository.CrudRepository

/**
 * This interface will be auto-implemented by Spring into a Bean called 'progressiveRepository'. The Crud Repository
 * refers Create, Read, Update, Delete.
 *
 * @author  	Markus Graf
 * @see     	org.springframework.data.repository.CrudRepository
 */
interface ProgressiveRepository : CrudRepository<Progressive, Int>