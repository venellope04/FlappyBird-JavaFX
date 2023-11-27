package com.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FlappyBirdJavaFX extends Application {
    private Bird bird;
    private List<Pipe> pipes;
    private boolean gameRunning;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Flappy Bird");

        bird = new Bird();
        pipes = new ArrayList<>();
        initializePipes();

        Pane root = new Pane();
        Canvas canvas = new Canvas(800, 600);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                if (!gameRunning) {
                    restartGame();
                } else {
                    bird.jump();
                }
            }
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                render(canvas.getGraphicsContext2D());
            }
        }.start();

        primaryStage.show();
    }

    private void initializePipes() {
        pipes.clear();
        gameRunning = true;

        for (int i = 0; i < 2; i++) {
            pipes.add(new Pipe(800 + i * 300, 100 + (int) (Math.random() * 150))); 
        }
    }

    private void restartGame() {
        bird.reset();
        initializePipes();
    }

    private void updateGame() {
        if (gameRunning) {
            bird.update();

            Iterator<Pipe> iterator = pipes.iterator();
            while (iterator.hasNext()) {
                Pipe pipe = iterator.next();
                pipe.update();

                if (bird.intersects(pipe)) {
                    gameRunning = false;
                }

                if (pipe.getX() + pipe.getWidth() < 0) {
                    iterator.remove();
                    pipes.add(new Pipe(800, 150 + (int) (Math.random() * 200)));
                }
            }

            if (bird.getY() > 600 || bird.getY() < 0) {
                gameRunning = false;
            }

            if (pipes.size() < 2) {
                pipes.add(new Pipe(800, 150 + (int) (Math.random() * 200)));
            }
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillRect(0, 0, 800, 600);

        bird.render(gc);

        for (Pipe pipe : pipes) {
            pipe.render(gc);
        }

        if (!gameRunning) {
            gc.setFill(Color.RED);
            gc.setFont(new javafx.scene.text.Font(25));
            gc.fillText("Game Over. Press SPACE to restart.", 150, 300);
        }
    }
}

class Bird {
    private double x, y, velocity;
    private static final double GRAVITY = 0.7;
    private static final double JUMP_STRENGTH = -15;

    Bird() {
        x = 150;
        y = 300;
        velocity = 0;
    }

    void jump() {
        velocity = JUMP_STRENGTH;
    }

    void update() {
        y += velocity;
        velocity += GRAVITY;
        
    }

    void reset() {
        x = 150;
        y = 300;
        velocity = 0;
    }

    void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, 30, 30);
    }

    double getY() {
        return y;
    }

    boolean intersects(Pipe pipe) {
        double birdCenterX = x + 15; 
        double birdCenterY = y + 15; 

        return birdCenterX < pipe.getX() + pipe.getWidth() && birdCenterX > pipe.getX() &&
                birdCenterY < pipe.getY() || birdCenterY > pipe.getY() + pipe.getHeight();
    }

}

class Pipe {
    private double x, y, width, height;
    private static final double SPEED = 5;

    Pipe(double initialX, double initialY) {
        x = initialX;
        y = initialY;
        width = 50;
        height = 600 - y;
    }

    void update() {
        x -= SPEED;
    }

    void render(GraphicsContext gc) {
        gc.setFill(Color.GREEN);

        
        gc.fillRect(x, 0, width, y);


        gc.fillRect(x, y + 200, width, height - 200);
    }

    double getX() {
        return x;
    }

    double getWidth() {
        return width;
    }

    double getY() {
        return y;
    }

    double getHeight() {
        return height;
    }
}
