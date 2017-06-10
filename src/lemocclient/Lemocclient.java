/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package lemocclient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
*
* @author noname
*/
public class Lemocclient extends Application {



    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));


        Scene scene = new Scene(root);
        stage.setWidth(750); 
        stage.setHeight(720); 
        stage.setResizable(false);
        stage.setTitle("LEMOC - Client");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                if(FXMLDocumentController.connected)
                {
                    System.out.println("close websocket");
                    FXMLDocumentController.cc.close();                   
                }
                //
            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
