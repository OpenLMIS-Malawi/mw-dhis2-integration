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
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.Integration;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PayloadRequest {

  private final Integration integration;
  private final UUID facilityId;
  private final UUID periodId;
  private final boolean manualExecution;

  public static PayloadRequest forAutomaticExecution(Integration integration, UUID periodId) {
    return new PayloadRequest(integration, null, periodId, false);
  }

  public static PayloadRequest forManualExecution(Integration integration, UUID facilityId,
      UUID periodId) {
    return new PayloadRequest(integration, facilityId, periodId, true);
  }

  Execution createExecution(Clock clock) {
    if (manualExecution) {
      return Execution.forManualExecution(integration, facilityId, periodId, clock);
    } else {
      return Execution.forAutomaticExecution(integration, periodId, clock);
    }
  }

  public String getTargetUrl() {
    return integration.getTargetUrl();
  }
}
