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

package org.openlmis.report.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.openlmis.report.service.PermissionService.REPORTS_VIEW;
import static org.openlmis.report.service.PermissionService.REPORT_TEMPLATES_EDIT;

import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.report.dto.external.ResultDto;
import org.openlmis.report.dto.external.referencedata.RightDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.exception.PermissionMessageException;
import org.openlmis.report.service.referencedata.UserReferenceDataService;
import org.openlmis.report.utils.AuthenticationHelper;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class PermissionServiceTest {

  @Mock
  private AuthenticationHelper authenticationHelper;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private PermissionService permissionService;

  @Test
  public void shouldNotRejectViewReportsWhenUserHasViewReportsRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto right = mockRightFound(REPORTS_VIEW);
    mockUserHasRight(user, right);

    // when
    permissionService.canViewReports();

    // then
    verify(userReferenceDataService, atLeastOnce())
        .hasRight(user.getId(), right.getId());
  }

  @Test(expected = PermissionMessageException.class)
  public void shouldRejectViewReportsWhenUserDoesNotHaveViewReportsRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto right = mockRightFound(REPORTS_VIEW);
    mockUserDoesNotHaveRight(user, right);

    // when
    permissionService.canViewReports();
  }

  @Test
  public void shouldNotRejectEditReportsWhenUserHasEditReportsRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto right = mockRightFound(REPORT_TEMPLATES_EDIT);
    mockUserHasRight(user, right);

    // when
    permissionService.canEditReportTemplates();

    // then
    verify(userReferenceDataService, atLeastOnce())
        .hasRight(user.getId(), right.getId());
  }

  @Test(expected = PermissionMessageException.class)
  public void shouldRejectEditReportsWhenUserDoesNotHaveEditReportsRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto right = mockRightFound(REPORT_TEMPLATES_EDIT);
    mockUserDoesNotHaveRight(user, right);

    // when
    permissionService.canEditReportTemplates();
  }

  @Test
  public void shouldNotRejectMultiplePermissionsWhenUserHasEveryRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto viewRight = mockRightFound(REPORTS_VIEW);
    RightDto editRight = mockRightFound(REPORT_TEMPLATES_EDIT);

    mockUserHasRight(user, viewRight);
    mockUserHasRight(user, editRight);

    // when
    permissionService.validatePermissions(REPORTS_VIEW, REPORT_TEMPLATES_EDIT);

    // then
    verify(userReferenceDataService, atLeastOnce())
        .hasRight(user.getId(), viewRight.getId());
    verify(userReferenceDataService, atLeastOnce())
        .hasRight(user.getId(), editRight.getId());
  }

  @Test(expected = PermissionMessageException.class)
  public void shouldRejectMultiplePermissionsWhenUserDoesNotHaveEveryRight() {
    // given
    UserDto user = mockUserLoggedIn();
    RightDto viewRight = mockRightFound(REPORTS_VIEW);
    RightDto editRight = mockRightFound(REPORT_TEMPLATES_EDIT);

    mockUserDoesNotHaveRight(user, viewRight);
    mockUserHasRight(user, editRight);

    // when
    permissionService.validatePermissions(REPORTS_VIEW, REPORT_TEMPLATES_EDIT);
  }

  private UserDto mockUserLoggedIn() {
    UserDto user = new UserDto();
    user.setId(UUID.randomUUID());

    given(authenticationHelper.getCurrentUser())
        .willReturn(user);

    return user;
  }

  private RightDto mockRightFound(String rightName) {
    RightDto right = new RightDto();
    right.setId(UUID.randomUUID());
    right.setName(rightName);

    given(authenticationHelper.getRight(rightName))
        .willReturn(right);

    return right;
  }

  private void mockUserHasRight(UserDto user, RightDto right) {
    mockHasRightResponse(user.getId(), right.getId(), true);
  }

  private void mockUserDoesNotHaveRight(UserDto user, RightDto right) {
    mockHasRightResponse(user.getId(), right.getId(), false);
  }

  private void mockHasRightResponse(UUID userId, UUID rightId, boolean result) {
    given(userReferenceDataService.hasRight(userId, rightId))
        .willReturn(new ResultDto<>(result));
  }

}
