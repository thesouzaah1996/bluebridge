package com.blue.bridge.appointment.service;

import com.blue.bridge.appointment.dto.AppointmentDTO;
import com.blue.bridge.appointment.entity.Appointment;
import com.blue.bridge.appointment.repo.AppointmentRepo;
import com.blue.bridge.doctor.entity.Doctor;
import com.blue.bridge.doctor.repo.DoctorRepository;
import com.blue.bridge.enums.AppointmentStatus;
import com.blue.bridge.exceptions.BadRequestException;
import com.blue.bridge.exceptions.NotFoundException;
import com.blue.bridge.notification.dto.NotificationDTO;
import com.blue.bridge.notification.service.NotificationService;
import com.blue.bridge.patient.entity.Patient;
import com.blue.bridge.patient.repo.PatientRepo;
import com.blue.bridge.res.Response;
import com.blue.bridge.users.entity.User;
import com.blue.bridge.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImp implements AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");

    @Override
    public Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO) {

        User currentUser = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Patient profile required for booking."));

        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found."));

        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(60);

        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Appointments must be booked at least 1 hour in advance.");
        }

        LocalDateTime checkStart = startTime.minusMinutes(60);

        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                doctor.getId(),
                checkStart,
                endTime
        );

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Doctor is not available at the requested time. Please check their schedule.");
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueRoomName = "dat-" + uuid.substring(0, 10);

        String meetingLink = "https://meet.jit.si/" + uniqueRoomName;

        log.info("Generated Jitsi meeting link: {}", meetingLink);

        Appointment appointment = Appointment.builder()
                .startTime(appointmentDTO.getStartTime())
                .endTime(appointmentDTO.getStartTime().plusMinutes(60))
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();

        Appointment savedAppointment = appointmentRepo.save(appointment);

        sendAppointmentConfirmation(savedAppointment);

        return Response.<AppointmentDTO>builder()
                .statusCode(200)
                .message("Appointment booked successfully.")
                .build();
    }

    @Override
    public Response<List<AppointmentDTO>> getMyAppointments()
    {

        User user = userService.getCurrentUser();

        Long userId = user.getId();

        List<Appointment> appointments;

        boolean isDoctor = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("DOCTOR"));

        if (isDoctor) {
            doctorRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Doctor profile not found"));

            appointments = appointmentRepo.findByDoctor_User_IdOrderByIdDesc(userId);
        } else {
            patientRepo.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Patient profile not found."));

            appointments = appointmentRepo.findByPatient_User_IdOrderByIdDesc(userId);
        }

        List<AppointmentDTO> appointmentDTOList = appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .toList();

        return Response.<List<AppointmentDTO>>builder()
                .statusCode(200)
                .message("Appointments retrieved successfully.")
                .data(appointmentDTOList)
                .build();

    }

    @Override
    public Response<AppointmentDTO> cancelAppointment(Long appointmentId) {

        User user = userService.getCurrentUser();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found."));

        boolean isOwner = appointment.getPatient().getUser().getId().equals(user.getId()) ||
                appointment.getDoctor().getUser().getId().equals(user.getId());

        if (!isOwner) {
            throw new BadRequestException("You do not have permission to cancel this appointment.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepo.save(appointment);

        sendAppointmentCancellation(savedAppointment, user);

        return Response.<AppointmentDTO>builder()
                .statusCode(200)
                .message("Appointment cancelled successfully.")
                .build();

    }

    @Override
    public Response<?> complementAppointment(Long appointmentId) {

        User currentUser = userService.getCurrentUser();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the assigned doctor can mark this appointment as complete.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepo.save(appointment);

        modelMapper.map(updatedAppointment, AppointmentDTO.class);

        return Response.builder()
                .statusCode(200)
                .message("Appointment successfully marked as completed. You may now proceed to create the consultation notes.")
                .build();
    }

    private void sendAppointmentConfirmation(Appointment appointment) {

        User patientUser = appointment.getPatient().getUser();
        String formattedTime = appointment.getStartTime().format(FORMATTER);

        Map<String, Object> patientVars = new HashMap<>();
        patientVars.put("patientName", patientUser.getName());
        patientVars.put("doctorName", appointment.getDoctor().getUser().getName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Your Appointment is Confirmed")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched confirmation email for patient: {}", patientUser.getEmail());

        User doctorUser = appointment.getDoctor().getUser();

        Map<String, Object> doctorVars = new HashMap<>();
        doctorVars.put("doctorName", doctorUser.getName());
        doctorVars.put("patientFullName", patientUser.getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("initialSymptoms", appointment.getInitialSymptoms());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: New Appointment Booked")
                .templateName("doctor-appointment")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched new appointment email for doctor: {}", doctorUser.getEmail());
    }

    private void sendAppointmentCancellation(Appointment appointment, User cancelingUser){

        User patientUser = appointment.getPatient().getUser();
        User doctorUser = appointment.getDoctor().getUser();

        boolean isOwner = patientUser.getId().equals(cancelingUser.getId()) || doctorUser.getId().equals(cancelingUser.getId());
        if (!isOwner) {
            log.error("Cancellation initiated by user not associated with appointment. User ID: {}", cancelingUser.getId());
            return;
        }

        String formattedTime = appointment.getStartTime().format(FORMATTER);
        String cancellingPartyName = cancelingUser.getName();

        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", cancellingPartyName);
        baseVars.put("appointmentTime", formattedTime);
        baseVars.put("doctorName", appointment.getDoctor().getLastName());
        baseVars.put("patientFullName", patientUser.getName());

        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        doctorVars.put("recipientName", doctorUser.getName());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("Blue Bridge Health: Appointment Cancellation")
                .templateName("appointment-cancellation")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched cancellation email to Doctor: {}", doctorUser.getEmail());


        Map<String, Object> patientVars = new HashMap<>(baseVars);
        patientVars.put("recipientName", patientUser.getName());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("Blue Bridge: Appointment CANCELED (ID: " + appointment.getId() + ")")
                .templateName("appointment-cancellation")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched cancellation email to Patient: {}", patientUser.getEmail());
    }
}




























