package eu.tsystems.mms.tic.testerra.plugins.xray.mapper.xray;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.tsystems.mms.tic.testerra.plugins.xray.jql.predefined.TestType;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.MediaTypeSerializer;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraIssue;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraKeyReference;
import eu.tsystems.mms.tic.testerra.plugins.xray.mapper.jira.JiraNameReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;

public class XrayTestExecutionImport {

    /**
     * This date pattern differs from {@link JiraIssue#PATTERN_DATE_FORMAT}
     */
    private static final String PATTERN_DATE_FORMAT ="yyyy-MM-dd'T'HH:mm:ssXXX";

    public static abstract class AbstractInfo {
        private String summary;
        private String description;

        public String getSummary() {
            return summary;
        }

        public String getDescription() {
            return description;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Result {
        private JiraKeyReference testExecIssue;

        public JiraKeyReference getTestExecIssue() {
            return testExecIssue;
        }
    }

    public static class Info extends AbstractInfo {
        private String project;
        private String version;
        private String revision;
        private String user;
        private Date startDate;
        private Date finishDate;
        private String testPlanKey;
        private List<String> testEnvironments;

        public String getVersion() {
            return version;
        }

        public String getRevision() {
            return revision;
        }

        public String getUser() {
            return user;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_FORMAT, timezone = "CET")
        public Date getStartDate() {
            return startDate;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_FORMAT, timezone = "CET")
        public Date getFinishDate() {
            return finishDate;
        }

        public String getTestPlanKey() {
            return testPlanKey;
        }

        public List<String> getTestEnvironments() {
            return testEnvironments;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }
    }

    public static class Test {
        public enum Status {
            PASS,
            PASSED_BEFORE,
            FAIL,
            TODO,
            EXECUTING,
            SKIPPED,
            ABORTED
        }
        public static class Info extends AbstractInfo {
            public static class Step {
                private String action;
                private String result;
                private String data;

                public Step() {

                }

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
                    this.action = action;
                }

                public String getResult() {
                    return result;
                }

                public void setResult(String result) {
                    this.result = result;
                }

                public String getData() {
                    return data;
                }

                public void setData(String data) {
                    this.data = data;
                }
            }

            private String projectKey;
            private List<String> labels;
            private List<Step> steps;
            private String testType;
            private String definition;

            public String getTestType() {
                return testType;
            }

            public void setTestType(String testType) {
                this.testType = testType;
            }

            public void addStep(Step step) {
                if (this.steps == null) {
                    this.steps = new ArrayList<>();
                }
                this.steps.add(step);
            }

            public void setSteps(List<Step> steps) {
                this.steps = steps;
            }

            public List<Step> getSteps() {
                return steps;
            }

            public String getProjectKey() {
                return projectKey;
            }

            public void setProjectKey(String projectKey) {
                this.projectKey = projectKey;
            }

            public List<String> getLabels() {
                return labels;
            }

            public void setLabels(List<String> labels) {
                this.labels = labels;
            }

            public String getDefinition() {
                return definition;
            }

            public void setDefinition(String definition) {
                this.definition = definition;
            }
        }

        public static class Evidence {
            private String data;
            private String filename;
            private MediaType contentType;

            @JsonSerialize(using = MediaTypeSerializer.class)
            public MediaType getContentType() {
                return contentType;
            }

            public void setContentType(MediaType contentType) {
                this.contentType = contentType;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getData() {
                return data;
            }

            public void setData(String data) {
                this.data = data;
            }
        }

        public static class Step {
            private Status status;
            private String actualResult;

            public Step() {
            }

            public Status getStatus() {
                return status;
            }

            public void setStatus(Status status) {
                this.status = status;
            }

            public String getActualResult() {
                return actualResult;
            }

            public void setActualResult(String actualResult) {
                this.actualResult = actualResult;
            }
        }

        private Info testInfo;
        private String testKey;
        private Date start;
        private Date finish;
        private String comment;
        private Set<Evidence> evidences;
        private Status status;
        private List<Step> steps;

        public Test() {
        }

        public Test(JiraIssue issue) {
            this.testKey = issue.getKey();
            this.testInfo = new Info();
            this.testInfo.setDescription(issue.getDescription());
            this.testInfo.setSummary(issue.getSummary());
            this.testInfo.setLabels(issue.getLabels());
            this.testInfo.setDefinition(issue.getSummary());
            this.testInfo.testType = TestType.AutomatedGeneric.toString();
            this.testInfo.projectKey = issue.getProject().getKey();
        }

        public Test(String testKey) {
            this.testKey = testKey;
        }

        public Info getTestInfo() {
            return testInfo;
        }

        public void setTestInfo(Info testInfo) {
            this.testInfo = testInfo;
        }

        public String getTestKey() {
            return testKey;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_FORMAT, timezone = "CET")
        public Date getStart() {
            return start;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_FORMAT, timezone = "CET")
        public Date getFinish() {
            return finish;
        }

        public void setFinish(Date finish) {
            this.finish = finish;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Set<Evidence> getEvidences() {
            return evidences;
        }

        public void setEvidences(Set<Evidence> evidences) {
            this.evidences = evidences;
        }

        public void addEvidence(Evidence evidence) {
            if (this.evidences == null) {
                this.evidences = new HashSet<>();
            }
            this.evidences.add(evidence);
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public void addStep(Step step) {
            if (this.steps == null) {
                this.steps = new ArrayList<>();
            }
            this.steps.add(step);
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }

        public List<Step> getSteps() {
            return steps;
        }
    }

    private final Info info = new Info();
    private String testExecutionKey;
    private final Set<Test> tests = new HashSet<>();;

    public XrayTestExecutionImport(String testExecutionKey) {
        this.testExecutionKey = testExecutionKey;
    }

    public XrayTestExecutionImport(XrayTestExecutionIssue testExecutionIssue) {
        this.testExecutionKey = testExecutionIssue.getKey();
        this.info.setProject(testExecutionIssue.getProject().getKey());
        this.info.setDescription(testExecutionIssue.getDescription());
        this.info.setSummary(testExecutionIssue.getSummary());
        this.info.version = testExecutionIssue.getFixVersions().stream().findFirst().map(JiraNameReference::getName).orElse(null);
        this.info.revision = testExecutionIssue.getRevision();
        this.info.user = testExecutionIssue.getAssignee().getName();
        this.info.startDate = testExecutionIssue.getStartDate();
        this.info.finishDate = testExecutionIssue.getFinishDate();

        List<String> testPlanKeys = testExecutionIssue.getTestPlanKeys();
        if  (testPlanKeys.size() > 0) {
            this.info.testPlanKey = testPlanKeys.get(0);
        }

        List<String> testEnvironments = testExecutionIssue.getTestEnvironments();
        if (testEnvironments.size() > 0) {
            this.info.testEnvironments = testEnvironments;
        }
    }

    public Info getInfo() {
        return info;
    }

    public void setTestExecutionKey(String testExecutionKey) {
        this.testExecutionKey = testExecutionKey;
    }

    public void setTests(Set<Test> tests) {
        this.tests.clear();
        this.addTests(tests);
    }

    public void addTests(Set<Test> tests) {
        this.tests.addAll(tests);
    }

    public void setTestKeys(Set<String> testKeys, Test.Status status) {
        this.tests.clear();
        this.addTestKeys(testKeys, status);
    }

    public void addTestKeys(Set<String> testKeys, Test.Status status) {
        this.tests.addAll(testKeys.stream()
                .map(Test::new)
                .peek(test -> test.setStatus(status))
                .collect(Collectors.toSet())
        );
    }

    public void addTest(Test test) {
        this.tests.add(test);
    }

    public Set<Test> getTests() {
        return this.tests;
    }

    public String getTestExecutionKey() {
        return testExecutionKey;
    }
}
