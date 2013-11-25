package trendanalysis.main;
import py4j.GatewayServer;
import trendanalysis.main.WekaTSForecaster;
public class WekaTSEntryPoint {
	
	private WekaTSForecaster wekaTSForcaster;
	
	public WekaTSEntryPoint() {
		wekaTSForcaster = new WekaTSForecaster();
	}
	
	public WekaTSForecaster getWekaTSForecaster() {
        return new WekaTSForecaster();
    }

    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new WekaTSEntryPoint());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
	
}
