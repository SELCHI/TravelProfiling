package trendanalysis.main;
import py4j.GatewayServer;
import trendanalysis.iohandle.Messages;
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
        GatewayServer gatewayServer = new GatewayServer(new WekaTSEntryPoint(),Integer.parseInt(Messages.getString("WekaTSEntryPoint.GatewayServerPort")) ); //$NON-NLS-1$
        gatewayServer.start();
        System.out.println("Gateway Server Started"); //$NON-NLS-1$
    }
	
}
