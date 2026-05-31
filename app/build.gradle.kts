plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
    id("org.sonarqube") version "7.3.0.8198"
    application
    checkstyle
    jacoco
}

application {
    mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:7.2.2")
    implementation("io.javalin:javalin-rendering-jte:7.2.2")
    implementation("gg.jte:jte:3.2.4")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.slf4j:slf4j-simple:2.0.18")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.javalin:javalin-testtools:7.2.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

sonar {
    properties {
        property("sonar.projectKey", "Xomyakkk_java-project-72")
        property("sonar.organization", "xomyakkk")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml"
        )
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}

dependencyLocking {
    lockAllConfigurations()
}
