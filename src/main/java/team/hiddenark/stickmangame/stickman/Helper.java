package team.hiddenark.stickmangame.stickman;

class Helper{
	public static double waveFunction(double x, boolean sharp) {
		return sharp? 2/Math.PI*Math.asin(Math.cos(x)): Math.cos(x);
	}
	public static double incrementTowards(double value, double target, double step) {
        if (value < target) {
            return Math.min(value + step, target); 
        } else if (value > target) {
            return Math.max(value - step, target); 
        }
        return value;
    }
	
}