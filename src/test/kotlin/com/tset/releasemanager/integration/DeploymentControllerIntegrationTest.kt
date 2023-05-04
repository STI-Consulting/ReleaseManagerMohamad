package com.tset.releasemanager.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.tset.releasemanager.ReleaseManagerApplication
import com.tset.releasemanager.dto.DeploymentDto
import com.tset.releasemanager.dto.ServiceDto
import com.tset.releasemanager.model.ApplicationService
import com.tset.releasemanager.model.Deployment
import com.tset.releasemanager.repository.ApplicationServiceRepository
import com.tset.releasemanager.repository.DeploymentRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@SpringBootTest(
    classes = [ReleaseManagerApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
//@TestPropertySource(locations = ["classpath:application.properties"])
class DeploymentControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var deploymentRepository: DeploymentRepository

    @Autowired
    private lateinit var serviceRepository: ApplicationServiceRepository

    @AfterEach
    fun tearDown() {
        deploymentRepository.deleteAll()
        serviceRepository.deleteAll()
    }

    @Test
    fun shouldDeployNewService() {
        val serviceDto = ServiceDto(name = "Service A", version = 1)
        val objectMapper = ObjectMapper()
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        val objectWriter = objectMapper.writer().withDefaultPrettyPrinter()
        val jsonRequest = objectWriter.writeValueAsString(serviceDto)
        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/deploy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andReturn()
        Assertions.assertThat(mvcResult.response.status).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(mvcResult.response.contentAsString).isEqualTo("1")

    }

    @Test
    fun shouldKeepDeploymentUnchanged() {
        val serviceDto = ServiceDto(name = "Service A", version = 1)
        val deployment = deploymentRepository.save(
            Deployment(
                systemVersion = 5, deployedApplicationServices = arrayListOf(
                    ApplicationService(serviceName = serviceDto.name, version = serviceDto.version)
                )
            )
        )
        val objectMapper = ObjectMapper()
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        val objectWriter = objectMapper.writer().withDefaultPrettyPrinter()
        val jsonRequest = objectWriter.writeValueAsString(serviceDto)
        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/deploy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
        ).andReturn()
        Assertions.assertThat(mvcResult.response.status).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(mvcResult.response.contentAsString).isEqualTo(deployment.systemVersion.toString())
    }

    @Test
    fun shouldServicesForVersionNumber() {
        val deployment = deploymentRepository.save(
            Deployment(
                systemVersion = 5, deployedApplicationServices = arrayListOf(
                    ApplicationService(serviceName = "Service A", version = 6)
                )
            )
        )
        val objectMapper = ObjectMapper()
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        objectMapper.findAndRegisterModules()
        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.get("/services?systemVersion=5")
        ).andReturn()
        val deploymentDto = objectMapper.readValue(mvcResult.response.contentAsString, DeploymentDto::class.java)
        Assertions.assertThat(mvcResult.response.status).isEqualTo(HttpStatus.OK.value())
        Assertions.assertThat(deploymentDto.deployedApplicationServices.size).isEqualTo(1)
        Assertions.assertThat(deploymentDto.deployedApplicationServices[0].name).isEqualTo("Service A")
        Assertions.assertThat(deploymentDto.deployedApplicationServices[0].version).isEqualTo(6)
    }
}