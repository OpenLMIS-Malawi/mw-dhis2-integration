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

package org.openlmis.integration.dhis2.repository;

import java.util.Map;
import java.util.UUID;
import org.openlmis.integration.dhis2.domain.Execution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ExecutionRepository extends JpaRepository<Execution, UUID> {

  @Query(
          value = "SELECT "
                    + "new map("
                      + "e.id AS id,"
                      + "e.manualExecution AS manualExecution,"
                      + "e.programId AS programId,"
                      + "e.facilityId AS facilityId,"
                      + "e.processingPeriodId AS processingPeriodId,"
                      + "e.targetUrl AS targetUrl,"
                      + "e.startDate AS startDae,"
                      + "e.endDate AS endDate,"
                      + "e.description AS description,"
                      + "e.userId AS userId,"
                      + "e.status AS status, "
                      + "er.responseDate AS responseDate, "
                      + "er.statusCode AS statusCode, "
                      + "er.body AS body "
                    + ") "
                  + "FROM Execution e LEFT JOIN e.response er"
  )
  Page<Map<String, Object>> findAllExcludingRequestBody(Pageable pageable);
}
