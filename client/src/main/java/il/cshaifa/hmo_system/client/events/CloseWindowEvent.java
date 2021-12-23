package il.cshaifa.hmo_system.client.events;

public class CloseWindowEvent {
    private String windowTitle;

    public CloseWindowEvent(String windowTitle){
        this.windowTitle = windowTitle;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
