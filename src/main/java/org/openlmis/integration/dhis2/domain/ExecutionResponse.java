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

package org.openlmis.integration.dhis2.domain;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "execution_responses")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "execution")
@ToString(exclude = "execution")
@SuppressWarnings("PMD.UnusedPrivateField")
public final class ExecutionResponse {

  @Id
  private UUID id;

  @Getter
  @Column(nullable = false, columnDefinition = BaseEntity.TIMESTAMP_COLUMN_DEFINITION)
  private ZonedDateTime responseDate;

  @Getter
  @Column(nullable = false)
  private int statusCode;

  @Getter
  @Column(nullable = false, columnDefinition = BaseEntity.TEXT_COLUMN_DEFINITION)
  private String body;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  private Execution execution;

  /**
   * Creates new instance with passed data.
   */
  public ExecutionResponse(ZonedDateTime responseDate, int statusCode, String body) {
    this.responseDate = responseDate;
    this.statusCode = statusCode;
    this.body = body;
  }

  void setExecution(Execution execution) {
    if (null != execution) {
      this.id = execution.getId();
      this.execution = execution;
    } else {
      this.id = null;
      this.execution = null;
    }
  }

  boolean isSuccess() {
    HttpStatus status = HttpStatus.valueOf(statusCode);

    return status.is1xxInformational()
        || status.is2xxSuccessful()
        || status.is3xxRedirection();
  }

  /**
   * Export the current object state.
   */
  public void export(Exporter exporter) {
    exporter.setResponseDate(responseDate);
    exporter.setStatusCode(statusCode);
    exporter.setBody(body);
  }

  public interface Exporter {

    void setResponseDate(ZonedDateTime responseDate);

    void setStatusCode(int statusCode);

    void setBody(String body);

  }

}
