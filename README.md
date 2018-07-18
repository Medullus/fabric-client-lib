# fabric-client

# ENV Setup
Use Gradle test runner:
    Preferences > Build, Execution, Deployment > Build tools > Gradle > Runner > Run test using Gradle tests runner

FabricITTest.java requires networkStartup to be up and running with CC

# To install to local Maven
`./gradlew install`

# implementation standards -- Please follow
- Logger, use Apache log4j.

        private static Logger logger = Logger.getLogger(Class.class);

- Exception Handling
    catch, log then throw again. Let all exceptions propogate upstream to caller.
    Do not throw Super class Exception, throw something else with more granular information.
    
        try{
            object.call(fcn);
        }catch(IOException e){
            logger.error(e);
            throw e;
        }

- Separate responsibilities into smaller methods

- Format for Config properties should follow this format:

MHC_FABRIC_\<PROPERTY>

For example:

    MHC_FABRIC_CLOUDANTID
    MHC_FABRIC_CLOUDANTSECRET
    
- Install SonarLint pluggin and analyze your code and fix all major and critical issues before pushing.
    
    To use: install plugin then right click on Project's root > Analyze > Analyze with Sonar Lint

