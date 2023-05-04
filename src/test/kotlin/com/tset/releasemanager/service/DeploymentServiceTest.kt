package com.tset.releasemanager.service

import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.model.ApplicationService
import com.tset.releasemanager.model.Deployment
import com.tset.releasemanager.repository.ApplicationServiceRepository
import com.tset.releasemanager.repository.DeploymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DeploymentServiceTest{
    @Mock
    private lateinit var deploymentRepository: DeploymentRepository
    @Mock
    private lateinit var serviceRepository: ApplicationServiceRepository
    @InjectMocks
    private lateinit var deploymentService: DeploymentService

    @Test
    fun shouldKeepCurrentDeploymentUnchanged(){
        val service = ApplicationService(serviceName = "Service A", version = 2)
        val dummyDeployment = Deployment(systemVersion = 1, deployedApplicationServices = arrayListOf(service))
        `when`(deploymentRepository.findFirstByOrderBySystemVersionDesc()).thenReturn(dummyDeployment)
        `when`(serviceRepository.findByServiceNameAndVersion(service.serviceName, service.version)).thenReturn(service)
        val systemVersion = deploymentService.deployService(ServiceDto(name = service.serviceName, version = service.version))
        assertEquals(dummyDeployment.systemVersion, systemVersion)
    }
    @Test
    fun shouldAddNewService(){
        val newService = ApplicationService(serviceName = "Service A", version = 2)
        val dummyDeployment = Deployment(systemVersion = 1)
        `when`(deploymentRepository.findFirstByOrderBySystemVersionDesc()).thenReturn(dummyDeployment)
        `when`(deploymentRepository.save(ArgumentMatchers.any())).thenReturn(Deployment(systemVersion = 5))
        `when`(serviceRepository.findByServiceNameAndVersion(newService.serviceName, newService.version)).thenReturn(null)
        `when`(serviceRepository.save(newService)).thenReturn(newService)
        val systemVersion = deploymentService.deployService(ServiceDto(name = newService.serviceName, version = newService.version))
        assertEquals(5, systemVersion)
    }

    @Test
    fun shouldReturnServicesWhenSystemVersionExist(){
        val service = ApplicationService(serviceName = "Service A", version = 2)
        val dummyDeployment = Deployment(systemVersion = 1, deployedApplicationServices = arrayListOf(service))
        `when`(deploymentRepository.findBySystemVersion(1)).thenReturn(dummyDeployment)
        val deploymentDto = deploymentService.getAllServicesForVersionNumber(1)
        assertEquals(1, deploymentDto.deployedApplicationServices.size)
        assertEquals(service.serviceName, deploymentDto.deployedApplicationServices[0].name)
    }
}