package team.hiddenark.stickmangame;

import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class PhysicsObject extends GameObject{

    protected Body body;

    public Body getBody(){
        return body;
    }

    private MassType defaultMass;

    public void disableBody(Body body) {
         // Stop motion
        body.setLinearVelocity(Vector2.create(0,0));
        body.setAngularVelocity(0.0);

        // Set the body to static to prevent forces from affecting it
        body.setMass(MassType.INFINITE);

        defaultMass = body.getMass().getType();

        // Ignore collisions completely
        for (BodyFixture fixture :  body.getFixtures()){
            fixture.setFilter(filter -> false);
        }

        // Optionally, set the body to sleep
        body.setAtRest(true);
    }

    public void enableBody(Body body) {
        // Restore the dynamic behavior
        body.setMass(defaultMass);

        // Allow collisions
        for (BodyFixture fixture :  body.getFixtures()){
            fixture.setFilter(Filter.DEFAULT_FILTER);
        }

        // Wake the body
        body.setAtRest(false);
    }

}
