//noinspection UseTomlInstead
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("org.gradle.jacoco")
}
jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "net.bytebuddy") {
            useVersion("1.15.11")
            because("Jacoco transform compatibility")
        }
    }
}

android {
    namespace = "br.com.chatnoir.ggwave_kotlin"
    compileSdk = 36
    ndkVersion = " 28.2.13676358"

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.test:rules:1.7.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("io.mockk:mockk-android:1.14.5")
}

//>>>>>>>>>> publish config
val getVersionName = "1.0.0"
val getArtifactId = "ggwave-kotlin"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/diogosq/ggwave-kotlin")
            credentials(PasswordCredentials::class)
        }
    }
}
mavenPublishing {
    //For Maven Central
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
    //For Maven Central

    coordinates("io.github.diogosq", getArtifactId, getVersionName)
    pom {
        name.set(getVersionName)
        description.set("ggwave-kotlin is a Kotlin/Android library that provides a JNI wrapper for the GGWave C++ library")
        inceptionYear.set("2025")
        url.set("https://github.com/diogosq/ggwave-kotlin")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/diogosq/ggwave-kotlin?tab=MIT-1-ov-file")
            }
        }
        scm {
            url = "https://github.com/diogosq/ggwave-kotlin"
            connection = "scm:git://github.com/diogosq/ggwave-kotlin.git"
            developerConnection = "scm:git://github.com/diogosq/ggwave-kotlin.git"
        }
        developers {
            developer {
                id = "diogosq"
                name = "Diogo Queiroz"
                email = "diogosq@gmail.com"
            }
        }
    }
}

//<<<<<<<<<< publish config

// =================================
// JaCoCo Report Configuration Start
// =================================
val jacocoFileFilter = listOf(
    // Android-specific generated files
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/resources/**",
    "**/values/**",

    // Test files
    "**/*Test*.*",
    "**/*Test$*.*",
    "**/androidTest/**",
    "**/test/**",

    // Hilt/Dagger-generated code
    "**/hilt_aggregated_deps/**",
    "**/dagger/hilt/internal/**",
    "**/dagger/hilt/android/internal/**",
    "**/*_MembersInjector.class",
    "**/Dagger*Component.class",
    "**/*Module_*Factory.class",
    "**/*_Factory.class",
    "**/*_Provide*Factory.class",
    "**/*_Impl.class",

    // Kotlin-generated classes
    "**/*\$Lambda$*.*",
    "**/*\$inlined$*.*",
    "**/*\$*.*", // anonymous classes and lambdas
    "**/Companion.class",

    // Navigation safe args (generated)
    "**/*Directions*.class",
    "**/*Args.class",

    // Jetpack Compose compiler-generated
    "**/*Preview*.*",
    "**/*ComposableSingletons*.*",

    // Room and other annotation processors
    "**/*_Impl.class",
    "**/*Serializer.class", // For Moshi, Retrofit, etc.

    // Miscellaneous
    "android/**/*.*",

    // Project-specific exclusions
    // "**/di/**",
    // "**/state/**",
    // "**/mapper/**",
    // "**/domain/**"
)

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}
// Register a JacocoReport task for code coverage analysis
tasks.register<JacocoReport>("JacocoFullCodeCoverage") {

    // Depend on unit tests and Android tests tasks
    dependsOn(listOf("testDebugUnitTest", "connectedDebugAndroidTest"))

    // Set task grouping and description
    group = "Reports"
    description = "Execute UI and unit tests, generate and combine Jacoco coverage report"

    // Configure reports to generate both XML and HTML formats
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val javaSrc = mutableListOf<String>()
    val kotlinSrc = mutableListOf<String>()
    val javaClasses = mutableListOf<FileTree>()
    val kotlinClasses = mutableListOf<FileTree>()
    val execution = mutableListOf<FileTree>()

    rootProject.subprojects.filter { it.name == "ggwave-kotlin" }.forEach { proj ->
        proj.tasks.findByName("testDebugUnitTest")?.let { dependsOn(it) }
        proj.tasks.findByName("connectedDebugAndroidTest")?.let { dependsOn(it) }

        javaSrc.add("${proj.projectDir}/src/main/java")
        kotlinSrc.add("${proj.projectDir}/src/main/kotlin")

        javaClasses.add(proj.fileTree(proj.layout.buildDirectory.dir("intermediates/javac/debug")) {
            exclude(jacocoFileFilter)
        })

        kotlinClasses.add(proj.fileTree(proj.layout.buildDirectory.dir("/tmp/kotlin-classes/debug")) {
            exclude(jacocoFileFilter)
        })

        execution.add(proj.fileTree(proj.layout.buildDirectory) {
            include(
                "jacoco/testDebugUnitTest.exec", // Unit test,
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec", // Unit test
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec" // UI test
            )
        })

    }

    sourceDirectories.setFrom(files(javaSrc + kotlinSrc))
    classDirectories.setFrom(files(javaClasses + kotlinClasses))
    executionData.setFrom(files(execution))

    doLast {
        println("✅ Combined coverage report generated at: ${reports.html.outputLocation.get()}\\index.html")
    }
}

// =================================
// JaCoCo Report Configuration Start
// =================================