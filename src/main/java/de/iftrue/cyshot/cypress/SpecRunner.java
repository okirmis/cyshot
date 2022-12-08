package de.iftrue.cyshot.cypress;

import de.iftrue.cyshot.cypress.commands.HeadedSpecRunnerCommand;
import de.iftrue.cyshot.cypress.commands.HeadlessSpecRunnerCommand;
import de.iftrue.cyshot.cypress.models.Spec;
import de.iftrue.cyshot.cypress.output.SpecOutput;
import de.iftrue.cyshot.cypress.commands.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.Dependent;
import java.util.List;

@Slf4j
@Dependent
public class SpecRunner {

  @SneakyThrows
  public List<SpecOutput> runAllSpecsWithUi(List<Spec> specs) {
    if (specs.isEmpty()) {
      log.warn("No specs found");
      return List.of();
    }

    log.info("Running specs with UI");

    final Result result = new HeadedSpecRunnerCommand(
        specs.get(0).filePath().getParent().toString()
    ).execute();

    if (result.isSuccess()) {
      log.info("{} specs completed", specs.size());
    } else {
      log.info("(Some) specs failed");
      log.debug(result.getOutput());
      log.debug(result.getError());
    }

    return specs.stream().map(SpecOutput::new).toList();
  }

  @SneakyThrows
  public SpecOutput runSingleSpecHeadless(Spec spec) {
    log.info("Running spec {}", spec.name());

    final Result result = new HeadlessSpecRunnerCommand(
        spec.filePath().getParent().toString(),
        spec.filePath().getFileName().toString()
    ).execute();

    if (result.isSuccess()) {
      log.info("Spec {} completed", spec.name());
    } else {
      log.error("Spec {} failed", spec.name());
      log.debug(result.getOutput());
      log.debug(result.getError());
    }

    return new SpecOutput(spec);
  }
}
