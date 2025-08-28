package com.example.E_tech.Controller;

import com.example.E_tech.Service.CertificateService;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class CertificateController {

    @Autowired
    private final CertificateService certificateService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadCertificate(@RequestParam String courseId, @RequestParam String email) {
        Resource certificate = certificateService.generateCertificate(courseId, email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
                .body(certificate);
    }
}

