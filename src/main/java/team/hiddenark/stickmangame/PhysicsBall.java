package team.hiddenark.stickmangame;

import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.AbstractPhysicsBody;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;

import java.awt.*;

public class PhysicsBall extends PhysicsObject{

    private int radius;
    GameWindow window;

    public PhysicsBall (GameWindow window, int x,int y,int r){
        this.window = window;
        Circle c = new Circle(window.toPUnits(r));
        this.body = new Body();
        this.body.addFixture(c);
        this.body.translate(window.toVector2(x,y));
        this.body.setMass(MassType.NORMAL);
        this.radius = r;
        this.setVisible(true);
    }

    @Override
    public void draw(Graphics g) {

        Point p = window.toGraphicsPoint(body.getWorldCenter());
        g.setColor(Color.WHITE);
        g.fillOval(p.x-radius,p.y-radius,radius*2,radius*2);

    }

    @Override
    public Body getBody() {
        return body;
    }
}
