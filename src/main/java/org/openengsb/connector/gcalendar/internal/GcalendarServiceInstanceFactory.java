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

import java.util.Map;

import org.openengsb.core.api.Domain;
import org.openengsb.core.api.ekb.EngineeringKnowledgeBaseService;
import org.openengsb.core.common.AbstractConnectorInstanceFactory;
import org.openengsb.domain.appointment.AppointmentDomainEvents;

public class GcalendarServiceInstanceFactory extends AbstractConnectorInstanceFactory<GcalendarServiceImpl> {

    private AppointmentDomainEvents appointmentEvents;
    private EngineeringKnowledgeBaseService ekbService;

    @Override
    public Domain createNewInstance(String id) {
        GcalendarServiceImpl service = new GcalendarServiceImpl(id);
        service.setAppointmentEvents(appointmentEvents);
        service.setEkbService(ekbService);

        service.setDomainId(getDomainId());
        service.setConnectorId(getConnectorId());

        return service;
    }

    @Override
    public void doApplyAttributes(GcalendarServiceImpl instance, Map<String, String> attributes) {
        instance.setGoogleUser(attributes.get("google.user"));
        instance.setGooglePassword(attributes.get("google.password"));
    }

    public void setAppointmentEvents(AppointmentDomainEvents appointmentEvents) {
        this.appointmentEvents = appointmentEvents;
    }

    public void setEkbService(EngineeringKnowledgeBaseService ekbService) {
        this.ekbService = ekbService;
    }
}
