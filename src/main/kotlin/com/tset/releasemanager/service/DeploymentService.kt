package com.tset.releasemanager.service

import com.tset.releasemanager.dto.DeploymentDto
import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.model.ApplicationService
import com.tset.releasemanager.model.Deployment
import com.tset.releasemanager.repository.DeploymentRepository
import com.tset.releasemanager.repository.ApplicationServiceRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class DeploymentService(
    private val applicationServiceRepository: ApplicationServiceRepository,
    private val deploymentRepository: DeploymentRepository,
) {
    companion object : KLogging()

    fun deployService(service: ServiceDto): Int? {
        val currentDeployment = deploymentRepository.findFirstByOrderBySystemVersionDesc()
        println("retrieved current deployment: ${currentDeployment?.deployedApplicationServices?.size} element")
        val deployedService = applicationServiceRepository.findByServiceNameAndVersion(service.name, service.version)
        println("retrieved deployed service: ${deployedService?.serviceName} version: ${deployedService?.version}")
        val latestSystemVersion = currentDeployment?.systemVersion ?: 0
        if (currentDeployment?.containsService(deployedService) == true) {
            logger.info("Service ${service.name} with version ${service.version} is already deployed")
            return latestSystemVersion
        } else {
            logger.trace("Adding a new service: $service")
            val newService = applicationServiceRepository.save(
                ApplicationService(
                    serviceName = service.name,
                    version = service.version
                )
            )
            var newDeployment = Deployment(systemVersion = latestSystemVersion.plus(1))
            val services = currentDeployment?.deployedApplicationServices?.toMutableList() ?: arrayListOf()
            val oldVersionService = currentDeployment?.retrieveByServiceNameIfExist(service.name)
            if (oldVersionService != null) {
                services.remove(oldVersionService)
            }
            services.add(newService)
            for (element in services) {
                logger.trace("Adding service ${element.serviceName} to new deployment")
                newDeployment.addService(element)
            }
            logger.trace("new deployment has ${newDeployment.deployedApplicationServices.size} services")
            newDeployment = deploymentRepository.save(newDeployment)
            return newDeployment.systemVersion
        }

    }

    fun getAllServicesForVersionNumber(systemVersion: Int): DeploymentDto {
        val deployment = deploymentRepository.findBySystemVersion(systemVersion)
        val services = deployment?.deployedApplicationServices ?: arrayListOf()
        val servicesDto = arrayListOf<ServiceDto>()
        for (service in services) {
            servicesDto.add(ServiceDto(service.serviceName, service.version))
        }
        return DeploymentDto(servicesDto)
    }
}