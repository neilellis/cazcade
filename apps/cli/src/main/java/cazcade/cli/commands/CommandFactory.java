package cazcade.cli.commands;

import cazcade.common.Logger;
import cazcade.fountain.common.app.ApplicationLifecycle;
import cazcade.fountain.common.app.ApplicationLifecycleListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to obtain a job by it's name.
 *
 * @author Neil Ellis
 */

public class CommandFactory implements ApplicationLifecycleListener {
    private final static Logger log = Logger.getLogger(CommandFactory.class);

    private Map<String, Command> jobMap = new HashMap<String, Command>();

    public CommandFactory(ApplicationLifecycle applicationLifecycle, Map<String, Command> jobMap) {
        applicationLifecycle.register(this);
        this.jobMap= jobMap;

    }

    public Command getCommandByName(String key) {
        return jobMap.get(key);

    }

    public void add(Command command) {
        if (jobMap.containsKey(command.getName())) {
            throw new Error("Cannot have two commands with the same name " + command.getName());
        }
        jobMap.put(command.getName(), command);
        if (command.getShortName() != null) {
            if (jobMap.containsKey(command.getShortName())) {
                throw new Error("Cannot have two commands with the same short name " + command.getShortName());
            }
            jobMap.put(command.getShortName(), command);
        }
    }

    public Collection<Command> getAll() {
        return jobMap.values();
    }

    public void shutdown() throws Exception {
        for (Command command : jobMap.values()) {
            try {
                command.stopIfNotStopped();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
