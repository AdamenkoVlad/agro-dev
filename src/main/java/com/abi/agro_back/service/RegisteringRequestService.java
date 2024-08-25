package com.abi.agro_back.service;

import com.abi.agro_back.auth.AuthenticationResponse;
import com.abi.agro_back.collection.RegisteringRequest;
import com.abi.agro_back.collection.Role;

import java.util.List;

public interface RegisteringRequestService {
    RegisteringRequest createRegisteringRequest(RegisteringRequest registeringRequest);

    RegisteringRequest getRegisteringRequestById(String registeringRequestId);

    List<RegisteringRequest> getAllRegisteringRequests();

    RegisteringRequest updateRegisteringRequest(String registeringRequestId, RegisteringRequest updatedRegisteringRequest);

    void deleteRegisteringRequest(String registeringRequestId);

    AuthenticationResponse approveRegisteringRequestById(String id, Role role, List<String> permissions);
}
