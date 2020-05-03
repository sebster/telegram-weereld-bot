package com.sebster.remarkable.cli.commands;

import static com.sebster.remarkable.cli.commands.completion.CommandCompleterBuilder.commandCompleter;
import static com.sebster.remarkable.cloudapi.RemarkableItemType.FOLDER;
import static com.sebster.remarkable.cloudapi.RemarkablePath.parsePaths;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jline.builtins.Completers.SystemCompleter;

import com.sebster.remarkable.cli.commands.completion.RemarkableItemCompleter;
import com.sebster.remarkable.cloudapi.RemarkableClient;
import lombok.NonNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "mkdir", description = "Make new folders")
public class MkdirCommand extends BaseCommand {

	@Parameters(
			index = "0",
			paramLabel = "folder",
			description = "The new folder(s) to create.",
			arity = "1..*"
	)
	private List<String> paths;

	@Override
	public void run() {
		cli.getSelectedClient().createFolders(parsePaths(paths));
	}

	public static SystemCompleter completer(@NonNull Function<String, Optional<RemarkableClient>> clientProvider) {
		return commandCompleter(MkdirCommand.class)
				.argumentCompleter(new RemarkableItemCompleter(clientProvider, FOLDER, false))
				.build();
	}

}
