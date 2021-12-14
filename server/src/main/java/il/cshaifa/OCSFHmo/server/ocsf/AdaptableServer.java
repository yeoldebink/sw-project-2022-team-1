// This file contains material supporting section 6.13 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package il.cshaifa.OCSFHmo.server.ocsf;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The <code> AdaptableServer </code> is an adapter class that extends the <code> AbstractServer
 * </code> class in place of the <code> AbstractObservableServer </code>.
 *
 * <p>Project Name: OCSF (Object Client-Server Framework)
 *
 * <p>
 *
 * @author Dr. Robert Lagani&egrave;re
 * @version Febuary 2001
 */
class AdaptableServer extends AbstractServer {
  // Instance variables **********************************************

  /** The adapter used to simulate multiple class inheritance. */
  private ObservableServer server;

  // CONSTRUCTORS *****************************************************

  /**
   * Constructs the server adapter.
   *
   * @param host the server's host name.
   * @param port the port number.
   */
  public AdaptableServer(int port, ObservableServer server) {
    super(port);
    this.server = server;
  }

  // OVERRIDDEN METHODS ---------

  /**
   * Hook method called each time a new client connection is accepted.
   *
   * @param client the connection connected to the client.
   */
  protected final void clientConnected(ConnectionToClient client) {
    server.clientConnected(client);
  }

  /**
   * Hook method called each time a client disconnects.
   *
   * @param client the connection with the client.
   */
  protected final void clientDisconnected(ConnectionToClient client) {
    server.clientDisconnected(client);
  }

  /**
   * Hook method called each time an exception is raised in a client thread.
   *
   * @param client the client that raised the exception.
   * @param exception the exception raised.
   */
  protected final void clientException(ConnectionToClient client, Throwable exception) {
    server.clientException(client, exception);
  }

  /**
   * Hook method called when the server stops accepting connections because an exception has been
   * raised.
   *
   * @param exception the exception raised.
   */
  protected final void listeningException(Throwable exception) {
    server.listeningException(exception);
  }

  /** Hook method called when the server stops accepting connections. */
  protected final void serverStopped() {
    server.serverStopped();
  }

  /** Hook method called when the server starts listening for connections. */
  protected final void serverStarted() {
    server.serverStarted();
  }

  /** Hook method called when the server is closed. */
  protected final void serverClosed() {
    server.serverClosed();
  }

  /**
   * Handles a command sent from the client to the server.
   *
   * @param msg the message sent.
   * @param client the connection connected to the client that sent the message.
   */
  protected final void handleMessageFromClient(Object msg, ConnectionToClient client) {
    server.handleMessageFromClient(msg, client);
  }
}
