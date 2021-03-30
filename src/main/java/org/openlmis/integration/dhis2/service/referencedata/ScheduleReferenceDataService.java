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

package org.openlmis.integration.dhis2.service.referencedata;

import java.util.List;
import java.util.UUID;
import org.openlmis.integration.dhis2.service.RequestParameters;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class ScheduleReferenceDataService extends BaseReferenceDataService<ProcessingScheduleDto> {

  @Override
  protected String getUrl() {
    return "/api/processingSchedules/";
  }

  @Override
  protected Class<ProcessingScheduleDto> getResultClass() {
    return ProcessingScheduleDto.class;
  }

  @Override
  protected Class<ProcessingScheduleDto[]> getArrayResultClass() {
    return ProcessingScheduleDto[].class;
  }

  /**
   * Retrieves schedules from the reference data service by program and facility ids.
   */
  public List<ProcessingScheduleDto> search(UUID programId, UUID facilityId) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("programId", programId)
        .set("facilityId", facilityId)
        .setPage(new PageRequest(0, Integer.MAX_VALUE, Direction.ASC, "name"));

    return getPage(parameters).getContent();
  }

}
