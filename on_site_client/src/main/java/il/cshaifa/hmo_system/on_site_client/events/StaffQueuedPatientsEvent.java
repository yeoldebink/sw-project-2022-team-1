package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

import java.util.ArrayList;

public class StaffQueuedPatientsEvent extends Event {
    User staff_member;
    ArrayList<Patient> patients;

    public StaffQueuedPatientsEvent(User staff_member, ArrayList<Patient> patients, Object sender) {
        super(sender);
        this.patients = patients;
        this.staff_member = staff_member;
    }
}
