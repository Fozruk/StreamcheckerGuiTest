package com.github.fozruk.streamcheckerguitest;

import com.github.epilepticz.streamchecker.controller.StreamcheckerController;
import com.github.epilepticz.streamchecker.exception.NoSuchChannelViewInOverviewException;
import com.github.epilepticz.streamchecker.model.channel.interf.IChannel;
import com.github.epilepticz.streamchecker.view.interf.IOverview;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable , IOverview {

    private static final Logger logger = Logger.getLogger(Controller.class);

    @FXML
    private ListView listView;

    @FXML
    private GridPane modalMenuGrid;

    @FXML
    private GridPane top;

    @FXML
    private VBox blurBox;

    @FXML
    private GridPane grid;

    @FXML
    private Button settingsButton;

    @FXML
    private Button addButton;

    @FXML
    private Parent root;

    @FXML
    private Button addChannelBack;

    @FXML
    private Button exitButton;

    private ObservableList<StreamPane> list;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        list = FXCollections.observableArrayList();

        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        list.addListener(new ListChangeListener<StreamPane>() {
            @Override
            public void onChanged(Change<? extends StreamPane> c) {
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Controller.this.grid.setVisible(true);
                fadeIn(grid);
            }
        });

        addChannelBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.trace("Add Channel Back Event fired");
               fadeOutAddNewChannel();
            }
        });

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(1);
            }
        });



        AddChannelForm form = new AddChannelForm();
        form.getImage().setImage(new Image("pictures\\twitch-logo-black.png"));

        AddChannelForm form2 = new AddChannelForm();
        form2.getImage().setImage(new Image("pictures\\hitboxlogogreen.png"));
        grid.add(form, 0, 1);
        grid.add(form2, 0, 2);

        listView.setItems(list);

        Main.controller = new StreamcheckerController(this);
    }


    @Override
    public void addChannel(IChannel channel) {
        StreamPane pane = new StreamPane(channel);
        list.add(pane);
    }

    @Override
    public void updateDataInChannelViewFor(IChannel channel) {
        for(StreamPane channelObject : list)
        {
           if(channel.equals(channelObject.getChannel()))
           {
               channelObject.updateLabels();
               break;
           }
        }
    }

    @Override
    public void deleteChannelViewFor(IChannel channel) throws NoSuchChannelViewInOverviewException {
        for(StreamPane channelObject : list)
        {
            if(channel.equals(channelObject.getChannel()))
            {
                list.remove(channelObject);
                return;
            }
        }
        throw new NoSuchChannelViewInOverviewException();
    }

    @Override
    public IChannel[] getAddedChannels() {
        return new IChannel[0];
    }

    @Override
    public void errorCodeChangedFor(IChannel channel, int errorcount) {

    }

    private void fadeout(Node node)
    {
        FadeTransition ft = new FadeTransition(Duration.millis(100),node);
        ft.setToValue(0.0);
        ft.play();
    }

    private void fadeIn(Node node)
    {
        FadeTransition ft = new FadeTransition(Duration.millis(100),node);
        ft.setToValue(1.0);
        ft.play();
    }

    public void fadeOutAddNewChannel()
    {
        fadeout(grid);
        Controller.this.grid.setVisible(false);
    }
}
