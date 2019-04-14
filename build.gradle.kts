buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.3.2")
        classpath(kotlin("gradle-plugin", "1.3.30"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks {
    named<Wrapper>("wrapper") {
        gradleVersion = "5.4-rc-1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

gradle.taskGraph.whenReady {
    tasks.forEach { task ->
        if (task.name == "mockableAndroidJar") {
            task.enabled = false
        }
    }
}
