package com.example.school_management.result.service;

import com.example.school_management.result.dto.resultDisplay.ComponentResultDto;
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
                .append("th, td { border: 1px solid #000; padding: 8px; text-align: center; vertical-align: middle; }")
                .append("th { background-color: #f2f2f2; }")
                .append(".pass { color: green; font-weight: bold; }")
                .append(".fail { color: red; font-weight: bold; }")
                .append(".component-list { text-align: left; margin: 0; padding: 0; list-style-type: none; font-size: 14px; }")
                .append("</style>")
                .append("</head><body>");

        // Letterhead as running header
        if (result.getLetterheadUrl() != null) {
            html.append("<header>")
                    .append("<img src='").append(result.getLetterheadUrl())
                    .append("' style='width:100%; max-height:150px; object-fit:contain;'/>")
                    .append("<hr style='border:1px solid #ccc; margin:5px 0;'/>")
                    .append("</header>");
        }

        html.append("<h2 style='text-align:center;'>Result Card - ").append(result.getExamTypeName()).append("</h2>")
                .append("<p><b>Name:</b> ").append(result.getStudentName()).append("</p>")
                .append("<p><b>Class:</b> ").append(result.getClassName()).append("</p>")
                .append("<p><b>Roll No:</b> ").append(result.getRollNumber()).append("</p>")
                .append("<p><b>Academic Year:</b> ").append(result.getAcademicYear()).append("</p>");

        // Dynamic Table Headers
        html.append("<table>")
                .append("<tr>")
                .append("<th>Subject</th>")
                .append("<th>Component Breakdown (Max/Obt)</th>")
                .append("<th>Total (Max/Obt)</th>")
                .append("<th>Status</th>")
                .append("</tr>");

        // Dynamic Subject Rows
        for (SubjectMarkDto subject : result.getSubjects()) {
            html.append("<tr>")
                    .append("<td><b>").append(subject.getSubjectName()).append("</b></td>")
                    .append("<td><ul class='component-list'>");

            // Loop through dynamic components
            for (ComponentResultDto comp : subject.getComponents()) {
                html.append("<li>").append(comp.getComponentName()).append(": ")
                        .append(comp.getMaxMarks()).append(" / <b>")
                        .append(comp.getObtainedMarks()).append("</b></li>");
            }

            html.append("</ul></td>")
                    .append("<td>").append(subject.getTotalMax()).append(" / <b>").append(subject.getTotalObtained()).append("</b></td>")
                    .append("<td class='").append(Boolean.TRUE.equals(subject.getIsPassed()) ? "pass" : "fail").append("'>")
                    .append(Boolean.TRUE.equals(subject.getIsPassed()) ? "PASS" : "FAIL").append("</td>")
                    .append("</tr>");
        }

        // Grand Total Footer
        html.append("<tr>")
                .append("<td colspan='2' style='text-align:right;'><b>Grand Total</b></td>")
                .append("<td><b>").append(result.getGrandTotalMax()).append(" / ").append(result.getGrandTotalObtained()).append("</b></td>")
                .append("<td class='").append(Boolean.TRUE.equals(result.getIsOverallPassed()) ? "pass" : "fail").append("'>")
                .append("<b>").append(Boolean.TRUE.equals(result.getIsOverallPassed()) ? "PASS" : "FAIL").append("</b></td>")
                .append("</tr>")
                .append("</table>");

        html.append("<p style='text-align:right;'><b>Percentage: ")
                .append(String.format("%.2f", result.getPercentage())).append("%</b></p>");

        html.append("</body></html>");
        return html.toString();
    }
}