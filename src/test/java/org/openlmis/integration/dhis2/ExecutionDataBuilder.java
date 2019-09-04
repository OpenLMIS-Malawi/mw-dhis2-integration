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

package org.openlmis.integration.dhis2;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;
import org.openlmis.integration.dhis2.domain.Integration;

public class ExecutionDataBuilder {

  private static AtomicInteger instanceNumber = new AtomicInteger(0);
  private UUID id = UUID.randomUUID();
  private boolean manualExecution = false;
  private UUID programId = UUID.randomUUID();
  private UUID facilityId = UUID.randomUUID();
  private UUID processingPeriodId = UUID.randomUUID();
  private String targetUrl =  "https://lmis-dev.health.gov.mw";
  private ZonedDateTime startDate = ZonedDateTime.now();
  private ZonedDateTime endDate = ZonedDateTime.now();
  private ExecutionResponse response = new ExecutionResponseDataBuilder().buildAsNew();


  public ExecutionDataBuilder withManualExecution(boolean manualExecution) {
    this.manualExecution = manualExecution;
    return this;
  }

  public ExecutionDataBuilder withProgramId(UUID programId) {
    this.programId = programId;
    return this;
  }

  public ExecutionDataBuilder withFacilityId(UUID facilityId) {
    this.facilityId = facilityId;
    return this;
  }

  public ExecutionDataBuilder withProcessingPeriodId(UUID processingPeriodId) {
    this.processingPeriodId = processingPeriodId;
    return this;
  }

  public ExecutionDataBuilder withTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
    return this;
  }

  public ExecutionDataBuilder withStartDate(ZonedDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  public ExecutionDataBuilder withEndDate(ZonedDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  public ExecutionDataBuilder withResponse(ExecutionResponse response) {
    this.response = response;
    return this;
  }

  /**
   * Builds new instance of Execution (with id field).
   */
  public Execution build() {
    Execution execution = buildAsAutomatic();
    execution.setId(id);
    return execution;
  }

  /**
   * Builds new instance of Execution as a new object (without id field) as Automatic execution.
   */

  public Execution buildAsAutomatic() {
    Integration integration = new IntegrationDataBuilder().build();
    return Execution.forAutomaticExecution(
        integration, UUID.randomUUID(), Clock.systemUTC());
  }

  /**
   * Builds new instance of Execution as a new object (without id field) as Manual execution.
   */

  public Execution buildAsManual() {
    Integration integration = new IntegrationDataBuilder().build();
    return Execution.forManualExecution(
        integration, UUID.randomUUID(),UUID.randomUUID(), Clock.systemUTC());
  }
}
