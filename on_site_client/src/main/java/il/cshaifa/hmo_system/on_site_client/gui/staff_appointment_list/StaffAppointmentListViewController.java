package il.cshaifa.hmo_system.on_site_client.gui.staff_appointment_list;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StaffAppointmentListViewController {

    @FXML private TableView<?> appt_table;
    @FXML private TableColumn<?, ?> appt_time;
    @FXML private TableColumn<?, ?> appt_type_name;
    @FXML private Label current_date;
    @FXML private TableColumn<?, ?> patient_home_clinic;
    @FXML private TableColumn<?, ?> patient_name;
    @FXML private Label staff_member_role_name;

}
