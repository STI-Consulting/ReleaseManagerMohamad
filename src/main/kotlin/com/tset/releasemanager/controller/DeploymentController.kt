package com.tset.releasemanager.controller

import com.tset.releasemanager.dto.DeploymentDto
import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.service.DeploymentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DeploymentController(val deploymentService: DeploymentService) {

    @PostMapping("/deploy")
    fun deployService(@RequestBody service: ServiceDto): Int{
        return deploymentService.deployService(service) ?: 0
    }

    @GetMapping("/services")
    fun getServicesForVersionNumber(@RequestParam("systemVersion") systemVersion: Int): DeploymentDto{
        return deploymentService.getAllServicesForVersionNumber(systemVersion)
    }
}