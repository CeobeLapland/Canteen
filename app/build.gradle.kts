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
        minSdk = 26//26//把这个改成了26
        targetSdk = 36//34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {

            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)


    //implementation(libs.datastore.rxjava2)
    //implementation(libs.datastore.preferences.rxjava2)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // DataStore Preferences RxJava3（Java专用）
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.1")
    //implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    //implementation("io.reactivex.rxjava3:rxandroid:3.0.2")




    // 仅调试时使用，打包上线不会包含
    //debugImplementation ("com.amitshekhar.android:debug-db:1.0.6")
    //debugImplementation("com.amitshekhar.android:debug-db:1.1.0")

    // Lombok（编译时注解处理器）
    // 版本不匹配，JDK17
    compileOnly("org.projectlombok:lombok:1.18.32")
    //annotationProcessor("org.projectlombok:lombok:1.18.28")
    //implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.32")


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
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")



    implementation("com.google.code.gson:gson:2.10.1")


    // Room 适配 RxJava3
    implementation("androidx.room:room-rxjava3:2.6.1")
    // RxJava 核心
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    // Android 主线程调度器（必须！否则没有 observeOn 主线程）
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")


    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson 解析
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // 新增：日志拦截器（必须加！）
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")





    implementation("androidx.navigation:navigation-fragment:2.8.0")
    implementation("androidx.navigation:navigation-ui:2.8.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Navigation Fragment + UI
    //implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    //implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.cardview:cardview:1.0.0")

    //需要import com.google.android.flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")


    //需要Spinner组件
    //implementation("com.google.android.material:material:1.11.0")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
// 必须依赖协程（Java 调用需要）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // AI功能模块
    //implementation project(":mylibrary")

    //Execution failed for task ':app:processDebugNavigationResources'.
    //> Could not resolve all files for configuration ':app:debugRuntimeClasspath'.
    //   > Could not find com.amitshekhar.android:debug-db:1.0.6.
    //implementation("com.amitshekhar.android:debug-db:1.0.6") {
    //    // 这里可以添加一些排除规则，避免冲突
    //    exclude(group = "com.google.code.gson", module = "gson")
    //}
}