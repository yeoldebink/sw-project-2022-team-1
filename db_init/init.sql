drop database if exists hmo_system;

create database hmo_system;

use hmo_system;

-- tables

create table users(
  id int primary key,
  password_salt varchar(20),
  password char(64),
  firstName varchar(50),
  lastName varchar(50),
  role_id int,
  email varchar(50),
  phone varchar(10)
);

create table roles (
  id int primary key auto_increment,
  name varchar(30),
  is_specialist bool
);

create table clinics (
  id int primary key auto_increment,
  name varchar(50),
  address varchar(50),
  sun_hours varchar(30),
  mon_hours varchar(30),
  tue_hours varchar(30),
  wed_hours varchar(30),
  thu_hours varchar(30),
  fri_hours varchar(30),
  sat_hours varchar(30)
);

create table patients (
  user_id int,
  home_clinic_id int,
  birthday datetime
);

create table appointments (
  id int primary key auto_increment,
  type_id int,
  patient_user_id int,
  specialist_role_id int,
  staff_member_id int,
  clinic_id int,
  appt_date datetime,
  taken bool,
  comments varchar(500),
  lock_time datetime
);

create table appointment_types (
  id int primary key auto_increment,
  name varchar(20)
);

create table clinic_staff (
  clinic_id int,
  user_id int
);

-- database constants

insert into roles values
  (null, "HMO Manager", false),
  (null, "Clinic Manager", false),
  (null, "Family Doctor", false),
  (null, "Pediatrician", false),
  (null, "Nurse", false),
  (null, "Lab Technician", false),
  (null, "Patient", false)
;

insert into appointment_types values
  (null, "Family Doctor"),
  (null, "Pediatrician"),
  (null, "Specialist"),
  (null, "Nurse"),
  (null, "COVID-19 Test"),
  (null, "COVID-19 Vaccine"),
  (null, "Flu Vaccine"),
  (null, "Lab Tests")
;

-- views

create view v_users as
  select users.id, firstName, lastName, roles.name as role, email, phone
  from users left outer join roles on users.role_id = roles.id
;

create view v_clinic_staff as
  select clinics.id as clinic_id, clinics.name, clinics.address, clinic_staff.user_id, firstName, lastName, roles.name as role
  from
    clinics left outer join clinic_staff on clinics.id = clinic_staff.clinic_id
    join users on clinic_staff.user_id = users.id
    left outer join roles on users.role_id = roles.id
    order by clinic_id, role_id asc
;
