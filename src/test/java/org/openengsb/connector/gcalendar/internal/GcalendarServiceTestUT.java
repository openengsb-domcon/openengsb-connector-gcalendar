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
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openengsb.core.api.ekb.PersistInterface;
import org.openengsb.core.common.util.ModelUtils;
import org.openengsb.domain.appointment.Appointment;

public class GcalendarServiceTestUT {
    private static GcalendarServiceImpl service;
    private static ArrayList<Appointment> appointments;
    private static final String USERNAME = "openengsb.notification.test@gmail.com";
    private static final String PASSWORD = "pwd-openengsb";

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
        Appointment a = ModelUtils.createEmptyModelObject(Appointment.class);
        a.setDescription("teststring");
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, +3);
        Date end = c.getTime();
        a.setStart(start);
        a.setEnd(end);
        a.setLocation("somewhere");
        a.setName("this is a test");

        return a;
    }

    @Test
    public void testCreateAppointment() {
        Appointment a = createTestAppointment();
        service.createAppointment(a);

        assertNotNull(a.getId());
        appointments.add(a);
    }

    @Test
    public void testDeleteAppointment() {
        Appointment a = createTestAppointment();

        service.createAppointment(a);
        
        service.deleteAppointment(a.getId());

        boolean testFail = true;
        try {
            service.loadAppointment(a.getId());
        } catch (Exception e) {
            testFail = false;
        }

        if (!testFail) {
            assert false;
        }
    }

    @Test
    public void testUpdateAppointment() {
        Appointment a = createTestAppointment();

        service.createAppointment(a);

        a.setDescription("i hope this works");
        service.updateAppointment(a);

        a = service.loadAppointment(a.getId());
        assertEquals("i hope this works", a.getDescription());

        appointments.add(a);
    }

    @Test
    public void testRetrievingAppointments() {
        Appointment a1 = createTestAppointment();
        
        service.createAppointment(a1);
        
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -2);
        Date start = c.getTime();
        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, +2);
        Date end = c.getTime();
        ArrayList<Appointment> apps = service.getAppointments(start, end);
        assertFalse(apps.isEmpty());
    }

    @AfterClass
    public static void cleanUp() {
        for (Appointment appointment : appointments) {
            service.deleteAppointment(appointment.getId());
        }
    }

}
