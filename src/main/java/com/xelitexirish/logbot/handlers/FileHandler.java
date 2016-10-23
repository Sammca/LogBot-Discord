package com.xelitexirish.logbot.handlers;

import com.xelitexirish.logbot.utils.BotLogger;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;

import java.io.File;
import java.io.IOException;

/**
 * Created by XeliteXirish on 10/10/2016. www.xelitexirish.com
 */
public class FileHandler {

    public static File getLogFile(Guild guild, TextChannel textChannel) {

        File serverDataFolder = getServerLogFolder(guild);
        File channelFile = null;

        for (File files : serverDataFolder.listFiles()){
            String[] fileName = files.getName().split("-");
            if (fileName.length > 2 && fileName[1].contains(textChannel.getId())){
                channelFile = files;
            }
        }

        String channelFolderName = textChannel.getName() + " -[" + textChannel.getId() + "].txt";
        if (channelFile == null) channelFile = new File(serverDataFolder + "/" + channelFolderName);

        if (!doesFileExist(channelFile)) {
            try {
                BotLogger.info("Creating new channel folder: " + textChannel.getName() + " on server: " + guild.getName());
                channelFile.createNewFile();
            } catch (IOException e) {
                BotLogger.debug("Wasn't able to create the server log file for the server: " + guild.getName(), e);
            }
        }
        return channelFile;
    }

    public static File getServerVipFile(Guild guild) {

        File serverVipFile = new File(getServerDataFolder(guild) + "/" + "vip_users.json");

        if (!doesFileExist(serverVipFile)) {
            try {
                serverVipFile.createNewFile();
                BotLogger.info("Creating new vip file for server: " + guild.getName());
            } catch (IOException e) {
                BotLogger.debug("Wasn't able to create the vip file for the server: " + guild.getName(), e);
            }
        }
        return serverVipFile;
    }

    public static File getServerEventLogFile(Guild guild){

        File serverEventLogFile = new File(getServerDataFolder(guild) + "/" + "event_log.txt");

        if (!doesFileExist(serverEventLogFile)){
            try {
                serverEventLogFile.createNewFile();
                BotLogger.info("Creating new event log file for the server: " + guild.getName());
            } catch (IOException e) {
                BotLogger.debug("Wasn't able to create the event log file for the server: " + guild.getName());
            }
        }
        return serverEventLogFile;
    }

    /**
     * Helper Methods
     */

    public static File[] getAllServerLogFiles(Guild guild){
        String serverFolderName = getServerFolder(guild) + "/channels/";
        File serverFolder = new File(serverFolderName);
        return serverFolder.listFiles();
    }

    public static File getServerLogFolder(Guild guild) {
        String serverFolderName = getServerFolder(guild) + "/channels/";
        File serverFolder = new File(serverFolderName);
        if (!serverFolder.exists()) serverFolder.mkdirs();
        return serverFolder;
    }

    public static File getServerDataFolder(Guild guild) {
        String serverFolderName = getServerFolder(guild) + "/data/";
        File serverFolder = new File(serverFolderName);
        if (!serverFolder.exists()) serverFolder.mkdirs();
        return serverFolder;
    }

    private static File getServerFolder(Guild guild) {
        assert guild != null;
        File baseFileDir = new File(getBaseFileDir());
        File serverFolder = null;

        for (File file : baseFileDir.listFiles()){
            String[] nameSplit = file.getName().split("-");
            if (nameSplit.length > 2 && nameSplit[1].contains(guild.getId())){
                serverFolder = file;
            }
        }

        String serverFolderName = guild.getName() + " -[" + guild.getId() + "]";
        if (serverFolder == null) serverFolder = new File(getBaseFileDir() + serverFolderName);

        if (!serverFolder.exists()) serverFolder.mkdirs();
        return serverFolder;
    }

    public static String getBaseFileDir() {
        return "discord_servers/";
    }

    private static boolean doesFileExist(File file) {
        return file != null && file.exists();
    }
}
