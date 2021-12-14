package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.util.Objects;

public class HMOServer extends AbstractServer {

  public HMOServer(int port) {
    super(port);
  }

  @Override
  protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String msgString = msg.toString();
    if (msgString.startsWith("#warning")) {
      Warning warning = new Warning("Warning from server!");
      try {
        client.sendToClient(warning);
        System.out.format(
            "Sent warning to client %s\n",
            Objects.requireNonNull(client.getInetAddress()).getHostAddress());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
    // TODO Auto-generated method stub

    System.out.println("Client Disconnected.");
    super.clientDisconnected(client);
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
    super.clientConnected(client);
    System.out.println("Client connected: " + client.getInetAddress());
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Required argument: <port>");
    } else {
      HMOServer server = new HMOServer(Integer.parseInt(args[0]));
      server.listen();
    }
  }
}
