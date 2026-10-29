package kz.temirlan.studentapi.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String groupName,
        @NotBlank String studentNumber) {
}
