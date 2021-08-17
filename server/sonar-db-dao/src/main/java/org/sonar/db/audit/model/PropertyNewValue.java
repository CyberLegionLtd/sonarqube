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
package org.sonar.db.audit.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.db.property.PropertyDto;
import org.sonar.db.user.UserPropertyDto;

public class PropertyNewValue extends NewValue {
  @Nullable
  private String propertyKey;

  @Nullable
  private String propertyValue;

  @Nullable
  private String userUuid;

  @Nullable
  private String userLogin;

  @Nullable
  private String componentUuid;

  @Nullable
  private String componentKey;

  @Nullable
  private String componentName;

  @Nullable
  private String qualifier;

  public PropertyNewValue(UserPropertyDto userPropertyDto, String login) {
    this.propertyKey = userPropertyDto.getKey();
    this.userUuid = userPropertyDto.getUserUuid();
    this.userLogin = login;

    setValue(propertyKey, userPropertyDto.getValue());
  }

  public PropertyNewValue(PropertyDto propertyDto, @Nullable String userLogin, @Nullable String componentKey,
    @Nullable String componentName, @Nullable String qualifier) {
    this.propertyKey = propertyDto.getKey();
    this.userUuid = propertyDto.getUserUuid();
    this.userLogin = userLogin;
    this.componentUuid = propertyDto.getComponentUuid();
    this.componentKey = componentKey;
    this.componentName = componentName;
    this.qualifier = getQualifier(qualifier);

    setValue(propertyKey, propertyDto.getValue());
  }

  public PropertyNewValue(String propertyKey) {
    this.propertyKey = propertyKey;
  }

  public PropertyNewValue(String propertyKey, @Nullable String userUuid, String userLogin) {
    this.propertyKey = propertyKey;
    this.userUuid = userUuid;
    this.userLogin = userLogin;
  }

  public PropertyNewValue(String propertyKey, String propertyValue) {
    this.propertyKey = propertyKey;

    setValue(propertyKey, propertyValue);
  }

  public PropertyNewValue(@Nullable String propertyKey, @Nullable String projectUuid, @Nullable String componentKey,
    @Nullable String componentName, @Nullable String qualifier, @Nullable String userUuid) {
    this.propertyKey = propertyKey;
    this.componentUuid = projectUuid;
    this.componentKey = componentKey;
    this.componentName = componentName;
    this.userUuid = userUuid;
    this.qualifier = getQualifier(qualifier);
  }

  @CheckForNull
  public String getPropertyKey() {
    return this.propertyKey;
  }

  @CheckForNull
  public String getPropertyValue() {
    return this.propertyValue;
  }

  @CheckForNull
  public String getUserUuid() {
    return this.userUuid;
  }

  @CheckForNull
  public String getUserLogin() {
    return this.userLogin;
  }

  @CheckForNull
  public String getComponentUuid() {
    return this.componentUuid;
  }

  @CheckForNull
  public String getComponentKey() {
    return this.componentKey;
  }

  @CheckForNull
  public String getComponentName() {
    return this.componentName;
  }

  @CheckForNull
  public String getQualifier() {
    return this.qualifier;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    addField(sb, "\"propertyKey\": ", this.propertyKey, true);
    addField(sb, "\"propertyValue\": ", this.propertyValue, true);
    addField(sb, "\"userUuid\": ", this.userUuid, true);
    addField(sb, "\"userLogin\": ", this.userLogin, true);
    addField(sb, "\"componentUuid\": ", this.componentUuid, true);
    addField(sb, "\"componentKey\": ", this.componentKey, true);
    addField(sb, "\"componentName\": ", this.componentName, true);
    addField(sb, "\"qualifier\": ", this.qualifier, true);
    endString(sb);
    return sb.toString();
  }

  private void setValue(String propertyKey, String value) {
    if (!propertyKey.contains(".secured")) {
      this.propertyValue = value;
    }
  }
}
