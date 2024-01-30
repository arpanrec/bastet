import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.springframework.boot.gradle.tasks.bundling.BootJar
plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    idea
}

group = "com.arpanrec"
version = getVersions()

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        java { srcDirs("src/main/java") }
        kotlin { srcDirs("src/main/kotlin") }
    }
    test {
        java { srcDirs("src/test/java") }
        kotlin { srcDirs("src/test/kotlin") }
    }
}

configure<IdeaModel> {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

dependencies {
    implementation("org.apache.commons:commons-lang3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains:annotations")

    implementation("org.bouncycastle:bcpg-jdk18on:1.77")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Log4j2
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.slf4j:jcl-over-slf4j")
    implementation("org.slf4j:jul-to-slf4j")
    implementation("org.slf4j:log4j-over-slf4j")
    implementation("org.slf4j:osgi-over-slf4j:2.1.0-alpha1")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    compileOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.create<Delete>("cleanAll") {
    group = "build"
    delete("logs", "bin", "build", "storage", "gradlew.bat", "gradle", "gradlew", ".gradle")
}

tasks.getByName<Jar>("jar") {
    enabled = true
    archiveAppendix.set("original")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClass = getMainClassName()
    archiveAppendix.set("boot")
}

fun getMainClassName(): String {
    val mainClass = "com.arpanrec.minerva.Application"
    return mainClass
}

fun getVersions(): String {
    val file = File("VERSION")
    return if (file.exists()) {
        file.readText()
    } else {
        "9.9.9-SNAPSHOT"
    }
}
