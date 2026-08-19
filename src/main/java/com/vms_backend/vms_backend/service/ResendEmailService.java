package com.vms_backend.vms_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResendEmailService {

    private final Resend resend;

    @Value("${resend.from:onboarding@resend.dev}")
    private String fromAddress;

    @Value("${app.frontend-url}")
    private String frontendUrl;


    // ============================================================
    // COMMON RESEND EMAIL SENDER
    // ============================================================

    private void sendEmail(
            String to,
            String subject,
            String htmlContent) {

        if (to == null || to.isBlank()) {
            log.warn("Email not sent: recipient email is empty.");
            return;
        }

        try {

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response =
                    resend.emails().send(params);

            log.info(
                    "Resend email sent successfully to {} | Resend ID: {}",
                    to,
                    response.getId()
            );

        } catch (ResendException e) {

            log.error(
                    "Failed to send Resend email to {}: {}",
                    to,
                    e.getMessage(),
                    e
            );
        }
    }


    // ============================================================
    // MEETING STATUS EMAIL
    // ============================================================

    /**
     * Sends a meeting status email with optional action link.
     */
    public void sendMeetingStatusEmail(
            String to,
            String visitorName,
            String hostName,
            String statusLabel,
            String statusColor,
            String date,
            String time,
            String message,
            String actionUrl,
            String actionLabel) {

        if (to == null || to.isBlank()) {
            return;
        }

        String html = buildSimpleHtml(
                visitorName,
                hostName,
                statusLabel,
                date,
                time,
                message,
                actionUrl,
                actionLabel
        );

        sendEmail(
                to,
                "Meeting " + statusLabel + " — Visitor Management",
                html
        );
    }


    /**
     * Overload without action link.
     */
    public void sendMeetingStatusEmail(
            String to,
            String visitorName,
            String hostName,
            String statusLabel,
            String statusColor,
            String date,
            String time,
            String message) {

        sendMeetingStatusEmail(
                to,
                visitorName,
                hostName,
                statusLabel,
                statusColor,
                date,
                time,
                message,
                null,
                null
        );
    }


    // ============================================================
    // HOST APPROVAL EMAIL
    // ============================================================

    /**
     * Sends the HOST an approval request with a direct link.
     */
    public void sendHostApprovalEmail(
            String hostEmail,
            String visitorName,
            String hostName,
            String registeredDate,
            String date,
            String time,
            String hostId,
            String mobileNo) {

        String actionUrl =
                frontendUrl
                        + "/HostApproval?hostId="
                        + hostId
                        + "&mobileNo="
                        + mobileNo;

        String html = buildHostApprovalHtml(
                hostName,
                visitorName,
                registeredDate,
                date,
                time,
                actionUrl
        );

        sendEmail(
                hostEmail,
                "Meeting Request — Visitor Management",
                html
        );
    }


    // ============================================================
    // HOST APPROVAL HTML
    // ============================================================

    private String buildHostApprovalHtml(
            String hostName,
            String visitorName,
            String registeredDate,
            String date,
            String time,
            String actionUrl) {

        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">

              <p>Dear <b>%s</b>,</p>

              <p>
                Mr./Ms. <b>%s</b>, who registered on <b>%s</b>,
                has requested a meeting with you on
                <b>%s at %s</b>.
              </p>

              <p>
                Please click the link below to take the required action:
              </p>

              <p>
                <b>Approve / Postpone / Reject</b>
              </p>

              <p>
                <a href="%s">%s</a>
              </p>

              <p>
                Regards,<br>
                <b>Visitor Management System</b>
              </p>

            </body>
            </html>
            """.formatted(
                hostName,
                visitorName,
                registeredDate,
                date,
                time,
                actionUrl,
                actionUrl
        );
    }


    // ============================================================
    // SIMPLE HTML TEMPLATE
    // ============================================================

    /**
     * Common HTML template used for:
     * Approved
     * Rejected
     * On Hold
     * Meeting Invitation
     * Request Submitted
     */
    private String buildSimpleHtml(
            String visitorName,
            String hostName,
            String statusLabel,
            String date,
            String time,
            String message,
            String actionUrl,
            String actionLabel) {

        String linkBlock = "";

        if (actionUrl != null && !actionUrl.isBlank()) {

            linkBlock = """
                <p><b>%s</b></p>
                <p>
                    <a href="%s">%s</a>
                </p>
                """.formatted(
                    actionLabel != null
                            ? actionLabel
                            : "View Details",
                    actionUrl,
                    actionUrl
            );
        }

        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">

              <p>Dear <b>%s</b>,</p>

              <p>%s</p>

              <p>
                 Host: <b>%s</b><br>
                 Date: <b>%s</b><br>
                 Time: <b>%s</b><br>
                 Status: <b>%s</b>
              </p>

              %s

              <p>
                Regards,<br>
                <b>Visitor Management System</b>
              </p>

            </body>
            </html>
            """.formatted(
                visitorName,
                message,
                hostName,
                date,
                time,
                statusLabel,
                linkBlock
        );
    }


    // ============================================================
    // HOST APPROVAL REQUEST - SIMPLE STYLE
    // ============================================================

    /**
     * Convenience method for sending the host an approval request.
     */
    public void sendHostApprovalRequestEmail(
            String hostEmail,
            String hostName,
            String visitorName,
            String date,
            String time,
            String approvalUrl) {

        sendMeetingStatusEmail(
                hostEmail,
                visitorName,
                hostName,
                "Pending Approval",
                "#f59e0b",
                date,
                time,
                visitorName
                        + " has requested a meeting with you. "
                        + "Please review and respond.",
                approvalUrl,
                "Review Request"
        );
    }


    // ============================================================
    // VISITOR APPROVAL LINK EMAIL
    // ============================================================

    /**
     * Sends the visitor a link to check meeting status.
     */
    public void sendVisitorApprovalLinkEmail(
            String visitorEmail,
            String visitorName,
            String hostName,
            String date,
            String time,
            String token) {

        String statusUrl =
                frontendUrl
                        + "/meeting-status?token="
                        + token;

        sendMeetingStatusEmail(
                visitorEmail,
                visitorName,
                hostName,
                "Request Submitted",
                "#4f46e5",
                date,
                time,
                "Your meeting request has been sent to "
                        + hostName
                        + ". You can track its status using the link below.",
                statusUrl,
                "Check Status"
        );
    }


    // ============================================================
    // VISITOR STATUS EMAIL
    // ============================================================

    /**
     * Sends the visitor a status update.
     *
     * Used for:
     * Approved
     * Rejected
     * On Hold
     */
    public void sendVisitorStatusEmail(
            String to,
            String visitorName,
            String hostName,
            String statusLabel,
            String statusColor,
            String date,
            String time,
            String message,
            String passLink) {

        if (to == null || to.isBlank()) {
            return;
        }

        sendMeetingStatusEmail(
                to,
                visitorName,
                hostName,
                statusLabel,
                statusColor,
                date,
                time,
                message,
                passLink,
                "View Meeting Status"
        );
    }


    // ============================================================
    // PARTICIPANT INVITATION EMAIL
    // ============================================================

    /**
     * Sends an employee meeting invitation to a participant.
     */
    public void sendParticipantInviteEmail(
            String to,
            String participantName,
            String organizerName,
            String date,
            String time,
            String token) {

        if (to == null || to.isBlank()) {
            return;
        }

        String reviewUrl =
                frontendUrl
                        + "/participant-response?token="
                        + token;

        sendMeetingStatusEmail(
                to,
                participantName,
                organizerName,
                "Meeting Invitation",
                "#4f46e5",
                date,
                time,
                organizerName
                        + " has scheduled a meeting with you. "
                        + "Please review the details and respond "
                        + "using the link below.",
                reviewUrl,
                "Review & Respond"
        );
    }


    // ============================================================
    // HOST APPROVED EMAIL / GATEPASS
    // ============================================================

    /**
     * Sends the host confirmation that the visitor meeting
     * has been approved and the gatepass has been generated.
     */
    public void sendHostApprovedEmail(
            String hostEmail,
            String visitorName,
            String hostName,
            String date,
            String time,
            String passNo,
            String passLink) {

        String html = buildHostApprovedHtml(
                hostName,
                visitorName,
                date,
                time,
                passNo,
                passLink
        );

        sendEmail(
                hostEmail,
                "Visitor Meeting Approved",
                html
        );
    }


    // ============================================================
    // HOST APPROVED HTML
    // ============================================================

    private String buildHostApprovedHtml(
            String hostName,
            String visitorName,
            String date,
            String time,
            String passNo,
            String passLink) {

        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">

              <p>Dear <b>%s</b>,</p>

              <p>
                Your meeting with <b>%s</b> has been
                successfully approved.
              </p>

              <p>
                <b>Date:</b> %s<br>
                <b>Time:</b> %s<br>
                <b>Status:</b>
                <span style="color:#16a34a;">
                    <b>Approved</b>
                </span>
              </p>

              <p>
                <b>Visitor Pass No:</b> %s
              </p>

              <p>
                You can view the visitor's gatepass using the link below:
              </p>

              <p>
                <a href="%s">%s</a>
              </p>

              <p>
                Regards,<br>
                <b>Visitor Management System</b>
              </p>

            </body>
            </html>
            """.formatted(
                hostName,
                visitorName,
                date,
                time,
                passNo,
                passLink,
                passLink
        );
    }
}