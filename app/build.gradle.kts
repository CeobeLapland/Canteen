plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.canteen"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.canteen"
        minSdk = 24//26
        targetSdk = 36//34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ── Lifecycle & ViewModel ─────────────────────────
    // ViewModel + LiveData（Java 版）
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // ── Room 数据库 ───────────────────────────────────
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1") // Java 用 annotationProcessor

    // ── Fragment ──────────────────────────────────────
    implementation("androidx.fragment:fragment:1.6.2")

    // ── RecyclerView ──────────────────────────────────
    implementation("androidx.recyclerview:recyclerview:1.3.2")


    // ── Material Design ───────────────────────────────
    implementation ("com.google.android.material:material:1.11.0")

    // Transformations.switchMap 等工具
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // ── 测试 ──────────────────────────────────────────
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    // Tests
    // Room 单元测试
    testImplementation("androidx.room:room-testing:2.6.1")

    // SwipeRefreshLayout 自动刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    implementation("com.google.code.gson:gson:2.10.1")


    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson 解析
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // 新增：日志拦截器（必须加！）
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}