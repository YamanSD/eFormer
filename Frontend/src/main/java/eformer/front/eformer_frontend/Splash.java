package eformer.front.eformer_frontend;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * This is my own splash screen, that I made myself.
 *
 */
public class Splash {
    static Scene splash;
    static Rectangle rect = new Rectangle();
    final private Pane pane;
    final private SequentialTransition seqT;

    public Splash()
    {
        pane = new StackPane();
        pane.setStyle("-fx-background-color:black");

        splash = new Scene(pane);
        seqT = new SequentialTransition();
    }

    public void show()
    {
        /*
         * Part 1:
         * This is the rolling square animation.
         * This animation looks cool for a loading screen,
         * so I made this. Only the lines of code for fading
         * from Stack Overflow.
         */
        //rectangle insertion
        int scale = 30;
        int dur = 800;
        rect = new Rectangle(100 - 2 * scale, 20, scale, scale);
        rect.setFill(Color.PURPLE);

        //actual animations
        //initialising the sequentialTranslation
        //umm, ignore this, just some configs that work magic
        int[] rotins = {scale, 2 * scale, 3 * scale, 4 * scale, 5 * scale, -6 * scale, -5 * scale, -4 * scale, -3 * scale, -2 * scale};
        int x, y;
        for (int i : rotins) {
            //rotating the square
            RotateTransition rt = new RotateTransition(Duration.millis(dur), rect);
            rt.setByAngle(i / Math.abs(i) * 90);
            rt.setCycleCount(1);
            //moving the square horizontally
            TranslateTransition pt = new TranslateTransition(Duration.millis(dur), rect);
            x = (int) (rect.getX() + Math.abs(i));
            y = (int) (rect.getX() + Math.abs(i) + (Math.abs(i) / i) * scale);
            pt.setFromX(x);
            pt.setToX(y);
            //parallelly execute them and you get a rolling square
            ParallelTransition pat = new ParallelTransition();
            pat.getChildren().addAll(pt, rt);
            pat.setCycleCount(1);
            seqT.getChildren().add(pat);
        }
        //playing the animation
        seqT.play();
        //lambda code sourced from StackOverflow, fades away stage
        seqT.setNode(rect);
        //The text part

        var hbox = new HBox();

        Label label0 = new Label("e");
        label0.setFont(new Font("Verdana", 40));
        label0.setStyle("-fx-text-fill:white");

        Label label1 = new Label("Former");
        label1.setFont(new Font("Verdana", 40));
        label1.setStyle("-fx-text-fill:#ff9900");

        hbox.getChildren().addAll(label0, label1);
        hbox.setLayoutX(140);
        hbox.setLayoutY(70);

        Label lab = new Label("Launching...");
        lab.setFont(new Font("Times New Roman", 15));
        lab.setStyle("-fx-text-fill:white");
        lab.setLayoutX(170);
        lab.setLayoutY(180);
        //A complimentary image

        var temp = new Pane(rect, hbox, lab);

        //now adding everything to position, opening the stage, start the animation
        pane.getChildren().add(temp);

        seqT.play();
    }

    public Scene getSplashScene()
    {
        return splash;
    }

    public SequentialTransition getSequentialTransition()
    {
        return seqT;
    }
}
