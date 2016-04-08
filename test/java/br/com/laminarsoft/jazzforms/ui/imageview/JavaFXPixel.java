package br.com.laminarsoft.jazzforms.ui.imageview;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoInstancia;
import br.com.laminarsoft.jazzforms.ui.communicator.WebServiceController;

public class JavaFXPixel extends Application {
    
    ImageView myImageView;
     
    @Override
    public void start(Stage primaryStage) {
         
        Button btnLoad = new Button("Load from file");
        btnLoad.setOnAction(btnLoadEventListener);

        Button btnLoad2 = new Button("Load from database");
        btnLoad2.setOnAction(btnLoadFromDatabase);

        myImageView = new ImageView();        
         
        VBox rootBox = new VBox();
        rootBox.getChildren().addAll(btnLoad, btnLoad2, myImageView);
         
        Scene scene = new Scene(rootBox, 300, 350);
         
        primaryStage.setTitle("java-buddy.blogspot.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
     
    EventHandler<ActionEvent> btnLoadFromDatabase = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
        	WebServiceController web = WebServiceController.getInstance();
        	InfoRetornoInstancia info = web.getFotosPorInstancia(8l);
            Image image = new Image(new ByteArrayInputStream(info.fotos.get(0).getPicture()));
            myImageView.setImage(image);
        }    	
    };
    
    EventHandler<ActionEvent> btnLoadEventListener = new EventHandler<ActionEvent>(){
 
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
             
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
              
            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
                       
            try {
                if (file != null) {
					BufferedImage bufferedImage = ImageIO.read(file);
					Image image = SwingFXUtils.toFXImage(bufferedImage, null);
					myImageView.setImage(image);
				}
            } catch (IOException ex) {
                Logger.getLogger(JavaFXPixel.class.getName()).log(Level.SEVERE, null, ex);
            }
 
        }
    };
}
