package de.iftrue.cyshot.cypress;

import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.cypress.models.TestCase;
import de.iftrue.cyshot.utils.ResourceExtractor;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Dependent
public class SpecCodeGenerator {
  private String JAVASCRIPT_LIBRARY_CODE;
  private String TEST_CASE_TEMPLATE;
  private String SPEC_TEMPLATE;

  public String buildSpec(Spec spec) throws IOException {
    return replaceInTemplate(
        loadSpecTemplate(),
        Map.of(
            "libraryCode", loadLibraryCode(),
            "specName", spec.name(),
            "specs", spec.testCases().stream().map((TestCase testCase) -> buildCase(spec, testCase)).collect(Collectors.joining(",\n"))
        )
    );
  }

  private String buildCase(Spec spec, TestCase testCase) {
    return replaceInTemplate(
        loadTestCaseTemplate(),
        Map.of(
            "caseName", testCase.name(),
            "content", testCase.content(),
            "fileName", spec.filePath().getFileName().toString()
        )
    );
  }

  private String loadLibraryCode() {
    if (this.JAVASCRIPT_LIBRARY_CODE == null) {
      this.JAVASCRIPT_LIBRARY_CODE = ResourceExtractor.forceReadResource("/cypress/functions.js");
    }

    return this.JAVASCRIPT_LIBRARY_CODE;
  }

  private String loadSpecTemplate() {
    if (this.SPEC_TEMPLATE == null) {
      this.SPEC_TEMPLATE = ResourceExtractor.forceReadResource("/cypress/spec.js.template");
    }

    return this.SPEC_TEMPLATE;
  }

  private String loadTestCaseTemplate() {
    if (this.TEST_CASE_TEMPLATE == null) {
      this.TEST_CASE_TEMPLATE = ResourceExtractor.forceReadResource("/cypress/test-case.js.template");
    }

    return this.TEST_CASE_TEMPLATE;
  }

  private String replaceInTemplate(String content, Map<String, String> variables) {
    for (String key : variables.keySet()) {
      content = this.replaceInTemplate(content, key, variables.get(key));
    }

    return content;
  }

  private String replaceInTemplate(String content, String name, String value) {
    return content.replace("{{" + name + "}}", value);
  }
}
