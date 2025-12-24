package com.blue.bridge.consultation.service;

import com.blue.bridge.consultation.dto.ConsultationDTO;
import com.blue.bridge.res.Response;

import java.util.List;

public interface ConsultationService {

    Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);

    Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);

    Response<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId);
}
