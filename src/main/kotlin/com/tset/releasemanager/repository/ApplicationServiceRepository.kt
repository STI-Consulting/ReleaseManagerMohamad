package com.tset.releasemanager.repository

import com.tset.releasemanager.model.ApplicationService
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationServiceRepository : JpaRepository<ApplicationService, Long>