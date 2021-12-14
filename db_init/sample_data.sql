use hmo_system;

delete from roles where is_specialist = true;

-- specialist roles may change, so we'll leave them outside the init
insert into roles values
  (null, "Cardiologist", true),
  (null, "Endocrinologist", true),
  (null, "Neurologist", true),
  (null, "Orthopedist", true),
  (null, "Dermatologist", true)
;

delete from users;

insert into users (id, firstName, lastName, role_id) values
  (9000, "Jordan", "Sullivan", (select id from roles where name = "HMO Manager")),
  (1618, "Carla", "Espinosa", (select id from roles where name = "Clinic Manager")),
  (8793, "Carmen", "Sandiego", (select id from roles where name = "Clinic Manager")),
  (3141, "Bob", "Kelso", (select id from roles where name = "Family Doctor")),
  (7893, "Elliot", "Reed", (select id from roles where name = "Family Doctor")),
  (5123, "John", "Dorian", (select id from roles where name = "Pediatrician")),
  (5973, "Sarah", "Tizdale", (select id from roles where name = "Nurse")),
  (4532, "LaVerne", "Roberts", (select id from roles where name = "Nurse")),
  (1499, "Perry", "Cox", (select id from roles where name = "Endocrinologist")),
  (8561, "Christopher", "Turk", (select id from roles where name = "Neurologist")),
  (2887, "Glenn", "Matthews", (select id from roles where name = "Lab Technician")),
  (1967, "Franklyn", "Kurosawa", (select id from roles where name = "Lab Technician")),
  (459721591, "Avi", "Ron", (select id from roles where name = "Patient")),
  (254789321, "Tyler", "Durden", (select id from roles where name = "Patient")),
  (985241266, "Marquis", "De Carabas", (select id from roles where name = "Patient"))
;

delete from clinics;

insert into clinics values
      (null, "Carmel Center", "HaNassi 45, Haifa", "8:00-16:00", "8:00-16:00", "8:00-12:00, 16:00-20:00", "8:00-16:00", "8:00-16:00", "8:00-12:00", null),
      (null, "Dizengoff", "Dizengoff 12, Tel Aviv", "8:00-16:00", "8:00-12:00, 16:00-20:00", "8:00-16:00", "8:00-16:00", "8:00-16:00", "8:00-12:00", null)
;

delete from clinic_staff;

insert into clinic_staff (clinic_id, user_id) values
  (1, 1618),
  (2, 8793),
  (1, 3141),
  (2, 7893),
  (1, 5123),
  (2, 5123),
  (1, 5973),
  (2, 4532),
  (1, 1499),
  (2, 1499),
  (1, 8561),
  (1, 2887),
  (2, 1967)
;
