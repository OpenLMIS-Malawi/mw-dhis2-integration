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

import java.time.ZonedDateTime;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;


public class ExecutionResponseDataBuilder {

  //  private static AtomicInteger instanceNumber = new AtomicInteger(0);
  //  private UUID id = UUID.randomUUID();

  private ZonedDateTime responseDate;
  private int statusCode;
  private String body;

  //  private Execution execution;

  public ExecutionResponseDataBuilder withResponseDate(ZonedDateTime responseDate) {
    this.responseDate = responseDate;
    return this;
  }

  public ExecutionResponseDataBuilder withStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public ExecutionResponseDataBuilder withBody(String body) {
    this.body = body;
    return this;
  }

  //  /**
  //   * Builds new instance of Execution (with id field).
  //   */
  //  public ExecutionResponse build() {
  //    ExecutionResponse executionResponse = buildAsNew();
  //    executionResponse.setId(id);
  //    return executionResponse;
  //  }

  /**
   * Builds new instance of Execution as a new object (without id field).
   */

  public ExecutionResponse buildAsNew() {
    return new ExecutionResponse(responseDate,statusCode,body);
  }

}
