package com.tset.releasemanager.model

import jakarta.persistence.*

@Entity
class Deployment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "deployment_seq_gen")
    @SequenceGenerator(name = "service_seq_gen", sequenceName = "DEPLOYMENT_SEQUENCE")
    var id: Long? = null,
    @Column(unique = true)
    var systemVersion: Int?,
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var deployedApplicationServices: MutableList<ApplicationService> = arrayListOf(),
) {
    fun addService(service: ApplicationService) {
        deployedApplicationServices.add(service)
    }

    fun containsService(service: ApplicationService?): Boolean {
        return deployedApplicationServices.contains(service)
    }

    fun retrieveByServiceNameIfExist(serviceName: String?): ApplicationService? {
        return deployedApplicationServices.stream()
            .filter { it.serviceName.equals(serviceName) }
            .findFirst().orElse(null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Deployment

        if (id != other.id) return false
        if (systemVersion != other.systemVersion) return false
        if (deployedApplicationServices != other.deployedApplicationServices) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (systemVersion ?: 0)
        result = 31 * result + deployedApplicationServices.hashCode()
        return result
    }

    override fun toString(): String {
        return "Deployment(id=$id, systemVersion=$systemVersion)"
    }


}