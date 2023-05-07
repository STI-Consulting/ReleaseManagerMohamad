package com.tset.releasemanager.repository

import com.tset.releasemanager.model.Deployment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeploymentRepository : JpaRepository<Deployment, Long> {
    fun findFirstByOrderBySystemVersionDesc(): Deployment?
    fun findBySystemVersion(systemVersion: Int): Deployment?
}