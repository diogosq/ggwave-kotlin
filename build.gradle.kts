plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.34.0"
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

//>>>>>>>>>> publish config
val getVersionName = "0.8.0-SNAPSHOT"
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
