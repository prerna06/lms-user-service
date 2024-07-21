package com.tekcapzule.lms.user.domain.service;

import com.tekcapzule.lms.user.domain.command.GetCertificateCommand;


public interface PdfService {
    byte[] generateCertificate(GetCertificateCommand getCertificateCommand);
}
