package org.eclipse.birt.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.eclipse.birt.report.engine.api.ReportRunner;

public class ReportGenerator {

  public void generate(InputStream design, OutputStream output, String format) throws IOException {
    var inputFile = Files.createTempFile("birt", ".rptdesign");
    var outputFile = Files.createTempFile("birt", "." + format);

    // Delete them as they will be re-created
    Files.deleteIfExists(inputFile);
    Files.deleteIfExists(outputFile);

    try {
      // Copy the design input stream to a temporary file
      Files.copy(design, inputFile, StandardCopyOption.REPLACE_EXISTING);

      // Generate the report
      generate(inputFile, outputFile);

      // Copy the output file to the output stream
      Files.copy(outputFile, output);
    } finally {
      Files.deleteIfExists(inputFile);
      Files.deleteIfExists(outputFile);
    }
  }

  private String getFileExtension(Path path) {
    String fileName = path.getFileName().toString();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }

  public void generate(Path design, Path output) throws IOException {
    var format = getFileExtension(output);
    var args =
        new String[] {
          "-o",
          output.toString(),
          "-f",
          format,
          "-m",
          "RunAndRender",
          "-p",
          "paramString=my parameter",
          "-p",
          "paramInteger=1",
          "-p",
          "paramList=1,2,3",
          design.toString()
        };

    var runner = new ReportRunner(args);
    var exitCode = runner.execute();
    if (exitCode != 0) {
      throw new IOException("Unable to generate report: " + exitCode);
    }
  }
}
