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

package org.openengsb.connector.gcalendar.internal.misc;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.openengsb.core.common.util.ModelUtils;
import org.openengsb.domain.appointment.Appointment;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;

/**
 * does the converting work between the google appointment object (CalendarEventEntry) and the object of the appointment
 * domain
 */
public final class AppointmentConverter {
    private AppointmentConverter() {
    }
    
    /**
     * converts a calendar event entry to an appointment object
     */
    public static Appointment convertCalendarEventEntryToAppointment(CalendarEventEntry entry) {
        Appointment appointment = ModelUtils.createEmptyModelObject(Appointment.class);
        appointment.setId(entry.getEditLink().getHref());
        // in google multiple Locations can be set
        appointment.setLocation(entry.getLocations().get(0).getValueString());
        appointment.setName(entry.getTitle().getPlainText());
        appointment.setDescription(entry.getPlainTextContent());
        
        for (When time : entry.getTimes()) {
            appointment.setFullDay(time.getStartTime().isDateOnly());
            
            Date start = new Date(time.getStartTime().getValue());
            Date end = new Date(time.getEndTime().getValue());
            
            if(appointment.getFullDay()) {
                start = trimTime(start, TimeZone.getDefault());
                end = trimTime(end, TimeZone.getDefault());
            }
            appointment.setStart(start);
            appointment.setEnd(end);
            break;
        }
        
        return appointment;
    }

    /**
     * converts an appointment object to a calendar event entry
     */
    public static CalendarEventEntry convertAppointmentToCalendarEventEntry(Appointment appointment) {
        CalendarEventEntry entry = new CalendarEventEntry();
        return extendCalendarEventEntryWithAppointment(entry, appointment);
    }

    /**
     * extends a calendar event entry with information of an appointment
     */
    public static CalendarEventEntry extendCalendarEventEntryWithAppointment(CalendarEventEntry entry,
            Appointment appointment) {
        entry.setId(appointment.getId());

        Where eventLocation = new Where();
        eventLocation.setValueString(appointment.getLocation());

        entry.setTitle(TextConstruct.plainText(appointment.getName()));
        entry.setContent(TextConstruct.plainText(appointment.getDescription()));

        extendCalendarEventEntryWhen(entry, appointment);

        return entry;
    }
    
    /**
     * updates the start and end times of a given CalendarEventEntry from the given Appointment
     */
    private static CalendarEventEntry extendCalendarEventEntryWhen(CalendarEventEntry entry, Appointment appointment) {
        DateTime startTime;
        DateTime endTime;

        if (appointment.getFullDay() == null || !appointment.getFullDay()) {
            startTime = new DateTime(appointment.getStart(), TimeZone.getDefault());
            endTime = new DateTime(appointment.getEnd(), TimeZone.getDefault());
        }
        else {
            startTime = new DateTime(trimTime(appointment.getStart(), TimeZone.getTimeZone("UTC")));
            endTime = new DateTime(trimTime(appointment.getEnd(), TimeZone.getTimeZone("UTC")));

            startTime.setDateOnly(true);
            endTime.setDateOnly(true);
        }

        When eventTimes;
        if (!entry.getTimes().isEmpty()) {
            eventTimes = entry.getTimes().get(0);
        }
        else {
            eventTimes = new When();
            entry.addTime(eventTimes);
        }

        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);

        return entry;
    }

    /**
     * sets the time information of a given Date to midnight in the given time zone 
     */
    private static Date trimTime(Date date, TimeZone tz) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        cal.setTimeZone(tz);

        return cal.getTime();
    }
}
