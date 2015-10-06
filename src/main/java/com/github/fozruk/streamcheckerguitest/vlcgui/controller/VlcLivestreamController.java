package com.github.fozruk.streamcheckerguitest.vlcgui.controller;



import com.github.epilepticz.streamchecker.exception.CreateChannelException;
import com.github.epilepticz.streamchecker.exception.ReadingWebsiteFailedException;
import com.github.epilepticz.streamchecker.exception.UpdateChannelException;
import com.github.epilepticz.streamchecker.model.channel.interf.IChannel;
import com.github.fozruk.streamcheckerguitest.chat.ChatObserver;
import com.github.fozruk.streamcheckerguitest.chat.MessageHighlighter;
import com.github.fozruk.streamcheckerguitest.plugins.base.Stream;
import com.github.fozruk.streamcheckerguitest.exception.PropertyKeyNotFoundException;
import com.github.fozruk.streamcheckerguitest.persistence.PersistedSettingsManager;
import com.github.fozruk.streamcheckerguitest.plugins.base.PluginLoader;
import com.github.fozruk.streamcheckerguitest.vlcgui.chat.*;
import com.github.fozruk.streamcheckerguitest.vlcgui.ui.ChatMessage;
import com.github.fozruk.streamcheckerguitest.vlcgui.ui.StreamWindow;
import com.github.fozruk.streamcheckerguitest.util.Util;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.pircbotx.exception.IrcException;

public class VlcLivestreamController implements ChatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger
            (VlcLivestreamController.class);
    private StreamWindow streamWindow;
    private PersistedSettingsManager persistenceManager =
            PersistedSettingsManager.getInstance();
    private boolean isLoaded;
    private boolean shutdownRequest;
    private static final String PLUGIN_PACKAGE_PATH = "com.github.fozruk" +
            ".streamcheckerguitest.plugins.";

    private Stream stream;

    public static final MessageHighlighter highligter = new
            MessageHighlighter(new ArrayList(Arrays.asList("f0zruk","fozruk")));
    private boolean loaded;

    public VlcLivestreamController(IChannel channel) throws IOException,
            IrcException, PropertyKeyNotFoundException, ReadingWebsiteFailedException, CreateChannelException, JSONException {
        this.streamWindow = new StreamWindow(this);

        PluginLoader loader = null;
        try {
            loader = (PluginLoader) Class.forName(PLUGIN_PACKAGE_PATH+channel.getClass()
                    .getSimpleName() +
                    "_Gui")
                    .newInstance();
            loader.create(channel);
            this.stream = loader.returnObject();
            stream.getChannel().addObserver(streamWindow);
            startPlayer();
            startChat();
            stream.getChannel().refreshData();
            this.loaded = true;
        } catch (InstantiationException | ClassCastException |
                IllegalAccessException | ClassNotFoundException e) {
            Util.printExceptionToMessageDialog(e);
        } catch (UpdateChannelException e) {
            Util.printExceptionToMessageDialog(e);
        }
        if(shutdownRequest)
            this.stopWindow();
    }

    private void startPlayer() throws IOException {
        stream.getPlayer().setCanvas(this.streamWindow.getVlcPlayerCanvas());
        stream.getPlayer().play(new URL(stream.getChannel().getChannelLink()),
                stream.quality[0]);
    }

    private void startChat() throws ReadingWebsiteFailedException, JSONException, IOException {
        stream.getChat().setObserver(this);
        stream.getChat().start();
    }

    //ResizeableList Stuffs

    @Override
    public void _onMessage(ChatMessage message) {
        streamWindow.appendChatMessage(message);
    }

    public void sendMessage(String message) {
        stream.getChat()._sendMessage(stream.getChannel().getChannelName(), message);
    }

    public String getUsername()
    {
        return stream.getChat().getUsername();
    }

    //Methods for Closing events

    @Override
    public void _onDisconnect() {

    }

    public void stopWindow() {

        if(loaded)
        {
            stream.getPlayer().onShutdown(0);
            stream.getChat().disconnect();
        } else
        {
            LOGGER.info("Shutdown Request detected, gonna stop all processes " +
                    "if window is loaded complemetely.");
            shutdownRequest = true;
        }
        //chat._leaveChannel((TwitchTVChannel) channel);

    }

    //Player stuffs

    public void setVolume(int volume)
    {
        stream.getPlayer().setVolume(volume);
    }

    public void toggleFullscreen()
    {
        stream.getPlayer().toggleFullScreen();
    }


    public String[] reloadViewerList() throws ReadingWebsiteFailedException, JSONException, MalformedURLException {
        return stream.getChat().getUserList();
    }

    public void setFullscreen()
    {
        stream.getPlayer().toggleFullScreen();
    }

    public StreamWindow getStreamWindow()
    {
        return this.streamWindow;
    }

}
