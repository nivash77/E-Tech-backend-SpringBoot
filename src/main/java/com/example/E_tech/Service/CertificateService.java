package com.example.E_tech.Service;

import com.example.E_tech.Entity.Course;
import com.example.E_tech.Entity.User;
import com.example.E_tech.Repository.CourseRepository;
import com.example.E_tech.Repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class CertificateService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    public ByteArrayResource generateCertificate(String courseId, String email) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            ClassPathResource imgFile = new ClassPathResource("templates/tp244-bg2-04.jpg");
            Image background = Image.getInstance(imgFile.getInputStream().readAllBytes());
            background.setAbsolutePosition(0, 0);
            background.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            document.add(background);
            User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
            Course courses=courseRepository.findById(courseId).orElseThrow(()->new RuntimeException("Course not found"));
            //Add dynamic text
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD);
            Paragraph title = new Paragraph("Certificate of Completion", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(150);
            document.add(title);

            Font subFont = new Font(Font.FontFamily.HELVETICA, 18);
            Paragraph awardedTo = new Paragraph("Awarded to", subFont);
            awardedTo.setAlignment(Element.ALIGN_CENTER);
            awardedTo.setSpacingBefore(20);
            document.add(awardedTo);

            Font nameFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
            Paragraph name = new Paragraph(user.getName(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingBefore(10);
            document.add(name);

            Font courseFont = new Font(Font.FontFamily.HELVETICA, 16);
            Paragraph course = new Paragraph("For successfully completing the course: " + courses.getTitle(), courseFont);
            course.setAlignment(Element.ALIGN_CENTER);
            course.setSpacingBefore(20);
            document.add(course);

            document.close();

            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating certificate", e);
        }
    }
}
