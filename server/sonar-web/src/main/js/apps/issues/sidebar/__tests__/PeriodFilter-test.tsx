/*
 * SonarQube
 * Copyright (C) 2009-2022 SonarSource SA
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
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import * as React from 'react';
import PeriodFilter, { PeriodFilterProps } from '../PeriodFilter';

it('should be collapsible', async () => {
  const user = userEvent.setup();

  renderPeriodFilter();

  expect(screen.getByText('issues.new_code')).toBeInTheDocument();

  await user.click(screen.getByText('issues.facet.period'));

  expect(screen.queryByText('issues.new_code')).not.toBeInTheDocument();
});

it('should filter when clicked', async () => {
  const user = userEvent.setup();
  const onChange = jest.fn();

  renderPeriodFilter({ onChange });

  await user.click(screen.getByText('issues.new_code'));

  expect(onChange).toBeCalledWith({
    createdAfter: undefined,
    createdAt: undefined,
    createdBefore: undefined,
    createdInLast: undefined,
    inNewCodePeriod: true
  });
});

it('should be clearable', async () => {
  const user = userEvent.setup();
  const onChange = jest.fn();

  renderPeriodFilter({ onChange, newCodeSelected: true });

  await user.click(screen.getByText('clear'));

  expect(onChange).toBeCalledWith({
    inNewCodePeriod: undefined
  });
});

function renderPeriodFilter(overrides: Partial<PeriodFilterProps> = {}) {
  return render(
    <PeriodFilter
      fetching={false}
      newCodeSelected={false}
      onChange={jest.fn()}
      stats={{}}
      {...overrides}
    />
  );
}
