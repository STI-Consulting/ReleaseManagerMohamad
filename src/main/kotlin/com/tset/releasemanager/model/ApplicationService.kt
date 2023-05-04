package com.tset.releasemanager.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator

@Entity
class ApplicationService(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "service_seq_gen")
    @SequenceGenerator(name = "service_seq_gen", sequenceName = "APPLICATION_SERVICE_SEQUENCE")
    var id: Long? = null,
    var serviceName: String?,
    var version: Int?,
/*    @ManyToOne
    var deployment: Deployment? = null,*/
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicationService

        if (id != other.id) return false
        if (serviceName != other.serviceName) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (serviceName?.hashCode() ?: 0)
        result = 31 * result + (version ?: 0)
        return result
    }

    override fun toString(): String {
        return "Service(id=$id, serviceName=$serviceName, version=$version)"
    }


}