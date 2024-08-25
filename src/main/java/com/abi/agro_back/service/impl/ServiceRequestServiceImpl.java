package com.abi.agro_back.service.impl;

import com.abi.agro_back.collection.ServiceRequest;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.repository.ServiceRequestRepository;
import com.abi.agro_back.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Override
    public ServiceRequest createServiceRequest(ServiceRequest serviceRequest) {

        return serviceRequestRepository.save(serviceRequest);
    }

    @Override
    public ServiceRequest getServiceRequestById(String serviceRequestId) {

        return serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceRequest is not exists with given id : " + serviceRequestId));
    }

    @Override
    public List<ServiceRequest> getAllServiceRequests() {

        return serviceRequestRepository.findAll();
    }

    @Override
    public ServiceRequest updateServiceRequest(String serviceRequestId, ServiceRequest updatedServiceRequest) {

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId).orElseThrow(
                () -> new ResourceNotFoundException("ServiceRequest is not exists with given id: " + serviceRequestId)
        );

        serviceRequest.setFirstName(updatedServiceRequest.getFirstName());
        serviceRequest.setLastName(updatedServiceRequest.getLastName());
        serviceRequest.setPhone(updatedServiceRequest.getPhone());
        serviceRequest.setEmail(updatedServiceRequest.getEmail());

        return serviceRequestRepository.save(serviceRequest);
    }

    @Override
    public void deleteServiceRequest(String serviceRequestId) {

       ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ServiceRequest is not exists with given id : " + serviceRequestId));

       serviceRequestRepository.deleteById(serviceRequestId);
    }
}
