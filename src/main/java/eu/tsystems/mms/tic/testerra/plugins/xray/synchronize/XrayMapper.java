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

package eu.tsystems.mms.tic.testerra.plugins.xray.synchronize;

import eu.tsystems.mms.tic.testerra.plugins.xray.jql.JqlQuery;
import eu.tsystems.mms.tic.testerra.plugins.xray.jql.predefined.IssueTypeEquals;
import eu.tsystems.mms.tic.testerra.plugins.xray.jql.predefined.ProjectEquals;
import eu.tsystems.mms.tic.testerra.plugins.xray.jql.predefined.RevisionContainsExact;
import eu.tsystems.mms.tic.testerra.plugins.xray.jql.predefined.SummaryContainsExact;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraIssue;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraNameReference;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayTestExecutionIssue;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayTestIssue;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray.XrayTestSetIssue;
import eu.tsystems.mms.tic.testframework.report.model.context.ClassContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import java.util.Optional;
import org.testng.ITestClass;
import org.testng.ITestResult;

public interface XrayMapper {

    /**
     * called for matching Test method against Xray Test
     * create a Jql-Query that is able to retrieve a single Xray-Test
     * 'project = $projektKey AND issuetype = Test AND key in testSetTests($testSetKey)' is prepended automatically
     * return null if no automatic matching is required
     *
     * @param testNgResult the test result
     * @return JqlQuery that is able to match a single Xray-Test or null
     * @deprecated Use {@link #createXrayTestQuery(MethodContext)}
     */
    default JqlQuery resultToXrayTest(ITestResult testNgResult) {
        return null;
    }

    /**
     * Creates a {@link JqlQuery} for mapping {@link MethodContext} to a JiraTest
     */
    default Optional<JqlQuery> createXrayTestQuery(MethodContext methodContext) {
        return methodContext.getTestNgResult().map(this::resultToXrayTest);
    }

    /**
     * called for matching Test class against Xray TestSet
     * create a Jql-Query that is able to retrieve a single Xray-TestSet
     * 'project = $projektKey AND issuetype = "Test Set"' is prepended automatically
     * return null if no automatic matching is required
     *
     * @param testNgClass test class
     * @return JqlQuery that is able to match a single Xray-TestSet or null
     * @deprecated Use {@link #createXrayTestSetQuery(ClassContext)} instead
     */
    default JqlQuery classToXrayTestSet(ITestClass testNgClass) {
        return null;
    }

    default Optional<JqlQuery> createXrayTestSetQuery(ClassContext classContext) {
        return classContext.readMethodContexts()
                .findFirst()
                .flatMap(MethodContext::getTestNgResult)
                .map(testResult -> classToXrayTestSet(testResult.getMethod().getTestClass()));
    }

    default Optional<JqlQuery> createXrayTestExecutionQuery(XrayTestExecutionIssue xrayTestExecutionIssue) {
        final JqlQuery jqlQuery = JqlQuery.create()
                .addCondition(new ProjectEquals(xrayTestExecutionIssue.getProject().getKey()))
                .addCondition(new IssueTypeEquals(xrayTestExecutionIssue.getIssueType()))
                .addCondition(new SummaryContainsExact(xrayTestExecutionIssue.getSummary()))
                .addCondition(new RevisionContainsExact(xrayTestExecutionIssue.getRevision()))
                .build();
        return Optional.of(jqlQuery);
    }

    /**
     * Updates the test execution before creating or updating.
     */
    default void updateXrayTestExecution(XrayTestExecutionIssue xrayTestExecutionIssue, ExecutionContext executionContext) {
    }

    /**
     * Gets called every time when a test is assigned to the test set.
     */
    default void updateXrayTestSet(XrayTestSetIssue xrayTestSetIssue, ClassContext classContext) {
    }

    /**
     * Gets called every time a test will be synchronized.
     */
    default void updateXrayTest(XrayTestIssue xrayTestIssue, MethodContext methodContext) {
    }
}
