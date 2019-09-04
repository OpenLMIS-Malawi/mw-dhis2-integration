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
import java.util.UUID;

import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.Integration;

public class ExecutionDataBuilder {

  private UUID id = UUID.randomUUID();
  private UUID facilityId = UUID.randomUUID();
  private UUID processingPeriodId = UUID.randomUUID();
  private Clock startDate = Clock.systemUTC();

  public ExecutionDataBuilder withFacilityId(UUID facilityId) {
    this.facilityId = facilityId;
    return this;
  }

  public ExecutionDataBuilder withProcessingPeriodId(UUID processingPeriodId) {
    this.processingPeriodId = processingPeriodId;
    return this;
  }

  public ExecutionDataBuilder withStartDate(Clock startDate) {
    this.startDate = startDate;
    return this;
  }


  /**
   * Builds new instance of Execution (with id field) as Automatic execution.
   */
  public Execution buildAsAutomatic() {
    Execution execution = buildAsNewAutomatic();
    execution.setId(id);
    return execution;
  }

  /**
   * Builds new instance of Execution (with id field) as Manual execution.
   */
  public Execution buildAsManual() {
    Execution execution = buildAsNewManual();
    execution.setId(id);
    return execution;
  }

  /**
   * Builds new instance of Execution as a new object (without id field) as Automatic execution.
   */

  public Execution buildAsNewAutomatic() {
    Integration integration = new IntegrationDataBuilder().build();
    return Execution.forAutomaticExecution(
        integration, processingPeriodId, startDate);
  }

  /**
   * Builds new instance of Execution as a new object (without id field) as Manual execution.
   */

  public Execution buildAsNewManual() {
    Integration integration = new IntegrationDataBuilder().build();
    return Execution.forManualExecution(
        integration, facilityId, processingPeriodId, startDate);
  }
}
