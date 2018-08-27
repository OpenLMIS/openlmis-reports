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

package org.openlmis.report.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.report.service.PermissionService.REPORTS_VIEW;

import guru.nidi.ramltester.junit.RamlMatchers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.dto.JasperTemplateDto;
import org.openlmis.report.exception.JasperReportViewException;
import org.openlmis.report.exception.PermissionMessageException;
import org.openlmis.report.repository.JasperTemplateRepository;
import org.openlmis.report.service.JasperReportsViewService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

@SuppressWarnings("PMD.TooManyMethods")
public class JasperTemplateControllerIntegrationTest extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/reports/templates/common";
  private static final String ID_URL = RESOURCE_URL + "/{id}";
  private static final String FORMAT_PARAM = "format";
  private static final String REPORT_URL = ID_URL + "/{" + FORMAT_PARAM + "}";
  private static final String PDF_FORMAT = "pdf";

  @MockBean
  private JasperTemplateRepository jasperTemplateRepository;

  @MockBean
  private JasperReportsViewService jasperReportsViewService;

  @Before
  public void setUp() {
    mockUserAuthenticated();
  }

  // GET /api/reports/templates

  @Test
  public void shouldGetVisibleTemplates() {
    // given
    JasperTemplate[] templates = { generateExistentTemplate(), generateExistentTemplate() };
    given(jasperTemplateRepository.findByVisible(true)).willReturn(Arrays.asList(templates));

    // when
    JasperTemplateDto[] result = restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // DELETE /api/reports/templates

  @Test
  public void shouldDeleteExistentTemplate() {
    // given
    JasperTemplate template = generateExistentTemplate();

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(204);

    // then
    verify(jasperTemplateRepository, atLeastOnce()).delete(eq(template));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotDeleteNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}

  @Test
  public void shouldGetExistentTemplate() {
    // given
    JasperTemplate template = generateExistentTemplate();

    // when
    JasperTemplateDto result = restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto.class);

    // then
    assertEquals(template.getId(), result.getId());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotGetNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(404);

    // them
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}/{format}

  @Test
  public void generateReportShouldRejectWhenUserHasNoViewReportsRight() {
    // given
    JasperTemplate template = generateExistentTemplate();
    given(jasperTemplateRepository.findOne(template.getId())).willReturn(template);

    doThrow(mockPermissionException(REPORTS_VIEW)).when(permissionService).canViewReports();

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void generateReportShouldNotRejectWhenUserHasNoViewReportsRightAndTemplateIsHidden()
      throws JasperReportViewException {
    // given
    JasperTemplate template = generateExistentTemplate();
    template.setVisible(false);

    JasperReportsMultiFormatView view = mock(JasperReportsMultiFormatView.class);
    given(view.getContentDispositionMappings()).willReturn(new Properties());

    given(jasperTemplateRepository.findOne(template.getId())).willReturn(template);
    given(jasperReportsViewService
        .getJasperReportsView(eq(template), any(HttpServletRequest.class)))
        .willReturn(view);

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  public void generateReportShouldReturnNotFoundWhenReportTemplateDoesNotExist() {
    // given
    given(jasperTemplateRepository.findOne(anyUuid())).willReturn(null);

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void generateReportShouldRejectWhenUserHasNoReportSpecificRight() {
    // given
    String deniedPermission = "USERS_MANAGE";

    JasperTemplate template = generateExistentTemplate();
    template.setRequiredRights(Collections.singletonList(deniedPermission));

    PermissionMessageException ex = mockPermissionException(deniedPermission);
    doThrow(ex).when(permissionService).validatePermissions(any(String[].class));

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(403);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldGenerateReportInPdfFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/pdf", PDF_FORMAT);
  }

  @Test
  public void shouldGenerateReportInCsvFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/csv", "csv");
  }

  @Test
  public void shouldGenerateReportInXlsFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("application/xls", "xls");
  }

  @Test
  public void shouldGenerateReportInHtmlFormat() throws JasperReportViewException {
    testGenerateReportInGivenFormat("text/html", "html");
  }

  // Helper methods

  private void testGenerateReportInGivenFormat(String contentType, String formatParam)
      throws JasperReportViewException {
    // given
    JasperTemplate template = generateExistentTemplate();

    JasperReportsMultiFormatView view = mock(JasperReportsMultiFormatView.class);
    given(view.getContentType()).willReturn(contentType);
    given(view.getContentDispositionMappings()).willReturn(mock(Properties.class));
    given(view.getContentDispositionMappings().getProperty("attachment.pdf")).willReturn("text");

    given(jasperTemplateRepository.findOne(template.getId())).willReturn(template);
    given(jasperReportsViewService
        .getJasperReportsView(any(JasperTemplate.class), any(HttpServletRequest.class)))
        .willReturn(view);

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, formatParam)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(200);
  }

  private JasperTemplate generateExistentTemplate() {
    return generateExistentTemplate(UUID.randomUUID());
  }

  private JasperTemplate generateExistentTemplate(UUID id) {
    JasperTemplate template = new JasperTemplate();

    template.setId(id);
    template.setName("name");
    template.setRequiredRights(new ArrayList<>());

    given(jasperTemplateRepository.findOne(id)).willReturn(template);

    return template;
  }
}
