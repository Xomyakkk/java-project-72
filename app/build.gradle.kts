plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.sonarqube)
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
    implementation(libs.javalin)
    implementation(libs.javalin.rendering.jte)
    implementation(libs.jte)
    implementation(libs.hikari)
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.unirest.core)
    implementation(libs.jsoup)
    implementation(libs.slf4j.simple)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.javalin.testtools)
    testImplementation(libs.mockwebserver)
    testRuntimeOnly(libs.junit.platform.launcher)
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
