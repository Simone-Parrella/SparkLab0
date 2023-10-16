package SparkLab.Project;

import SparkLab.Project.DatabaseInterface.MongoDatabaseAdapterTest;
import SparkLab.Project.ProjectService;
import SparkLab.Project.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses( {MongoDatabaseAdapterTest.class ,ProjectServiceTest.class,UserServiceTest.class,ResourceService.class})
public class MyTestSuite {}

