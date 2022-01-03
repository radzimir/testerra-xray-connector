/*
 * Testerra Xray-Connector
 *
 * (C) 2020, Martin Cölln, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package eu.tsystems.mms.tic.testerra.plugins.xray.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.WebResource;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraKeyReference;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayInfo;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayTestExecutionImport;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayTestExecutionResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;


public final class XrayUtils extends JiraUtils {

    private static final String IMPORT_EXECUTION_PATH = "raven/1.0/import/execution";
    private static final String EXECUTION_RESULT_PATH = "raven/1.0/execution/result";

    public XrayUtils(WebResource webResource) {
        super(webResource);
    }

    /**
     * @deprecated Use {@link #createOrUpdateTestExecution(XrayTestExecutionImport)} instead
     */
    public static String syncTestExecutionReturnKey(final WebResource webResource, final XrayTestExecutionImport testExecution) {
        XrayUtils xrayUtils = new XrayUtils(webResource);
        xrayUtils.createOrUpdateTestExecution(testExecution);
        return testExecution.getTestExecutionKey();
    }

    public void createOrUpdateTestExecution(XrayTestExecutionImport testExecutionImport) {
        try {
            String jsonResponse = post(IMPORT_EXECUTION_PATH, testExecutionImport);
            XrayTestExecutionResult xrayTestExecutionResult = getObjectMapper().readValue(jsonResponse, XrayTestExecutionResult.class);
            testExecutionImport.setTestExecutionKey(xrayTestExecutionResult.getTestExecIssue().getKey());
//            JsonNode jsonNode = getObjectMapper().readTree(jsonResponse);
//            if (jsonNode.has("testExecKey")) {
//                testExecutionImport.setTestExecutionKey(jsonNode.get("testExecKey").asText());
//            }
        } catch (IOException e) {
            log().error("Unable to import TestExecution", e);
        }
    }

    @Deprecated
    public static XrayTestExecutionImport createUpdateTestExecution(final String issueKey, final Iterable<String> testKeys) {
        final XrayInfo xrayInfo = new XrayInfo();
        xrayInfo.setKey(issueKey);
        xrayInfo.setStartDate(new Date());
        xrayInfo.setFinishDate(new Date());
        final XrayTestExecutionImport execution = new XrayTestExecutionImport(xrayInfo);
        execution.setTests(keysToXrayTestWithTodoStatus(testKeys));
        return execution;
    }

    @Deprecated
    public static XrayTestExecutionImport createFreshTestExecution(XrayInfo xrayInfo, final Iterable<String> testKeys) {
        final XrayTestExecutionImport execution = new XrayTestExecutionImport(xrayInfo);
        execution.setTests(keysToXrayTestWithTodoStatus(testKeys));
        return execution;
    }

    private static LinkedHashSet<XrayTestExecutionImport.Test> keysToXrayTestWithTodoStatus(final Iterable<String> testKeys) {
        final LinkedHashSet<XrayTestExecutionImport.Test> xrayTestIssues = new LinkedHashSet<>();
        for (final String testKey : testKeys) {
            final XrayTestExecutionImport.Test xrayTestIssue = new XrayTestExecutionImport.Test(testKey);
            xrayTestIssue.setStatus(XrayTestExecutionImport.Test.Status.TODO);
            xrayTestIssues.add(xrayTestIssue);
        }
        return xrayTestIssues;
    }

    public static LinkedHashSet<XrayTestExecutionImport.Test> getTestsFromExecution(final WebResource webResource, final String issueKey) throws IOException {
        final String result = webResource.path(EXECUTION_RESULT_PATH).queryParam("testExecKey", issueKey).get(String.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        final XrayTestExecutionImport.Test[] testIssues = objectMapper.readValue(result, XrayTestExecutionImport.Test[].class);
        return Sets.newLinkedHashSet(Arrays.asList(testIssues));
    }

    public static String exportTestExecutionAsJson(final WebResource webResource, final String issueKey) {
        return webResource.path(EXECUTION_RESULT_PATH)
                .queryParam("testExecKey", issueKey)
                .get(String.class);
    }

}
