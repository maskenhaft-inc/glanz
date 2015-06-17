package ru.mr123150;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import ru.mr123150.conn.Connection;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML Canvas canvas;
    @FXML BorderPane rootPane;
    GraphicsContext gc;

    Connection conn=null;
    Connection hconn=null;

    int users=0;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        gc= canvas.getGraphicsContext2D();
        gc.beginPath();
        gc.moveTo(0, 0);
        gc.lineTo(canvas.getWidth(),0);
        gc.lineTo(canvas.getWidth(), canvas.getHeight());
        gc.lineTo(0, canvas.getHeight());
        gc.lineTo(0, 0);
        //gc.closePath();
        gc.stroke();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,event->{
            gc.fillOval(event.getX(), event.getY(), 2 * gc.getLineWidth(), 2 * gc.getLineWidth());
            send("DRAW;CLICK;"+event.getX()+";"+event.getY());
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            send("DRAW;PRESS;"+event.getX()+";"+event.getY());
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
            send("DRAW;DRAG;"+event.getX()+";"+event.getY());
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            //gc.closePath();
            send("DRAW;RELEASE");
        });
    }

    public void resizeCanvas(double width, double height){
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);
        gc.beginPath();
        gc.moveTo(0, 0);
        gc.lineTo(canvas.getWidth(), 0);
        gc.lineTo(canvas.getWidth(), canvas.getHeight());
        gc.lineTo(0, canvas.getHeight());
        gc.lineTo(0, 0);
        gc.stroke();
        //gc.closePath();
    }

    @FXML public void connect(){
        try{
            conn=new Connection("192.168.0.110",5050);
            hconn=new Connection("192.168.0.110",5051);
            listen();
            send("CONNECT;REQUEST");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML public void host(){
        try{
            hconn=new Connection(5050);
            conn=new Connection(5051);
            System.out.println("//SERVER STARTED");
            listen();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listen(){
        Task task=new Task<Void>(){
            @Override protected Void call(){
                while(true){
                    try{
                        String str=hconn.receive();
                        if (!str.equals("")) Platform.runLater(()->{receive(str);});
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        break;
                    }
                }
                return null;
            }
        };

        Thread th=new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void send(String str){
        if(conn!=null&&(!conn.isHost()||users>0)) {
            try {
                //if (!conn.isHost()) str += (";" + conn.getAddress());
                conn.send(str);
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public void receive(String str){
        System.out.println(str);
        String arr[]=str.split(";");
        switch(arr[0]){
            case "CONNECT":
                if(conn.isHost()||arr[arr.length-1].equals(conn.getAddress())){
                    switch(arr[1]) {
                        case "REQUEST":
                            ++users;
                            try {
                                conn.send("CONNECT;TEST");
                                if (true) {
                                    conn.send("CONNECT;ACCEPT;" + arr[2]);
                                    conn.send("SYNC;SIZE;" + canvas.getWidth() + ";" + canvas.getHeight());
                                    conn.send("SYNC;LAYERS;1"); //Stub for multi-layers
                                    conn.send("SYNC;DATA;0;data"); //Stub for data sync
                                } else {
                                    conn.send("CONNECT;REJECT;" + arr[2]);
                                    --users;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case "DISCONNECT":
                if(conn.isHost()||arr[arr.length-1].equals(conn.getAddress())) {
                    if (conn.isHost()) --users;
                    else conn = null;
                    break;
                }
            case "DRAW":
                if(conn.isHost()||!arr[arr.length-1].equals(conn.getAddress())) {
                    switch (arr[1]) {
                        case "CLICK":
                            gc.fillOval(Double.parseDouble(arr[2]), Double.parseDouble(arr[3]), 2 * gc.getLineWidth(), 2 * gc.getLineWidth());
                            if (conn.isHost()) send(str);
                            break;
                        case "PRESS":
                            gc.beginPath();
                            gc.moveTo(Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
                            if (conn.isHost()) send(str);
                            break;
                        case "DRAG":
                            gc.lineTo(Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
                            gc.stroke();
                            if (conn.isHost()) send(str);
                            break;
                        case "RELEASE":
                            //gc.closePath();
                            if (conn.isHost()) send(str);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void disconnect(){
        send("DISCONNECT");
    }
}
