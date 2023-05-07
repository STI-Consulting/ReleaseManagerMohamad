package com.tset.releasemanager.service

import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.exception.DeploymentException
import com.tset.releasemanager.model.ApplicationService
import com.tset.releasemanager.model.Deployment
import com.tset.releasemanager.repository.ApplicationServiceRepository
import com.tset.releasemanager.repository.DeploymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DeploymentServiceTest {
    @Mock
    private lateinit var deploymentRepository: DeploymentRepository

    @Mock
    private lateinit var serviceRepository: ApplicationServiceRepository

    @InjectMocks
    private lateinit var deploymentService: DeploymentService

    @Test
    fun shouldKeepCurrentDeploymentUnchanged() {
        val service = ApplicationService(serviceName = "Service A", version = 2)
        val dummyDeployment = Deployment(systemVersion = 1, deployedApplicationServices = arrayListOf(service))
        `when`(deploymentRepository.findFirstByOrderBySystemVersionDesc()).thenReturn(dummyDeployment)
        val systemVersion =
            deploymentService.deployService(ServiceDto(name = service.serviceName, version = service.version))
        assertEquals(dummyDeployment.systemVersion, systemVersion)
    }

    @Test
    fun shouldAddNewService() {
        val serviceName = "Service A"
        val serviceVersion = 2
        val newService = ApplicationService(serviceName = serviceName, version = serviceVersion)
        val currentDeployment = Deployment(systemVersion = 1)
        val updatedDeployment = Deployment(systemVersion = 2)
        `when`(deploymentRepository.findFirstByOrderBySystemVersionDesc()).thenReturn(currentDeployment)
        `when`(deploymentRepository.save(any())).thenReturn(updatedDeployment)
        `when`(serviceRepository.save(newService)).thenReturn(newService)

        val systemVersion = deploymentService.deployService(ServiceDto(name = serviceName, version = serviceVersion))

        assertEquals(updatedDeployment.systemVersion, systemVersion)
        verify(deploymentRepository).findFirstByOrderBySystemVersionDesc()
        verify(deploymentRepository).save(any())
        verify(serviceRepository).save(newService)
    }

    @Test
    fun shouldReturnServicesWhenSystemVersionExist() {
        val service = ApplicationService(serviceName = "Service A", version = 2)
        val dummyDeployment = Deployment(systemVersion = 1, deployedApplicationServices = arrayListOf(service))
        `when`(deploymentRepository.findBySystemVersion(1)).thenReturn(dummyDeployment)
        val deploymentDto = deploymentService.getAllServicesForVersionNumber(1)
        assertEquals(1, deploymentDto.deployedApplicationServices.size)
        assertEquals(service.serviceName, deploymentDto.deployedApplicationServices[0].name)
    }
}