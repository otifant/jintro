package ch.otticello.jintro.jdbc;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import ch.otticello.jintro.jdbc.commands.*;

/**
 * Entry Point of the Application.
 * 
 * This Application is a CLI for managing School Grades.
 * First, create a new Database with this Command: `<cmd> create database`.
 * Then, you can add a new Grade like this: `<cmd> add grade 4.5 for Math`.
 * To show all grades, run: `<cmd> show all`.
 * To edit an existing grade, run something like: `<cmd> edit grade for Math of 2022-06-09 to 5.5`
 * To delete an existing grade, run something like: `<cmd> remove grade for Math of 2022-06-09`
 */
public class Main {
    public static void main(String[] args) throws Exception {

        var commandsMapping = new HashMap<String, Callable<Command>>();


        // create database:
        commandsMapping.put("create database", TableCreator::new);

        // add grade 4.5 for Math:
        commandsMapping.put("add grade",
                () -> new AddGrade(Float.parseFloat(args[2]), args[4], new Date(System.currentTimeMillis())));

        // show all
        commandsMapping.put("show all", ShowGrades::new);

        // avg for Math:
        commandsMapping.put("avg for", () -> new GetAverageOfGrades(args[3]));

        // edit grade for Math of 2022-06-09 to 5.5:
        commandsMapping.put("edit grade for",
                () -> new EditGrade(args[3], Date.valueOf(args[5]), Float.parseFloat(args[7])));

        // remove grade for Math of 2022-06-09:
        commandsMapping.put("remove grade for", () -> new RemoveGrade(args[3], Date.valueOf(args[5])));

        Command command = null;
        commands_loop: for (Map.Entry<String, Callable<Command>> entry : commandsMapping.entrySet()) {
            String[] prefixOfKnownCommand = entry.getKey().split(" ");
            if (prefixOfKnownCommand.length > args.length) {
                continue;
            }
            for (int i = 0; i < prefixOfKnownCommand.length; i++) {
                if (!args[i].equals(prefixOfKnownCommand[i])) {
                    continue commands_loop;
                }
            }
            command = entry.getValue().call();
            break;
        }

        if (command == null) {
            throw new UnsupportedOperationException("Couldn't recognize this command.");
        }

        command.execute();

        System.out.println("Operation finished successfully.");
    }
}
