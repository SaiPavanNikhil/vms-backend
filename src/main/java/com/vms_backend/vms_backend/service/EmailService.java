//package com.vms_backend.vms_backend.service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String fromAddress;
//
//    @Value("${app.frontend-url}")
//    private String frontendUrl;
//
//    public EmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    /** Sends a simple plain-text email (with a clickable link) for meeting status updates. */
//    public void sendMeetingStatusEmail(String to, String visitorName, String hostName,
//                                        String statusLabel, String statusColor,
//                                        String date, String time, String message,
//                                        String actionUrl, String actionLabel) {
//        if (to == null || to.isBlank()) return;
//
//        String html = buildSimpleHtml(visitorName, hostName, statusLabel, date, time, message, actionUrl, actionLabel);
//
//        try {
//            MimeMessage mime = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
//            helper.setFrom(fromAddress);
//            helper.setTo(to);
//            helper.setSubject("Meeting " + statusLabel + " — Visitor Management");
//            helper.setText(html, true);
//            mailSender.send(mime);
//        } catch (MessagingException e) {
//            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
//        }
//    }
//
//    /** Overload for emails with no action link (keeps old calls working). */
//    public void sendMeetingStatusEmail(String to, String visitorName, String hostName,
//                                        String statusLabel, String statusColor,
//                                        String date, String time, String message) {
//        sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor,
//                                date, time, message, null, null);
//    }
//
//    /** Convenience method: sends the HOST an approval request with a direct link. */
//    public void sendHostApprovalEmail(String hostEmail, String visitorName, String hostName,
//                                       String registeredDate, String date, String time,
//                                       String hostId, String mobileNo) {
//        String actionUrl = frontendUrl + "/HostApproval?hostId=" + hostId + "&mobileNo=" + mobileNo;
//        String html = buildHostApprovalHtml(hostName, visitorName, registeredDate, date, time, actionUrl);
//
//        try {
//            MimeMessage mime = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
//            helper.setFrom(fromAddress);
//            helper.setTo(hostEmail);
//            helper.setSubject("Meeting Request — Visitor Management");
//            helper.setText(html, true);
//            mailSender.send(mime);
//        } catch (MessagingException e) {
//            System.err.println("Failed to send host approval email to " + hostEmail + ": " + e.getMessage());
//        }
//    }
//
//    private String buildHostApprovalHtml(String hostName, String visitorName, String registeredDate,
//                                          String date, String time, String actionUrl) {
//        return """
//            <!DOCTYPE html>
//            <html>
//            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
//              <p>Dear <b>%s</b>,</p>
//              <p>Mr./Ms. <b>%s</b>, who registered on <b>%s</b>, has requested a meeting with you on
//                 <b>%s at %s</b>.</p>
//              <p>Please click the link below to take the required action:</p>
//              <p><b>Approve / Postpone / Reject</b></p>
//              <p><a href="%s">%s</a></p>
//              <p>Regards,<br><b>Visitor Management System</b></p>
//            </body>
//            </html>
//            """.formatted(hostName, visitorName, registeredDate, date, time, actionUrl, actionUrl);
//    }
//
//    /** Simple plain-text template used for all status update emails (approved/rejected/hold/invite/etc). */
//    private String buildSimpleHtml(String visitorName, String hostName, String statusLabel,
//                                    String date, String time, String message,
//                                    String actionUrl, String actionLabel) {
//        String linkBlock = "";
//        if (actionUrl != null && !actionUrl.isBlank()) {
//            linkBlock = """
//                <p><b>%s</b></p>
//                <p><a href="%s">%s</a></p>
//                """.formatted(actionLabel != null ? actionLabel : "View Details", actionUrl, actionUrl);
//        }
//
//        return """
//            <!DOCTYPE html>
//            <html>
//            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
//              <p>Dear <b>%s</b>,</p>
//              <p>%s</p>
//              <p>Host: <b>%s</b><br>
//                 Date: <b>%s</b><br>
//                 Time: <b>%s</b><br>
//                 Status: <b>%s</b></p>
//              %s
//              <p>Regards,<br><b>Visitor Management System</b></p>
//            </body>
//            </html>
//            """.formatted(visitorName, message, hostName, date, time, statusLabel, linkBlock);
//    }
//
//    /** Convenience method: sends the HOST an approval request with a direct link (simple text style). */
//    public void sendHostApprovalRequestEmail(String hostEmail, String hostName, String visitorName,
//            String date, String time, String approvalUrl) {
//        sendMeetingStatusEmail(hostEmail, visitorName, hostName,
//                "Pending Approval", "#f59e0b",
//                date, time,
//                visitorName + " has requested a meeting with you. Please review and respond.",
//                approvalUrl, "Review Request");
//    }
//
//    /** Convenience method: sends the VISITOR a link to check their meeting status. */
//    public void sendVisitorApprovalLinkEmail(String visitorEmail, String visitorName, String hostName,
//                                              String date, String time, String token) {
//        String statusUrl = frontendUrl + "/meeting-status?token=" + token;
//        sendMeetingStatusEmail(visitorEmail, visitorName, hostName,
//                "Request Submitted", "#4f46e5",
//                date, time,
//                "Your meeting request has been sent to " + hostName + ". You can track its status using the link below.",
//                statusUrl, "Check Status");
//    }
//
//    /** Sends the VISITOR a status-update email (approved/rejected/hold) with a link to check details. */
//    public void sendVisitorStatusEmail(String to, String visitorName, String hostName,
//                                        String statusLabel, String statusColor,
//                                        String date, String time, String message, String token) {
//        if (to == null || to.isBlank()) return;
//
//        String statusUrl = frontendUrl + "/meeting-status?token=" + token;
//        sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor,
//                                date, time, message, statusUrl, "View Meeting Status");
//    }
//
//    /** Sends the participant an interview/meeting invite with a link to review and respond. */
//    public void sendParticipantInviteEmail(String to, String participantName, String organizerName,
//                                            String date, String time, String token) {
//        if (to == null || to.isBlank()) return;
//
//        String reviewUrl = frontendUrl + "/participant-response?token=" + token;
//        sendMeetingStatusEmail(to, participantName, organizerName,
//                "Meeting Invitation", "#4f46e5",
//                date, time,
//                organizerName + " has scheduled a meeting with you. Please review the details and respond using the link below.",
//                reviewUrl, "Review & Respond");
//    }
//}
////package com.vms_backend.vms_backend.service;
////
////import jakarta.mail.MessagingException;
////import jakarta.mail.internet.MimeMessage;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.mail.javamail.JavaMailSender;
////import org.springframework.mail.javamail.MimeMessageHelper;
////import org.springframework.stereotype.Service;
////
////@Service
////public class EmailService {
////
////	private final JavaMailSender mailSender;
////
////	@Value("${spring.mail.username}")
////	private String fromAddress;
////
////	@Value("${app.frontend-url}")
////	private String frontendUrl;
////
////	public EmailService(JavaMailSender mailSender) {
////		this.mailSender = mailSender;
////	}
////
////	/**
////	 * Sends a styled HTML card email for meeting status updates, with an optional
////	 * action button.
////	 */
//////	public void sendMeetingStatusEmail(String to, String visitorName, String hostName, String statusLabel,
//////			String statusColor, String date, String time, String message, String actionUrl, String actionLabel) {
//////		if (to == null || to.isBlank())
//////			return;
//////
//////		String html = buildCardHtml(visitorName, hostName, statusLabel, statusColor, date, time, message, actionUrl,
//////				actionLabel);
//////
//////		try {
//////			MimeMessage mime = mailSender.createMimeMessage();
//////			MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
//////			helper.setFrom(fromAddress);
//////			helper.setTo(to);
//////			helper.setSubject("Meeting " + statusLabel + " — Visitor Management");
//////			helper.setText(html, true);
//////			mailSender.send(mime);
//////		} catch (MessagingException e) {
//////			System.err.println("Failed to send email to " + to + ": " + e.getMessage());
//////		}
//////	}
////
////	public void sendMeetingStatusEmail(String to, String visitorName, String hostName, String statusLabel,
////			String statusColor, String date, String time, String message, String actionUrl, String actionLabel) {
////		if (to == null || to.isBlank())
////			return;
////
////		String html = buildCardHtml(visitorName, hostName, statusLabel, statusColor, date, time, message, actionUrl,
////				actionLabel);
////
////		try {
////			MimeMessage mime = mailSender.createMimeMessage();
////			MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
////			helper.setFrom(fromAddress);
////			helper.setTo(to);
////			helper.setSubject("Meeting " + statusLabel + " — Visitor Management");
////			helper.setText(html, true);
////			mailSender.send(mime);
////		} catch (MessagingException e) {
////			System.err.println("Failed to send email to " + to + ": " + e.getMessage());
////		}
////	}
////
////	/** Overload for emails with no action button (keeps old calls working). */
////	public void sendMeetingStatusEmail(String to, String visitorName, String hostName, String statusLabel,
////			String statusColor, String date, String time, String message) {
////		sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor, date, time, message, null, null);
////	}
////
////	/**
////	 * Convenience method: sends the HOST an approval request with Approve/Reject
////	 * links.
////	 */
////	/**
////	 * Convenience method: sends the HOST an approval request with a direct link.
////	 */
////	public void sendHostApprovalEmail(String hostEmail, String visitorName, String hostName,
////            String registeredDate, String date, String time,
////            String hostId, String mobileNo) {
////String actionUrl = frontendUrl + "/HostApproval?hostId=" + hostId + "&mobileNo=" + mobileNo;
////String html = buildHostApprovalHtml(hostName, visitorName, registeredDate, date, time, actionUrl);
////
////try {
////MimeMessage mime = mailSender.createMimeMessage();
////MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
////helper.setFrom(fromAddress);
////helper.setTo(hostEmail);
////helper.setSubject("Meeting Request — Visitor Management");
////helper.setText(html, true);
////mailSender.send(mime);
////} catch (MessagingException e) {
////System.err.println("Failed to send host approval email to " + hostEmail + ": " + e.getMessage());
////}
////}
////
////private String buildHostApprovalHtml(String hostName, String visitorName, String registeredDate,
////               String date, String time, String actionUrl) {
////return """
////<!DOCTYPE html>
////<html>
////<body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
////<p>Dear <b>%s</b>,</p>
////<p>Mr./Ms. <b>%s</b>, who registered on <b>%s</b>, has requested a meeting with you on
////<b>%s at %s</b>.</p>
////<p>Please click the link below to take the required action:</p>
////<p><b>Approve / Postpone / Reject</b></p>
////<p><a href="%s">%s</a></p>
////<p>Regards,<br><b>Visitor Management System</b></p>
////</body>
////</html>
////""".formatted(hostName, visitorName, registeredDate, date, time, actionUrl, actionUrl);
////}
////
////	/** Overload for emails with no action button (keeps old calls working). */
//////    public void sendMeetingStatusEmail(String to, String visitorName, String hostName,
//////                                        String statusLabel, String statusColor,
//////                                        String date, String time, String message) {
//////        sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor,
//////                                date, time, message, null, null);
//////    }
//////
//////    /** Convenience method: sends the HOST an approval request with Approve/Reject links. */
//////    /** Convenience method: sends the HOST an approval request with Approve/Reject links. */
//////    /** Convenience method: sends the HOST an approval request with a direct link. */
//////    public void sendHostApprovalEmail(String hostEmail, String visitorName, String hostName,
//////                                       String registeredDate, String date, String time,
//////                                       String hostId, String mobileNo) {
//////        String actionUrl = frontendUrl + "/HostApproval?hostId=" + hostId + "&mobileNo=" + mobileNo;
//////        String html = buildHostApprovalHtml(hostName, visitorName, registeredDate, date, time, actionUrl);
//////
//////        try {
//////            MimeMessage mime = mailSender.createMimeMessage();
//////            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
//////            helper.setFrom(fromAddress);
//////            helper.setTo(hostEmail);
//////            helper.setSubject("Meeting Request — Visitor Management");
//////            helper.setText(html, true);
//////            mailSender.send(mime);
//////        } catch (MessagingException e) {
//////            System.err.println("Failed to send host approval email to " + hostEmail + ": " + e.getMessage());
//////        }
//////    }
//////
//////    private String buildHostApprovalHtml(String hostName, String visitorName, String registeredDate,
//////                                          String date, String time, String actionUrl) {
//////        return """
//////            <!DOCTYPE html>
//////            <html>
//////            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
//////              <p>Dear <b>%s</b>,</p>
//////              <p>Mr./Ms. <b>%s</b>, who registered on <b>%s</b>, has requested a meeting with you on
//////                 <b>%s at %s</b>.</p>
//////              <p>Please click the link below to take the required action:</p>
//////              <p><b>Approve / Postpone / Reject</b></p>
//////              <p><a href="%s">%s</a></p>
//////              <p>Regards,<br><b>Visitor Management System</b></p>
//////            </body>
//////            </html>
//////            """.formatted(hostName, visitorName, registeredDate, date, time, actionUrl, actionUrl);
//////    }
////
////	/**
////	 * Convenience method: sends the VISITOR a link to check their meeting status.
////	 */
////	public void sendVisitorApprovalLinkEmail(String visitorEmail, String visitorName, String hostName, String date,
////			String time, String token) {
////		String statusUrl = frontendUrl + "/meeting-status?token=" + token;
////		sendMeetingStatusEmail(visitorEmail, visitorName, hostName, "Request Submitted", "#4f46e5", date, time,
////				"Your meeting request has been sent to " + hostName
////						+ ". You can track its status using the button below.",
////				statusUrl, "Check Status");
////	}
////
////	/**
////	 * Sends the VISITOR a status-update email (approved/rejected/hold) with a link
////	 * to check details.
////	 */
////	public void sendVisitorStatusEmail(String to, String visitorName, String hostName, String statusLabel,
////			String statusColor, String date, String time, String message, String token) {
////		if (to == null || to.isBlank())
////			return;
////
////		String statusUrl = frontendUrl + "/meeting-status?token=" + token;
////		sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor, date, time, message, statusUrl,
////				"View Meeting Status");
////	}
////
////	/**
////	 * Sends the participant an interview/meeting invite with a link to review and
////	 * respond.
////	 */
////	public void sendParticipantInviteEmail(String to, String participantName, String organizerName, String date,
////			String time, String token) {
////		if (to == null || to.isBlank())
////			return;
////
////		String reviewUrl = frontendUrl + "/participant-response?token=" + token;
////		String html = buildInviteHtml(participantName, organizerName, date, time, reviewUrl);
////
////		try {
////			MimeMessage mime = mailSender.createMimeMessage();
////			MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
////			helper.setFrom(fromAddress);
////			helper.setTo(to);
////			helper.setSubject("Meeting Invitation — Visitor Management");
////			helper.setText(html, true);
////			mailSender.send(mime);
////		} catch (MessagingException e) {
////			System.err.println("Failed to send invite email to " + to + ": " + e.getMessage());
////		}
////	}
////
////	public void sendHostApprovalRequestEmail(String hostEmail, String hostName, String visitorName, String date,
////			String time, String approvalUrl) {
////		sendMeetingStatusEmail(hostEmail, visitorName, hostName, "Pending Approval", "#f59e0b", date, time,
////				visitorName + " has requested a meeting with you. Please review and respond.", approvalUrl,
////				"Review Request");
////	}
////
////	private String buildInviteHtml(String participantName, String organizerName, String date, String time,
////			String reviewUrl) {
////		return """
////				<!DOCTYPE html>
////				<html>
////				<body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
////				  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
////				    <tr>
////				      <td align="center">
////				        <table width="480" cellpadding="0" cellspacing="0" style="background:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 2px 10px rgba(16,24,40,0.08);">
////
////				          <tr>
////				            <td style="background:#4f46e5; padding:28px 32px;">
////				              <span style="color:#ffffff; font-size:20px; font-weight:700;">Visitor Management</span>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:28px 32px 8px 32px;">
////				              <span style="display:inline-block; background:#f59e0b; color:#ffffff; font-size:12px; font-weight:600; padding:4px 12px; border-radius:20px;">Meeting Invitation</span>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:8px 32px 0 32px;">
////				              <p style="font-size:16px; color:#1e1b2e; margin:12px 0 0 0;">Hi <b>%s</b>,</p>
////				              <p style="font-size:14px; color:#52525b; line-height:1.6; margin:8px 0 20px 0;">
////				                %s has scheduled a meeting with you. Please review the details and respond using the link below.
////				              </p>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:0 32px 24px 32px;">
////				              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f8f8fc; border-radius:12px; border:1px solid #eef0f4;">
////				                <tr>
////				                  <td style="padding:16px 20px;">
////				                    <table width="100%%" cellpadding="0" cellspacing="0">
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Host</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Date</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Time</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                    </table>
////				                  </td>
////				                </tr>
////				              </table>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:0 32px 28px 32px;" align="center">
////				              <a href="%s" style="color:#4f46e5; font-size:14px; font-weight:600; text-decoration:underline; word-break:break-all;">%s</a>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="background:#fafafe; padding:16px 32px; border-top:1px solid #f0f0f4;">
////				              <p style="font-size:12px; color:#9494a3; margin:0;">Regards,<br>Visitor Management Team</p>
////				            </td>
////				          </tr>
////
////				        </table>
////				      </td>
////				    </tr>
////				  </table>
////				</body>
////				</html>
////				"""
////				.formatted(participantName, organizerName, organizerName, date, time, reviewUrl, reviewUrl);
////	}
////
////	private String buildCardHtml(String visitorName, String hostName, String statusLabel, String statusColor,
////			String date, String time, String message, String actionUrl, String actionLabel) {
////
////		String buttonHtml = "";
////		if (actionUrl != null && !actionUrl.isBlank()) {
////			buttonHtml = """
////					<tr>
////					  <td style="padding:0 32px 28px 32px;" align="center">
////					    <a href="%s" style="display:inline-block; background:#4f46e5; color:#ffffff;
////					       font-size:14px; font-weight:600; text-decoration:none; padding:12px 28px;
////					       border-radius:8px;">%s</a>
////					  </td>
////					</tr>
////					""".formatted(actionUrl, actionLabel != null ? actionLabel : "View Details");
////		}
////
////		return """
////				<!DOCTYPE html>
////				<html>
////				<body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
////				  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;">
////				    <tr>
////				      <td align="center">
////				        <table width="480" cellpadding="0" cellspacing="0" style="background:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 2px 10px rgba(16,24,40,0.08);">
////
////				          <tr>
////				            <td style="background:#4f46e5; padding:28px 32px;">
////				              <span style="color:#ffffff; font-size:20px; font-weight:700;">Visitor Management</span>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:28px 32px 8px 32px;">
////				              <span style="display:inline-block; background:%s; color:#ffffff; font-size:12px; font-weight:600; padding:4px 12px; border-radius:20px;">%s</span>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:8px 32px 0 32px;">
////				              <p style="font-size:16px; color:#1e1b2e; margin:12px 0 0 0;">Hi <b>%s</b>,</p>
////				              <p style="font-size:14px; color:#52525b; line-height:1.6; margin:8px 0 20px 0;">%s</p>
////				            </td>
////				          </tr>
////
////				          <tr>
////				            <td style="padding:0 32px 24px 32px;">
////				              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f8f8fc; border-radius:12px; border:1px solid #eef0f4;">
////				                <tr>
////				                  <td style="padding:16px 20px;">
////				                    <table width="100%%" cellpadding="0" cellspacing="0">
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Host</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Date</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                      <tr>
////				                        <td style="padding:6px 0; font-size:13px; color:#8a8a99;">Time</td>
////				                        <td style="padding:6px 0; font-size:13px; color:#1e1b2e; font-weight:600; text-align:right;">%s</td>
////				                      </tr>
////				                    </table>
////				                  </td>
////				                </tr>
////				              </table>
////				            </td>
////				          </tr>
////
////				          %s
////
////				          <tr>
////				            <td style="background:#fafafe; padding:16px 32px; border-top:1px solid #f0f0f4;">
////				              <p style="font-size:12px; color:#9494a3; margin:0;">Regards,<br>Visitor Management Team</p>
////				            </td>
////				          </tr>
////
////				        </table>
////				      </td>
////				    </tr>
////				  </table>
////				</body>
////				</html>
////				"""
////				.formatted(statusColor, statusLabel, visitorName, message, hostName, date, time, buttonHtml);
////	}
////}
////

package com.vms_backend.vms_backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Autowired
    private EncryptionService encryptionService;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Sends a simple plain-text email (with a clickable link) for meeting status updates. */
    public void sendMeetingStatusEmail(String to, String visitorName, String hostName,
                                        String statusLabel, String statusColor,
                                        String date, String time, String message,
                                        String actionUrl, String actionLabel) {
        if (to == null || to.isBlank()) return;

        String html = buildSimpleHtml(visitorName, hostName, statusLabel, date, time, message, actionUrl, actionLabel);

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("Meeting " + statusLabel + " — Visitor Management");
            helper.setText(html, true);
            mailSender.send(mime);
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    /** Overload for emails with no action link (keeps old calls working). */
    public void sendMeetingStatusEmail(String to, String visitorName, String hostName,
                                        String statusLabel, String statusColor,
                                        String date, String time, String message) {
        sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor,
                                date, time, message, null, null);
    }

    /** Convenience method: sends the HOST an approval request with a direct link. */
    public void sendHostApprovalEmail(
        String hostEmail,
        String visitorName,
        String hostName,
        String registeredDate,
        String date,
        String time,
        String hostId,
        String mobileNo) throws Exception {

        // Create payload
    String payload = "hostId=" + hostId + "&mobileNo=" + mobileNo;
    
    String encryptedData = encryptionService.encrypt(payload);
    
   /// String actionUrl = frontendUrl + "/HostApproval?hostId="+ hostId + "&mobileNo="+ mobileNo;
    
    String actionUrl = frontendUrl
            + "/HostApproval?token="
            + URLEncoder.encode(
                    encryptedData,
                    StandardCharsets.UTF_8
            );

    String html = buildHostApprovalHtml(
            hostName,
            visitorName,
            registeredDate,
            date,
            time,
            actionUrl
    );

    try {
        MimeMessage mime = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(mime, false, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(hostEmail);
        helper.setSubject("Meeting Request — Visitor Management");
        helper.setText(html, true);

        mailSender.send(mime);

    } catch (MessagingException e) {
        System.err.println(
                "Failed to send host approval email to "
                        + hostEmail
                        + ": "
                        + e.getMessage()
        );
    }
}

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

    /** Simple plain-text template used for all status update emails (approved/rejected/hold/invite/etc). */
    private String buildSimpleHtml(String visitorName, String hostName, String statusLabel,
                                    String date, String time, String message,
                                    String actionUrl, String actionLabel) {
        String linkBlock = "";
        if (actionUrl != null && !actionUrl.isBlank()) {
            linkBlock = """
                <p><b>%s</b></p>
                <p><a href="%s">%s</a></p>
                """.formatted(actionLabel != null ? actionLabel : "View Details", actionUrl, actionUrl);
        }

        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f7; font-family:'Segoe UI', Arial, sans-serif;">
              <p>Dear <b>%s</b>,</p>
              <p>%s</p>
              <p>Host: <b>%s</b><br>
                 Date: <b>%s</b><br>
                 Time: <b>%s</b><br>
                 Status: <b>%s</b></p>
              %s
              <p>Regards,<br><b>Visitor Management System</b></p>
            </body>
            </html>
            """.formatted(visitorName, message, hostName, date, time, statusLabel, linkBlock);
    }

    /** Convenience method: sends the HOST an approval request with a direct link (simple text style). */
    public void sendHostApprovalRequestEmail(String hostEmail, String hostName, String visitorName,
            String date, String time, String approvalUrl) {
        sendMeetingStatusEmail(hostEmail, visitorName, hostName,
                "Pending Approval", "#f59e0b",
                date, time,
                visitorName + " has requested a meeting with you. Please review and respond.",
                approvalUrl, "Review Request");
    }

    /** Convenience method: sends the VISITOR a link to check their meeting status. */
    public void sendVisitorApprovalLinkEmail(String visitorEmail, String visitorName, String hostName,
                                              String date, String time, String token) {
        String statusUrl = frontendUrl + "/meeting-status?token=" + token;
        sendMeetingStatusEmail(visitorEmail, visitorName, hostName,
                "Request Submitted", "#4f46e5",
                date, time,
                "Your meeting request has been sent to " + hostName + ". You can track its status using the link below.",
                statusUrl, "Check Status");
    }

    /** Sends the VISITOR a status-update email (approved/rejected/hold) with a link to check details. */
    public void sendVisitorStatusEmail(String to, String visitorName, String hostName,
            String statusLabel, String statusColor,
            String date, String time, String message, String passLink) {
if (to == null || to.isBlank()) return;
sendMeetingStatusEmail(to, visitorName, hostName, statusLabel, statusColor,
    date, time, message, passLink, "View Meeting Status");
}

    /** Sends the participant an interview/meeting invite with a link to review and respond. */
    public void sendParticipantInviteEmail(String to, String participantName, String organizerName,
                                            String date, String time, String token) {
        if (to == null || to.isBlank()) return;

        String reviewUrl = frontendUrl + "/participant-response?token=" + token;
        sendMeetingStatusEmail(to, participantName, organizerName,
                "Meeting Invitation", "#4f46e5",
                date, time,
                organizerName + " has scheduled a meeting with you. Please review the details and respond using the link below.",
                reviewUrl, "Review & Respond");
    }
    
    
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

        try {
            MimeMessage mime =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mime,
                            false,
                            "UTF-8"
                    );

            helper.setFrom(fromAddress);
            helper.setTo(hostEmail);

            helper.setSubject(
                    "Visitor Meeting Approved"
            );

            helper.setText(html, true);

            mailSender.send(mime);

        } catch (MessagingException e) {

            System.err.println(
                    "Failed to send host approval confirmation email to "
                            + hostEmail
                            + ": "
                            + e.getMessage()
            );
        }
    }
    
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