package il.cshaifa.hmo_system.client.gui.login;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class LoginController extends Controller {

  public LoginController(ViewController view_controller) {
    super(view_controller);
    EventBus.getDefault().register(this);
  }

  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {

  }

  @Subscribe
  public void OnLoginRequestEvent(LoginEvent event){
    try {
      HMOClient.getClient().loginRequest(event.id, event.password);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void OnLoginRequestResponse(LoginMessage message) throws Exception {
    if(!message.message_type.equals(Message.messageType.RESPONSE)) return;

    //Now we know we got a response from the server with a user, lets see the data
    var user = message.user;
    if(user == null){
      incorrectUser();
      return;
    }

    var role = message.user.getRole();
    if (role.getName().contains("Manager")){
      //Open MangerDashboard
      throw new Exception("MEOWOWOWOWO");
    }

  }

  private void incorrectUser(){
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        ((LoginViewController) view_controller).setFailedText();
      }
    });
  }
}
