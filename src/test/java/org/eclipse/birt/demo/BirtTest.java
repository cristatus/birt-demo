package org.eclipse.birt.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lowagie.text.pdf.LayoutProcessor;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BirtTest {

  private String extractText(Path pdf) throws IOException {
    // Causing font not found issue
    // See https://github.com/LibrePDF/OpenPDF/issues/1159#issuecomment-2297508049
    LayoutProcessor.disable();

    var sb = new StringBuilder();
    try (var reader = new PdfReader(pdf.toString())) {
      var extractor = new PdfTextExtractor(reader);
      for (var i = 1; i <= reader.getNumberOfPages(); i++) {
        sb.append(extractor.getTextFromPage(i));
      }
    }
    return sb.toString();
  }

  private String run(String format) throws IOException {
    var design = Path.of("target/test-classes/hello_world.rptdesign");
    var output = Path.of("target/hello_world." + format);

    var generator = new ReportGenerator();
    generator.generate(design, output);

    return "pdf".equals(format) ? extractText(output) : Files.readString(output);
  }

  private void checkOutput(String text) {
    assertTrue(text.contains("Congratulations!"));
    assertTrue(
        text.contains(
            "If you can see this report, it means that the BIRT Engine is installed correctly."));
  }

  @Test
  public void testRunner() throws Exception {
    var formats = List.of("html", "pdf");
    for (var format : formats) {
      var text = run(format);
      checkOutput(text);
    }
  }
}
