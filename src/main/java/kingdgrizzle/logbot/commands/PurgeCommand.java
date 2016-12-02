package kingdgrizzle.logbot.commands;

import kingdgrizzle.logbot.handlers.FileHandler;
import kingdgrizzle.logbot.handlers.PermissionHandler;
import kingdgrizzle.logbot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PurgeCommand implements ICommand {

	private final String HELP_MSG = "Deletes the specified channel logs. Usage: 'purge channel <mentioned channels>' or 'purge temp'"; 
	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		event.getAuthor().openPrivateChannel();		
		if (PermissionHandler.isUserAdmin(event.getGuild(), event.getAuthor())) {
            if (args[0].equalsIgnoreCase("channel")) {
                for (TextChannel channel : event.getMessage().getMentionedChannels()) {
                    event.getAuthor().getPrivateChannel().sendMessage("Deleting the channel file for the following channel: " + channel).queue();
                    FileHandler.getLogFile(event.getGuild(), channel).delete();
                }

            } else if (args[0].equalsIgnoreCase("temp")) {
                if (PermissionHandler.isUserMaintainer(event.getAuthor())) {
                    event.getAuthor().getPrivateChannel().sendMessage("Deleting the temp folder for LogBot").queue();
                    FileHandler.removeTempFolder();
                } else {
                    event.getAuthor().getPrivateChannel().sendMessage(MessageUtils.getNoPermissionMsg(PermissionHandler.ADMIN_PERMISSION)).queue();
                }
            }
        } else {
            event.getAuthor().getPrivateChannel().sendMessage(MessageUtils.getNoPermissionMsg(PermissionHandler.ADMIN_PERMISSION)).queue();
        }
	}

	@Override
	public String help() {
		return HELP_MSG;
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent event) {
	}

	@Override
	public String getTag() {
		return "purge";
	}

}
