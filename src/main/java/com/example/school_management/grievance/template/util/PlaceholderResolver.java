package com.example.school_management.grievance.template.util;

import com.example.school_management.grievance.template.dto.PlaceholderContext;
import com.example.school_management.common.enums.PlaceholderKey;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderResolver {

    public String resolve(String content, PlaceholderContext ctx) {
        return content
                // ── Student Basic Info ─────────────────────────────────────────
                .replace(PlaceholderKey.STUDENT_NAME.getToken(),            safe(ctx.getStudentName()))
                .replace(PlaceholderKey.STUDENT_FIRST_NAME.getToken(),      safe(ctx.getStudentFirstName()))
                .replace(PlaceholderKey.STUDENT_MIDDLE_NAME.getToken(),     safe(ctx.getStudentMiddleName()))
                .replace(PlaceholderKey.STUDENT_LAST_NAME.getToken(),       safe(ctx.getStudentLastName()))
                .replace(PlaceholderKey.STUDENT_GENDER.getToken(),          safe(ctx.getStudentGender()))
                .replace(PlaceholderKey.STUDENT_DOB.getToken(),             safe(ctx.getStudentDob()))
                .replace(PlaceholderKey.STUDENT_BLOOD_GROUP.getToken(),     safe(ctx.getStudentBloodGroup()))
                .replace(PlaceholderKey.STUDENT_NATIONALITY.getToken(),     safe(ctx.getStudentNationality()))
                .replace(PlaceholderKey.STUDENT_RELIGION.getToken(),        safe(ctx.getStudentReligion()))
                .replace(PlaceholderKey.STUDENT_CATEGORY.getToken(),        safe(ctx.getStudentCategory()))
                .replace(PlaceholderKey.STUDENT_AADHAR.getToken(),          safe(ctx.getStudentAadharNo()))
                .replace(PlaceholderKey.STUDENT_REG_ID.getToken(),          safe(ctx.getStudentRegId()))

                // ── Student Academic Info ──────────────────────────────────────
                .replace(PlaceholderKey.STUDENT_ROLL_NUMBER.getToken(),     safe(ctx.getStudentRollNumber()))
                .replace(PlaceholderKey.STUDENT_CLASS.getToken(),           safe(ctx.getStudentClass()))
                .replace(PlaceholderKey.STUDENT_DIVISION.getToken(),        safe(ctx.getStudentDivision()))
                .replace(PlaceholderKey.STUDENT_STANDARD.getToken(),        safe(ctx.getStudentStandard()))
                .replace(PlaceholderKey.STUDENT_ACADEMIC_YEAR.getToken(),   safe(ctx.getStudentAcademicYear()))
                .replace(PlaceholderKey.STUDENT_PREVIOUS_SCHOOL.getToken(), safe(ctx.getStudentPreviousSchool()))

                // ── Parent Info ────────────────────────────────────────────────
                .replace(PlaceholderKey.FATHER_NAME.getToken(),             safe(ctx.getFatherName()))
                .replace(PlaceholderKey.FATHER_OCCUPATION.getToken(),       safe(ctx.getFatherOccupation()))
                .replace(PlaceholderKey.MOTHER_NAME.getToken(),             safe(ctx.getMotherName()))
                .replace(PlaceholderKey.MOTHER_OCCUPATION.getToken(),       safe(ctx.getMotherOccupation()))
                .replace(PlaceholderKey.GUARDIAN_NAME.getToken(),           safe(ctx.getGuardianName()))
                .replace(PlaceholderKey.GUARDIAN_RELATION.getToken(),       safe(ctx.getGuardianRelation()))
                .replace(PlaceholderKey.PARENT_ADDRESS.getToken(),          safe(ctx.getParentAddress()))
                .replace(PlaceholderKey.PARENT_CITY.getToken(),             safe(ctx.getParentCity()))
                .replace(PlaceholderKey.PARENT_STATE.getToken(),            safe(ctx.getParentState()))
                .replace(PlaceholderKey.PARENT_PINCODE.getToken(),          safe(ctx.getParentPincode()))
                .replace(PlaceholderKey.PARENT_MOBILE.getToken(),           safe(ctx.getParentMobile()))
                .replace(PlaceholderKey.PARENT_EMAIL.getToken(),            safe(ctx.getParentEmail()))

                // ── Bonafide Specific ──────────────────────────────────────────
                .replace(PlaceholderKey.BONAFIDE_PURPOSE.getToken(),        safe(ctx.getBonafidePurpose()))

                // ── LC Specific ────────────────────────────────────────────────
                .replace(PlaceholderKey.LC_LEAVING_DATE.getToken(),         safe(ctx.getLcLeavingDate()))
                .replace(PlaceholderKey.LC_REASON.getToken(),               safe(ctx.getLcReason()))
                .replace(PlaceholderKey.LC_CONDUCT.getToken(),              safe(ctx.getLcConduct()))
                .replace(PlaceholderKey.LC_LAST_ATTENDED_YEAR.getToken(),   safe(ctx.getLcLastAttendedYear()))

                // ── General ────────────────────────────────────────────────────
                .replace(PlaceholderKey.CURRENT_DATE.getToken(),            safe(ctx.getCurrentDate()))
                .replace(PlaceholderKey.CURRENT_YEAR.getToken(),            safe(ctx.getCurrentYear()))

                // ── Fees Receipt Specific ────────────────────────────────────────────────
                .replace(PlaceholderKey.FEE_INSTALLMENT_NUMBER.getToken(), safe(ctx.getFeeInstallmentNumber()))
                .replace(PlaceholderKey.FEE_TOTAL_INSTALLMENTS.getToken(), safe(ctx.getFeeTotalInstallments()))
                .replace(PlaceholderKey.FEE_AMOUNT_PAID.getToken(), safe(ctx.getFeeAmountPaid()))
                .replace(PlaceholderKey.FEE_PAYMENT_DATE.getToken(), safe(ctx.getFeePaymentDate()))
                .replace(PlaceholderKey.FEE_TOTAL_FEE.getToken(), safe(ctx.getFeeTotalFee()));

    }

    private String safe(String value) {
        return value != null ? value : "N/A";
    }
}