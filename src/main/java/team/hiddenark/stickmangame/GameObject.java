package team.hiddenark.stickmangame;

import java.awt.*;

public class GameObject {
    private boolean visible;
    private boolean markedForRemoval;

    public boolean isMarkedForRemoval(){
        return markedForRemoval;
    }

    public void remove(){
        markedForRemoval = true;
    }

    public boolean isVisable(){
        return visible;
    }

    public void setVisible(boolean v){
        visible = v;
    }

    public void tick(double deltaTime){

    }

    public void draw(Graphics g){

    }

}
