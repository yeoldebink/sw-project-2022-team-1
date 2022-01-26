package il.cshaifa.hmo_system.server;

import java.io.IOException;


public class App {
  private static HMOServer server;

  public static void main(String[] args) throws IOException {
    server = new HMOServer(3000);
    server.listen();
  }
}
