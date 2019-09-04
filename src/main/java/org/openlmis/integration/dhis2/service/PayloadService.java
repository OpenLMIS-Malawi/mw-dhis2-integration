/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.integration.dhis2.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;
import org.openlmis.integration.dhis2.repository.ExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayloadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PayloadService.class);

  @Autowired
  private ExecutionRepository executionRepository;

  @Autowired
  private Clock clock;

  private RestTemplate restTemplate;

  /**
   * Method is responsible for sending payload to Interop layer. Response is a status (202, 500 or
   * 503), message and notificationsChannel.
   */

  public void postPayload(PayloadRequest payloadRequest) {
    Execution execution = payloadRequest.createExecution(clock);
    executionRepository.saveAndFlush(execution);

    Payload payload = createPayload();
    ExecutionResponse response = sendPayload(payloadRequest, payload);

    execution.markAsDone(response, clock);
    executionRepository.saveAndFlush(execution);

    LOGGER.info("Response status: {}; Message: {}", response.getStatusCode(), response.getBody());
  }

  private ExecutionResponse sendPayload(PayloadRequest request, Payload payload) {
    ResponseEntity<String> response = restTemplate
        .postForEntity(request.getTargetUrl(), payload, String.class);
    ZonedDateTime responseTime = ZonedDateTime.now(clock);
    int status = response.getStatusCodeValue();

    return new ExecutionResponse(responseTime, status, response.getBody());
  }

  private Payload createPayload() {
    Set<PayloadFacility> facilities = new HashSet<>(); //get from repository
    LocalDate reportingPeriod = LocalDate.of(2019, 5, 1);
    return new Payload(facilities, reportingPeriod);
  }
}
