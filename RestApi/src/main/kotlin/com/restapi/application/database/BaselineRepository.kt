package com.restapi.application.database

import org.springframework.data.repository.CrudRepository

/**
 * This will be AUTO IMPLEMENTED by Spring into a Bean called basementRepository
 * CRUD refers Create, Read, Update, Delete
 */

interface BaselineRepository : CrudRepository<Baseline, Int>