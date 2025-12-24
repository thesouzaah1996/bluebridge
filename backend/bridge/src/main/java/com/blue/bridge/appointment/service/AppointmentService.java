package com.blue.bridge.appointment.service;

import com.blue.bridge.appointment.dto.AppointmentDTO;
import com.blue.bridge.res.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<AppointmentDTO> cancelAppointment(Long appointmentId);

    Response<?> complementAppointment(Long appointmentId);
}
