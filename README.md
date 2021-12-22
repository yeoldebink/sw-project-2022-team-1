# Setup For Successful CI

Every time a pull request is submitted, two CI checks are run:

- Code style is checked to ensure it matches with the Google Java Code Style
- The project is built as a `.jar` with dependencies

If either of these tests fail, your code will not be merged.

## IntelliJ Setup

### Google Java Format Plugin

1. File -> Settings -> Plugins
2. Search for google-java-format
3. Install the plugin
4. Close & Re-Open IntelliJ
5. File -> Settings -> Other Settings -> google-java-format Settings
6. Check "Enable google-java-format"

### Code Style XML

1. [Download the XML](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml)
2. File -> Settings -> Editor -> Code Style
3. Click the gear icon next to Scheme -> Import Scheme -> IntelliJ IDEA code style XML
4. Choose the XML file and import it
5. Switch schemes to GoogleStyle

## Formatting Your Code

You can reformat the file you're currently editing with Code -> Reformat Code or reformat entire
directories by right-clicking on them and selectin Reformat Code. This is probably the best option
to make sure you haven't missed any files.

## Building

`mvn clean install`

## Initializing a test DB

1. Make sure MySQL is installed and running on your computer
2. Either change the root password to `Admin@2021` or edit the `hibernate.properties` file found in 
`testing_database_init/src/main/resources/hibernate.properties` (DO NOT COMMIT CHANGES TO THIS
   FILE)
3. Run `mvn clean compile exec:java` in the directory `testing_database_init`
