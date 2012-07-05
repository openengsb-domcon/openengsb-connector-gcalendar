/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.connector.gcalendar.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openengsb.core.api.ekb.PersistInterface;
import org.openengsb.domain.appointment.Appointment;

public class GcalendarServiceTestUT {
    private static GcalendarServiceImpl service;
    private static ArrayList<Appointment> appointments;
    private static final String USERNAME = "openengsb.notification.test@gmail.com";
    private static final String PASSWORD = "pwd-openengsb";

    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    @BeforeClass
    public static void initiate() {
        appointments = new ArrayList<Appointment>();
        service = new GcalendarServiceImpl("id");
        service.setGoogleUser(USERNAME);
        service.setGooglePassword(PASSWORD);

        PersistInterface persistInterface = mock(PersistInterface.class);
        service.setPersistInterface(persistInterface);
    }

    private Appointment createTestAppointment() {
        Appointment appointment = new Appointment();

        appointment.setName("Breakfast at Tiffanys");
        appointment.setLocation("Tiffanys");
        appointment.setDescription("Breakfast");

        Calendar cal = Calendar.getInstance();
        Date start;
        Date end;

        start = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 2);
        end = cal.getTime();

        appointment.setStart(start);
        appointment.setEnd(end);

        return appointment;
    }

    @Test
    public void testCreateAppointment() {
        Appointment appointment = createTestAppointment();
        service.createAppointment(appointment);

        assertNotNull(appointment.getId());
        appointments.add(appointment);
    }

    @Test
    public void testLoadAppointment() {
        Appointment appointment = createTestAppointment();

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        appointment = service.loadAppointment(id);

        assertEquals(id, appointment.getId());
        assertEquals("Breakfast at Tiffanys", appointment.getName());
        assertEquals("Tiffanys", appointment.getLocation());
        assertEquals("Breakfast", appointment.getDescription());
    }

    @Test
    public void testDeleteAppointment() {
        Appointment appointment = createTestAppointment();

        service.createAppointment(appointment);

        service.deleteAppointment(appointment.getId());

        boolean testFail = true;
        try {
            service.loadAppointment(appointment.getId());
        } catch (Exception e) {
            testFail = false;
        }

        if (!testFail) {
            assert false;
        }
    }

    @Test
    public void testSingleFullDayAppointment() throws Exception {
        Appointment appointment = createTestAppointment();

        appointment.setStart(dateFormat.parse("01.01.2000"));
        appointment.setEnd(dateFormat.parse("02.01.2000"));
        appointment.setFullDay(true);

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        appointment = service.loadAppointment(id);

        String expectedStart = "01.01.2000 00:00";
        String expectedEnd = "02.01.2000 00:00";

        assertEquals(true, appointment.getFullDay());
        assertEquals(expectedStart, dateTimeFormat.format(appointment.getStart()));
        assertEquals(expectedEnd, dateTimeFormat.format(appointment.getEnd()));
    }

    @Test
    public void testMultiFullDayAppointment() throws Exception {
        Appointment appointment = createTestAppointment();

        appointment.setStart(dateFormat.parse("30.01.1999"));
        appointment.setEnd(dateFormat.parse("02.01.2000"));
        appointment.setFullDay(true);

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        appointment = service.loadAppointment(id);

        String expectedStart = "30.01.1999 00:00";
        String expectedEnd = "02.01.2000 00:00";

        assertEquals(true, appointment.getFullDay());
        assertEquals(expectedStart, dateTimeFormat.format(appointment.getStart()));
        assertEquals(expectedEnd, dateTimeFormat.format(appointment.getEnd()));
    }

    @Test
    public void testUpdateAppointmentDetails() {
        Appointment appointment = createTestAppointment();

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        String newName = "Lunch at Bettys";
        String newLocation = "Bettys";
        String newDescription = "Lunch";

        appointment.setName(newName);
        appointment.setLocation(newLocation);
        appointment.setDescription(newDescription);

        service.updateAppointment(appointment);

        appointment = service.loadAppointment(appointment.getId());
        assertEquals(id, appointment.getId());
        assertEquals(newName, appointment.getName());
        assertEquals(newLocation, appointment.getLocation());
        assertEquals(newDescription, appointment.getDescription());
    }

    @Test
    public void testUpdateAppointmentTime() throws Exception {
        Appointment appointment = createTestAppointment();

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        Date newStartTime = dateTimeFormat.parse("01.01.2000 10:00");
        Date newEndTime = dateTimeFormat.parse("01.01.2000 12:00");

        appointment.setStart(newStartTime);
        appointment.setEnd(newEndTime);

        service.updateAppointment(appointment);

        appointment = service.loadAppointment(id);
        assertEquals(id, appointment.getId());
        assertEquals(newStartTime, appointment.getStart());
        assertEquals(newEndTime, appointment.getEnd());
    }

    @Test
    public void testUpdateAppointmentToFullDay() throws Exception {
        Appointment appointment = createTestAppointment();

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        Date newStartDate = dateFormat.parse("01.01.2000");
        Date newEndDate = dateFormat.parse("02.01.2000");

        appointment.setStart(newStartDate);
        appointment.setEnd(newEndDate);
        appointment.setFullDay(true);

        service.updateAppointment(appointment);

        appointment = service.loadAppointment(id);

        String expectedStartDate = "01.01.2000";
        String expectedEndDate = "02.01.2000";

        assertTrue(appointment.getFullDay());
        assertEquals(expectedStartDate, dateFormat.format(appointment.getStart()));
        assertEquals(expectedEndDate, dateFormat.format(appointment.getEnd()));
    }

    @Test
    public void testRetrievingAppointments() {
        Appointment appointment = createTestAppointment();

        String id = service.createAppointment(appointment);
        appointments.add(appointment);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -2);
        Date start = c.getTime();
        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, +2);
        Date end = c.getTime();
        ArrayList<Appointment> apps = service.getAppointments(start, end);

        assertFalse(apps.isEmpty());

        boolean found = false;
        for (Appointment app : apps) {
            if (id.equals(app.getId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            fail("created Appointment was not retrieved");
        }
    }

    @AfterClass
    public static void cleanUp() {
        for (Appointment appointment : appointments) {
            service.deleteAppointment(appointment.getId());
        }
    }

}
