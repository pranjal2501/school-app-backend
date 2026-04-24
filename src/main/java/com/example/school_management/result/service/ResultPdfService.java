package com.example.school_management.result.service;

import com.example.school_management.result.dto.resultDisplay.StudentResultResponseDto;
import com.example.school_management.result.dto.resultDisplay.SubjectMarkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class ResultPdfService {

    public byte[] generateResultPdf(StudentResultResponseDto result) throws Exception {
        String html = buildHtml(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        return outputStream.toByteArray();
    }

    private String buildHtml(StudentResultResponseDto result) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; margin-top: 180px; }")
                .append("@page { margin-top: 170px; margin-bottom: 20px; margin-left: 20px; margin-right: 20px; }")
                .append("header { display: block; position: running(header); text-align: center; }")
                .append("@page { @top-center { content: element(header); } }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                .append("th, td { border: 1px solid #000; padding: 8px; text-align: center; }")
                .append("th { background-color: #f2f2f2; }")
                .append(".pass { color: green; font-weight: bold; }")
                .append(".fail { color: red; font-weight: bold; }")
                .append("</style>")
                .append("</head><body>");

        // letterhead as running header - repeats on every page
        if (result.getLetterheadUrl() != null) {
            html.append("<header>")
                    .append("<img src='").append(result.getLetterheadUrl())
                    .append("' style='width:100%; max-height:150px; object-fit:contain;'/>")
                    .append("<hr style='border:1px solid #ccc; margin:5px 0;'/>")
                    .append("</header>");
        }

        // rest of content stays same
        html.append("<h2 style='text-align:center;'>Result Card - ").append(result.getExamTerm()).append("</h2>")
                .append("<p><b>Name:</b> ").append(result.getStudentName()).append("</p>")
                .append("<p><b>Class:</b> ").append(result.getClassName()).append("</p>")
                .append("<p><b>Roll No:</b> ").append(result.getRollNo()).append("</p>")
                .append("<p><b>Academic Year:</b> ").append(result.getAcademicYear()).append("</p>");

        html.append("<table>")
                .append("<tr>")
                .append("<th>Subject</th>")
                .append("<th>Theory (Max/Obt)</th>")
                .append("<th>Internal (Max/Obt)</th>")
                .append("<th>Practical (Max/Obt)</th>")
                .append("<th>Project (Max/Obt)</th>")
                .append("<th>Oral (Max/Obt)</th>")
                .append("<th>Total (Max/Obt)</th>")
                .append("<th>Status</th>")
                .append("</tr>");

        for (SubjectMarkDto subject : result.getSubjectMarks()) {
            html.append("<tr>")
                    .append("<td>").append(subject.getSubjectName()).append("</td>")
                    .append("<td>").append(subject.getTheoryMax()).append("/").append(nullSafe(subject.getTheoryObtained())).append("</td>")
                    .append("<td>").append(subject.getInternalMax()).append("/").append(nullSafe(subject.getInternalObtained())).append("</td>")
                    .append("<td>").append(subject.getPracticalMax()).append("/").append(nullSafe(subject.getPracticalObtained())).append("</td>")
                    .append("<td>").append(subject.getProjectMax()).append("/").append(nullSafe(subject.getProjectObtained())).append("</td>")
                    .append("<td>").append(subject.getOralMax()).append("/").append(nullSafe(subject.getOralObtained())).append("</td>")
                    .append("<td>").append(subject.getTotalMax()).append("/").append(subject.getTotalObtained()).append("</td>")
                    .append("<td class='").append(subject.getIsPassed() ? "pass" : "fail").append("'>")
                    .append(subject.getIsPassed() ? "PASS" : "FAIL").append("</td>")
                    .append("</tr>");
        }

        html.append("<tr>")
                .append("<td><b>Grand Total</b></td>")
                .append("<td colspan='5'></td>")
                .append("<td><b>").append(result.getGrandTotalMax()).append("/").append(result.getGrandTotalObtained()).append("</b></td>")
                .append("<td class='").append(result.getIsOverallPassed() ? "pass" : "fail").append("'>")
                .append("<b>").append(result.getIsOverallPassed() ? "PASS" : "FAIL").append("</b></td>")
                .append("</tr>")
                .append("</table>");

        html.append("<p style='text-align:right;'><b>Percentage: ")
                .append(String.format("%.2f", result.getPercentage())).append("%</b></p>");

        html.append("</body></html>");
        return html.toString();
    }

    private String nullSafe(Integer value) {
        return value != null ? String.valueOf(value) : "0";
    }
}