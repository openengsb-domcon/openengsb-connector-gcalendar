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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openengsb.core.api.model.OpenEngSBModelEntry;
import org.openengsb.domain.appointment.models.Appointment;

public class TestAppointmentModel implements Appointment {
    private String id;
    private String name;
    private String description;
    private String location;
    private Date start;
    private Date end;
    private boolean fullDay;

    @Override
    public List<OpenEngSBModelEntry> getOpenEngSBModelEntries() {
        List<OpenEngSBModelEntry> entries = new ArrayList<OpenEngSBModelEntry>();
        entries.add(new OpenEngSBModelEntry("id", id, String.class));
        entries.add(new OpenEngSBModelEntry("name", name, String.class));
        entries.add(new OpenEngSBModelEntry("description", description, String.class));
        entries.add(new OpenEngSBModelEntry("location", location, String.class));
        entries.add(new OpenEngSBModelEntry("start", start, Date.class));
        entries.add(new OpenEngSBModelEntry("end", end, Date.class));
        entries.add(new OpenEngSBModelEntry("fullDay", fullDay, Boolean.class));
        return entries;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public void setStart(Date start) {
        this.start = start;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    @Override
    public boolean isFullDay() {
        return fullDay;
    }
}
