package com.ehr.snake2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class Main extends Application {
/*
    public enum Direction{ //pre defined states!
        UP, DOWN, LEFT, RIGHT
    }
*/
    public static Stage window;

    public static final int BLOCK_SIZE = 20;//size of 1 block
    public static final int APP_W = 20*BLOCK_SIZE; // application width
    public static final int APP_H = 15*BLOCK_SIZE; // application height
    public static boolean isEndless = true;
    private static double speed = 0.15;
    private int lastScore = 0;
    private int bestScore = 0;
    private ArrayList<Integer> allScores = new ArrayList<>();

    private Direction direction = Direction.RIGHT; // default direction
    private boolean moved = false; // moving (don't allows moving in different directions at the same time
    private boolean running = false; // is our application running

//    private Path p = Paths.get("." + File.separator +"LeaderBoard.txt");
//    private File leaderBoard = new File("sample/LeaderBoard.txt");

    private Timeline timeline = new Timeline(); // our animation

    private ObservableList<Node> snake; // we will display and iterate over it nad our snake body, we will iterate over it.
    private int scores = 0;

    private Parent createContent() throws Exception {
        URL leaderBoardUrl = getClass().getResource("LeaderBoard.txt"); //gettin file from relative package
        File leaderBoard = new File(leaderBoardUrl.getPath()); // adding file from relative package
        if(leaderBoard.exists()){
            System.out.println("YEAHH!HH!H!");
        } else {
            System.out.println("NO!");
        }
        System.out.println(leaderBoard);
        Parent root1 = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Pane root2 = (Pane) root1.lookup("#paneOut");
        Pane root = (Pane) root2.lookup("#panePlay");

        root.setPrefSize(APP_W, APP_H); // setting pane's size
        double getRootTX= root.getTranslateX();
        double getRootTY = root.getTranslateY();

        Group snakeBody = new Group(); // we get children from the group and assign them to our snake list below
        snake = snakeBody.getChildren();// snake list

        Label scoresVal = (Label) root2.lookup("#scoresVal");
        scoresVal.setText(""+scores);
        Label lastScor = (Label) root1.lookup("#lastScoreVal");
        lastScor.setText(""+lastScore);
        Label bestScor = (Label) root1.lookup("#bestScoreVal");
        bestScor.setText(""+bestScore);
        Label speedVal = (Label) root1.lookup("#speedVal");
        if(speed == 0.2){
            speedVal.setText("Slow");
        } else if(speed == 0.15){
            speedVal.setText("Medium");
        } else if(speed == 0.09){
            speedVal.setText("Expert");
        }

        NewBody food = new NewBody(getRootTX,getRootTY);

        Image foodIm = new Image("com/ehr/snake2/food.png");
        ImagePattern foodImp = new ImagePattern(foodIm);
        food.setrOut1(foodImp);

        foodRand(food);

        KeyFrame frame = new KeyFrame(/* KeyFrame is like a single frame in animation!!!*/ Duration.seconds(speed /*To increase difficulty lower the value*/), event -> {
            if(!running)
                return; //if not running just simple return

            boolean toRemove = snake.size() > 1; // at least two blocks in the snake body;
            Node tail = toRemove ? snake.remove(snake.size()-1) : snake.get(0); //

            double tailX = tail.getTranslateX();
            double tailY = tail.getTranslateY();

            switch (direction) {
                case UP:
                    tail.setTranslateX(snake.get(0).getTranslateX());
                    tail.setTranslateY(snake.get(0).getTranslateY() - BLOCK_SIZE);
                    break;
                case DOWN:
                    tail.setTranslateX(snake.get(0).getTranslateX());
                    tail.setTranslateY(snake.get(0).getTranslateY()+BLOCK_SIZE);
                    break;
                case LEFT:
                    tail.setTranslateX(snake.get(0).getTranslateX()-BLOCK_SIZE);
                    tail.setTranslateY(snake.get(0).getTranslateY());
                    break;
                case RIGHT:
                    tail.setTranslateX(snake.get(0).getTranslateX()+BLOCK_SIZE);
                    tail.setTranslateY(snake.get(0).getTranslateY());
                    break;
            }

            moved = true; // we can now physically move, we have changed duration

            if(toRemove)
                snake.add(0, tail); // we put tail in front -- the zeroth element

            //collision detection collision with itself!
            for(Node rect : snake) {
                if(rect != tail && tail.getTranslateX() == rect.getTranslateX() && tail.getTranslateY() == rect.getTranslateY()) { // tail name is little confusing, cause it must be a head now!!!
                    allScores.add(scores);
                    lastScore = scores;
                    lastScor.setText(""+lastScore);
                    bestScore = Collections.max(allScores, null);
                    bestScor.setText(""+bestScore);
                    scores = 0;
                    scoresVal.setText(""+scores);
                    restartGame();
                    break;
                }
            }


            if(isEndless)
                fieldIsEndless((NewBody) tail);
            else
                fieldNOTEndless((NewBody) tail, scoresVal, food, lastScor, bestScor);


            if (tail.getTranslateX() == food.getTranslateX() && tail.getTranslateY() == food.getTranslateY()) {
                foodRand(food); // setting x, and y of food to random value
                scores += 20;
                scoresVal.setText(""+scores);
                NewBody rect = new NewBody(tailX,tailY);
                snake.add(rect); //adding rectangle to snake
            }
        });

        timeline.getKeyFrames().addAll(frame); // add frame to the timeline KeyFrames
        timeline.setCycleCount(Timeline.INDEFINITE); // it will always run same frame(there is any one frame to run

        root.getChildren().addAll(food, snakeBody);

        return root2;
    }

    private void fieldNOTEndless(NewBody tail, Label scoresVal, NewBody food, Label lastScor, Label bestScor){
        // below is code for field with EDGES. you can comment the block which is above and uncomment block below to make field with EDGES
        if (tail.getTranslateX() < 0 /*to the left screen*/ || tail.getTranslateX() >= APP_W/*to the right screen*/ || tail.getTranslateY() < 0 /*up*/|| tail.getTranslateY() >= APP_H/*down*/) {
            allScores.add(scores);
            lastScore = scores;
            lastScor.setText(""+lastScore);
            bestScore = Collections.max(allScores, null);
            bestScor.setText(""+bestScore);

            scores = 0;
            scoresVal.setText(""+scores);
            restartGame();
            foodRand(food);
        }
    }

    private void fieldIsEndless(NewBody tail){
        //below is four if statements for creating endless field
        if(tail.getTranslateX() < 0) // to the left screeen
            tail.setTranslateX(APP_W-BLOCK_SIZE);


        if(tail.getTranslateX() >= APP_W) // right screen
            tail.setTranslateX(0.0);


        if(tail.getTranslateY() < 0) //top screen
            tail.setTranslateY(APP_H-BLOCK_SIZE);

        if(tail.getTranslateY() >= APP_H) //down screen
            tail.setTranslateY(0.0);
    }

    private void foodReset(ObservableList<Node> snake, NewBody food){ // reset if food is under snake's body. Maybe it could be more lightweight?
        boolean flag = true;
        while(flag){
            flag=false;
            ListIterator<Node> it = snake.listIterator();
            while(it.hasNext()){
                Node x = it.next();
                boolean match = x.getTranslateX() == food.getTranslateX() && x.getTranslateY() == food.getTranslateY();
                if(match) {
                    foodRand(food);
                    while(it.hasPrevious()){
                        it.previous();
                    }
                }
            }
        }

    }

    private void foodRand(NewBody food) {
        food.setTranslateX((int)(Math.random() * (APP_W - BLOCK_SIZE)/*in order to stay within screen*/)/ BLOCK_SIZE * BLOCK_SIZE);
        food.setTranslateY((int)(Math.random() * (APP_H - BLOCK_SIZE))/ BLOCK_SIZE * BLOCK_SIZE); // setting x, and y of food to random value
        foodReset(snake, food);
    }

    private void restartGame() {
        scores = 0;
        stopGame();
        startGame();
    }

    private void stopGame() {
        running = false;
        timeline.stop();
        snake.clear(); // clear the elements within snake list
    }

    private void startGame() {
        direction = Direction.RIGHT;
        NewBody head = new NewBody(100, 100);
        snake.add(head);
        timeline.play();
        running = true;
    }

    private void recursKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!moved)
                return;

            switch (event.getCode()) {
                case W:
                    if (direction != Direction.DOWN)
                        direction = Direction.UP;
                    break;
                case UP:
                    if (direction != Direction.DOWN)
                        direction = Direction.UP;
                    break;
                case S:
                    if(direction != Direction.UP)
                        direction = Direction.DOWN;
                    break;
                case DOWN:
                    if(direction != Direction.UP)
                        direction = Direction.DOWN;
                    break;
                case A:
                    if(direction != Direction.RIGHT)
                        direction = Direction.LEFT;
                    break;
                case LEFT:
                    if(direction != Direction.RIGHT)
                        direction = Direction.LEFT;
                    break;
                case D:
                    if(direction != Direction.LEFT)
                        direction = Direction.RIGHT;
                    break;
                case RIGHT:
                    if(direction != Direction.LEFT)
                        direction = Direction.RIGHT;
                    break;
                case SPACE:
                    timeline.pause();
                    scene.setOnKeyPressed(event1 -> {
                        switch(event1.getCode()){
                            case SPACE:
                                timeline.play();
                                recursKey(scene);
                                break;
                            case ESCAPE:
                                allScores.add(scores);
                                bestScore = Collections.max(allScores, null);
                                running = false;
                                timeline.stop();
                                try{
                                    Parent root2 = FXMLLoader.load(getClass().getResource("start.fxml"));
                                    window.setScene(new Scene(root2, 404, 400));
                                    prepareMainScreen(root2);
                                }
                                catch(Exception e) {}
                                break;
                        }
                    });
                    break;
                case ESCAPE:
                    allScores.add(scores);
                    bestScore = Collections.max(allScores, null);
                    running = false;
                    timeline.stop();
                    try{
                        Parent root2 = FXMLLoader.load(getClass().getResource("start.fxml"));
                        window.setScene(new Scene(root2, 404, 400));
                        prepareMainScreen(root2);
                    }
                    catch(Exception e) {}
                    break;
            }

            moved = false;
        });
    }

    public void prepareMainScreen(Parent root){

        Button changeBtn = (Button) root.lookup("#speedBt");
        Label changeText = (Label) root.lookup("#labelSpeed");
        Button endlessBtn = (Button) root.lookup("#endlessBtn");
        if(isEndless){
            endlessBtn.setText("Endless field");
        } else {
            endlessBtn.setText("NOT endless field");
        }

        if(speed == 0.2) {
            changeText.setText("Slow");
        } else if(speed == 0.15) {
            changeText.setText("Medium");
        } else if(speed == 0.09){
            changeText.setText("Expert");
        }

        endlessBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isEndless){
                    isEndless = false;
                    endlessBtn.setText("NOT endless field");
                } else {
                    isEndless = true;
                    endlessBtn.setText("Endless field");
                }
            }
        });

        changeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                if(speed == 0.2) {
                    Main.speed = 0.15;
                    changeText.setText("Medium");
                } else if(speed == 0.15) {
                    Main.speed = 0.09;
                    changeText.setText("Expert");
                } else if(speed == 0.09){
                    Main.speed = 0.2;
                    changeText.setText("Slow");
                }
            }
        });
    }



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml"));
        primaryStage.setTitle("Snake!");

        window = primaryStage;
        window.setScene(new Scene(root, 404, 400)); // setting application size

        prepareMainScreen(root);

        primaryStage.show();
    }

    public void startBtn() throws Exception {
        Scene scene = new Scene(createContent(), 404, 400);
        recursKey(scene);
        window.setScene(scene);
        window.show();
        startGame();
    }

    public void quit() {
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}