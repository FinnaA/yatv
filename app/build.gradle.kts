plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "ru.digitalavatar.secondlife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidtvapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.0"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }
    }

    // Ключ подписи берётся из переменных окружения, в репозиторий не кладётся.
    // Перед сборкой: source ~/keys/yatv-release.env
    val releaseKeystore = System.getenv("YATV_KEYSTORE")
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = file(releaseKeystore)
                storePassword = System.getenv("YATV_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("YATV_KEY_ALIAS")
                keyPassword = System.getenv("YATV_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            if (releaseKeystore != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.coreKtx)
    implementation(libs.media3Exoplayer)
    implementation(libs.media3Ui)
    implementation(libs.media3Session)
    implementation(libs.media3Database)
    implementation(libs.okhttp)
    implementation(libs.coroutinesAndroid)

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
