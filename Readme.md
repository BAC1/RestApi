# Progressive Image Loader for Responsive Websites

This repository contains the infrastructure and source code to run a Spring MVC framework to load progressive-encoded
images on devices with different quality levels. 

## Getting Started

These readme gives you instructions to run the Spring application on your local machine for development and testing 
purposes.

### Prerequisites

#### Intellij

The backend code is written with Kotlin. Therefore, it is recommended to install the IDE
[IntelliJ IDEA](https://www.jetbrains.com/idea/) to change, improve and run the code in this project.

#### Gradle

Gradle is required for the dependency management. Carry out the following steps:

* Install Gradle

```
$ sudo apt-get install gradle
```

* Make sure Gradle 4.0 or higher is installed. Check your Gradle version as follows:

```
$ gradle -version
```

* If Gradle version 4.0 or lower is installed, update it as follows

```
$ sudo add-apt-repository ppa:cwchien/gradle
$ sudo apt-get update 
$ sudo apt-get upgrade gradle
```

* Create the gradle wrapper with any Gradle version higher than 4.0

```
$ gradle wrapper --gradle-version <Gradle version>
```

For the latest gradle versions see [here](https://services.gradle.org/distributions/).

#### JAVA_HOME
Make sure Java 8 is set for the JAVA_HOME variable on your local machine (Windows and Linux).

##### Linux
Run the following commands in the command line of Intellij:

* Check your JAVA_HOME variable
```
$ echo $JAVA_HOME
```

* Change your JAVA_HOME path if not Java 8 is selected
```
$ export JAVA_HOME=<path to Java 8 folder>
```

#### MySQL
In order to run this project, an installed and running MySQL server is required. This project have been tested with the 
"MySQL 8 Server" on Windows and Linux. 

##### Download

Get here the latest version for [Windows](https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html).

Get here the latest version for [Linux](https://dev.mysql.com/doc/refman/8.0/en/linux-installation.html).

##### Installation
1. Run the MySQL installer.
2. Install the MySQL server with default settings. Make sure the port **3306** is selected.
3. In the installation wizard, select the option "add User" and add an user with custom user credentials.
4. Finish the installation.
5. Open the property file **application.properties** in the resource folder of this project and enter the username and
password of the previously created MySQL user in the properties **spring.datasource.username** and 
**spring.datasource.password**.

**Attention**: Make sure the MySQL server is running before you execute this project!

### Install project
In order to open this project in Intellij, the following three methods are possible:

* Retrieve the project into Intellij via **VCS -> git -> clone...** and enter the url **git@github.com:BAC1/RestApi.git**.

* Import or open the downloaded project as Gradle project in Intellij.

* Checkout the project via the command line:
```
$ git checkout git@github.com:BAC1/RestApi.git
```

**Note**: In order to pull (via SSH connection) or push via git, the public SSH key must be saved in the git repository. 
For assistance, see **Authors** below.

### Run Project with Gradle

This project can be executed with Gradle in two ways.

#### via Intellij Interface

1. Open the "Gradle Project" tool at **View -> Tool Windows -> Gradle**.
2. Right-click on the project node **RestApi** and select **Refresh dependencies**. Wait until all dependencies have 
been loaded. The current status is shown in the footer of Intellij.
3. Right-click on the project node **RestApi** and select **Refresh Gradle project**. Wait until the project have been 
updated. The current status is shown in the footer of Intellij.
4. Right-click on file **Application.kt** and click on **Run 'com.restapi.application**
5. Open your browser and enter the link **http://localhost:8080/** or **http://127.0.0.1:8080/**.

**Note**: It is recommended to use Chrome.

#### via Command Line
Run the following commands in the command line of Intellij:

* Refresh the dependencies:
```
$ ./gradlew --refresh-dependencies
```

**Note**: If the command './gradlew' isn't available, see **Gradle** in chapter **Prerequisites** above.

* Run this project
```
$ gradle bootRun
```

* Open your browser and enter the link **http://localhost:8080/** or **http://127.0.0.1:8080/**. 

**Note**: It is recommended to use Chrome.

## JavaDoc

* Run this command in the command line of Intellij
```
$ ./gradlew dokka
```

The JavaDoc is exported as html and saved at **/build/javadoc/**.

**Note**: If the command './gradlew' isn't available, see **Gradle** in chapter **Prerequisites** above.

## Code formatter

### How to run Ktlint

Ktlint is an anti-bikeshedding Kotlin linter with built-in formatter. For more information see 
[here](https://ktlint.github.io/).

* Check code style (it's also bound to "gradle check"):
```
$ ./gradlew ktlint  (for Linux user)
$ gradlew ktlint    (for Windows user)
```

* Fix code style deviations (runs built-in formatter):
```
$ ./gradlew ktlintFormat  (for Linux user)
$ gradlew ktlintFormat    (for Windows user)
```

## Known issues
* In Firefox, images won't be shown after resizing the browser. Click on the appropriate image button to re-display the
image.
* In Firefox, the "car.jpg" image won't be shown when it will be displayed as first image after running the application.
Click on the image button twice or click on another image button first.

## Built With

* [Kotlin](https://kotlinlang.org/) - Program code language for backend
* [Spring](https://spring.io/) - Application framework
* [Gradle](https://gradle.org/) - Dependency Management
* [MySQL](https://www.mysql.com/) - Database connector API for Java
* [Ktlint](https://ktlint.github.io/) - Code Formatter

## Versioning

We use [SemVer](http://semver.org/) for versioning. The current release version is defined in the **build.gradle** file. 
All available releases can be found on our [GitHub repository](https://github.com/BAC1/RestApi/releases). 

## License

This project is not licensed.

## Authors

* **Markus Graf**   <fhs39198@fh-salzburg.ac.at>