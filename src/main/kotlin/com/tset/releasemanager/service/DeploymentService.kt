package com.tset.releasemanager.service

import com.tset.releasemanager.dto.DeploymentDto
import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.model.ApplicationService
import com.tset.releasemanager.model.Deployment
import com.tset.releasemanager.repository.DeploymentRepository
import com.tset.releasemanager.repository.ApplicationServiceRepository
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class DeploymentService(
    private val applicationServiceRepository: ApplicationServiceRepository,
    private val deploymentRepository: DeploymentRepository,
) {
    companion object : KLogging()

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun deployService(service: ServiceDto): Int? {
        val currentDeployment = deploymentRepository.findFirstByOrderBySystemVersionDesc()
        val latestSystemVersion = currentDeployment?.systemVersion ?: 0
        if (isServiceDeployed(currentDeployment, service)) {
            logger.info("Service ${service.name} with version ${service.version} is already deployed")
            return latestSystemVersion
        }
        logger.trace("Adding a new service: $service")
        val newService =
            applicationServiceRepository.save(ApplicationService(serviceName = service.name, version = service.version))
        val updatedDeployment = currentDeployment?.copy(
            systemVersion = latestSystemVersion.plus(1),
            deployedApplicationServices = currentDeployment.deployedApplicationServices
                .filterNot { it.serviceName == service.name }
                .plus(newService)
                .toMutableList()
        ) ?: Deployment(
            systemVersion = latestSystemVersion.plus(1),
            deployedApplicationServices = mutableListOf(newService)
        )
        return deploymentRepository.save(updatedDeployment).systemVersion
    }

    fun getAllServicesForVersionNumber(systemVersion: Int): DeploymentDto {
        return deploymentRepository.findBySystemVersion(systemVersion)
            ?.deployedApplicationServices
            .orEmpty()
            .map { ServiceDto(it.serviceName, it.version) }
            .toMutableList()
            .let(::DeploymentDto)
    }

    private fun isServiceDeployed(currentDeployment: Deployment?, service: ServiceDto): Boolean {
        return currentDeployment?.deployedApplicationServices
            ?.any { it.serviceName == service.name && it.version == service.version }
            ?: false
    }
}