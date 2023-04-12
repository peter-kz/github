import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion by System.getProperties()
    val springBootVersion by System.getProperties()
    val springBootDependencyManagementVersion by System.getProperties()

    id("au.com.dius.pact") version "4.5.6"
    id("jacoco")
    id("org.springframework.boot") version "$springBootVersion"
    id("io.spring.dependency-management") version "$springBootDependencyManagementVersion"

    id("jvm-test-suite")
    id("jacoco-report-aggregation")
    id("org.cyclonedx.bom") version "1.7.4" // Version must match https://gitlab.kfplc.com/SFD/FFX/ffx-jenkins-library/-/blob/main/vars/securityDepTrack.groovy#L65

    kotlin("jvm") version "$kotlinVersion"
    kotlin("plugin.spring") version "$kotlinVersion"
    kotlin("kapt") version "$kotlinVersion"

    // plugin to generate git.properties to be used by the info endpoint
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

group = "com.screwfix"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":"))
    implementation(project(":ffx-aci-adapter"))
    implementation(project(":ffx-token-adapter"))
    implementation(project(":ffx-verifone-adapter"))
    implementation(project(":ffx-payment-service-common"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("javax.validation:validation-api:2.0.1.Final")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.0.0-rc1")
}

// Security Update: CVE-2022-25857
// Spring will update SnakeYaml version on 3.x https://github.com/spring-projects/spring-boot/issues/32221
val snakeYamlVersion: String by project

allprojects {
    apply(plugin = "java")

    dependencies {
        implementation("org.springframework:spring-context")
        implementation("io.github.microutils:kotlin-logging:2.1.23")
        implementation("org.yaml:snakeyaml:$snakeYamlVersion")

        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

        testImplementation("au.com.dius.pact.provider:junit5spring:4.3.15")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
        testImplementation("com.ninja-squad:springmockk:3.1.1")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
}

springBoot {
    buildInfo()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks {
    testCodeCoverageReport {
        classDirectories.setFrom(files(
            allprojects.map {
                fileTree("${it.buildDir}/classes/kotlin/main") {
                    exclude(
                        "**/vanguard/*",
                        "**/generated/*",
                        "**/model/*"
                    )
                }
            }
        ))
    }
}

tasks.cyclonedxBom {
    setSkipConfigs(listOf("testCodeCoverageReportExecutionData", "allCodeCoverageReportSourceDirectories"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(PASSED, SKIPPED, FAILED)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    systemProperties["pactbroker.url"] = "https://contracttesting-pact-broker-internal-gts-tooling.k8s.an.digikfplc.com/"
    systemProperties["pact.verifier.publishResults"] = "false"
}

tasks {
    test {
        finalizedBy(testCodeCoverageReport)
    }
}

val kotlinSrcPath = "src/main/kotlin"

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs(kotlinSrcPath)
        }
    }
}
