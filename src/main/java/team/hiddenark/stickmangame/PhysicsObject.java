package team.hiddenark.stickmangame;

import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class PhysicsObject extends GameObject{

    protected Body body;
    
    protected boolean enabled = true;

    public Body getBody(){
        return body;
    }
    
    public void setDefaultFilter() {
    	setFilter(new Filter() {

			@Override
			public boolean isAllowed(Filter filter) {
				// TODO Auto-generated method stub
				return !(filter instanceof DisabledFilter);	
			}
    		
    	});
    }
    
    public void setFilter(Filter f) {
    	for (BodyFixture fixture : body.getFixtures()) {
    		fixture.setFilter(f);
    	}
    }

    private MassType defaultMass;

    public void disableBody() {
    	
//    	if (!enabled) return;
    	

         // Stop motion
        body.setLinearVelocity(Vector2.create(0,0));
        body.setAngularVelocity(0.0);

        // Set the body to static to prevent forces from affecting it
        if (enabled)
             defaultMass = body.getMass().getType();

//        System.out.println("Disabled " + body.getMass().getType());
        
        body.setMass(MassType.INFINITE);

        

        // Ignore collisions completely
        for (BodyFixture fixture :  body.getFixtures()){
            fixture.setFilter(new DisabledFilter());
        }

        // Optionally, set the body to sleep
        body.setAtRest(true);

        enabled = false;
    }

    public void enableBody() {
    	
    	if (enabled) return;
    	
    	enabled = true;
        // Restore the dynamic behavior
        body.setMass(defaultMass);
//        System.out.println("Disabled " + defaultMass);

        // Allow collisions
        setDefaultFilter();

        // Wake the body
        body.setAtRest(false);
    }
    
    public static class DisabledFilter implements Filter{

		@Override
		public boolean isAllowed(Filter filter) {
			// TODO Auto-generated method stub
			return false;
		}
    	
    }

}
