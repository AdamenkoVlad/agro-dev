package com.abi.agro_back.service;

import com.abi.agro_back.collection.ServiceRequest;

import java.util.List;

public interface ServiceRequestService {
    ServiceRequest createServiceRequest(ServiceRequest serviceRequest);

    ServiceRequest getServiceRequestById(String serviceRequestId);

    List<ServiceRequest> getAllServiceRequests();

    ServiceRequest updateServiceRequest(String serviceRequestId, ServiceRequest updatedServiceRequest);

    void deleteServiceRequest(String serviceRequestId);

}
