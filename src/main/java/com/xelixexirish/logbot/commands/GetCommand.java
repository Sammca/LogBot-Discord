package com.xelitexirish.logbot.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.core.exceptions.RateLimitedException;
import com.xelitexirish.logbot.handlers.FileHandler;
import com.xelitexirish.logbot.handlers.PermissionHandler;
import com.xelitexirish.logbot.utils.BotLogger;
import com.xelitexirish.logbot.utils.MessageUtils;

public class GetCommand implements ICommand {

	public static final int MAX_LENGTH = 1000;
	private final String HELP_MSG = "Returns the log file for the specified channel or user. Usage: get `channel <mentioned channel>` or `user <mentioned user>`";
	
	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public void action(String[] args, MessageReceivedEvent event) {
		
		event.getAuthor().openPrivateChannel();
		if (PermissionHandler.isUserAdmin(event.getGuild(), event.getAuthor())) {
            if (args.length > 0 && args[0].equalsIgnoreCase("channel")) {
			    getChannelLog(args, event);

			} else if (args.length > 0 && args[0].equalsIgnoreCase("user")) {
			    getUserLog(args, event);
			}
        } else {
            event.getAuthor().getPrivateChannel().sendMessage(MessageUtils.getNoPermissionMsg(PermissionHandler.ADMIN_PERMISSION)).queue();
        }
	}
	
	private void sendHelpMessage(MessageReceivedEvent event) {
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
		return "get";
	}
	
	private void getChannelLog(String[] args, MessageReceivedEvent event) {
		event.getAuthor().openPrivateChannel();
        if (args.length == 1) {
            // get channel

            File logFile = FileHandler.getLogFile(event.getGuild(), event.getTextChannel());
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.appendString("This is the log file for channel: " + event.getTextChannel().getName());
            
            try {
				event.getAuthor().getPrivateChannel().sendFile(logFile, messageBuilder.build()).queue();
			} catch (IOException e) {
				e.printStackTrace();
			}
            BotLogger.info(event.getAuthor().getName() + " asked for file: " + logFile.getName() + " on server: " + event.getGuild().getName());

        } else if (args.length > 1) {

            if (args[1].equalsIgnoreCase("all")) {
                // get channel all

                File[] channelFiles = FileHandler.getAllServerLogFiles(event.getGuild());
                if (channelFiles != null) {
                    event.getAuthor().getPrivateChannel().sendMessage("Here are the chats logs for the server: " + event.getGuild().getName()).queue();

                    for (File file : channelFiles) {
                        try {
							event.getAuthor().getPrivateChannel().sendFile(file, null).queue();
						} catch (IOException e) {
							e.printStackTrace();
						}
                        BotLogger.info(event.getAuthor() + " asked for file: " + file.getName() + " on server: " + event.getGuild().getName());
                    }
                } else {
                    event.getAuthor().getPrivateChannel().sendMessage("Sorry but an error occurred or there was no chat logs found!").queue();
                }

            } else if (event.getMessage().getMentionedChannels().size() > 0) {
                // get channel #channelName

                event.getAuthor().getPrivateChannel().sendMessage("Here are the chat logs for the channels you asked for:").queue();
                for (TextChannel channel : event.getMessage().getMentionedChannels()) {
                    File channelFile = FileHandler.getLogFile(event.getGuild(), channel);
                    try {
						event.getAuthor().getPrivateChannel().sendFile(channelFile, null).queue();
					} catch (IOException e) {
						e.printStackTrace();
					}

                    BotLogger.info(event.getAuthor() + " asked for file: " + channelFile.getName() + " on server: " + event.getGuild().getName());
                }

            } else {
                sendHelpMessage(event);
            }
        }         
    }

    private void getUserLog(String[] args, MessageReceivedEvent event) {
        // get user <users> <length>
    	event.getAuthor().openPrivateChannel();
        List<User> logUser = event.getMessage().getMentionedUsers();
        int searchLength = 0;
        try {
            searchLength = Integer.parseInt(args[args.length - 1]);
        } catch (Exception e) {
        }

        event.getAuthor().getPrivateChannel().sendMessage("Here are the chat logs for the user you asked for, this may take a long time:").queue();
        for (User user : logUser) {

            File logFile = FileHandler.getTempLogFile(event, user, searchLength);
            try {
				event.getAuthor().getPrivateChannel().sendFile(logFile, null).queue();
			} catch (IOException e) {
				e.printStackTrace();
			}

            BotLogger.info(event.getAuthor().getName() + " asked for file: " + logFile.getName() + " on the server: " + event.getGuild().getName());
        }
    }
}
