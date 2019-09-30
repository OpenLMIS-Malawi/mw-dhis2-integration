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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.integration.dhis2.domain.Configuration;
import org.openlmis.integration.dhis2.domain.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles({"test", "init-audit-log"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuditLogInitializerIntegrationTest {

  private static final String[] CONFIGURATION_FIELDS = {
      "id", "name", "targetUrl"
  };

  private static final String[] INTEGRATION_FIELDS = {
      "id", "programId", "cronExpression", "configurationId"
  };

  private static final String INSERT_CONFIGURATION_SQL = String.format(
      "INSERT INTO dhis2integration.configurations (%s) VALUES (%s) ",
      StringUtils.join(CONFIGURATION_FIELDS, ", "),
      StringUtils.repeat("?", ", ", CONFIGURATION_FIELDS.length)
  );

  private static final String INSERT_INTEGRATION_SQL = String.format(
      "INSERT INTO dhis2integration.integrations (%s) VALUES (%s) ",
      StringUtils.join(INTEGRATION_FIELDS, ", "),
      StringUtils.repeat("?", ", ", INTEGRATION_FIELDS.length)
  );

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private Javers javers;

  @PersistenceContext
  private EntityManager entityManager;

  @Test
  public void shouldCreateSnapshotForConfiguration() {
    UUID configurationId = UUID.randomUUID();
    addConfiguration(configurationId);

    executeTest(configurationId, Configuration.class);
  }

  @Test
  public void shouldCreateSnapshotForIntegration() {
    UUID configurationId = UUID.randomUUID();
    UUID integrationId = UUID.randomUUID();

    addConfiguration(configurationId);
    addIntegration(integrationId, configurationId);

    executeTest(integrationId, Integration.class);
  }

  private void executeTest(Object id, Class clazz) {
    //when
    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, clazz);
    List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

    assertThat(snapshots, hasSize(0));

    AuditLogInitializer auditLogInitializer = new AuditLogInitializer(applicationContext, javers);
    auditLogInitializer.run();

    snapshots = javers.findSnapshots(jqlQuery.build());

    // then
    assertThat(snapshots, hasSize(1));

    CdoSnapshot snapshot = snapshots.get(0);
    GlobalId globalId = snapshot.getGlobalId();

    assertThat(globalId, is(notNullValue()));
    assertThat(globalId, instanceOf(InstanceId.class));

    InstanceId instanceId = (InstanceId) globalId;
    assertThat(instanceId.getCdoId(), is(id));
    assertThat(instanceId.getTypeName(), is(clazz.getSimpleName()));
  }

  private void addConfiguration(UUID id) {
    entityManager.flush();
    entityManager
        .createNativeQuery(INSERT_CONFIGURATION_SQL)
        .setParameter(1, id)
        .setParameter(2, "test-configuration")
        .setParameter(3, "http://localhost")
        .executeUpdate();
  }

  private void addIntegration(UUID id, UUID configurationId) {
    entityManager.flush();
    entityManager
        .createNativeQuery(INSERT_INTEGRATION_SQL)
        .setParameter(1, id)
        .setParameter(2, UUID.randomUUID())
        .setParameter(3, "0/30 * * * * ")
        .setParameter(4, configurationId)
        .executeUpdate();
  }
}
