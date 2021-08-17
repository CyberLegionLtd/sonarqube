/*
 * SonarQube
 * Copyright (C) 2009-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.alm.setting;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.api.impl.utils.TestSystem2;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.audit.AuditPersister;
import org.sonar.db.audit.model.DevOpsPlatformSettingNewValue;
import org.sonar.db.project.ProjectDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.sonar.db.almsettings.AlmSettingsTesting.newGithubProjectAlmSettingDto;

public class ProjectAlmSettingDaoWithPersisterTest {
  private static final long A_DATE = 1_000_000_000_000L;
  private static final long A_DATE_LATER = 1_700_000_000_000L;
  private static final String A_UUID = "SOME_UUID";

  private final ArgumentCaptor<DevOpsPlatformSettingNewValue> newValueCaptor = ArgumentCaptor.forClass(DevOpsPlatformSettingNewValue.class);
  private final AuditPersister auditPersister = mock(AuditPersister.class);

  private final TestSystem2 system2 = new TestSystem2().setNow(A_DATE);
  @Rule
  public final DbTester db = DbTester.create(system2, auditPersister);

  private final DbSession dbSession = db.getSession();
  private final UuidFactory uuidFactory = mock(UuidFactory.class);
  private final ProjectAlmSettingDao underTest = db.getDbClient().projectAlmSettingDao();

  @Test
  public void insertAndUpdateExistingBindingArePersisted() {
    when(uuidFactory.create()).thenReturn(A_UUID);
    AlmSettingDto githubAlmSetting = db.almSettings().insertGitHubAlmSetting();
    ProjectDto project = db.components().insertPrivateProjectDto();
    ProjectAlmSettingDto projectAlmSettingDto = newGithubProjectAlmSettingDto(githubAlmSetting, project)
      .setSummaryCommentEnabled(false);
    underTest.insertOrUpdate(dbSession, projectAlmSettingDto, githubAlmSetting.getKey(), project.getName(), project.getKey());

    verify(auditPersister).addDevOpsPlatformSetting(eq(dbSession), newValueCaptor.capture());
    DevOpsPlatformSettingNewValue newValue = newValueCaptor.getValue();
    assertThat(newValue)
      .extracting(DevOpsPlatformSettingNewValue::getDevOpsPlatformSettingUuid, DevOpsPlatformSettingNewValue::getKey,
        DevOpsPlatformSettingNewValue::getProjectUuid, DevOpsPlatformSettingNewValue::getProjectKey,
        DevOpsPlatformSettingNewValue::getProjectName)
      .containsExactly(githubAlmSetting.getUuid(), githubAlmSetting.getKey(), project.getUuid(), project.getKey(), project.getName());
    assertThat(newValue.toString()).doesNotContain("\"url\"");

    AlmSettingDto anotherGithubAlmSetting = db.almSettings().insertGitHubAlmSetting();
    system2.setNow(A_DATE_LATER);
    ProjectAlmSettingDto newProjectAlmSettingDto = newGithubProjectAlmSettingDto(anotherGithubAlmSetting, project)
      .setSummaryCommentEnabled(false);
    underTest.insertOrUpdate(dbSession, newProjectAlmSettingDto, anotherGithubAlmSetting.getKey(), project.getName(), project.getKey());

    verify(auditPersister).updateDevOpsPlatformSetting(eq(dbSession), newValueCaptor.capture());
    newValue = newValueCaptor.getValue();
    assertThat(newValue)
      .extracting(DevOpsPlatformSettingNewValue::getDevOpsPlatformSettingUuid, DevOpsPlatformSettingNewValue::getKey,
        DevOpsPlatformSettingNewValue::getProjectUuid, DevOpsPlatformSettingNewValue::getProjectName,
        DevOpsPlatformSettingNewValue::getAlmRepo, DevOpsPlatformSettingNewValue::getAlmSlug,
        DevOpsPlatformSettingNewValue::isSummaryCommentEnabled, DevOpsPlatformSettingNewValue::isMonorepo)
      .containsExactly(anotherGithubAlmSetting.getUuid(), anotherGithubAlmSetting.getKey(), project.getUuid(), project.getName(),
        newProjectAlmSettingDto.getAlmRepo(), newProjectAlmSettingDto.getAlmSlug(),
        newProjectAlmSettingDto.getSummaryCommentEnabled(), newProjectAlmSettingDto.getMonorepo());
    assertThat(newValue.toString()).doesNotContain("\"url\"");
  }

  @Test
  public void deleteByProjectIsPersisted() {
    when(uuidFactory.create()).thenReturn(A_UUID);
    AlmSettingDto githubAlmSetting = db.almSettings().insertGitHubAlmSetting();
    ProjectDto project = db.components().insertPrivateProjectDto();
    ProjectAlmSettingDto projectAlmSettingDto = newGithubProjectAlmSettingDto(githubAlmSetting, project)
      .setSummaryCommentEnabled(false);
    underTest.insertOrUpdate(dbSession, projectAlmSettingDto, githubAlmSetting.getKey(), project.getName(), project.getKey());
    underTest.deleteByProject(dbSession, project);

    verify(auditPersister).deleteDevOpsPlatformSetting(eq(dbSession), newValueCaptor.capture());
    DevOpsPlatformSettingNewValue newValue = newValueCaptor.getValue();
    assertThat(newValue)
      .extracting(DevOpsPlatformSettingNewValue::getProjectUuid, DevOpsPlatformSettingNewValue::getProjectKey,
        DevOpsPlatformSettingNewValue::getProjectName)
      .containsExactly(project.getUuid(), project.getKey(), project.getName());
    assertThat(newValue.toString()).doesNotContain("devOpsPlatformSettingUuid");
  }

  @Test
  public void deleteByWithoutAffectedRowsProjectIsNotPersisted() {
    ProjectDto project = db.components().insertPrivateProjectDto();

    underTest.deleteByProject(dbSession, project);

    verify(auditPersister).addComponent(any(), any(), any());
    verifyNoMoreInteractions(auditPersister);
  }

  @Test
  public void deleteByAlmSettingNotTrackedIsNotPersisted() {
    AlmSettingDto githubAlmSetting = db.almSettings().insertGitHubAlmSetting();
    underTest.deleteByAlmSetting(dbSession, githubAlmSetting);

    verifyNoInteractions(auditPersister);
  }
}
