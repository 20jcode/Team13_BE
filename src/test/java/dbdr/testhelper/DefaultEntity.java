package dbdr.testhelper;

import dbdr.domain.admin.entity.Admin;
import dbdr.domain.careworker.entity.Careworker;
import dbdr.domain.guardian.entity.Guardian;
import dbdr.domain.institution.entity.Institution;
import dbdr.domain.recipient.entity.Recipient;
import java.time.LocalDate;

public class DefaultEntity {

    public String institutionName = "testInstitution";
    public Long institutionNumber = 123122L;
    public String careworkerName = "testCareworker";
    public String careworkerEmail = "testcareworker@test.com";
    public String careworkerPhone ="01012345678";
    public String guardianName = "testGuardian";
    public String guardianPhone = "01087654321";
    public String recipientName = "testRecipient";
    public LocalDate recipientBirth = LocalDate.of(1999, 1, 1);
    public String recipientGender = "gender";
    public String recipientCareNumber = "123455L";
    public String recipientCareLevel = "carelevel";
    public LocalDate recipientStartDate = LocalDate.of(2021, 1, 1);
    public String adminLoginId = "testAdmin";
    public String adminLoginPassword = "testAdmin";

    public Institution createInstitution() {
        return Institution.builder()
            .institutionName(institutionName)
            .institutionNumber(institutionNumber)
            .build();
    }

    public Careworker createCareworker(){
        return Careworker.builder()
            .name(careworkerName)
            .email(careworkerEmail)
            .phone(careworkerPhone)
            .build();
    }

    public Guardian createGuardian() {
        return Guardian.builder()
            .name(guardianName)
            .phone(guardianPhone)
            .build();
    }

    public Recipient createRecipient() {
        Institution institution = createInstitution();
        Careworker careworker = createCareworker();
        return Recipient.builder()
            .name(recipientName)
            .birth(recipientBirth)
            .institution(institution)
            .institutionNumber(institution.getInstitutionNumber())
            .gender(recipientGender)
            .careNumber(recipientCareNumber)
            .careLevel(recipientCareLevel)
            .careworker(careworker)
            .startDate(recipientStartDate)
            .build();
    }


    public Admin createAdmin() {
        return Admin.builder()
            .loginId(adminLoginId)
            .loginPassword(adminLoginPassword)
            .build();
    }

}
