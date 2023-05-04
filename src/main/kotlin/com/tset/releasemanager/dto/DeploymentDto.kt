package com.tset.releasemanager.dto


data class DeploymentDto(var deployedApplicationServices: MutableList<ServiceDto> = arrayListOf()) {
}