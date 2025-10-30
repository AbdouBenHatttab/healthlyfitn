// // package com.healthapp.doctor.client;

// // import com.healthapp.doctor.dto.request.EmailNotificationRequest;
// // import org.springframework.cloud.openfeign.FeignClient;
// // import org.springframework.web.bind.annotation.PostMapping;
// // import org.springframework.web.bind.annotation.RequestBody;

// // /**
// //  * Client Feign pour communiquer avec le Notification Service
// //  */
// // @FeignClient(name = "notification-service")
// // public interface NotificationClient {
    
// //     @PostMapping("/api/notifications/email")
// //     void sendEmail(@RequestBody EmailNotificationRequest request);
// // }

// package com.healthapp.doctor.client;

// import com.healthapp.doctor.dto.request.EmailNotificationRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;


// @Component
// @Slf4j
// public class NotificationClient {
    
//     public void sendEmail(EmailNotificationRequest request) {
//         log.info("═══════════════════════════════════════════════════════");
//         log.info("📧 EMAIL NOTIFICATION SENT");
//         log.info("═══════════════════════════════════════════════════════");
//         log.info("To: {}", request.getTo());
//         log.info("Subject: {}", request.getSubject());
//         log.info("Template: {}", request.getTemplateType());
//         log.info("Variables: {}", request.getTemplateVariables());
//         log.info("═══════════════════════════════════════════════════════");
        
//         // Simuler l'envoi d'email basé sur le template
//         if ("DOCTOR_REGISTRATION_PENDING".equals(request.getTemplateType())) {
//             log.info("📧 Message au Doctor:");
//             log.info("   Bonjour {},", request.getTemplateVariables().get("doctorName"));
//             log.info("   Votre compte est en cours de validation.");
//             log.info("   Vous recevrez un email dès que votre compte sera approuvé.");
//         } 
//         else if ("DOCTOR_ACTIVATION_CONFIRMATION".equals(request.getTemplateType())) {
//             log.info("📧 Message au Doctor:");
//             log.info("   Bonjour {},", request.getTemplateVariables().get("doctorFirstName"));
//             log.info("   ✅ Votre compte a été activé avec succès!");
//             log.info("   Vous pouvez maintenant vous connecter à la plateforme.");
//         }
//         else if ("DOCTOR_ACTIVATION_REJECTION".equals(request.getTemplateType())) {
//             log.info("📧 Message au Doctor:");
//             log.info("   Bonjour {},", request.getTemplateVariables().get("doctorLastName"));
//             log.info("   ❌ Votre demande d'inscription a été rejetée.");
//             log.info("   Raison: {}", request.getTemplateVariables().get("reason"));
//         }
//         else if ("DOCTOR_REGISTRATION_ADMIN_NOTIFICATION".equals(request.getTemplateType())) {
//             log.info("📧 Message aux Admins:");
//             log.info("   🏥 Nouveau médecin en attente d'approbation:");
//             log.info("   Nom: {}", request.getTemplateVariables().get("doctorName"));
//             log.info("   Email: {}", request.getTemplateVariables().get("doctorEmail"));
//             log.info("   Licence: {}", request.getTemplateVariables().get("medicalLicense"));
//             log.info("   Spécialisation: {}", request.getTemplateVariables().get("specialization"));
//         }
        
//         log.info("═══════════════════════════════════════════════════════");
//     }
// }



package com.healthapp.doctor.client;

import com.healthapp.doctor.dto.request.EmailNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ✅ Client Feign pour communiquer avec le Notification Service
 */
@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {
    
    @PostMapping("/api/notifications/email")
    void sendEmail(@RequestBody EmailNotificationRequest request);
}