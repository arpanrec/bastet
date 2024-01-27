import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    application
    groovy
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    idea
    eclipse
    id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "com.arpanrec"
version = "0.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}


sourceSets {
    main {
        java { srcDirs("src/main/java") }
        kotlin { srcDirs("src/main/kotlin") }
        groovy { srcDirs("src/main/groovy") }
    }
    test {
        java { srcDirs("src/test/java") }
        kotlin { srcDirs("src/test/kotlin") }
        groovy { srcDirs("src/test/groovy") }
    }
}

configure<IdeaModel> {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

configure<EclipseModel> {
    classpath {
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
    implementation("org.apache.groovy:groovy")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

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

    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.apache.commons:commons-lang3")
    implementation("org.jetbrains:annotations")
    implementation("org.postgresql:postgresql:42.7.1")
    // implementation("org.xerial:sqlite-jdbc")
    // implementation("org.hibernate.orm:hibernate-community-dialects")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("com.h2database:h2")

    implementation("org.bouncycastle:bcpg-jdk18on:1.77")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")
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
    group = "clean"
    delete("logs", "bin", "build", "storage")
}

//springBoot() {
//    mainClass.set("com.arpanrec.minerva.Application")
//}

tasks.getByName<Jar>("jar") {
    enabled = true

}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClass = getMainClassName()
    archiveAppendix.set("boot")
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-Dorg.sqlite.lib.exportPath=${layout.buildDirectory}")
        }
    }
}
fun getMainClassName(): String {
    val mainClass = "com.arpanrec.minerva.Application"
    return mainClass
}