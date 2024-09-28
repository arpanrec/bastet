import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.springframework.boot.gradle.tasks.bundling.BootJar

logging.captureStandardOutput(LogLevel.INFO)

plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
//    kotlin("plugin.jpa") version "1.9.25"
//    id("org.hibernate.orm") version "6.6.0.Final"
    kotlin("plugin.serialization") version "1.9.25"
    idea
    // id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "com.arpanrec"
version = getVersions()

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
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
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    create("binaryTestResultsElements") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("test-report-data"))
        }
        outgoing.artifact(tasks.test.map { task -> task.binaryResultsDirectory.get() })
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.apache.commons:commons-lang3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains:annotations")

    implementation("org.bouncycastle:bcpg-jdk18on:1.78.1")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1")
    implementation("org.pgpainless:pgpainless-core:1.6.7")

//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.xerial:sqlite-jdbc")
//    implementation("org.hibernate.orm:hibernate-community-dialects")
//    runtimeOnly("org.postgresql:postgresql")

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
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")



    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")
}


tasks {
    getByName<Jar>("jar") {
        enabled = true
        archiveAppendix.set("original")
    }
    getByName<BootJar>("bootJar") {
        enabled = true
        mainClass = getMainClassName()
        archiveAppendix.set("boot")
    }
    create<Delete>("cleanAll") {
        group = "build"
        delete(
            "logs", "bin", "build", "storage", "gradlew.bat", "gradle", "gradlew", ".gradle", "node_modules",
            "package-lock.json", "package.json", "/tmp/bastet",
            "src/test/resources/tfstate/.terraform",
            "src/test/resources/tfstate/errored.tfstate",
            "src/test/resources/tfstate/.terraform.lock.hcl"
        )
    }
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "21"
        }
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        systemProperty("spring.profiles.active", "test")
        reports {
            html.required = true
            junitXml.required = true
        }
    }
}

fun getMainClassName(): String {
    val mainClass = "com.arpanrec.bastet.Application"
    return mainClass
}

fun getVersions(): String {

    if (project.hasProperty("version")) {
        val versionFromProject: Any? = project.property("version")
        if (versionFromProject is String && versionFromProject.isNotBlank() && versionFromProject != "unspecified") {
            return versionFromProject
        }
    }

    val versionFromEnv: String? = System.getenv("BASTET_VERSION")
    if (!versionFromEnv.isNullOrEmpty()) {
        return versionFromEnv
    }

    val file = File("BASTET_VERSION")
    if (file.exists()) {
        val versionFromFile: String = file.readText().trim()
        if (versionFromFile.isNotEmpty()) {
            return versionFromFile
        }
    }

    return "9.9.9-SNAPSHOT"
}

//hibernate {
//    enhancement {
//        enableAssociationManagement = true
//    }
//}
