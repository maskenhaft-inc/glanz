package ru.mr123150.conn;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.mr123150.tool.Brush;
import ru.mr123150.tool.Tool;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

/**
 * Created by victorsnesarevsky on 13.06.15.
 */
public class User {
    protected int id;
    protected InetAddress address;

    protected double h=0;
    protected double s=0;
    protected double b=0;

    protected double size=1;

    protected double x=0;
    protected double y=0;

    protected GraphicsContext gc=null;

    protected Tool tool=null;

    public User() throws IOException{
        this.id=0;
        this.address=InetAddress.getLocalHost();
    }

    public User(GraphicsContext gc) throws IOException{
        this.id=0;
        this.address=InetAddress.getLocalHost();
        this.gc=gc;
    }

    public User(int id) throws IOException{
        this.id=id;
        this.address=InetAddress.getLocalHost();
    }

    public User(int id, GraphicsContext gc) throws IOException{
        this.id=id;
        this.address=InetAddress.getLocalHost();
        this.gc=gc;
    }

    public User(int id,String address) throws IOException{
        this.id=id;
        this.address=InetAddress.getByName(address);
    }

    public User(int id,String address, GraphicsContext gc) throws IOException{
        this.id=id;
        this.address=InetAddress.getByName(address);
        this.gc=gc;
    }

    public void setContext(GraphicsContext gc){this.gc=gc;}

    public int id(){
        return id;
    }

    public InetAddress address(){return address;}

    public String addressText(){return address.getHostAddress();}

    public void setId(int id){this.id=id;}

    public Color color(){return Color.hsb(h,s,b);}

    public String colorText(){return h+";"+s+";"+b;}

    public void setColor(double h, double s, double b){
        this.h=h;
        this.s=s;
        this.b=b;
    }

    public void setColor(Color color){
        this.h=color.getHue();
        this.s=color.getSaturation();
        this.b=color.getBrightness();
    }

    public double size(){return size;}

    public void setSize(double size){this.size=size;}

    public double x(){return x;}

    public double y(){return y;}

    public void setCoord(double x, double y){
        this.x=x;
        this.y=y;
    }

    public String toolText(){return tool==null?"NULL":tool.title();}

    public void setTool(Tool tool){
        tool.setContext(gc);
        this.tool=tool;
    }

    public void lineTo(double x, double y){
        gc.beginPath();
        gc.setStroke(color());
        gc.setLineWidth(size);
        gc.moveTo(this.x,this.y);
        if(tool==null)tool=new Brush();//todo tmp
        Color tmp=tool.lineTo(x,y);
        if(tmp!=null)setColor(tmp);
        setCoord(x,y);
        gc.closePath();
    }

    public void dot(double x, double y){
        gc.setFill(color());
        Color tmp=tool.dot(x,y);
        if(tmp!=null)setColor(tmp);
    }
}
