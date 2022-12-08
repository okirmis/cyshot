package de.iftrue.cyshot.apps;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {Build.class, Info.class})
public class Entry {
}
